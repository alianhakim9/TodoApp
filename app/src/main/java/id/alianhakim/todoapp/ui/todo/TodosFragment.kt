package id.alianhakim.todoapp.ui.todo

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.alianhakim.todoapp.R
import id.alianhakim.todoapp.databinding.FragmentTodosBinding
import id.alianhakim.todoapp.utils.Constants.TAG
import id.alianhakim.todoapp.utils.onQueryTextChanged

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
            Log.d(TAG, "onViewCreated: $it")
            todosAdapter.submitList(it)
        }

        // actionbar menu
        val menuHosts: MenuHost = requireActivity()
        menuHosts.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_fragment_todos, menu)

                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView

                searchView.onQueryTextChanged {
                    viewModel.searchQuery.value = it
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_sort_by_name -> {
                        true
                    }

                    R.id.action_sort_by_date_created -> {
                        true
                    }

                    R.id.action_hide_completed_todo -> {
                        menuItem.isChecked = !menuItem.isChecked
                        true
                    }

                    R.id.action_delete_all_completed_todo -> {
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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