package org.tywrapstudios.krafter.extensions.funtility

import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.OverwriteType
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Guild
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.rest.builder.message.addFile
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kordex.core.extensions.event
import dev.kordex.core.utils.downloadToFile
import dev.kordex.core.utils.getJumpUrl
import org.tywrapstudios.krafter.checks.isBotModuleAdmin
import org.tywrapstudios.krafter.database.transactors.StarBoardTransactor
import org.tywrapstudios.krafter.funConfig
import org.tywrapstudios.krafter.getDataDirectory
import org.tywrapstudios.krafter.getOrCreateChannel
import org.tywrapstudios.krafter.i18n.Translations
import java.io.File
import kotlin.io.path.createDirectories

class StarBoardExtension : Extension() {
	override val name: String = "krafter.starboard"
	private var channel: TextChannel? = null
	val stars = StarBoardTransactor

	override suspend fun setup() {
		event<GuildCreateEvent> {
			action {
				channel = getOrCreateChannel(
					funConfig().functions.star_channel,
					"starboard",
					"Special messages starred by you!",
					getOverwrites(event.guild),
					event.guild
				)
			}
		}

		event<ReactionAddEvent> {
			action {
				val starEmoji = ReactionEmoji.Unicode("⭐")
				if (stars.starred(event.messageId)) return@action
				event.message.asMessage().reactions.forEach {
					if (it.emoji.name == starEmoji.name) {
						if (it.count >= funConfig().functions.min_stars) {
							val message = channel!!.createMessage {
								content = "${event.message.asMessage().content}\n\n" +
									"-# Original message: ${event.message.asMessage().getJumpUrl()}"
								event.message.asMessage().attachments.forEach { attachment ->
									val file = File(
										getDataDirectory().resolve("star_attachments").createDirectories().toFile(),
										attachment.filename
									)
									addFile(attachment.downloadToFile(file))
								}
							}
							stars.set(event.messageId, message.id)
						}
					}
				}
			}
		}

		ephemeralSlashCommand {
			name = Translations.Commands.starBoard
			description = Translations.Commands.StarBoard.description

			check { isBotModuleAdmin(funConfig().administrators) }

			action {
				val count = stars.removeAll()

				respond {
					content = "Removed $count messages from database."
				}
			}
		}
	}

	private fun getOverwrites(guild: Guild): MutableSet<Overwrite> {
		val overwrites = mutableSetOf<Overwrite>()

		overwrites.add(
			Overwrite(
				guild.id,
				OverwriteType.Role,
				Permissions {
					-Permission.SendMessages
				},
				Permissions {
					+Permission.SendMessages
				}
			)
		)

		return overwrites
	}
}
