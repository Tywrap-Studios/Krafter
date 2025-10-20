package org.tywrapstudios.krafter

import dev.kordex.core.ExtensibleBot
import dev.kordex.core.utils.env
import dev.kordex.core.utils.envOrNull
import dev.kordex.data.api.DataCollection
import dev.kordex.modules.func.phishing.extPhishing
import dev.kordex.modules.func.tags.tags
import dev.kordex.modules.func.welcome.welcomeChannel
import dev.kordex.modules.pluralkit.extPluralKit
import org.quiltmc.community.cozy.modules.ama.extAma
import org.quiltmc.community.cozy.modules.logs.extLogParser
import org.quiltmc.community.cozy.modules.logs.processors.PiracyProcessor
import org.quiltmc.community.cozy.modules.logs.processors.ProblematicLauncherProcessor
import org.tywrapstudios.blossombridge.api.config.ConfigManager
import org.tywrapstudios.blossombridge.api.logging.LoggingHandler
import org.tywrapstudios.krafter.checks.isBotModuleAdmin
import org.tywrapstudios.krafter.checks.isGlobalBotAdmin
import org.tywrapstudios.krafter.config.BotConfig
import org.tywrapstudios.krafter.database.DatabaseManager
import org.tywrapstudios.krafter.extensions.data.KrafterAmaData
import org.tywrapstudios.krafter.extensions.data.KrafterTagsData
import org.tywrapstudios.krafter.extensions.data.KrafterWelcomeChannelData
import org.tywrapstudios.krafter.extensions.`fun`.FunExtension
import org.tywrapstudios.krafter.extensions.`fun`.HaikuExtension
import org.tywrapstudios.krafter.extensions.logs.RuleBreakingModProcessor
import org.tywrapstudios.krafter.extensions.logs.WrongLocationMessageSender
import org.tywrapstudios.krafter.extensions.minecraft.MinecraftExtension
import org.tywrapstudios.krafter.extensions.sab.SafetyAndAbuseExtension
import org.tywrapstudios.krafter.extensions.sab.getOverwrites
import org.tywrapstudios.krafter.extensions.suggestion.SuggestionsExtension
import org.tywrapstudios.krafter.extensions.utility.UtilityExtension
import java.io.File

val TEST_SERVER_ID = envOrNull("TEST_SERVER")?.snowflake()
private val TOKEN = env("TOKEN")   // Get the bot's token from the env vars or a .env file
val CFG = ConfigManager(
	BotConfig::class.java,
	File(getConfigDirectory().toFile(), "krafter.json5")
)
val LOGGING: LoggingHandler<BotConfig> = LoggingHandler(
	"Krafter",
	CFG
)

var initialised: Boolean = false

suspend fun setup(): ExtensibleBot {
	if (!initialised) {
		loadAllConfigs()
		LOGGING.debug("Before db setup")
		DatabaseManager.setup(null)
		LOGGING.debug("After db setup")

		LOGGING.debug("Current configuration:")
		LOGGING.debug(getAllConfigJsonAsString(comments = false, newlines = true))
	}

    return ExtensibleBot(TOKEN) {

        chatCommands {
            defaultPrefix = mainConfig().prefix
            enabled = true

            prefix { default ->
                // If TEST_SERVER_ID isn't null, we are in test mode and should not use the default prefix.
                if (TEST_SERVER_ID != null) {
                    "?>"
                } else {
                    default
                }
            }
        }

        applicationCommands {
            if (TEST_SERVER_ID != null) {
                defaultGuild = TEST_SERVER_ID
            }
        }

        extensions {

            if (mainConfig().plural_kit) extPluralKit()

            add(::SafetyAndAbuseExtension)
			add(::UtilityExtension)

            if(minecraftConfig().enabled) {
                add(::MinecraftExtension)
            }
            if (sabConfig().block_phishing) extPhishing {
                for (domain in sabConfig().banned_domains) badDomain(domain)
                logChannelName =
                    if (
                        sabConfig().channel == "new" ||
						sabConfig().channel.isEmpty()
                    ) {
                        "moderation"
                    } else {
						sabConfig().channel
                    }
            }
            if (tagsConfig().enabled) tags(KrafterTagsData()) {
                staffCommandCheck { isBotModuleAdmin(tagsConfig().administrators) }
                loggingChannelName =
                    if (
						sabConfig().channel == "new" ||
						sabConfig().channel.isEmpty()
                    ) {
                        "moderation"
                    } else {
						sabConfig().channel
                    }
            }
            if (amaConfig().enabled) extAma(KrafterAmaData())
            if (crashAnalyticsConfig().enabled) extLogParser {
                processor(PiracyProcessor())
                processor(ProblematicLauncherProcessor())
                processor(RuleBreakingModProcessor())

                parser(WrongLocationMessageSender())
                staffCommandCheck { isGlobalBotAdmin() }
            }
            if (embedChannelsConfig().enabled) welcomeChannel(KrafterWelcomeChannelData()) {
                staffCommandCheck { isBotModuleAdmin(embedChannelsConfig().administrators) }
                getLogChannel { channel, guild ->
                    return@getLogChannel getOrCreateChannel(
                        sabConfig().channel,
                        "moderation",
                        "Safety and Abuse logging and dump channel for the Krafter software",
                        getOverwrites(guild),
                        guild
                    )
                }
            }

            if (suggestionsConfig().enabled) {
                add(::SuggestionsExtension)
            }
            if (funConfig().enabled) {
                if (funConfig().functions.haiku) add(::HaikuExtension)
				add(::FunExtension)
            }
        }

        dataCollectionMode = DataCollection.fromDB(sabConfig().data_collection)

        presence {
            fromString(
				mainConfig().status_type,
				mainConfig().status_text,
				mainConfig().status_url
			)
        }

		about {
			addCopyright()
		}
    }
}

suspend fun main() {
	val bot = setup()
	LOGGING.info("${mainConfig().enabled}")
	if (mainConfig().enabled) {
		bot.start()
		initialised = true
	} else {
		LOGGING.warn("The bot is disabled in the configuration! Please enable it to run.")
	}
}
