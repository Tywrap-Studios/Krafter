package org.tywrapstudios.krafter.api.objects

import dev.kord.core.entity.Message
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture

/**
 * Frame type that contains a [Message] with certain "helper"
 * functions that allow for getting basic values in a classpath that does not contain
 * Kord(Ex).
 */
class McMessage(val message: Message) {

	/**
	 * Returns the content of the [message].
	 */
	fun getContent(): String {
		return message.content
	}

	/**
	 * Gets an [McAuthor] with an associated [McPlayer] object in case a valid link exists in
	 * the database. Otherwise, the `player` value is set to `null`.
	 */
	@OptIn(DelicateCoroutinesApi::class)
	fun getAuthor(): CompletableFuture<McAuthor> = GlobalScope.future {
		if (message.author?.id != null) {
			return@future McAuthor(message.getAuthorAsMember(), getMcPlayer(message.author!!.id))
		} else {
			return@future McAuthor(message.getAuthorAsMember(), null)
		}
	}
}
