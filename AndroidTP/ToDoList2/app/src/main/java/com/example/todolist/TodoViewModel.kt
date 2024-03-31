package com.example.todolist

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.ui.Data

class TodoViewModel: ViewModel() {

    private val _todoItem = mutableStateListOf<Data>()


    fun addItem(item: Data) {
        _todoItem.add(item)
    }

    fun removeItem(item: Data) {
        _todoItem.remove(item)
    }
}