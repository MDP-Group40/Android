package com.example.mdpandroid.ui

import com.example.mdpandroid.domain.BluetoothMessage
import com.example.mdpandroid.domain.InfoMessage
import com.example.mdpandroid.domain.MovementMessage
import com.example.mdpandroid.domain.StartMessage
import com.example.mdpandroid.domain.TextMessage
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

object SerializationConfig {
    private val bluetoothMessageModule = SerializersModule {
        polymorphic(BluetoothMessage::class) {
            subclass(TextMessage::class)
            subclass(InfoMessage::class)
            subclass(StartMessage::class)
            subclass(MovementMessage::class)
        }
    }

    // Custom serializer to handle "category" key for both serialization and deserialization
    val json = Json {
        serializersModule = bluetoothMessageModule
        classDiscriminator = "type"  // Use "category" to differentiate between message types
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
}
