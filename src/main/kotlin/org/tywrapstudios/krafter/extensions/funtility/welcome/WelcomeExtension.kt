package org.tywrapstudios.krafter.extensions.funtility.welcome

import androidx.compose.ui.graphics.decodeToImageBitmap
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.event.guild.MemberJoinEvent
import dev.kord.rest.Image
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.event
import io.ktor.client.request.forms.*
import io.ktor.util.cio.*
import org.tywrapstudios.krafter.getOrCreateChannel
import org.tywrapstudios.krafter.utilityConfig

class WelcomeExtension : Extension() {
	override val name: String = "krafter.welcome"

	override suspend fun setup() {
		event<MemberJoinEvent> {
			action {
				val member = event.member
				val username = member.effectiveName
				val avatar = member.avatar?.getImage(
					size = Image.Size.Size4096
				) ?: member.defaultAvatar.getImage(
					size = Image.Size.Size4096
				)

				val welcomeImageFile = generateWelcomeImage(username, avatar.data.decodeToImageBitmap())

				val channel = getOrCreateChannel(
					utilityConfig().functions.welcome_channel,
					"welcome",
					"Welcome to the server!",
					null,
					event.guild.asGuild(),
				)
				channel.createMessage {
					val provider = ChannelProvider {
						welcomeImageFile.readChannel()
					}
					addFile("welcome.png", provider)
				}
			}
		}
	}
}
