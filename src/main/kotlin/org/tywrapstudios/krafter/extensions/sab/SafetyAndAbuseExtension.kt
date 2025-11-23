@file:Suppress("WildcardImport")
@file:OptIn(ExperimentalTime::class)

package org.tywrapstudios.krafter.extensions.sab

import dev.kord.common.entity.*
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.ban
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kord.core.event.guild.MemberJoinEvent
import dev.kord.core.event.guild.MemberUpdateEvent
import dev.kord.rest.builder.ban.BanCreateBuilder
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.create.FollowupMessageCreateBuilder
import dev.kord.rest.builder.message.embed
import dev.kordex.core.DISCORD_GREEN
import dev.kordex.core.DISCORD_LIGHT_BLURPLE
import dev.kordex.core.DISCORD_RED
import dev.kordex.core.annotations.DoNotChain
import dev.kordex.core.annotations.UnexpectedFunctionBehaviour
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.ephemeralSubCommand
import dev.kordex.core.commands.converters.impl.defaultingBoolean
import dev.kordex.core.commands.converters.impl.defaultingString
import dev.kordex.core.commands.converters.impl.optionalString
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.commands.converters.impl.user
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kordex.core.extensions.ephemeralUserCommand
import dev.kordex.core.extensions.event
import dev.kordex.core.utils.removeTimeout
import dev.kordex.core.utils.scheduling.Scheduler
import dev.kordex.core.utils.scheduling.Task
import dev.kordex.core.utils.timeout
import io.github.null8626.decancer.CuredString
import org.tywrapstudios.krafter.LOGGING
import org.tywrapstudios.krafter.SCHEDULER
import org.tywrapstudios.krafter.checks.isBotModuleAdmin
import org.tywrapstudios.krafter.config.SabConfig
import org.tywrapstudios.krafter.database.transactors.TempbanTransactor
import org.tywrapstudios.krafter.getOrCreateChannel
import org.tywrapstudios.krafter.i18n.Translations
import org.tywrapstudios.krafter.sabConfig
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

val HOIST_REGEX: String get() = sabConfig().hoist_regex
val REPLACEMENTS = arrayOf(
	"Robin",
	"Proelia",
	"Puddy",
	"Risk",
	"Mudpie",
	"Starshin",
	"Puzzlie",
	"Momo",
	"Harper",
	"Hayden",
	"Eli",
	"Ash",
	"Cameron",
)

class SafetyAndAbuseExtension : Extension() {
	override val name: String = "krafter.sab"
	var dumpChannel: TextChannel? = null
	val tempbans = TempbanTransactor
	private var unbanTask: Task? = null

