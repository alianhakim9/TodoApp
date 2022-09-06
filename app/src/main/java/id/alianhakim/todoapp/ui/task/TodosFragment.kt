package id.alianhakim.todoapp.ui.task

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.alianhakim.todoapp.R
import id.alianhakim.todoapp.databinding.FragmentTodosBinding

@AndroidEntryPoint
class TodosFragment : Fragment(R.layout.fragment_todos) {

    private var _binding: FragmentTodosBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TodoViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTodosBinding.bind(view)

        binding.fabAddTask.setOnClickListener {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {

    }
}