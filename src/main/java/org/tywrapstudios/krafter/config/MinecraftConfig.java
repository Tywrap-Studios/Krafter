package org.tywrapstudios.krafter.config;

import org.tywrapstudios.blossombridge.api.config.ConfigClass;

import java.util.List;

import blue.endless.jankson.Comment;

public class MinecraftConfig implements ConfigClass {

	@Comment("Whether to enable the Minecraft features altogether.")
	public boolean enabled = false;

	@Comment("People who administer Minecraft functions.")
	public AdministratorList administrators = new AdministratorList();

	public MinecraftServerStatus status = new MinecraftServerStatus();
	public ChatConnection connection = new ChatConnection();

	@Override
	public void validate() {
		connection.channel = Util.channelCheck.apply(connection.channel);

		if (!List.of("BOT", "CHANNEL").contains(status.use)) status.use = "BOT";

		if (!enabled) {
			connection.enabled = false;
			status.enabled = false;
		}
	}

	public static class MinecraftServerStatus {
		@Comment("Whether to enable the Minecraft server status feature.")
		public boolean enabled = false;

		@Comment("The address of the server to monitor.")
		public String server_address = "";

		@Comment("""
			What medium to use to reflect the status
			Must be of: "BOT" or "CHANNEL\"""")
		public String use = "BOT";

		@Comment("The name of the server to display in status messages.")
		public String server_name = "";
	}

	public static class ChatConnection {
		@Comment("Whether to enable the Minecraft chat connection.")
		public boolean enabled = false;

		@Comment("""
			The name of the channel which the bot will watch for messages to send. e.g. "mc-chat"
			Set to "new" to have one made automatically.""")
		public String channel = "mc-chat";

		@Comment("""
			An address (host plus port) to an RCON connection, which allows the bot to send commands to the server without
			a direct server connection.

			If you don't know what this is, or what it does, don't touch this. Krafter will always first try to
			maintain a direct connection before ultimately falling back to RCON.""")
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
}
