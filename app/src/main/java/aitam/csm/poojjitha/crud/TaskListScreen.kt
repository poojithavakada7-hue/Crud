package aitam.csm.poojjitha.crud


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import aitam.csm.poojjitha.crud.ui.theme.CrudTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class TaskListScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CRUDTheme {
                val taskList = remember { mutableStateListOf<TaskItem>() }
                var isLoading by remember { mutableStateOf(value = true) }

                // Listen to real-time Firestore updates
                DisposableEffect(Unit) {
                    val db = Firebase.firestore
                    val listenerRegistration = db.collection("tasks")
                        .addSnapshotListener { snapshot, error ->
                            isLoading = false
                            if ((error != null) || (snapshot == null)) return@addSnapshotListener

                            val tasks = snapshot.documents.map { doc ->
                                TaskItem(
                                    id = doc.id,
                                    title = doc.getString("title") ?: "",
                                    task = doc.getString("task") ?: "",
                                )
                            }
                            taskList.clear()
                            taskList.addAll(tasks)
                        }

                    onDispose {
                        listenerRegistration.remove()
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TaskListContent(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        taskList = taskList,
                        isLoading = isLoading,
                        onAddTaskClick = {
                            val intent = Intent(this@TaskListScreen, MainActivity::class.java)
                            startActivity(intent)
                        },
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SINGLE TASK CARD UI
// -------------------------------------------------------------
@Composable
fun TaskCard(
    taskItem: TaskItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = taskItem.title,
                style = MaterialTheme.typography.titleMedium,
            )
            if (taskItem.task.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = taskItem.task,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

// -------------------------------------------------------------
// TASK LIST SCREEN COMPOSABLE
// -------------------------------------------------------------
@Composable
fun TaskListContent(
    modifier: Modifier = Modifier,
    taskList: List<TaskItem> = emptyList(),
    isLoading: Boolean = false,
    onAddTaskClick: () -> Unit = {},
    onTaskClick: (TaskItem) -> Unit = {},
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Header Row with Title and Add Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "All Tasks",
                style = MaterialTheme.typography.headlineMedium,
            )
            Button(onClick = onAddTaskClick) {
                Text("+ Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else if (taskList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No tasks found. Click + Add to create one!",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        } else {
            // Scrollable list using LazyColumn
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(taskList, key = { it.id }) { item ->
                    TaskCard(
                        taskItem = item,
                        onClick = { onTaskClick(item) },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskListContentPreview() {
    CRUDTheme {
        TaskListContent(
            taskList = listOf(
                TaskItem(id = "1", title = "Sample Task 1", task = "Description 1"),
                TaskItem(id = "2", title = "Sample Task 2", task = "Description 2"),
            ),
        )
    }
}