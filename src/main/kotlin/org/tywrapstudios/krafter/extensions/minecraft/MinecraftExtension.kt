@file:Suppress("LongMethod", "CyclomaticComplexMethod")

package org.tywrapstudios.krafter.extensions.minecraft

import dev.kord.common.Color
import dev.kord.common.entity.ButtonStyle
import dev.kord.core.behavior.channel.edit
import dev.kord.core.behavior.createVoiceChannel
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.builder.components.emoji
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.addFile
import dev.kord.rest.builder.message.create.FollowupMessageCreateBuilder
import dev.kord.rest.builder.message.embed
import dev.kordex.core.DISCORD_BLURPLE
import dev.kordex.core.DISCORD_GREEN
import dev.kordex.core.DISCORD_RED
import dev.kordex.core.checks.anyGuild
import dev.kordex.core.checks.inChannel
import dev.kordex.core.checks.isNotBot
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.EphemeralSlashCommand
import dev.kordex.core.commands.application.slash.ephemeralSubCommand
import dev.kordex.core.commands.application.slash.publicSubCommand
import dev.kordex.core.commands.converters.impl.*
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kordex.core.extensions.event
import dev.kordex.core.types.EphemeralInteractionContext
import dev.kordex.core.utils.FilterStrategy
import dev.kordex.core.utils.scheduling.Task
import dev.kordex.core.utils.suggestStringMap
import kotlinx.serialization.json.Json
import org.tywrapstudios.krafter.SCHEDULER
import org.tywrapstudios.krafter.api.mcsrvstatus.AddOnInfo
import org.tywrapstudios.krafter.api.mcsrvstatus.OfflineResponse
import org.tywrapstudios.krafter.api.mcsrvstatus.OnlineResponse
import org.tywrapstudios.krafter.api.mcsrvstatus.asResponse
import org.tywrapstudios.krafter.api.mcsrvstatus.getImageAddress
import org.tywrapstudios.krafter.api.objects.McMessage
import org.tywrapstudios.krafter.api.objects.McPlayer
import org.tywrapstudios.krafter.api.objects.getMcPlayer
import org.tywrapstudios.krafter.checks.isBotModuleAdmin
import org.tywrapstudios.krafter.checks.isGlobalBotAdmin
import org.tywrapstudios.krafter.database.entities.MinecraftServer
import org.tywrapstudios.krafter.database.transactors.MinecraftLinkTransactor
import org.tywrapstudios.krafter.database.transactors.MinecraftServerTransactor
import org.tywrapstudios.krafter.getDataDirectory
import org.tywrapstudios.krafter.getOrCreateChannel
import org.tywrapstudios.krafter.i18n.Translations
import org.tywrapstudios.krafter.minecraftConfig
import org.tywrapstudios.krafter.platform.MC_CONNECTION
import java.io.File
import java.util.regex.Pattern
import kotlin.io.path.createDirectories
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class MinecraftExtension : Extension() {
	override val name: String = "krafter.minecraft"
	val links: MinecraftLinkTransactor = MinecraftLinkTransactor
	val servers = MinecraftServerTransactor
	private val cfg get() = minecraftConfig()
	var watchChannel: TextChannel? = null
	var watchTask: Task? = null

	@OptIn(ExperimentalUuidApi::class)
	override suspend fun setup() {
		watchTask = SCHEDULER.schedule(
			cfg.status.polling_seconds.seconds,
			name = "Status Refresher Task",
			repeat = true
		) {
			val list = servers.getAll()
			for (server in list) {
				val channel1 = kord.getChannelOf<VoiceChannel>(server.mainChannel)
				val channel2 = if (server.secondaryChannel != null) kord.getChannelOf<VoiceChannel>(server.secondaryChannel) else null
				when (val response = server.asResponse()) {
					is OnlineResponse -> {
						if (channel2 == null) {
							channel1?.edit {
								name = "${server.name}: ${response.players.online}/${response.players.max}"
							}
						} else {
							channel1?.edit {
								name = "${server.name}: Online"
							}
							channel2.edit {
								name = "${response.players.online} out of ${response.players.max} players online"
							}
						}
					}

					is OfflineResponse -> {
						if (channel2 == null) {
							channel1?.edit {
								name = "${server.name}: Offline"
							}
						} else {
							channel1?.edit {
								name = "${server.name}: Offline"
							}
							channel2.edit {
								name = "No players"
							}
						}
					}

					else -> {
						if (channel2 == null) {
							channel1?.edit {
								name = "${server.name}: Unavailable"
							}
						} else {
							channel1?.edit {
								name = "${server.name}: Unavailable"
							}
							channel2.edit {
								name = "Unavailable"
							}
						}
					}
				}
			}
		}

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
				check { isNotBot() }
				check { failIf(watchChannel == null) }
				check { inChannel(watchChannel!!.id) }

				action {
					if (!minecraftConfig().connection.enabled) return@action
					if (event.message.channel.asChannel() == watchChannel) {
						MC_CONNECTION.broadcast(McMessage(event.message))
					}
				}
			}
		}

		ephemeralSlashCommand {
			name = Translations.Commands.minecraft
			description = Translations.Commands.Minecraft.description
			if (cfg.linking) {
				ephemeralSubCommand(::LinkCommandArguments) {
					name = Translations.Commands.Minecraft.link
					description = Translations.Commands.Minecraft.Link.description
					action {
						if (!minecraftConfig().enabled) {
							respond {
								content =
									Translations.Responses.Minecraft.Link.Error.disabled.withLocale(getLocale())
										.withLocale(getLocale()).translate()
							}
							return@action
						}

						val uuid = arguments.uuid
						val member = event.interaction.user.id

						val link =
							links.setLinkStatus(
								member,
								MinecraftLinkTransactor.LinkStatus(member, Uuid.parse(uuid).toJavaUuid())
							)

						respond {
							content = Translations.Responses.Minecraft.Link.success.withOrdinalPlaceholders(
								link.code
							).withLocale(getLocale()).translate()
						}
					}
				}

				ephemeralSubCommand {
					name = Translations.Commands.Minecraft.unlink
					description = Translations.Commands.Minecraft.Unlink.description
					action {
						if (!minecraftConfig().enabled) {
							respond {
								content = Translations.Responses.Minecraft.Unlink.Error.disabled.withLocale(getLocale())
									.translate()
							}
							return@action
						}

						val member = event.interaction.user.id
						val uuid = links.unlink(member)
						if (uuid == null) {
							respond {
								content =
									Translations.Responses.Minecraft.Unlink.Error.notLinked.withLocale(getLocale())
										.translate()
							}
						} else {
							respond {
								content = Translations.Responses.Minecraft.Unlink.success.withOrdinalPlaceholders(
									uuid
								).withLocale(getLocale()).translate()
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
								content =
									Translations.Responses.Minecraft.ForceLink.Error.disabled.withLocale(getLocale())
										.translate()
							}
							return@action
						}

						val member = arguments.member
						val uuid = arguments.uuid

						val currentLink = links.getLinkStatus(member.id)

						if (currentLink == null) {
							links.setLinkStatus(
								member.id,
								MinecraftLinkTransactor.LinkStatus(member.id, Uuid.parse(uuid).toJavaUuid())
							)
						} else if (currentLink.uuid.toString() == uuid && currentLink.verified) {
							respond {
								content =
									Translations.Responses.Minecraft.ForceLink.Error.alreadyLinked.withOrdinalPlaceholders(
										currentLink.uuid
									).withLocale(getLocale()).translate()
							}
							return@action
						} else if (currentLink.uuid.toString() != uuid && currentLink.verified) {
							respond {
								content = Translations.Responses.Minecraft.ForceLink.Error.alreadyLinkedDifferent
									.withOrdinalPlaceholders(currentLink.uuid)
									.withLocale(getLocale()).translate()
							}
							return@action
						} else if (!currentLink.verified) {
							links.verify(member.id, currentLink.code)
							respond {
								content = Translations.Responses.Minecraft.ForceLink.success.withOrdinalPlaceholders(
									member.mention,
									currentLink.uuid
								).withLocale(getLocale()).translate()
							}
						}
					}
				}
			}

			if (cfg.profile_utils) {
				publicSubCommand(::LookupCommandArguments) {
					name = Translations.Commands.Minecraft.lookup
					description = Translations.Commands.Minecraft.Lookup.description
					action {
						val mcLink = links.getLinkStatus(arguments.member.id)
						val player = mcLink?.getMcPlayer()
						if (mcLink != null && player != null) {
							respond {
								embed {
									title = "Minecraft profile for ${arguments.member.effectiveName}"
									field {
										name = "Username and UUID"
										value = """
										```
										${player.name}
										```
										```
										${player.id}
										```
									""".trimIndent()
									}
									field {
										name = "Link status"
										value = "Verified: ${if (mcLink.verified) "✅" else "❌"}"
									}
									thumbnail {
										url = "https://mc-heads.net/avatar/${mcLink.uuid}/90"
									}
									footer {
										text = player.name
										icon = "https://mc-heads.net/avatar/${mcLink.uuid}/90"
									}
								}
								actionRow {
									interactionButton(ButtonStyle.Primary, "minecraft:force-link") {
										label = "Force link"
										emoji(ReactionEmoji.Unicode("🔗"))
									}
								}
							}
						} else {
							respond {
								embed {
									title = "Minecraft profile for ${arguments.member.effectiveName}"
									field {
										name = "Could not present profile:"
										value = if (mcLink == null)
											"Our database does not contain this member."
										else if (player == null) "Our database contains a linked UUID, but this player profile " +
											"does not actually exist or could not be found due to other reasons."
										else "Something unexpected happened, please contact a staff member."
									}
								}
							}
						}
					}
				}

				publicSubCommand(::SearchUuidArguments) {
					name = Translations.Commands.Minecraft.searchUuid
					description = Translations.Commands.Minecraft.SearchUuid.description

					action {
						val player = getMcPlayer(Uuid.parse(arguments.uuid).toJavaUuid())
						respond {
							mcPlayerProfileEmbed(arguments.uuid, player)
						}
					}
				}

				publicSubCommand(::SearchUsernameArguments) {
					name = Translations.Commands.Minecraft.searchUsername
					description = Translations.Commands.Minecraft.SearchUsername.description

					action {
						val player = getMcPlayer(arguments.username)
						respond {
							mcPlayerProfileEmbed(arguments.username, player)
						}
					}
				}
			}
		}

		if (cfg.connection.console) {
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
								).withLocale(getLocale()).translate(),
								DISCORD_BLURPLE
							)
						} else {
							customMscEmbed(
								Translations.Responses.Cmd.Mclogs.error.withOrdinalPlaceholders(
									resp
								).withLocale(getLocale()).translate(),
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

						basicMsc("/whitelist ${if (enable) "on" else "off"}")
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

		if (cfg.status.enabled) {
			ephemeralSlashCommand {
				name = Translations.Commands.minecraftServer
				description = Translations.Commands.MinecraftServer.description

				ephemeralSubCommand(::TrackArguments) {
					name = Translations.Commands.MinecraftServer.track
					description = Translations.Commands.MinecraftServer.Track.description

					check { anyGuild() }

					action {
						val channel1 = guild!!.createVoiceChannel("${arguments.name}: ?/?")
						val channel2 =
							if (arguments.twoChannels) guild!!.createVoiceChannel("${arguments.name}: ?/?") else null

						servers.set(
							MinecraftServer(
								arguments.address,
								arguments.bedrock,
								arguments.name,
								guild!!.id,
								channel1.id,
								channel2?.id
							)
						)

						respond {
							content = "Added ${arguments.name} to the tracking list"
						}
					}
				}

				ephemeralSubCommand(::RemoveTrackArguments) {
					name = Translations.Commands.MinecraftServer.removeTrack
					description = Translations.Commands.MinecraftServer.RemoveTrack.description

					action {
						val removed = servers.remove(arguments.server)
						respond {
							content =
								if (removed != null) "Stopped tracking ${removed.name}" else "The database did not return the server, but it may still be removed"
						}
					}
				}

				ephemeralSubCommand(::StatusArguments) {
					name = Translations.Commands.MinecraftServer.status
					description = Translations.Commands.MinecraftServer.Status.description

					action {
						val address = arguments.address
						val bedrock = arguments.bedrock
						val server = org.tywrapstudios.krafter.api.mcsrvstatus.MinecraftServer(
							address,
							bedrock
						)

						val response = try {
							server.asResponse()
						} catch (e: Exception) {
							e.printStackTrace()
							null
						}

						respond {
							when (response) {
								is OnlineResponse -> {
									embed {
										color = DISCORD_GREEN
										title = "`$address` is online"
										field {
											name = "Players"
											value = "${response.players.online}/${response.players.max}"
											inline = true
										}
										field {
											name = "Version info"
											value = response.protocol.toString()
											if (response.software != null) value += " - ${response.software}"
											inline = true
										}
										field {
											name = "MOTD"
											value = """
															RAW:
															```
															${response.motd.raw}
															```
															CLEAN:
															```
															${response.motd.clean}
															```
															HTML:
															```html
															${response.motd.html}
															```
														""".trimIndent()
										}
										footer {
											text = "Using the https://mcsrvstat.us API"
										}
										thumbnail {
											url = server.getImageAddress()
										}
									}
									actionRow {
										interactionButton(ButtonStyle.Primary, "srvstatus:get_addons:$address:$bedrock") {
											label = "View mods or plugins"
											disabled = response.getAllAddOns().isEmpty()
										}
									}
								}

								is OfflineResponse -> {
									embed {
										color = DISCORD_RED
										title = "`$address` is offline"
										footer {
											text = "Using the https://mcsrvstat.us API"
										}
										thumbnail {
											url = server.getImageAddress()
										}
									}
								}

								else -> {
									embed {
										color = DISCORD_BLURPLE
										title = "Something went wrong"
										description = "The server could not be found, or a different error occurred."
										footer {
											text = "Using the https://mcsrvstat.us API"
										}
										thumbnail {
											url = server.getImageAddress()
										}
									}
								}
							}
						}
					}
				}
			}
		}

		event<ButtonInteractionCreateEvent> {
			check { failIfNot(event.interaction.componentId.startsWith("srvstatus:get_addons:")) }

			action {
				val address = event.interaction.componentId.split(":")[2]
				val bedrock = event.interaction.componentId.split(":")[3].toBoolean()
				val response = org.tywrapstudios.krafter.api.mcsrvstatus.MinecraftServer(address, bedrock).asResponse()

				event.interaction.respondEphemeral {
					when (response) {
						is OnlineResponse -> {
							val file = File(
								getDataDirectory().resolve("addon_attachments").createDirectories().toFile(),
								"addons-$address.json"
							)
							val json = Json {
								prettyPrint = true
							}
							val element = json.encodeToString<List<AddOnInfo>>(response.getAllAddOns())
							file.writeText(element)
							addFile(file.toPath())
						}

						is OfflineResponse -> {
							content = "The server seems to be offline while fetching these files."
						}

						else -> {
							content = "Something went wrong while fetching the server."
						}
					}
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
			if (minecraftConfig().connection.console) {
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

	@OptIn(ExperimentalUuidApi::class)
	suspend fun FollowupMessageCreateBuilder.mcPlayerProfileEmbed(prompt: String, player: McPlayer?) {
		val mcLink = try {
			MinecraftLinkTransactor.getLinkStatus(Uuid.parse(player?.id ?: "").toJavaUuid())
		} catch (_: Exception) {
			null
		}

		if (player == null) {
			embed {
				title = "Minecraft profile for $prompt"
				field {
					name = "Could not present profile:"
					value = "This player profile does not actually exist or could not be found due to other reasons."
				}
			}
			return
		}
		embed {
			title = "Minecraft profile for $prompt"
			field {
				name = "Username and UUID"
				value = """
										```
										${player.name}
										```
										```
										${player.id}
										```
									""".trimIndent()
			}
			field {
				name = "Link status"
				value = if (mcLink == null) {
					"This UUID is currently not being linked."
				} else {
					"""
					User: <@${mcLink.member}>
					Verified: ${if (mcLink.verified) "✅" else "❌"}
					""".trimIndent()
				}
			}
			thumbnail {
				url = "https://mc-heads.net/body/${player.id}/600/left"
			}
			footer {
				text = player.name
				icon = "https://mc-heads.net/avatar/${player.id}/600"
			}
		}
		if (mcLink != null) {
			actionRow {
				interactionButton(ButtonStyle.Primary, "minecraft:force-link") {
					label = "Force link"
					emoji(ReactionEmoji.Unicode("🔗"))
				}
			}
		}
	}

	class TrackArguments : Arguments() {
		val name by string {
			name = Translations.Args.MinecraftServer.Track.name
			description = Translations.Args.MinecraftServer.Track.Name.description
		}
		val address by string {
			name = Translations.GeneralArgs.MinecraftServer.address
			description = Translations.GeneralArgs.MinecraftServer.Address.description
		}
		val bedrock by defaultingBoolean {
			name = Translations.GeneralArgs.MinecraftServer.bedrock
			description = Translations.GeneralArgs.MinecraftServer.Bedrock.description
			defaultValue = false
		}
		val twoChannels by defaultingBoolean {
			name = Translations.Args.MinecraftServer.Track.twoChannels
			description = Translations.Args.MinecraftServer.Track.TwoChannels.description
			defaultValue = false
		}
	}

	class RemoveTrackArguments : Arguments() {
		val server by string {
			name = Translations.Args.MinecraftServer.RemoveTrack.server
			description = Translations.Args.MinecraftServer.RemoveTrack.Server.description

			autoComplete {
				val list = MinecraftServerTransactor.getAll()

				suggestStringMap(
					list.associate {
						it.name to it.address
					},
					FilterStrategy.Contains
				)
			}
		}
	}

	class StatusArguments : Arguments() {
		val address by string {
			name = Translations.GeneralArgs.MinecraftServer.address
			description = Translations.GeneralArgs.MinecraftServer.Address.description
		}
		val bedrock by defaultingBoolean {
			name = Translations.GeneralArgs.MinecraftServer.bedrock
			description = Translations.GeneralArgs.MinecraftServer.Bedrock.description
			defaultValue = false
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

	class LookupCommandArguments : Arguments() {
		val member by member {
			name = Translations.Args.Minecraft.Lookup.member
			description = Translations.Args.Minecraft.Lookup.Member.description
		}
	}

	class SearchUuidArguments : Arguments() {
		val uuid by string {
			name = Translations.Args.Minecraft.SearchUuid.uuid
			description = Translations.Args.Minecraft.SearchUuid.Uuid.description
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

	class SearchUsernameArguments : Arguments() {
		val username by string {
			name = Translations.Args.Minecraft.SearchUsername.username
			description = Translations.Args.Minecraft.SearchUsername.Username.description
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
