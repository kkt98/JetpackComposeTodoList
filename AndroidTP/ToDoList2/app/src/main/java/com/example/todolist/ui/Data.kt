package com.example.todolist.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.todolist.R
import java.util.UUID

enum class ToDoIcons(val imageVector: ImageVector,@StringRes val contentDescription: Int) {
    Square(Icons.Default.CropSquare, R.string.square),
    Done(Icons.Default.Done, R.string.done),
    Event(Icons.Default.Event, R.string.event),
    Privacy(Icons.Default.PrivacyTip, R.string.privacy),
    Trash(Icons.Default.RestoreFromTrash, R.string.trash)


}
data class Data (
    val task: String,
    val icons: ToDoIcons = ToDoIcons.Square,
    val id: UUID = UUID.randomUUID()
)