package id.alianhakim.todoapp.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesRepository"

enum class SortOrder {
    SORT_BY_TITLE,
    SORT_BY_DATE
}

data class FilterPreferences(
    val sortOrder: SortOrder,
    val hideCompleted: Boolean
)

@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext context: Context
) {

    private val dataStore = context.createDataStore(name = "user_preferences")
    val preferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { pref ->
            val sortOrder =
                SortOrder.valueOf(pref[PreferencesKeys.SORT_ORDER] ?: SortOrder.SORT_BY_DATE.name)
            val hideCompleted = pref[PreferencesKeys.HIDE_COMPLETED] ?: false
            FilterPreferences(sortOrder, hideCompleted)
        }

    suspend fun updateSortOrder(sortOrder: SortOrder) {
        dataStore.edit { pref ->
            pref[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateHideCompleted(hideCompleted: Boolean) {
        dataStore.edit { pref ->
            pref[PreferencesKeys.HIDE_COMPLETED] = hideCompleted
        }
    }

    private object PreferencesKeys {
        val SORT_ORDER = preferencesKey<String>(name = "sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>(name = "hide_completed")
    }
}