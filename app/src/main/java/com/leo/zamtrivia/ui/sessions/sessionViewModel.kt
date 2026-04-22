package com.leo.zamtrivia.ui.session

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SessionViewModel : ViewModel() {

    private val _playerName = MutableLiveData<String>()
    val playerName: LiveData<String> = _playerName

    private val _selectedCategory = MutableLiveData<String?>(null)
    val selectedCategory: LiveData<String?> = _selectedCategory

    private val _isRandomMode = MutableLiveData(false)
    val isRandomMode: LiveData<Boolean> = _isRandomMode

    fun setPlayerName(name: String) {
        _playerName.value = name.trim()
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
        _isRandomMode.value = false
    }

    fun setRandomMode() {
        _selectedCategory.value = null
        _isRandomMode.value = true
    }

    fun clearMode() {
        _selectedCategory.value = null
        _isRandomMode.value = false
    }
}