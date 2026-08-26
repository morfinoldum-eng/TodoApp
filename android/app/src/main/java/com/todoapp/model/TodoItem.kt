package com.todoapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "todos")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val dueDate: String? = null,
    val category: String = "General",
    val createdAt: String = getCurrentDateTime(),
    val updatedAt: String = getCurrentDateTime(),
    val tags: String = "" // JSON array as string
) {
    companion object {
        fun getCurrentDateTime(): String {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            return LocalDateTime.now().format(formatter)
        }
    }
}

enum class Priority {
    LOW, MEDIUM, HIGH, URGENT
}

data class TodoStats(
    val totalTodos: Int = 0,
    val completedTodos: Int = 0,
    val pendingTodos: Int = 0,
    val highPriorityTodos: Int = 0
) {
    val completionPercentage: Float
        get() = if (totalTodos > 0) (completedTodos.toFloat() / totalTodos) * 100 else 0f
}

data class TodoFilter(
    val showCompleted: Boolean = true,
    val showPending: Boolean = true,
    val selectedPriorities: List<Priority> = Priority.values().toList(),
    val selectedCategory: String? = null,
    val searchQuery: String = ""
)
