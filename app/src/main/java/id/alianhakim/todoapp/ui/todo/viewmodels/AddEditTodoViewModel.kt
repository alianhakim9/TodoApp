package id.alianhakim.todoapp.ui.todo.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.alianhakim.todoapp.data.local.TodoDao
import id.alianhakim.todoapp.entity.Todo
import id.alianhakim.todoapp.ui.todo.ADD_TODO_RESULT_OK
import id.alianhakim.todoapp.ui.todo.EDIT_TODO_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTodoViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val todoDao: TodoDao
) : ViewModel() {
    val todo = state.get<Todo>("todo")

    var todoTitle = state.get<String>("todoTitle") ?: todo?.title ?: ""
        set(value) {
            field = value
            state["todoTitle"] = value
        }

    var isImportant = state.get<Boolean>("isImportant") ?: todo?.isImportant ?: false
        set(value) {
            field = value
            state["isImportant"] = value
        }

    private val addEditTodoEventChannel = Channel<AddEditTodoEvent>()
    val addEditTodoEvent = addEditTodoEventChannel.receiveAsFlow()

    fun onSaveClick() = viewModelScope.launch {
        if (todoTitle.isBlank()) {
            showInvalidInputMessage("Title cannot be empty")
            return@launch
        }

        if (todo != null) {
            val updatedTodo = todo.copy(
                title = todoTitle,
                isImportant = isImportant
            )
            updateTodo(updatedTodo)
        } else {
            val newTodo = Todo(
                title = todoTitle,
                isImportant = isImportant
            )
            createTodo(newTodo)
        }
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        addEditTodoEventChannel.send(AddEditTodoEvent.ShowInvalidInputMessage(text))
    }

    private fun updateTodo(todo: Todo) = viewModelScope.launch {
        todoDao.update(todo)
        // todo: navigate back
        addEditTodoEventChannel.send(AddEditTodoEvent.NavigateBackWithResult(EDIT_TODO_RESULT_OK))
    }

    private fun createTodo(newTodo: Todo) = viewModelScope.launch {
        todoDao.insert(newTodo)
        // todo: navigate back
        addEditTodoEventChannel.send(AddEditTodoEvent.NavigateBackWithResult(ADD_TODO_RESULT_OK))
    }


    sealed class AddEditTodoEvent {
        data class ShowInvalidInputMessage(val message: String) : AddEditTodoEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTodoEvent()
    }
}