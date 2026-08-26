package com.todoapp.data.repository

import com.todoapp.data.local.TodoDao
import com.todoapp.model.Priority
import com.todoapp.model.TodoItem
import com.todoapp.model.TodoStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class TodoRepository(private val todoDao: TodoDao) {

    // Get all todos
    fun getAllTodos(): Flow<List<TodoItem>> = todoDao.getAllTodos()

    // Get pending todos
    fun getPendingTodos(): Flow<List<TodoItem>> = todoDao.getPendingTodos()

    // Get completed todos
    fun getCompletedTodos(): Flow<List<TodoItem>> = todoDao.getCompletedTodos()

    // Get todos by priority
    fun getTodosByPriority(priority: Priority): Flow<List<TodoItem>> =
        todoDao.getTodosByPriority(priority)

    // Get todos by category
    fun getTodosByCategory(category: String): Flow<List<TodoItem>> =
        todoDao.getTodosByCategory(category)

    // Search todos
    fun searchTodos(query: String): Flow<List<TodoItem>> {
        val searchQuery = "%$query%"
        return todoDao.searchTodos(searchQuery)
    }

    // Get all categories
    fun getAllCategories(): Flow<List<String>> = todoDao.getAllCategories()

    // Get statistics
    fun getTodoStats(): Flow<TodoStats> = combine(
        todoDao.getTotalCount(),
        todoDao.getCompletedCount(),
        todoDao.getPendingCount(),
        todoDao.getUrgentCount()
    ) { total, completed, pending, urgent ->
        TodoStats(
            totalTodos = total,
            completedTodos = completed,
            pendingTodos = pending,
            highPriorityTodos = urgent
        )
    }

    // Insert todo
    suspend fun insertTodo(todo: TodoItem): Long = todoDao.insertTodo(todo)

    // Update todo
    suspend fun updateTodo(todo: TodoItem) = todoDao.updateTodo(todo)

    // Delete todo
    suspend fun deleteTodo(todo: TodoItem) = todoDao.deleteTodo(todo)

    // Delete todo by id
    suspend fun deleteTodoById(todoId: Int) = todoDao.deleteTodoById(todoId)

    // Get todo by id
    suspend fun getTodoById(todoId: Int): TodoItem? = todoDao.getTodoById(todoId)

    // Update completion status
    suspend fun updateTodoCompletionStatus(todoId: Int, completed: Boolean) =
        todoDao.updateTodoCompletionStatus(todoId, completed)

    // Delete completed todos
    suspend fun deleteCompletedTodos() = todoDao.deleteCompletedTodos()
}
