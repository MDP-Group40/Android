package com.example.mdpandroid.ui.buttons

import androidx.lifecycle.ViewModel

abstract class ControlViewModel : ViewModel() {

    // Abstract methods that child classes must implement
    abstract fun handleButtonUp()
    abstract fun handleButtonDown()
    abstract fun handleButtonLeft()
    abstract fun handleButtonRight()
    abstract fun handleButtonA()
    abstract fun handleButtonB()

    // Abstract method to stop movement
    abstract fun handleStopMovement()
}
