import ch.qos.logback.core.joran.spi.ConsoleTarget
import ch.qos.logback.core.ConsoleAppender
import org.tywrapstudios.krafter.DiscordLogAppender

def defaultLevel = INFO
def defaultTarget = ConsoleTarget.SystemErr
def logUrl = System.getenv("DISCORD_LOGGER_URL")

def DEV_MODE = System.getProperty("devMode")?.toBoolean() ||
	System.getenv("DEV_MODE") != null ||
	["dev", "development"].contains(System.getenv("ENVIRONMENT"))

if (DEV_MODE) {
	defaultLevel = DEBUG
	defaultTarget = ConsoleTarget.SystemOut

	// Silence warning about missing native PRNG
	logger("io.ktor.util.random", ERROR)
}

appender("CONSOLE", ConsoleAppender) {
	encoder(PatternLayoutEncoder) {
		pattern = "%boldGreen(%d{yyyy-MM-dd}) %boldYellow(%d{HH:mm:ss}) %gray(|) %highlight(%5level) %gray(|) %boldMagenta(%40.40logger{40}) %gray(|) %msg%n"
	}

	target = defaultTarget
}

if (logUrl != null) {
	appender("DISCORD_ERROR", DiscordLogAppender) {
		level = ERROR
		url = System.getenv("DISCORD_LOGGER_URL")
	}

	appender("DISCORD_WARN", DiscordLogAppender) {
		level = WARN
		url = System.getenv("DISCORD_LOGGER_URL")
	}

	appender("DISCORD_INFO", DiscordLogAppender) {
		level = INFO
		url = System.getenv("DISCORD_LOGGER_URL")
	}
}

root(defaultLevel, ["CONSOLE", "DISCORD_ERROR", "DISCORD_WARN"])
