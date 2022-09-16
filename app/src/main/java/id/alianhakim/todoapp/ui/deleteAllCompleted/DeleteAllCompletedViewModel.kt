package id.alianhakim.todoapp.ui.deleteAllCompleted

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.alianhakim.todoapp.data.local.TodoDao
import id.alianhakim.todoapp.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllCompletedViewModel @Inject constructor(
    private val todoDao: TodoDao,
    @ApplicationScope private val application: CoroutineScope
) : ViewModel() {

    fun onConfirmClick() = viewModelScope.launch {
        todoDao.deleteCompletedTodo()
    }

}