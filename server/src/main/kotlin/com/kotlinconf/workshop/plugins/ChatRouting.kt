package com.kotlinconf.workshop.plugins

import com.kotlinconf.workshop.ChatMessage
import com.kotlinconf.workshop.util.log
import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSocketServerSession
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

fun Application.configureChatRouting(chat: Chat = Chat()) {
    routing {
        webSocket("/chat") {
            handleSocket(this, chat)
        }
    }
}

// Task: Implement chat server using SharedFlow
suspend fun handleSocket(
    socket: WebSocketServerSession,
    chat: Chat,
) {
    coroutineScope {
        launch {
            // use socket.receiveDeserialized<ChatMessage>() to receive a message from the WebSocket
            while (true) {
                val chatMessage = socket.receiveDeserialized<ChatMessage>()
                log("Got message $chatMessage")
                chat.broadcastMessage(chatMessage)
            }
        }
        launch {
            // use session.sendSerialized(message) to send a message to the WebSocket
            chat.messageFlow.collect { chatMessage ->
                log("Got message $chatMessage")
                socket.sendSerialized(chatMessage)
            }
        }
    }
}

class Chat {
    private val _messageFlow: MutableSharedFlow<ChatMessage> = MutableSharedFlow(replay = 40)
    val messageFlow: SharedFlow<ChatMessage> get() = _messageFlow
    suspend fun broadcastMessage(message: ChatMessage) {
        _messageFlow.emit(message)
    }
}
