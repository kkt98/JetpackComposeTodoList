package com.example.todolist.ui

fun RandomData(): Data {
    val items = listOf(
        "a",
        "b",
        "c",
        "d"
    ).random()

    val icons = ToDoIcons.values().random()

    return Data(items, icons)
}
