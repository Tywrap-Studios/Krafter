package org.tywrapstudios.krafter.api.rcon

import nl.vv32.rcon.Rcon
import org.tywrapstudios.krafter.LOGGING
import org.tywrapstudios.krafter.api.objects.McMessage
import org.tywrapstudios.krafter.mainConfig
import org.tywrapstudios.krafter.config.BotConfig
import org.tywrapstudios.krafter.config.MinecraftConfig
import org.tywrapstudios.krafter.minecraftConfig
import org.tywrapstudios.krafter.platform.services.IMinecraftServerConnection

class RconMinecraftServerConnection : IMinecraftServerConnection {
	val cfg: MinecraftConfig = minecraftConfig()

	override fun broadcast(message: McMessage) {
		val author = message.getAuthor().get()
		val userText = if(author.getMcName() == null) "@" + author.getName() else author.getMcName()
		val tellrawCommand =
			"/tellraw @a [\"\"," +
				"{\"text\":\"[$userText] \"," +
				"\"color\":\"blue\"," +
				"\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"${author.getMention()}\"}," +
				"\"hoverEvent\":{\"action\":\"show_text\",\"contents\":" +
				"[{\"text\":\"@${author.getUsername()}\"," +
				"\"color\":\"dark_purple\"}]}}," +
				"{\"text\":\"${message.getContent()}\"," +
				"\"color\":\"white\"," +
				"\"hoverEvent\":{\"action\":\"show_text\",\"contents\":" +
				"[{\"text\":\"Sent from Discord\"," +
				"\"color\":\"dark_purple\"}]}}]"
		// I'm actually so done with this
		LOGGING.debug(command(tellrawCommand))
    }

    override fun broadcastPlain(message: String) {
		val tellrawCommand = "/tellraw @a [\"\",{\"text\":\"$message\"}]"
        command(tellrawCommand)
    }

	override fun command(command: String): String {
		Rcon.open(cfg.connection.rcon_host, cfg.connection.rcon_port.toInt()).use { rcon ->
			return if (rcon.authenticate(cfg.connection.rcon_password)) {
				rcon.sendCommand(command)
			} else {
				"Failed to authenticate"
			}
		}
	}
}