	@OptIn(UnexpectedFunctionBehaviour::class, DoNotChain::class)
	override suspend fun setup() {
		unbanTask = SCHEDULER.schedule(
			sabConfig().minute_interval.minutes,
			name = "Tempban Unbanning Task",
			repeat = true
		) {
			val toUnban = tempbans.getExpired()
			for ((userId, guildId) in toUnban) {
				LOGGING.debug("Unbanning user $userId from guild $guildId due to tempban expiry.")
				val guild = kord.getGuildOrNull(guildId)
				if (guild != null) {
					guild.unban(userId, "Temporary ban expired")
					tempbans.remove(userId, guildId)
					LOGGING.debug("Success")
				} else {
					LOGGING.warn("Could not unban user $userId from guild $guildId because the bot could not fetch the guild that contains the user.")
				}
			}
		}

		event<GuildCreateEvent> {
			action {
				dumpChannel = getOrCreateChannel(
					sabConfig().channel,
					"moderation",
					"Safety and Abuse logging and dump channel for the Krafter software",
					getOverwrites(event.guild),
					event.guild
				)
			}
		}

		event<MemberJoinEvent> {
			action {
				val member = event.member
				deHoist(member)
				deCancer(member)
			}
		}

		event<MemberUpdateEvent> {
			action {
				val member = event.member
				deHoist(member)
				deCancer(member)
			}
		}

		fun FollowupMessageCreateBuilder.cleanseEmbed(member: Member, oldName: String) {
			if (oldName == member.effectiveName) {
				embed {
					title = "No changes made to ${member.mention}"
					field {
						name = "Name"
						value = member.effectiveName
					}
					description = "The user's name did not require cleansing.\n" +
						"If you believe this to be an error, please report an issue and attach the debug info."
				}
				actionRow {
					interactionButton(ButtonStyle.Primary, "sab:debug_cleanse") {
						label = "Debug"
					}
					linkButton("https://github.com/Tywrap-Studios/Krafter/issues/new") {
						label = "Report Issue"
					}
				}
				return
			}
			embed {
				title = "Cleansed ${member.mention}"
				color = DISCORD_LIGHT_BLURPLE
				field {
					name = "Old Name"
					value = oldName
					inline = true
				}
				field {
					name = "New Name"
					value = member.effectiveName
					inline = true
				}
			}
		}

		fun FollowupMessageCreateBuilder.banEmbed(user: User, duration: Duration) {
			val durationString = if (duration == Duration.INFINITE) {
				"indefinitely"
			} else {
				"for ${
					duration.toComponents { days, hours, minutes, seconds, _ ->
						buildString {
							if (days > 0) append("$days day${if (days.toInt() == 1) "" else "s"} ")
							if (hours > 0) append("$hours hour${if (hours == 1) "" else "s"} ")
							if (minutes > 0) append("$minutes minute${if (minutes == 1) "" else "s"} ")
							if (seconds > 0) append("$seconds second${if (seconds == 1) "" else "s"}")
						}.trim()
					}
				}"
			}
			embed {
				description = "Banned ${user.mention} $durationString."
				color = DISCORD_RED
			}
		}

		fun FollowupMessageCreateBuilder.unbanEmbed(user: User) {
			embed {
				description = "Unbanned ${user.mention}."
				color = DISCORD_GREEN
			}
		}

		fun FollowupMessageCreateBuilder.kickEmbed(user: User) {
			embed {
				description = "Kicked ${user.mention}."
				color = DISCORD_RED
			}
		}

