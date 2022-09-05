package id.alianhakim.todoapp.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

@Entity(tableName = "todos_table")
@Parcelize
data class Todo(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    @ColumnInfo(name = "is_important")
    val isImportant: Boolean = false,
    @ColumnInfo(name = "created")
    val created: Long = System.currentTimeMillis()
) : Parcelable {
    val createdDateFormatter: String
        get() = DateFormat
            .getDateTimeInstance()
            .format(created)
}
