@file:Suppress("LongMethod", "CyclomaticComplexMethod")

package org.tywrapstudios.krafter.extensions.minecraft

import dev.kord.common.Color
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.embed
import dev.kordex.core.DISCORD_BLURPLE
import dev.kordex.core.DISCORD_RED
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.EphemeralSlashCommand
import dev.kordex.core.commands.application.slash.ephemeralSubCommand
import dev.kordex.core.commands.converters.impl.boolean
import dev.kordex.core.commands.converters.impl.int
import dev.kordex.core.commands.converters.impl.member
import dev.kordex.core.commands.converters.impl.optionalString
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kordex.core.extensions.event
import dev.kordex.core.types.EphemeralInteractionContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import org.tywrapstudios.krafter.api.objects.McMessage
import org.tywrapstudios.krafter.checks.isBotModuleAdmin
import org.tywrapstudios.krafter.checks.isGlobalBotAdmin
import org.tywrapstudios.krafter.mainConfig
import org.tywrapstudios.krafter.extensions.data.KrafterMinecraftLinkData
import org.tywrapstudios.krafter.getOrCreateChannel
import org.tywrapstudios.krafter.i18n.Translations
import org.tywrapstudios.krafter.minecraftConfig
import org.tywrapstudios.krafter.platform.MC_CONNECTION
import org.tywrapstudios.krafter.setup
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.regex.Pattern

var watchChannel: TextChannel? = null

class MinecraftExtension : Extension() {
    override val name: String = "krafter.minecraft"
    val data: KrafterMinecraftLinkData = KrafterMinecraftLinkData()

