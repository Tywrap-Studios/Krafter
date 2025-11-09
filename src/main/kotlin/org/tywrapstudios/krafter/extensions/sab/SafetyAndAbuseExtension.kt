@file:Suppress("WildcardImport")

package org.tywrapstudios.krafter.extensions.sab

import dev.kord.common.entity.*
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kord.core.event.guild.MemberJoinEvent
import dev.kord.core.event.guild.MemberUpdateEvent
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.event
import org.tywrapstudios.krafter.mainConfig
import org.tywrapstudios.krafter.getOrCreateChannel
import org.tywrapstudios.krafter.sabConfig

const val HOIST_REGEX = "^[a-zA-Z]"
val REPLACEMENTS = arrayOf(
	"Robin",
	"Proelia",
	"Puddy",
	"Risk",
	"Mudpie",
	"Starshin",
	"Puzzlie",
	"Momo"
)

class SafetyAndAbuseExtension : Extension() {
    override val name: String = "krafter.sab"
    var dumpChannel: TextChannel? = null

    override suspend fun setup() {
        val cfg = sabConfig()

        event<GuildCreateEvent> {
            action {
                dumpChannel = getOrCreateChannel(
                    cfg.channel,
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
    }

	suspend fun deHoist(member: MemberBehavior) {
		val member = member.asMember()
		val name = member.effectiveName
		if (sabConfig().block_hoisting) {
			member.edit {
				nickname = name
					.replace(HOIST_REGEX.toRegex(), "")
					.ifEmpty { REPLACEMENTS.random() }
			}
		}
	}

	suspend fun deCancer(member: MemberBehavior) {
		val member = member.asMember()
		val name = member.effectiveName
		if (sabConfig().decancer_usernames) {
			TODO("Awaiting external API")
		}
	}
}

fun getOverwrites(guild: Guild): MutableSet<Overwrite> {
    val cfg = mainConfig()
    val overwrites = mutableSetOf<Overwrite>()
    for (role in cfg.global_administrators.roles) {
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

    for (user in cfg.global_administrators.users) {
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
