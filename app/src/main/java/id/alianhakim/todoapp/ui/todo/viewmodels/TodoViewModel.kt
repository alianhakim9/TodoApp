package id.alianhakim.todoapp.ui.todo.viewmodels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.alianhakim.todoapp.data.PreferencesRepository
import id.alianhakim.todoapp.data.SortOrder
import id.alianhakim.todoapp.data.local.TodoDao
import id.alianhakim.todoapp.entity.Todo
import id.alianhakim.todoapp.ui.todo.ADD_TODO_RESULT_OK
import id.alianhakim.todoapp.ui.todo.EDIT_TODO_RESULT_OK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoDao: TodoDao,
    private val preferencesRepository: PreferencesRepository,
    state: SavedStateHandle
) : ViewModel() {
    val searchQuery = state.getLiveData("searchQuery", "")

    val preferencesFlow = preferencesRepository.preferencesFlow

    private val todoEventChannel = Channel<TodoEvent>()
    val todoEvent = todoEventChannel.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val todosFlow =
        combine(
            searchQuery.asFlow(),
            preferencesFlow
        ) { query, filterPreferences ->
            Pair(query, filterPreferences)
        }.flatMapLatest {
            // destructuring triple value
                (query, filterPreferences) ->
            todoDao.getTodos(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
        }

    val todos = todosFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesRepository.updateSortOrder(sortOrder)
    }

    fun onHideCompleted(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesRepository.updateHideCompleted(hideCompleted)
    }

    fun onTodoSelected(todo: Todo) = viewModelScope.launch {
        todoEventChannel.send(TodoEvent.NavigateToEditTodoScreen(todo))
    }

    fun onTodoCheckChanged(todo: Todo, checked: Boolean) = viewModelScope.launch {
        todoDao.update(todo.copy(isCompleted = checked))
    }

    fun onTodoSwiped(todo: Todo?) = viewModelScope.launch {
        if (todo != null) {
            todoDao.delete(todo)
            todoEventChannel.send(TodoEvent.ShowUndoDeleteTodoMessage(todo))
        }
    }

    fun onUndoDeleteClick(todo: Todo) = viewModelScope.launch {
        todoDao.insert(todo)
    }

    fun setOnAddTodoClick() = viewModelScope.launch {
        todoEventChannel.send(TodoEvent.NavigateToAddTodoScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TODO_RESULT_OK -> {
                showTaskSavedConfirmationMessage("Todo Added")
            }
            EDIT_TODO_RESULT_OK -> {
                showTaskSavedConfirmationMessage("Todo Updated")
            }
        }
    }

    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch {
        todoEventChannel.send(TodoEvent.ShowTodoSavedConfirmationMessage(text))
    }

    fun onDeleteAllCompletedClick() = viewModelScope.launch {
        todoEventChannel.send(TodoEvent.NavigateToDeleteAllCompletedScreen)
    }
}

// same as enum but sealed class can hold the data
sealed class TodoEvent {
    object NavigateToAddTodoScreen : TodoEvent()
    data class NavigateToEditTodoScreen(val todo: Todo) : TodoEvent()
    data class ShowUndoDeleteTodoMessage(val todo: Todo) : TodoEvent()
    data class ShowTodoSavedConfirmationMessage(val message: String) : TodoEvent()
    object NavigateToDeleteAllCompletedScreen : TodoEvent()
}