    override suspend fun setup() {
        val cfg = minecraftConfig()

		if (cfg.connection.enabled) {
			event<GuildCreateEvent> {
				action {
					watchChannel = getOrCreateChannel(
						cfg.connection.channel,
						"mc-chat",
						"Discord to Chat watch channel for the Krafter software. " +
							"Send your messages here to have them sent to Minecraft chat!",
						mutableSetOf(),
						event.guild
					)
				}
			}
			event<MessageCreateEvent> {
				action {
					if (!minecraftConfig().connection.enabled) return@action
					if (event.message.channel.asChannel() == watchChannel) {
						event.message.author?.isBot?.let { if (!it) MC_CONNECTION.broadcast(McMessage(event.message)) }
					}
				}
			}
		}



        ephemeralSlashCommand {
            name = Translations.Commands.minecraft
            description = Translations.Commands.Minecraft.description

            ephemeralSubCommand(::LinkCommandArguments) {
                name = Translations.Commands.Minecraft.link
                description = Translations.Commands.Minecraft.Link.description
                action {
                    if (!minecraftConfig().enabled) {
                        respond {
                            content = Translations.Responses.Minecraft.Link.Error.disabled.translate()
                        }
                        return@action
                    }

                    val uuid = arguments.uuid
                    val member = event.interaction.user.id

                    val link = data.setLinkStatus(member, KrafterMinecraftLinkData.LinkStatus(UUID.fromString(uuid)))

                    respond {
                        content = Translations.Responses.Minecraft.Link.success.withOrdinalPlaceholders(
                            link.code
                        ).translate()
                    }
                }
            }

            ephemeralSubCommand {
                name = Translations.Commands.Minecraft.unlink
                description = Translations.Commands.Minecraft.Unlink.description
                action {
                    if (!minecraftConfig().enabled) {
                        respond {
                            content = Translations.Responses.Minecraft.Unlink.Error.disabled.translate()
                        }
                        return@action
                    }

                    val member = event.interaction.user.id
                    val uuid = data.unlink(member)
                    if (uuid == null) {
                        respond {
                            content = Translations.Responses.Minecraft.Unlink.Error.notLinked.translate()
                        }
                    } else {
                        respond {
                            content = Translations.Responses.Minecraft.Unlink.success.withOrdinalPlaceholders(
                                uuid
                            ).translate()
                        }
                    }
                }
            }

            ephemeralSubCommand(::ForceLinkCommandArguments) {
                name = Translations.Commands.Minecraft.forceLink
                description = Translations.Commands.Minecraft.ForceLink.description
                check {
                    isGlobalBotAdmin()
                }
                action {
                    if (!minecraftConfig().enabled) {
                        respond {
                            content = Translations.Responses.Minecraft.ForceLink.Error.disabled.translate()
                        }
                        return@action
                    }

                    val member = arguments.member
                    val uuid = arguments.uuid

                    val currentLink = data.getLinkStatus(member.id)

                    if (currentLink == null) {
                        data.setLinkStatus(
                            member.id,
                            KrafterMinecraftLinkData.LinkStatus(UUID.fromString(uuid))
                        )
                    } else if (currentLink.uuid.toString() == uuid && currentLink.verified) {
                        respond {
                            content =
                                Translations.Responses.Minecraft.ForceLink.Error.alreadyLinked.withOrdinalPlaceholders(
                                    currentLink.uuid
                            ).translate()
                        }
                        return@action
                    } else if (currentLink.uuid.toString() != uuid && currentLink.verified) {
                        respond {
                            content = Translations.Responses.Minecraft.ForceLink.Error.alreadyLinkedDifferent
                                .withOrdinalPlaceholders(currentLink.uuid)
                                .translate()
                        }
                        return@action
                    } else if (!currentLink.verified) {
                        data.verify(member.id, currentLink.code)
                        respond {
                            content = Translations.Responses.Minecraft.ForceLink.success.withOrdinalPlaceholders(
                                member.mention,
                                currentLink.uuid
                            ).translate()
                        }
                    }
                }
            }
        }

		ephemeralSlashCommand {
			name = Translations.Commands.cmd
			description = Translations.Commands.Cmd.description

			ephemeralSubCommand {
				name = Translations.Commands.Cmd.list
				description = Translations.Commands.Cmd.List.description

				basicMsc("/list")
			}

			ephemeralSubCommand(::TellrawArguments) {
				name = Translations.Commands.Cmd.tellraw
				description = Translations.Commands.Cmd.Tellraw.description

				check { isBotModuleAdmin(minecraftConfig().administrators) }

				action {
					val text = arguments.text
					basicMsc("/tellraw @a $text")
				}
			}

			ephemeralSubCommand(::MclogsArguments) {
				name = Translations.Commands.Cmd.mclogs
				description = Translations.Commands.Cmd.Mclogs.description

				check { isBotModuleAdmin(minecraftConfig().administrators) }

				action {
					val log = arguments.log

					val resp: String = if (log != null) {
						MC_CONNECTION.command("/mclogs share $log")
					} else {
						MC_CONNECTION.command("/mclogs")
					}

					if (resp.startsWith("Your log")) {
						val url = resp.replace("Your log has been uploaded:", "").trim()
						val id = url.replace("https://mclo.gs/", "").trim()
						customMscEmbed(
							Translations.Responses.Cmd.Mclogs.success.withOrdinalPlaceholders(
								id,
								url
							).translate(),
							DISCORD_BLURPLE
						)
					} else {
						customMscEmbed(
							Translations.Responses.Cmd.Mclogs.error.withOrdinalPlaceholders(
								resp
							).translate(),
							DISCORD_RED
						)
					}
				}
			}

			ephemeralSubCommand(::MaintenanceArguments) {
				name = Translations.Commands.Cmd.maintenance
				description = Translations.Commands.Cmd.Maintenance.description

				check { isBotModuleAdmin(minecraftConfig().administrators) }

				action {
					val enable = arguments.enable

					basicMsc("/maintenance ${if (enable) "on" else "off"}")
				}
			}

			ephemeralSubCommand(::TpOfflineArguments) {
				name = Translations.Commands.Cmd.modTpOffline
				description = Translations.Commands.Cmd.ModTpOffline.description

				check { isBotModuleAdmin(minecraftConfig().administrators) }

				action {
					val playerName = arguments.playerName
					val position = arguments.position
					val x = arguments.x ?: "0"
					val y = arguments.y ?: "0"
					val z = arguments.z ?: "0"
					val command = if (position != null) {
						"/tp-offline $playerName $position"
					} else {
						"/tp-offline $playerName $x $y $z"
					}
					basicMsc(command)
				}
			}

			ephemeralSubCommand(::SingularPlayerArguments) {
				name = Translations.Commands.Cmd.modHeal
				description = Translations.Commands.Cmd.ModHeal.description

				check { isBotModuleAdmin(minecraftConfig().administrators) }

				action {
					val playerName = arguments.playerName

					basicMsc("/heal $playerName")
				}
			}

			ephemeralSubCommand(::DamageArguments) {
				name = Translations.Commands.Cmd.modDamage
				description = Translations.Commands.Cmd.ModDamage.description

				check { isBotModuleAdmin(minecraftConfig().administrators) }

				action {
					val playerName = arguments.playerName
					val amount = arguments.amount

					basicMsc("/damage $playerName $amount")
				}
			}

			ephemeralSubCommand(::WhiteListArguments) {
				name = Translations.Commands.Cmd.modWhitelist
				description = Translations.Commands.Cmd.ModWhitelist.description

				check { isBotModuleAdmin(minecraftConfig().administrators) }

				action {
					val enable = arguments.enable

					basicMsc("/whitelist ${if (enable == true) "on" else "off"}")
				}
			}

			ephemeralSubCommand(::SingularPlayerArguments) {
				name = Translations.Commands.Cmd.ModWhitelist.add
				description = Translations.Commands.Cmd.ModWhitelist.Add.description

				check { isBotModuleAdmin(minecraftConfig().administrators) }

				action {
					val playerName = arguments.playerName

					basicMsc("/whitelist add $playerName")
				}
			}

			ephemeralSubCommand(::SingularPlayerArguments) {
				name = Translations.Commands.Cmd.ModWhitelist.remove
				description = Translations.Commands.Cmd.ModWhitelist.Remove.description

				check { isBotModuleAdmin(minecraftConfig().administrators) }

				action {
					val playerName = arguments.playerName

					basicMsc("/whitelist remove $playerName")
				}
			}

			ephemeralSubCommand {
				name = Translations.Commands.Cmd.modListWhitelist
				description = Translations.Commands.Cmd.ModListWhitelist.description

				check { isBotModuleAdmin(minecraftConfig().administrators) }

				basicMsc("/whitelist list")
			}

			ephemeralSubCommand(::SingularPlayerArguments) {
				name = Translations.Commands.Cmd.viewBalance
				description = Translations.Commands.Cmd.ViewBalance.description

				action {
					val playerName = arguments.playerName

					basicMsc("/nm view $playerName")
				}
			}

			ephemeralSubCommand(::ModArguments) {
				name = Translations.Commands.Cmd.modBan
				description = Translations.Commands.Cmd.ModBan.description

				check { isBotModuleAdmin(minecraftConfig().administrators) }

				action {
					val player = arguments.player
					val reason = arguments.reason ?: ""

					basicMsc("/ban $player $reason")
				}
			}

			ephemeralSubCommand(::SingularPlayerArguments) {
				name = Translations.Commands.Cmd.modUnban
				description = Translations.Commands.Cmd.ModUnban.description

				check { isBotModuleAdmin(minecraftConfig().administrators) }

				action {
					val player = arguments.playerName

					basicMsc("/unban $player")
				}
			}

			ephemeralSubCommand(::ModArguments) {
				name = Translations.Commands.Cmd.modKick
				description = Translations.Commands.Cmd.ModKick.description

				check { isBotModuleAdmin(minecraftConfig().administrators) }

				action {
					val player = arguments.player
					val reason = arguments.reason ?: ""

					basicMsc("/kick $player $reason")
				}
			}

			ephemeralSubCommand(::ModArgumentsWithDuration) {
				name = Translations.Commands.Cmd.tempban
				description = Translations.Commands.Cmd.Tempban.description

				check { isBotModuleAdmin(minecraftConfig().administrators) }

				action {
					val player = arguments.player
					val reason = arguments.reason ?: ""
					val duration = arguments.duration

					basicMsc("/tempban $player $duration $reason")
				}
			}

			ephemeralSubCommand(::ModArguments) {
				name = Translations.Commands.Cmd.modMute
				description = Translations.Commands.Cmd.ModMute.description

				check { isBotModuleAdmin(minecraftConfig().administrators) }

				action {
					val player = arguments.player
					val reason = arguments.reason ?: ""

					basicMsc("/mute $player $reason")
				}
			}

			ephemeralSubCommand(::ModArgumentsWithDuration) {
				name = Translations.Commands.Cmd.modTempMute
				description = Translations.Commands.Cmd.ModTempMute.description

				check { isBotModuleAdmin(minecraftConfig().administrators) }

				action {
					val player = arguments.player
					val reason = arguments.reason ?: ""
					val duration = arguments.duration

					basicMsc("/tempmute $player $duration $reason")
				}
			}

			ephemeralSubCommand(::SingularPlayerArguments) {
				name = Translations.Commands.Cmd.modUnmute
				description = Translations.Commands.Cmd.ModUnmute.description

				check { isBotModuleAdmin(minecraftConfig().administrators) }

				action {
					val player = arguments.playerName

					basicMsc("/unmutes $player")
				}
			}

			ephemeralSubCommand(::SingularPlayerArguments) {
				name = Translations.Commands.Cmd.modPardon
				description = Translations.Commands.Cmd.ModPardon.description

				check { isBotModuleAdmin(minecraftConfig().administrators) }

				action {
					val player = arguments.playerName

					basicMsc("/pardon $player")
				}
			}

			ephemeralSubCommand(::SingularPlayerArguments) {
				name = Translations.Commands.Cmd.modClear
				description = Translations.Commands.Cmd.ModClear.description

				check { isBotModuleAdmin(minecraftConfig().administrators) }

				action {
					val player = arguments.playerName

					basicMsc("/clear $player")
				}
			}

			ephemeralSubCommand(::SingularPlayerArguments) {
				name = Translations.Commands.Cmd.modRestore
				description = Translations.Commands.Cmd.ModRestore.description

				check { isBotModuleAdmin(minecraftConfig().administrators) }

				action {
					val player = arguments.playerName

					basicMsc("/yigd restore $player")
				}
			}
		}
    }

