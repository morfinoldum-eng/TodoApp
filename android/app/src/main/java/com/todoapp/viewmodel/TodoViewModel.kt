package com.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.todoapp.data.repository.TodoRepository
import com.todoapp.model.Priority
import com.todoapp.model.TodoFilter
import com.todoapp.model.TodoItem
import com.todoapp.model.TodoStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {

    // UI State
    private val _todoList = MutableStateFlow<List<TodoItem>>(emptyList())
    val todoList: StateFlow<List<TodoItem>> = _todoList.asStateFlow()

    private val _todoStats = MutableStateFlow(TodoStats())
    val todoStats: StateFlow<TodoStats> = _todoStats.asStateFlow()

    private val _filter = MutableStateFlow(TodoFilter())
    val filter: StateFlow<TodoFilter> = _filter.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _selectedTodo = MutableStateFlow<TodoItem?>(null)
    val selectedTodo: StateFlow<TodoItem?> = _selectedTodo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadAllData()
    }

    private fun loadAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Combine all flows
                combine(
                    repository.getAllTodos(),
                    repository.getTodoStats(),
                    repository.getAllCategories()
                ) { todos, stats, categories ->
                    Triple(todos, stats, categories)
                }.collect { (todos, stats, categories) ->
                    _todoList.value = applyFilter(todos)
                    _todoStats.value = stats
                    _categories.value = categories
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading todos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun applyFilter(todos: List<TodoItem>): List<TodoItem> {
        val currentFilter = _filter.value
        return todos.filter { todo ->
            val completionMatch = when {
                todo.isCompleted && !currentFilter.showCompleted -> false
                !todo.isCompleted && !currentFilter.showPending -> false
                else -> true
            }

            val priorityMatch = currentFilter.selectedPriorities.contains(todo.priority)
            val categoryMatch = currentFilter.selectedCategory == null || todo.category == currentFilter.selectedCategory
            val searchMatch = if (currentFilter.searchQuery.isNotEmpty()) {
                todo.title.contains(currentFilter.searchQuery, ignoreCase = true) ||
                        todo.description.contains(currentFilter.searchQuery, ignoreCase = true)
            } else {
                true
            }

            completionMatch && priorityMatch && categoryMatch && searchMatch
        }
    }

    // Add new todo
    fun addTodo(title: String, description: String = "", priority: Priority = Priority.MEDIUM, category: String = "General", dueDate: String? = null) {
        viewModelScope.launch {
            try {
                val todo = TodoItem(
                    title = title,
                    description = description,
                    priority = priority,
                    category = category,
                    dueDate = dueDate
                )
                repository.insertTodo(todo)
                clearError()
            } catch (e: Exception) {
                _errorMessage.value = "Error adding todo: ${e.message}"
            }
        }
    }

    // Update todo
    fun updateTodo(todo: TodoItem) {
        viewModelScope.launch {
            try {
                repository.updateTodo(todo.copy(updatedAt = TodoItem.getCurrentDateTime()))
                clearError()
            } catch (e: Exception) {
                _errorMessage.value = "Error updating todo: ${e.message}"
            }
        }
    }

    // Delete todo
    fun deleteTodo(todo: TodoItem) {
        viewModelScope.launch {
            try {
                repository.deleteTodo(todo)
                _selectedTodo.value = null
                clearError()
            } catch (e: Exception) {
                _errorMessage.value = "Error deleting todo: ${e.message}"
            }
        }
    }

    // Toggle todo completion
    fun toggleTodoCompletion(todoId: Int, completed: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateTodoCompletionStatus(todoId, !completed)
                clearError()
            } catch (e: Exception) {
                _errorMessage.value = "Error updating todo: ${e.message}"
            }
        }
    }

    // Update filter
    fun updateFilter(filter: TodoFilter) {
        _filter.value = filter
        loadAllData()
    }

    // Search todos
    fun searchTodos(query: String) {
        val currentFilter = _filter.value.copy(searchQuery = query)
        updateFilter(currentFilter)
    }

    // Filter by category
    fun filterByCategory(category: String?) {
        val currentFilter = _filter.value.copy(selectedCategory = category)
        updateFilter(currentFilter)
    }

    // Filter by priority
    fun filterByPriority(priorities: List<Priority>) {
        val currentFilter = _filter.value.copy(selectedPriorities = priorities)
        updateFilter(currentFilter)
    }

    // Toggle completed todos visibility
    fun toggleCompletedVisibility() {
        val currentFilter = _filter.value
        updateFilter(currentFilter.copy(showCompleted = !currentFilter.showCompleted))
    }

    // Clear completed todos
    fun clearCompletedTodos() {
        viewModelScope.launch {
            try {
                repository.deleteCompletedTodos()
                clearError()
            } catch (e: Exception) {
                _errorMessage.value = "Error clearing completed todos: ${e.message}"
            }
        }
    }

    // Select todo
    fun selectTodo(todo: TodoItem) {
        _selectedTodo.value = todo
    }

    // Clear error
    fun clearError() {
        _errorMessage.value = null
    }
}

class TodoViewModelFactory(private val repository: TodoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
