package com.example.mdpandroid.domain

import kotlinx.serialization.Serializable

@Serializable
data class BluetoothMessage(
    val message: String,
    val senderName: String,
    val isFromLocalUser: Boolean
)