	fun EphemeralSlashCommand<*, *>.basicMsc(command: String) {
		action {
			basicMsc(command)
		}
	}

	suspend fun EphemeralInteractionContext.basicMsc(command: String) {
		respond {
			if (minecraftConfig().enabled) {
				val response = try {
					MC_CONNECTION.command(command.trim())
				} catch (e: Exception) {
					e.printStackTrace()
					e.message
				}

				embed {
					color = DISCORD_BLURPLE
					title = Translations.GeneralResponses.Cmd.Embed.title.translate()
					description = Translations.GeneralResponses.Cmd.response.withOrdinalPlaceholders(
						response
					).translate()
				}
			} else {
				embed {
					color = DISCORD_RED
					title = Translations.GeneralResponses.Cmd.Embed.title.translate()
					description = Translations.GeneralResponses.Cmd.Error.disabled.translate()
				}
			}
		}
	}

	suspend fun EphemeralInteractionContext.customMscEmbed(content: String, embedColor: Color) {
		respond {
			if (minecraftConfig().enabled) {
				embed {
					color = embedColor
					title = Translations.GeneralResponses.Cmd.Embed.title.translate()
					description = content
				}
			} else {
				embed {
					color = DISCORD_RED
					title = Translations.GeneralResponses.Cmd.Embed.title.translate()
					description = Translations.GeneralResponses.Cmd.Error.disabled.translate()
				}
			}
		}
	}

