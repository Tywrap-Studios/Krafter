package org.tywrapstudios.krafter.config;

import org.tywrapstudios.blossombridge.api.config.ConfigClass;
import org.tywrapstudios.blossombridge.api.config.InvalidConfigFileException;

import java.util.List;

import blue.endless.jankson.Comment;

public class MinecraftConfig implements ConfigClass {

	@Comment("Whether to enable the Minecraft features altogether.")
	public boolean enabled = false;

	@Comment("People who administer Minecraft functions.")
	public AdministratorList administrators = new AdministratorList();

	public MinecraftServerStatus status = new MinecraftServerStatus();
	public ChatConnection connection = new ChatConnection();

	public static class MinecraftServerStatus {
		@Comment("Whether to enable the Minecraft server status feature.")
		public boolean enabled = false;

		@Comment("How often to check and update the status. In seconds.")
		public int polling_seconds = 30;
	}

	public static class ChatConnection {
		@Comment("Whether to enable the Minecraft chat connection.")
		public boolean enabled = false;

		@Comment("Whether to enable the use of console via commands.")
		public boolean console = false;

		@Comment("""
			The name of the channel which the bot will watch for messages to send. e.g. "mc-chat"
			Set to "new" to have one made automatically.""")
		public String channel = "mc-chat";

		@Comment("""
			An address (host plus port) to an RCON connection, which allows the bot to send commands to the server without
			a direct server connection.

			These values are mandatory, the bot will deliberately crash if they are left empty.""")
		public String rcon_host = "";
		public String rcon_port = "";

		@Comment("The password for the RCON connection, if you have one set up.")
		public String rcon_password = "";

		@Comment("""
			An address (host plus port) to a CTD (Chat to Discord) connection, which is a better, more fluent way
			to maintain a connection without the use of RCON.

			If you don't know what this is, or what it does, don't touch this.""")
		public String ctd_host = "";
		public String ctd_port = "";

		@Comment("The password for the CTD connection, if you have one set up.")
		public String ctd_password = "";
	}

	@Comment("Whether to allow users to link their Minecraft profile to Discord.")
	public boolean linking = false;

	@Comment("Whether to add utility commands for Minecraft profiles.")
	public boolean profile_utils = false;

	@Override
	public void validate() {
		connection.channel = Util.channelCheck.apply(connection.channel);

		if (connection.console || connection.enabled) {
			if (connection.rcon_host.isEmpty() || connection.rcon_port.isEmpty() || connection.rcon_password.isEmpty()) {
				throw new InvalidConfigFileException("You tried to use the MC Connection features without RCON configured.");
			}
		}
	}
}
