# CRUD Android Application

A simple and user-friendly Android CRUD (Create, Read, Update, Delete) application developed using **Kotlin** and **Jetpack Compose**.

The application allows users to create, view, update, and delete task information through a clean and modern Android interface.

## 📱 Project Overview

This project demonstrates the implementation of CRUD operations in an Android application.

The application provides a task management interface where users can:

- ➕ Create new tasks
- 📋 View existing tasks
- ✏️ Update task details
- 🗑️ Delete tasks
- 👀 View detailed task information

## 🛠️ Technologies Used

- **Kotlin**
- **Android Studio**
- **Jetpack Compose**
- **Gradle**
- **Firebase**
- **Android SDK**

## ✨ Features

### Create
Users can add a new task by entering the required task information.

### Read
Users can view all available tasks in the application.

### Update
Users can modify existing task details.

### Delete
Users can remove tasks that are no longer required.

### Task Details
Users can open a task to view its detailed information.

## 📂 Project Structure

```text
Crud/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── aitam/
│   │   │   │       └── csm/
│   │   │   │           └── poojjitha/
│   │   │   │               └── crud/
│   │   │   │                   ├── MainActivity.kt
│   │   │   │                   ├── CRUDTheme.kt
│   │   │   │                   ├── TaskDetailActivity.kt
│   │   │   │                   ├── TaskItem.kt
│   │   │   │                   ├── TaskListScreen.kt
│   │   │   │                   └── ui/
│   │   │   │                       └── theme/
│   │   │   │
│   │   │   ├── AndroidManifest.xml
│   │   │   └── res/
│   │   │
│   │   └── test/
│   │
│   └── build.gradle.kts
│
├── gradle/
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
└── README.md