    class LinkCommandArguments : Arguments() {
        val uuid by string {
            name = Translations.Args.Minecraft.Link.uuid
            description = Translations.Args.Minecraft.Link.Uuid.description
            validate {
                failIfNot(Translations.Responses.Minecraft.Link.Error.invalidUuid) {
                    Pattern
                        .compile(
                            "([0-9a-f]{8})(?:-|)([0-9a-f]{4})(?:-|)(4[0-9a-f]{" +
                                    "3})(?:-|)([89ab][0-9a-f]{3})(?:-|)([0-9a-f]{12})"
                        )
                        .matcher(value)
                        .matches()
                }
            }
        }
    }

    class ForceLinkCommandArguments : Arguments() {
        val member by member {
            name = Translations.Args.Minecraft.ForceLink.member
            description = Translations.Args.Minecraft.ForceLink.Member.description
        }

        val uuid by string {
            name = Translations.Args.Minecraft.ForceLink.uuid
            description = Translations.Args.Minecraft.ForceLink.Uuid.description
            validate {
                failIfNot(Translations.Responses.Minecraft.ForceLink.Error.invalidUuid) {
                    Pattern
                        .compile(
                            "([0-9a-f]{8})(?:-|)([0-9a-f]{4})(?:-|)(4[0-9a-f]{" +
                                    "3})(?:-|)([89ab][0-9a-f]{3})(?:-|)([0-9a-f]{12})"
                        )
                        .matcher(value)
                        .matches()
                }
            }
        }
    }

