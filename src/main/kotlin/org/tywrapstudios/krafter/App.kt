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
import org.slf4j.LoggerFactory
import org.tywrapstudios.blossombridge.api.config.ConfigManager
import org.tywrapstudios.blossombridge.api.logging.LoggingHandler
import org.tywrapstudios.krafter.checks.isBotModuleAdmin
import org.tywrapstudios.krafter.checks.isGlobalBotAdmin
import org.tywrapstudios.krafter.config.BotConfig
import org.tywrapstudios.krafter.database.DatabaseManager
import org.tywrapstudios.krafter.extensions.data.KrafterAmaData
import org.tywrapstudios.krafter.extensions.data.KrafterTagsData
import org.tywrapstudios.krafter.extensions.data.KrafterWelcomeChannelData
import org.tywrapstudios.krafter.extensions.logs.RuleBreakingModProcessor
import org.tywrapstudios.krafter.extensions.logs.WrongLocationMessageSender
import org.tywrapstudios.krafter.extensions.minecraft.MinecraftExtension
import org.tywrapstudios.krafter.extensions.sab.SafetyAndAbuseExtension
import org.tywrapstudios.krafter.extensions.sab.getOverwrites
import org.tywrapstudios.krafter.extensions.suggestion.SuggestionsExtension
import java.io.File
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.CompletableFuture
import kotlin.io.path.createDirectories
import kotlin.io.path.createDirectory

val TEST_SERVER_ID = envOrNull("TEST_SERVER")?.snowflake()
private val TOKEN = env("TOKEN")   // Get the bot's token from the env vars or a .env file
val CFG: ConfigManager<BotConfig> = ConfigManager(
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
		CFG.loadConfig()
		LOGGING.debug("Before db setup")
		DatabaseManager.setup(null)
		LOGGING.debug("After db setup")

		LOGGING.debug("Current configuration:")
		LOGGING.debug(CFG.getConfigJsonAsString(comments = false, newlines = true))
	}

    val config = config()

    return ExtensibleBot(TOKEN) {

        chatCommands {
            defaultPrefix = config.prefix
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

            if (config.miscellaneous.plural_kit.enabled) extPluralKit()

            add(::SafetyAndAbuseExtension)

            if(config.minecraft.enabled) {
                add(::MinecraftExtension)
            }
            if (config.safety_and_abuse.moderation.block_phishing) extPhishing {
                for (domain in config.safety_and_abuse.moderation.banned_domains) badDomain(domain)
                logChannelName =
                    if (
                        config().safety_and_abuse.dump_channel == "new" ||
                        config().safety_and_abuse.dump_channel.isEmpty()
                    ) {
                        "krafter-sab"
                    } else {
                        config.safety_and_abuse.dump_channel
                    }
            }
            if (config.miscellaneous.tags.enabled) tags(KrafterTagsData()) {
                staffCommandCheck { isBotModuleAdmin(config.miscellaneous.tags.administrators) }
                loggingChannelName =
                    if (
                        config().safety_and_abuse.dump_channel == "new" ||
                        config().safety_and_abuse.dump_channel.isEmpty()
                    ) {
                        "krafter-sab"
                    } else {
                        config.safety_and_abuse.dump_channel
                    }
            }
            if (config.miscellaneous.ama.enabled) extAma(KrafterAmaData())
            if (config.miscellaneous.crash_analysing.enabled) extLogParser {
                processor(PiracyProcessor())
                processor(ProblematicLauncherProcessor())
                processor(RuleBreakingModProcessor())

                parser(WrongLocationMessageSender())
                staffCommandCheck { isGlobalBotAdmin() }
            }
            if (config.miscellaneous.embed_channels.enabled) welcomeChannel(KrafterWelcomeChannelData()) {
                staffCommandCheck { isBotModuleAdmin(config.miscellaneous.embed_channels.administrators) }
                getLogChannel { channel, guild ->
                    val cfg = config().safety_and_abuse

                    return@getLogChannel getOrCreateChannel(
                        cfg.dump_channel,
                        "krafter-sab",
                        "Safety and Abuse logging and dump channel for the Krafter software",
                        getOverwrites(guild),
                        guild
                    )
                }
            }

            if (config.miscellaneous.suggestion_forum.enabled) {
                add(::SuggestionsExtension)
            }
        }

        dataCollectionMode = DataCollection.fromDB(config.safety_and_abuse.data_collection)

        presence {
            playing("on ${config.status.server_name}")
        }

		about {
			addCopyright()
		}
    }
}

suspend fun main() {
	val bot = setup()
	LOGGING.info("${config().enabled}")
	if (config().enabled) {
		bot.start()
		initialised = true
	} else {
		LOGGING.warn("The bot is disabled in the configuration! Please enable it to run.")
	}
}
