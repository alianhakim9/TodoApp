package id.alianhakim.todoapp.ui.todo

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.alianhakim.todoapp.R
import id.alianhakim.todoapp.data.SortOrder
import id.alianhakim.todoapp.databinding.FragmentTodosBinding
import id.alianhakim.todoapp.entity.Todo
import id.alianhakim.todoapp.ui.todo.TodosAdapter.OnItemClickListener
import id.alianhakim.todoapp.ui.todo.viewmodels.TodoEvent
import id.alianhakim.todoapp.ui.todo.viewmodels.TodoViewModel
import id.alianhakim.todoapp.utils.exhaustive
import id.alianhakim.todoapp.utils.onQueryTextChanged
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class TodosFragment : Fragment(R.layout.fragment_todos), OnItemClickListener {

    private var _binding: FragmentTodosBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TodoViewModel>()
    private val todosAdapter by lazy { TodosAdapter(this) }
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTodosBinding.bind(view)
        setupRecyclerView()

        binding.apply {
            fabAddTask.setOnClickListener {
                viewModel.setOnAddTodoClick()
            }
            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val todo = todosAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTodoSwiped(todo)
                }
            }).attachToRecyclerView(recyclerViewTasks)
        }

        // get data from viewModel and set to recyclerView
        viewModel.todos.observe(viewLifecycleOwner) {
            todosAdapter.submitList(it)
        }

        // actionbar menu
        actionBarMenu()

        // handle event from viewModel
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.todoEvent.collect { event ->
                when (event) {
                    is TodoEvent.ShowUndoDeleteTodoMessage -> {
                        Snackbar.make(requireView(), "Todo has been deleted", Snackbar.LENGTH_SHORT)
                            .also { snackBar ->
                                snackBar.setAction("UNDO") {
                                    // event.todo get from smart cast
                                    viewModel.onUndoDeleteClick(event.todo)
                                    snackBar.dismiss()
                                }
                            }.show()
                    }
                    is TodoEvent.NavigateToAddTodoScreen -> {
                        val action =
                            TodosFragmentDirections.actionTodosFragmentToAddEditTodoFragment(title = "Add Todo")
                        findNavController().navigate(action)
                    }
                    is TodoEvent.NavigateToEditTodoScreen -> {
                        val action =
                            TodosFragmentDirections.actionTodosFragmentToAddEditTodoFragment(
                                title = "Edit Todo",
                                event.todo
                            )
                        findNavController().navigate(action)
                    }
                    is TodoEvent.ShowTodoSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT).show()
                    }
                    is TodoEvent.NavigateToDeleteAllCompletedScreen -> {
                        val action =
                            TodosFragmentDirections.actionGlobalDeleteAllCompletedDialogFragment()
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }

        // get result from add edit fragment
        setFragmentResultListener("add_edit_todo_request") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        searchView.setOnQueryTextListener(null)
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

    private fun actionBarMenu() {
        val menuHosts: MenuHost = requireActivity()
        menuHosts.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_fragment_todos, menu)

                val searchItem = menu.findItem(R.id.action_search)
                searchView = searchItem.actionView as SearchView

                searchView.onQueryTextChanged {
                    viewModel.searchQuery.value = it
                }

                val pendingQuery = viewModel.searchQuery.value
                if (pendingQuery != null && pendingQuery.isNotEmpty()) {
                    searchItem.expandActionView()
                    searchView.setQuery(pendingQuery, false)
                }

                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    menu.findItem(R.id.action_hide_completed_todo).isChecked =
                            // get single value from flow
                        viewModel.preferencesFlow.first().hideCompleted
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_sort_by_title -> {
                        viewModel.onSortOrderSelected(SortOrder.SORT_BY_TITLE)
                        true
                    }

                    R.id.action_sort_by_date_created -> {
                        viewModel.onSortOrderSelected(SortOrder.SORT_BY_DATE)
                        true
                    }

                    R.id.action_hide_completed_todo -> {
                        menuItem.isChecked = !menuItem.isChecked
                        viewModel.onHideCompleted(menuItem.isChecked)
                        true
                    }

                    R.id.action_delete_all_completed_todo -> {
                        viewModel.onDeleteAllCompletedClick()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onItemClick(todo: Todo) {
        viewModel.onTodoSelected(todo)
    }

    override fun onCheckboxClick(todo: Todo, isChecked: Boolean) {
        viewModel.onTodoCheckChanged(todo, isChecked)
    }
}