		fun FollowupMessageCreateBuilder.timeoutEmbed(user: User, duration: Duration) {
			embed {
				description = "${user.mention} " +
					if (duration == Duration.ZERO) "got their time-out removed."
					else "was timed out for ${
						duration.toComponents { days, hours, minutes, seconds, _ ->
							buildString {
								if (days > 0) append("$days day${if (days.toInt() == 1) "" else "s"} ")
								if (hours > 0) append("$hours hour${if (hours == 1) "" else "s"} ")
								if (minutes > 0) append("$minutes minute${if (minutes == 1) "" else "s"} ")
								if (seconds > 0) append("$seconds second${if (seconds == 1) "" else "s"}")
							}.trim()
						}
					}."
				color = if (duration == Duration.ZERO) DISCORD_GREEN else DISCORD_LIGHT_BLURPLE
			}
		}

		if (sabConfig().mod_commands) {
			ephemeralUserCommand {
				check { isBotModuleAdmin(sabConfig().administrators) }
				name = Translations.Commands.Moderate.UserCommand.cleanse
				action {
					val member = targetUsers.first().asMember(guild!!.id)
					val oldName = member.effectiveName
					var newMember = deHoist(member)
					newMember = deCancer(newMember)
					respond {
						cleanseEmbed(newMember, oldName)
					}
				}
			}

			ephemeralSlashCommand {
				name = Translations.Commands.moderate
				description = Translations.Commands.Moderate.description

				check { isBotModuleAdmin(sabConfig().administrators) }

				ephemeralSubCommand(::ModerateArguments) {
					name = Translations.Commands.Moderate.cleanse
					description = Translations.Commands.Moderate.Cleanse.description
					action {
						val member = arguments.user.asMember(guild!!.id)
						val oldName = member.effectiveName
						deHoist(member, arguments.reason)
						deCancer(member, arguments.reason)
						var newMember = deHoist(member, arguments.reason)
						newMember = deCancer(newMember, arguments.reason)
						if (arguments.silent) {
							respond {
								cleanseEmbed(newMember, oldName)
							}
						} else {
							respondOpposite {
								cleanseEmbed(newMember, oldName)
							}
						}
					}
				}

				ephemeralSubCommand(::BanArguments) {
					name = Translations.Commands.Moderate.ban
					description = Translations.Commands.Moderate.Ban.description
					action {
						val duration = Duration.parse(arguments.duration)
						LOGGING.debug("Parsed duration: $duration from input: ${arguments.duration} ISO ${duration.toIsoString()}")
						if (duration.isInfinite()) {
							arguments.user.ban(guild!!.id) {
								reason = arguments.reason
								deleteMessageDuration = if (arguments.deleteMessages) 7.days else null
							}
						} else {
							arguments.user.tempban(duration, guild!!.id) {
								reason = arguments.reason
								deleteMessageDuration = if (arguments.deleteMessages) 7.days else null
							}
						}
						if (arguments.silent) {
							respond {
								banEmbed(arguments.user, duration)
							}
						} else {
							respondOpposite {
								banEmbed(arguments.user, duration)
							}
						}
					}
				}

				ephemeralSubCommand {
					name = Translations.Commands.Moderate.task
					description = Translations.Commands.Moderate.Task.description
					action {
						SCHEDULER.callAllNow()
						respond {
							embed {
								description = "Task calls attempted.\n" +
									"Check yours logs to look for any errors."
								color = DISCORD_GREEN
							}
						}
					}
				}

				ephemeralSubCommand(::ModerateArguments) {
					name = Translations.Commands.Moderate.unban
					description = Translations.Commands.Moderate.Unban.description
					action {
						arguments.user.unban(guild!!.id)
						if (arguments.silent) {
							respond {
								unbanEmbed(arguments.user)
							}
						} else {
							respondOpposite {
								unbanEmbed(arguments.user)
							}
						}
					}
				}

				ephemeralSubCommand(::ModerateArguments) {
					name = Translations.Commands.Moderate.kick
					description = Translations.Commands.Moderate.Kick.description
					action {
						arguments.user.asMemberOrNull(guild!!.id)?.kick(arguments.reason)
						if (arguments.silent) {
							respond {
								kickEmbed(arguments.user)
							}
						} else {
							respond {
								kickEmbed(arguments.user)
							}
						}
					}
				}

				ephemeralSubCommand(::ModerateWithDurationArguments) {
					name = Translations.Commands.Moderate.timeout
					description = Translations.Commands.Moderate.Timeout.description
					action {
						val duration = Duration.parse(arguments.duration)
						if (duration > 28.days) {
							respond {
								embed {
									description =
										"`$duration` is too long, the absolute limit is 28 days (`28d`). Please insert a lower duration."
									color = DISCORD_RED
								}
							}
							return@action
						}
						arguments.user.asMemberOrNull(guild!!.id)?.timeout(duration, arguments.reason)
						if (arguments.silent) {
							respond {
								timeoutEmbed(arguments.user, duration)
							}
						} else {
							respond {
								timeoutEmbed(arguments.user, duration)
							}
						}
					}
				}

				ephemeralSubCommand(::ModerateArguments) {
					name = Translations.Commands.Moderate.removeTimeout
					description = Translations.Commands.Moderate.RemoveTimeout.description
					action {
						arguments.user.asMemberOrNull(guild!!.id)?.removeTimeout(arguments.reason)
						if (arguments.silent) {
							respond {
								timeoutEmbed(arguments.user, Duration.ZERO)
							}
						} else {
							respond {
								timeoutEmbed(arguments.user, Duration.ZERO)
							}
						}
					}
				}
			}
		}
	}

	open class ModerateArguments : Arguments() {
		val user by user {
			name = Translations.GeneralArgs.Moderate.member
			description = Translations.GeneralArgs.Moderate.Member.description
		}
		val reason by optionalString {
			name = Translations.GeneralArgs.Moderate.reason
			description = Translations.GeneralArgs.Moderate.Reason.description
		}
		val silent by defaultingBoolean {
			name = Translations.GeneralArgs.Moderate.silent
			description = Translations.GeneralArgs.Moderate.Silent.description
			defaultValue = false
		}
	}

	open class ModerateWithDurationArguments : Arguments() {
		val user by user {
			name = Translations.GeneralArgs.Moderate.member
			description = Translations.GeneralArgs.Moderate.Member.description
		}
		val duration by string {
			name = Translations.GeneralArgs.Moderate.duration
			description = Translations.GeneralArgs.Moderate.Duration.description
		}
		val reason by optionalString {
			name = Translations.GeneralArgs.Moderate.reason
			description = Translations.GeneralArgs.Moderate.Reason.description
		}
		val silent by defaultingBoolean {
			name = Translations.GeneralArgs.Moderate.silent
			description = Translations.GeneralArgs.Moderate.Silent.description
			defaultValue = false
		}
	}

	class BanArguments : ModerateArguments() {
		val duration by defaultingString {
			name = Translations.GeneralArgs.Moderate.duration
			description = Translations.GeneralArgs.Moderate.Duration.description
			defaultValue = "Infinity"
		}
		val deleteMessages by defaultingBoolean {
			name = Translations.Args.Moderate.Ban.deleteMessages
			description = Translations.Args.Moderate.Ban.DeleteMessages.description
			defaultValue = false
		}
	}

	suspend fun deHoist(member: MemberBehavior, givenReason: String? = "Username de-hoist"): Member {
		val member = member.asMember()
		val name = member.effectiveName
		if (sabConfig().block_hoisting) {
			return member.edit {
				reason = givenReason
				nickname = name
					.replace(HOIST_REGEX.toRegex(), "")
					.ifEmpty { REPLACEMENTS.random() }
			}
		}
		return member
	}

	suspend fun deCancer(member: MemberBehavior, givenReason: String? = "Username de-cancer"): Member {
		val member = member.asMember()
		member.effectiveName
		if (sabConfig().decancer_usernames) {
			CuredString(member.effectiveName).use {
				return member.edit {
					reason = givenReason
					nickname = it.toString()
						.ifEmpty { REPLACEMENTS.random() }
				}
			}
		}
		return member
	}

	suspend fun MemberBehavior.tempban(duration: Duration, builder: BanCreateBuilder.() -> Unit = {}) {
		tempbans.add(id, guildId, Clock.System.now() + duration)
		guild.ban(id, builder)
	}

	suspend fun UserBehavior.ban(guildId: Snowflake, builder: BanCreateBuilder.() -> Unit = {}) {
		val guild = kord.getGuild(guildId)
		tempbans.remove(id, guildId) // remove in case this is a reban from temp to perma
		guild.ban(id, builder)
	}

	suspend fun UserBehavior.tempban(
		duration: Duration,
		guildId: Snowflake,
		builder: BanCreateBuilder.() -> Unit = {}
	) {
		val guild = kord.getGuild(guildId)
		LOGGING.debug("Added tempban for user $id for duration $duration (unban at ${Clock.System.now() + duration})")
		tempbans.add(id, guildId, Clock.System.now() + duration)
		guild.ban(id, builder)
	}

	suspend fun UserBehavior.unban(guildId: Snowflake, reason: String? = null) {
		val guild = kord.getGuild(guildId)
		tempbans.remove(id, guildId)
		guild.unban(id, reason)
	}
}

fun getOverwrites(guild: Guild): MutableSet<Overwrite> {
	val cfg = sabConfig()
	val overwrites = mutableSetOf<Overwrite>()
	for (role in cfg.administrators.roles) {
		overwrites.add(
			Overwrite(
				Snowflake(role),
				OverwriteType.Role,
				Permissions {
					+Permission.ViewChannel
					-Permission.SendMessages
				},
				Permissions {
					-Permission.ViewChannel
					+Permission.SendMessages
				}
			)
		)
	}

	for (user in cfg.administrators.users) {
		overwrites.add(
			Overwrite(
				Snowflake(user),
				OverwriteType.Member,
				Permissions {
					+Permission.ViewChannel
					-Permission.SendMessages
				},
				Permissions {
					-Permission.ViewChannel
					+Permission.SendMessages
				}
			)
		)
	}

	overwrites.add(
		Overwrite(
			guild.id,
			OverwriteType.Role,
			Permissions {
				-Permission.ViewChannel
				-Permission.SendMessages
			},
			Permissions {
				+Permission.ViewChannel
				+Permission.SendMessages
			}
		)
	)

	return overwrites
}
