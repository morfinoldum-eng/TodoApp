package com.todoapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.todoapp.model.Priority
import com.todoapp.model.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert
    suspend fun insertTodo(todo: TodoItem): Long

    @Update
    suspend fun updateTodo(todo: TodoItem)

    @Delete
    suspend fun deleteTodo(todo: TodoItem)

    @Query("DELETE FROM todos WHERE id = :todoId")
    suspend fun deleteTodoById(todoId: Int)

    @Query("SELECT * FROM todos WHERE id = :todoId")
    suspend fun getTodoById(todoId: Int): TodoItem?

    @Query("SELECT * FROM todos ORDER BY updatedAt DESC")
    fun getAllTodos(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todos WHERE isCompleted = 0 ORDER BY priority DESC, dueDate ASC")
    fun getPendingTodos(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todos WHERE isCompleted = 1 ORDER BY updatedAt DESC")
    fun getCompletedTodos(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todos WHERE priority = :priority ORDER BY dueDate ASC")
    fun getTodosByPriority(priority: Priority): Flow<List<TodoItem>>

    @Query("SELECT * FROM todos WHERE category = :category ORDER BY updatedAt DESC")
    fun getTodosByCategory(category: String): Flow<List<TodoItem>>

    @Query("SELECT * FROM todos WHERE title LIKE :query OR description LIKE :query ORDER BY updatedAt DESC")
    fun searchTodos(query: String): Flow<List<TodoItem>>

    @Query("SELECT COUNT(*) FROM todos")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM todos WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM todos WHERE isCompleted = 0")
    fun getPendingCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM todos WHERE priority = 'URGENT'")
    fun getUrgentCount(): Flow<Int>

    @Query("SELECT DISTINCT category FROM todos ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    @Query("DELETE FROM todos WHERE isCompleted = 1")
    suspend fun deleteCompletedTodos()

    @Query("UPDATE todos SET isCompleted = :completed WHERE id = :todoId")
    suspend fun updateTodoCompletionStatus(todoId: Int, completed: Boolean)
}
