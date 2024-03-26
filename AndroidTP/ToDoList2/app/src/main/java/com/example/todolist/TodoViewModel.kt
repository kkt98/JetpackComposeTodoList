package com.example.todolist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.ui.Data

class TodoViewModel: ViewModel() {

    private val _todoItem = MutableLiveData<List<Data>>()
    val todoItem: LiveData<List<Data>> = _todoItem

    fun addItem(item: Data) {
        _todoItem.value = _todoItem.value!! + listOf(item)
    }

    fun removeItem(item: Data) {
        _todoItem.value = _todoItem.value!!.toMutableList().also {
            it.remove(item)
        }
    }
}