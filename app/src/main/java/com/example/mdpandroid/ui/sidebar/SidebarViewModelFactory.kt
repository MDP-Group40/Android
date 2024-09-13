package com.example.mdpandroid.ui.sidebar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mdpandroid.ui.shared.SharedViewModel

class SidebarViewModelFactory(
    private val sharedViewModel: SharedViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SidebarViewModel::class.java)) {
            return SidebarViewModel(sharedViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