	class TellrawArguments : Arguments() {
		val text by string {
			name = Translations.Args.Cmd.Tellraw.message
			description = Translations.Args.Cmd.Tellraw.Message.description
		}
	}

	class MclogsArguments : Arguments() {
		val log by optionalString {
			name = Translations.Args.Cmd.Mclogs.log
			description = Translations.Args.Cmd.Mclogs.Log.description
		}
	}

	class MaintenanceArguments : Arguments() {
		val enable by boolean {
			name = Translations.Args.Cmd.Maintenance.enable
			description = Translations.Args.Cmd.Maintenance.Enable.description
		}
	}

	class TpOfflineArguments : Arguments() {
		val playerName by string {
			name = Translations.GeneralArgs.Cmd.ModCommands.player
			description = Translations.GeneralArgs.Cmd.ModCommands.Player.description
		}

		val position by optionalString {
			name = Translations.Args.Cmd.ModTpOffline.location
			description = Translations.Args.Cmd.ModTpOffline.Location.description
		}

		val x by optionalString {
			name = Translations.Args.Cmd.ModTpOffline.x
			description = Translations.Args.Cmd.ModTpOffline.X.description
		}

		val y by optionalString {
			name = Translations.Args.Cmd.ModTpOffline.y
			description = Translations.Args.Cmd.ModTpOffline.Y.description
		}

		val z by optionalString {
			name = Translations.Args.Cmd.ModTpOffline.z
			description = Translations.Args.Cmd.ModTpOffline.Z.description
		}
	}

	class DamageArguments : Arguments() {
		val playerName by string {
			name = Translations.GeneralArgs.Cmd.ModCommands.player
			description = Translations.GeneralArgs.Cmd.ModCommands.Player.description
		}

		val amount by int {
			name = Translations.Args.Cmd.ModDamage.amount
			description = Translations.Args.Cmd.ModDamage.Amount.description
		}
	}

	class WhiteListArguments : Arguments() {
		val enable by boolean {
			name = Translations.Args.Cmd.ModWhitelist.enable
			description = Translations.Args.Cmd.ModWhitelist.Enable.description
		}
	}

	class SingularPlayerArguments : Arguments() {
		val playerName by string {
			name = Translations.GeneralArgs.Cmd.ModCommands.player
			description = Translations.GeneralArgs.Cmd.ModCommands.Player.description
		}
	}

	class ModArguments : Arguments() {
		val player by string {
			name = Translations.GeneralArgs.Cmd.ModCommands.player
			description = Translations.GeneralArgs.Cmd.ModCommands.Player.description
		}

		val reason by optionalString {
			name = Translations.GeneralArgs.Cmd.ModCommands.reason
			description = Translations.GeneralArgs.Cmd.ModCommands.Reason.description
		}
	}

	class ModArgumentsWithDuration : Arguments() {
		val player by string {
			name = Translations.GeneralArgs.Cmd.ModCommands.player
			description = Translations.GeneralArgs.Cmd.ModCommands.Player.description
		}

		val duration by string {
			name = Translations.GeneralArgs.Cmd.ModCommands.duration
			description = Translations.GeneralArgs.Cmd.ModCommands.Duration.description
		}

		val reason by optionalString {
			name = Translations.GeneralArgs.Cmd.ModCommands.reason
			description = Translations.GeneralArgs.Cmd.ModCommands.Reason.description
		}
	}
}

/**
 * Verifies a Minecraft link for the given UUID and code.
 *
 * SHOULD ONLY BE USED IF YOU'RE CERTAIN THAT THE MEMBER IS CURRENTLY BEING LINKED
 * TO A UUID IN THE CURRENT RUNTIME.
 *
 * - If the verification code does not match, the method will return -1.
 * - If the verification is successful, it will return 1.
 * - If no link status exists for the member, or a different error occurs it will return 0.
 */
@OptIn(DelicateCoroutinesApi::class)
fun verify(uuid: UUID, code: Int): CompletableFuture<Int> = GlobalScope.future {
    setup().extensions["krafter.minecraft"]?.let { ext ->
        if (ext is MinecraftExtension) {
            return@future ext.data.verify(uuid, code.toUInt())
        }
    }
    return@future 0
}
