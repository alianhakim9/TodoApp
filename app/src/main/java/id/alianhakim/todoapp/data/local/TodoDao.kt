package id.alianhakim.todoapp.data.local

import androidx.room.*
import id.alianhakim.todoapp.entity.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Query("SELECT * FROM todos_table WHERE title LIKE '%' || :searchQuery || '%' ORDER BY is_important DESC")
    fun getTodos(searchQuery: String): Flow<List<Todo>>
}