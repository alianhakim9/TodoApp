package id.alianhakim.todoapp.ui.todo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.alianhakim.todoapp.R
import id.alianhakim.todoapp.databinding.FragmentTodosBinding

@AndroidEntryPoint
class TodosFragment : Fragment(R.layout.fragment_todos) {

    private var _binding: FragmentTodosBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TodoViewModel>()
    private val todosAdapter by lazy { TodosAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTodosBinding.bind(view)
        setupRecyclerView()
        binding.apply {
            fabAddTask.setOnClickListener {
                // todo: navigate to add todo fragment
            }
        }
        viewModel.todos.observe(viewLifecycleOwner) {
            todosAdapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        binding.apply {
            recyclerViewTasks.apply {
                adapter = todosAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
    }
}