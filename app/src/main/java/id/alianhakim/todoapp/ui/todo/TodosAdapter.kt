package id.alianhakim.todoapp.ui.todo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.alianhakim.todoapp.databinding.ItemTodoBinding
import id.alianhakim.todoapp.entity.Todo

class TodosAdapter : ListAdapter<Todo, TodosAdapter.TodoViewHolder>(TodoCallBack()) {
    inner class TodoViewHolder(private val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(todo: Todo) {
            binding.apply {
                checkBoxCompleted.isChecked = todo.isCompleted
                textViewName.text = todo.title
                textViewName.paint.isStrikeThruText = todo.isImportant
                labelPriority.isVisible = todo.isImportant
            }
        }
    }

    class TodoCallBack : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(todo = currentItem)
    }
}