package aitam.csm.poojjitha.crud


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import aitam.csm.poojjitha.todo.ui.theme.ToDoTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class TaskDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        @Suppress("DEPRECATION")
        val taskItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("SELECTED_TASK", TaskItem::class.java)
        } else {
            intent.getSerializableExtra("SELECTED_TASK") as? TaskItem
        }
        setContent {
            ToDoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (taskItem != null) {
                        TaskDetailContent(
                            taskItem = taskItem,
                            onDeleteSuccess = { finish() },
                            modifier = Modifier
                                .padding(innerPadding)
                                .padding(16.dp)
                        )
                    } else {
                        Text(
                            text = "Task not found.",
                            modifier = Modifier
                                .padding(innerPadding)
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// FIREBASE HELPER FUNCTIONS
// -------------------------------------------------------------
fun updateTaskInFirestore(
    context: Context,
    taskId: String,
    title: String,
    task: String,
    onSuccess: () -> Unit
) {
    val db = Firebase.firestore
    val updatedData = mapOf(
        "title" to title,
        "task" to task
    )

    db.collection("tasks")
        .document(taskId)
        .update(updatedData)
        .addOnSuccessListener {
            Toast.makeText(context, "Task Updated!", Toast.LENGTH_SHORT).show()
            onSuccess()
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Update failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
}

fun deleteTaskFromFirestore(
    context: Context,
    taskId: String,
    onSuccess: () -> Unit
) {
    val db = Firebase.firestore

    db.collection("tasks")
        .document(taskId)
        .delete()
        .addOnSuccessListener {
            Toast.makeText(context, "Task Deleted!", Toast.LENGTH_SHORT).show()
            onSuccess()
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Delete failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
}

// -------------------------------------------------------------
// COMPOSABLE UI
// -------------------------------------------------------------
@Composable
fun TaskDetailContent(
    taskItem: TaskItem,
    onDeleteSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentTask by remember { mutableStateOf(taskItem) }

    // State for controlling the Edit Modal visibility
    var showEditDialog by remember { mutableStateOf(false) }

    // State for pre-filled input fields inside the modal
    var editedTitle by remember { mutableStateOf(currentTask.title) }
    var editedDescription by remember { mutableStateOf(currentTask.task) }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Task Details",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Title",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = currentTask.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Description",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = currentTask.task.ifBlank { "No description provided." },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // Ensure inputs are initialized with current values when opening modal
                editedTitle = currentTask.title
                editedDescription = currentTask.task
                showEditDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit Task")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                // Call Firebase delete helper
                deleteTaskFromFirestore(
                    context = context,
                    taskId = currentTask.id,
                    onSuccess = onDeleteSuccess
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Delete Task")
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Task") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedTitle,
                        onValueChange = { editedTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editedDescription,
                        onValueChange = { editedDescription = it },
                        label = { Text("Task Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editedTitle.isNotBlank()) {
                            // Call Firebase update helper
                            updateTaskInFirestore(
                                context = context,
                                taskId = currentTask.id,
                                title = editedTitle,
                                task = editedDescription,
                                onSuccess = {
                                    currentTask = currentTask.copy(
                                        title = editedTitle,
                                        task = editedDescription
                                    )
                                    showEditDialog = false
                                }
                            )
                        } else {
                            Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TaskDetailContentPreview() {
    ToDoTheme {
        Surface {
            TaskDetailContent(
                taskItem = TaskItem(
                    id = "1",
                    title = "Sample Task",
                    task = "This is a detailed description of the sample task."
                ),
                onDeleteSuccess = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}