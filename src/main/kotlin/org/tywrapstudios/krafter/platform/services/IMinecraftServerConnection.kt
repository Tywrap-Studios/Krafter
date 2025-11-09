package org.tywrapstudios.krafter.platform.services

import org.tywrapstudios.krafter.api.objects.McMessage

interface IMinecraftServerConnection {

	/**
	 * Broadcast a message to the connected Minecraft server chat.
	 *
	 * @param message The message behaviour to broadcast,
	 * it contains the content and other metadata such as authors, timestamps, etc. Whatever may come to use.
	 */
	fun broadcast(message: McMessage)

	/**
	 * Broadcast a text message directly to the connected Minecraft server chat.
	 *
	 * Don't do anything special with the message, just send it as plain text.
	 *
	 * @param message The plain text message to broadcast.
	 */
	fun broadcastPlain(message: String)

	/**
	 * Send a command to the connected Minecraft server and return the response.
	 * @param command The command to send.
	 * @return The response from the server.
	 */
	fun command(command: String): String
}
