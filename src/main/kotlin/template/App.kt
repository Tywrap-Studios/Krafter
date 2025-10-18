package template

import dev.kord.common.entity.Snowflake
import dev.kordex.core.ExtensibleBot
import dev.kordex.core.utils.env
import template.extensions.TestExtension
import java.io.File

val TEST_SERVER_ID = Snowflake(
	env("TEST_SERVER").toLong()  // Get the test server ID from the env vars or a .env file
)

private val TOKEN = env("TOKEN")   // Get the bot's token from the env vars or a .env file

suspend fun main() {
	val bot = ExtensibleBot(TOKEN) {
		chatCommands {
			defaultPrefix = "?"
			enabled = true

			prefix { default ->
				if (guildId == TEST_SERVER_ID) {
					// For the test server, we use ! as the command prefix
					"!"
				} else {
					// For other servers, we use the configured default prefix
					default
				}
			}
		}

		extensions {
			add(::TestExtension)
		}

		if (devMode) {
			// In development mode, load any plugins from `src/main/dist/plugin` if it exists.
			plugins {
				if (File("src/main/dist/plugins").isDirectory) {
					pluginPath("src/main/dist/plugins")
				}
			}
		}
	}

	bot.start()
}
