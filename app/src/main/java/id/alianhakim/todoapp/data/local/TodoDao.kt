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

    @Query("SELECT * FROM todos_table")
    fun getTodos(): Flow<List<Todo>>
}