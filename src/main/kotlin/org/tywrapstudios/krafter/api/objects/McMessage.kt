package org.tywrapstudios.krafter.api.objects

import dev.kord.core.entity.Message
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture

class McMessage(val message: Message) {

    fun getContent(): String {
        return message.content
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getAuthor(): CompletableFuture<McAuthor> = GlobalScope.future {
        if (message.author?.id != null) {
            return@future McAuthor(message.getAuthorAsMember(), getMcPlayer(message.author!!.id))
        } else {
            return@future McAuthor(message.getAuthorAsMember(), null)
        }
    }
}
