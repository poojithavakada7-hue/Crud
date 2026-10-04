package aitam.csm.poojjitha.crud

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import aitam.csm.poojjitha.crud.ui.theme.CrudTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            CrudTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AddTaskScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    )
                }
            }
        }
    }
}

fun addTaskToFirebase(context: Context, title: String, task: String, onComplete: () -> Unit = {}) {
    if (title.isBlank() || task.isBlank()) {
        Toast.makeText(context, "Please enter both title and description", Toast.LENGTH_SHORT).show()
        return
    }

    val db = FirebaseFirestore.getInstance()
    val taskData = hashMapOf(
        "title" to title,
        "task" to task,
        "timestamp" to System.currentTimeMillis()
    )

    Log.d("FirebaseStore", "Adding task to Firestore: $taskData")

    db.collection("tasks")
        .add(taskData)
        .addOnSuccessListener { docRef ->
            Log.d("FirebaseStore", "Task added successfully with ID: ${docRef.id}")
            Toast.makeText(context, "Task added successfully", Toast.LENGTH_SHORT).show()
            onComplete()
        }
        .addOnFailureListener { e ->
            Log.e("FirebaseStore", "Error adding task to Firestore", e)
            Toast.makeText(context, "Failed to add task: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
}

@Composable
fun AddTaskScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var task by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Add Task Screen",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(text = "Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = task,
            onValueChange = { task = it },
            label = { Text(text = "Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (title.isNotBlank() && task.isNotBlank()) {
                    addTaskToFirebase(
                        context = context,
                        title = title,
                        task = task,
                        onComplete = {
                            title = ""
                            task = ""
                        }
                    )
                } else {
                    Toast.makeText(context, "Please enter both title and description", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Add Task")
        }
        TextButton(
            onClick = {
                val taskListIntent = Intent(context, TaskListScreen::class.java)
                context.startActivity(taskListIntent)
            }
        ) {
            Text("View Tasks->")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddTaskScreenPreview() {
    CrudTheme {
        AddTaskScreen()
    }
}
