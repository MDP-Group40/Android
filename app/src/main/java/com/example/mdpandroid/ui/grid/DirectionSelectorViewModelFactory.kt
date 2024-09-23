package com.example.mdpandroid.ui.grid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mdpandroid.ui.shared.SharedViewModel
import com.example.mdpandroid.ui.sidebar.SidebarViewModel

// Factory for DirectionSelectorViewModel
class DirectionSelectorViewModelFactory(
    private val sidebarViewModel: SidebarViewModel, // Dependency injection of SidebarViewModel
    private val sharedViewModel: SharedViewModel
) : ViewModelProvider.Factory {

    // Create a new instance of the ViewModel, passing in the dependencies
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DirectionSelectorViewModel::class.java)) {
            // Return the DirectionSelectorViewModel with the provided sidebarViewModel
            return DirectionSelectorViewModel(sidebarViewModel, sharedViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
