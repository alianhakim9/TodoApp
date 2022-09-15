package id.alianhakim.todoapp.ui.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.alianhakim.todoapp.data.PreferencesRepository
import id.alianhakim.todoapp.data.SortOrder
import id.alianhakim.todoapp.data.local.TodoDao
import id.alianhakim.todoapp.entity.Todo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoDao: TodoDao,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    val searchQuery = MutableStateFlow("")

    val preferencesFlow = preferencesRepository.preferencesFlow

    @OptIn(ExperimentalCoroutinesApi::class)
    private val todosFlow =
        combine(
            searchQuery,
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

    fun onTodoSelected(todo: Todo) {

    }

    fun onTodoCheckChanged(todo: Todo, checked: Boolean) = viewModelScope.launch {
        todoDao.update(todo.copy(isCompleted = checked))
    }
}

