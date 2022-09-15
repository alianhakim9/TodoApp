package id.alianhakim.todoapp.data.local

import androidx.room.*
import id.alianhakim.todoapp.entity.Todo
import id.alianhakim.todoapp.ui.todo.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Query("SELECT * FROM todos_table WHERE (is_completed != :hideCompleted OR is_completed = 0) AND title LIKE '%' || :searchQuery || '%' ORDER BY is_important DESC, title ")
    fun getTodosSortedByTitle(searchQuery: String, hideCompleted: Boolean): Flow<List<Todo>>

    @Query("SELECT * FROM todos_table WHERE (is_completed != :hideCompleted OR is_completed = 0) AND title LIKE '%' || :searchQuery || '%' ORDER BY is_important DESC, created ")
    fun getTodosSortedByDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Todo>>

    fun getTodos(
        searchQuery: String,
        sortOrder: SortOrder,
        hideCompleted: Boolean
    ): Flow<List<Todo>> =
        when (sortOrder) {
            SortOrder.SORT_BY_DATE -> {
                getTodosSortedByDateCreated(searchQuery, hideCompleted)
            }

            SortOrder.SORT_BY_TITLE -> {
                getTodosSortedByTitle(searchQuery, hideCompleted)
            }
        }
}