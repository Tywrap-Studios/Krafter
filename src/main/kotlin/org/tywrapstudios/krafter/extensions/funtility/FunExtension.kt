package org.tywrapstudios.krafter.extensions.funtility

import dev.kord.rest.builder.message.embed
import dev.kordex.core.DISCORD_LIGHT_BLURPLE
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.member
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import org.tywrapstudios.krafter.funConfig
import org.tywrapstudios.krafter.i18n.Translations

class FunExtension : Extension() {
	override val name: String = "krafter.fun"

	override suspend fun setup() {
		if (funConfig().functions.bean) publicSlashCommand(::BeanArguments) {
			name = Translations.Commands.bean
			description = Translations.Commands.Bean.description

			action {
				respond {
					embed {
						title = "Member Beaned"
						color = DISCORD_LIGHT_BLURPLE
						description = "Member: ${arguments.target.mention} has been beaned,\n" +
							"\"Responsible\" \"moderator\": ${user.mention}"
					}
				}
			}
		}
	}

	class BeanArguments : Arguments() {
		val target by member {
			name = Translations.Args.Bean.target
			description = Translations.Args.Bean.Target.description
		}
	}
}
