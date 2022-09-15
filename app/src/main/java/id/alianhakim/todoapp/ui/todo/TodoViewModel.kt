package id.alianhakim.todoapp.ui.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import id.alianhakim.todoapp.data.local.TodoDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoDao: TodoDao
) : ViewModel() {
    val searchQuery = MutableStateFlow("")

    val sortOrder = MutableStateFlow(SortOrder.SORT_BY_DATE)
    val hideCompleted = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val todosFlow =
        combine(
            searchQuery,
            sortOrder,
            hideCompleted
        ) { query, sortOrder, hideCompleted ->
            Triple(query, sortOrder, hideCompleted)
        }.flatMapLatest {
            // destructuring triple value
                (query, sortOrder, hideCompleted) ->
            todoDao.getTodos(query, sortOrder, hideCompleted)
        }

    val todos = todosFlow.asLiveData()
}

enum class SortOrder {
    SORT_BY_TITLE,
    SORT_BY_DATE
}