package id.alianhakim.todoapp.ui.todo

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.alianhakim.todoapp.R
import id.alianhakim.todoapp.databinding.FragmentAddEditTodoBinding
import id.alianhakim.todoapp.ui.todo.viewmodels.AddEditTodoViewModel
import id.alianhakim.todoapp.utils.exhaustive
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditTodoFragment : Fragment(R.layout.fragment_add_edit_todo) {
    private var _binding: FragmentAddEditTodoBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AddEditTodoViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddEditTodoBinding.bind(view)
        binding.apply {
            editTextTodoTitle.setText(viewModel.todoTitle)
            checkBoxImportant.isChecked = viewModel.isImportant
            // skip checkbox animation
            checkBoxImportant.jumpDrawablesToCurrentState()
            textViewDateCreated.isVisible = viewModel.todo != null
            textViewDateCreated.text = "Created: ${viewModel.todo?.createdDateFormatter}"

            editTextTodoTitle.addTextChangedListener {
                viewModel.todoTitle = it.toString()
            }
            checkBoxImportant.setOnCheckedChangeListener { _, isChecked ->
                viewModel.isImportant = isChecked
            }
            fabSaveTask.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTodoEvent.collect { event ->
                when (event) {
                    is AddEditTodoViewModel.AddEditTodoEvent.NavigateBackWithResult -> {
                        // hide keyboard
                        binding.editTextTodoTitle.clearFocus()
                        setFragmentResult(
                            requestKey = "add_edit_todo_request",
                            result = bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is AddEditTodoViewModel.AddEditTodoEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "AddEditTodoFragment"
    }
}

const val ADD_TODO_RESULT_OK = Activity.RESULT_FIRST_USER
const val EDIT_TODO_RESULT_OK = Activity.RESULT_FIRST_USER + 1