package id.alianhakim.todoapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import id.alianhakim.todoapp.di.ApplicationScope
import id.alianhakim.todoapp.entity.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(
    entities = [Todo::class],
    version = 1,
    exportSchema = true
)
abstract class TodoDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "todo_db"
    }

    abstract fun todoDao(): TodoDao

    // callback used for dummy data
    class Callback @Inject constructor(
        // this injection call by dagger hilt after TodoDatabase has been build
        private val database: Provider<TodoDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // db operations
            val dao = database.get().todoDao()
            // coroutine scope to launch insert method
            applicationScope.launch {
                dao.insert(Todo(title = "Coding", isCompleted = false, isImportant = true))
                dao.insert(Todo(title = "Title 2", isCompleted = false, isImportant = true))
                dao.insert(Todo(title = "Title 3", isCompleted = false))
                dao.insert(Todo(title = "Title 4", isCompleted = true))
            }
        }
    }
}