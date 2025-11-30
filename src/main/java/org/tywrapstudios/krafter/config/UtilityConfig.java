package org.tywrapstudios.krafter.config;

import org.tywrapstudios.blossombridge.api.config.ConfigClass;

import blue.endless.jankson.Comment;

public class UtilityConfig implements ConfigClass {
	@Comment("Whether to enable the Utility feature.")
	public boolean enabled = false;

	@Comment("People who administer Utility functions.")
	public AdministratorList administrators = new AdministratorList();

	public UtilityConfig.Functions functions = new UtilityConfig.Functions();

	public static class Functions {

		@Comment("Useful commands to let users manage their threads.\n" +
			"(Especially useful when combined with Krafter's suggestions forum threads.)")
		public boolean thread_commands = true;

		@Comment("Lets you view the Raw JSON data of a message.")
		public boolean view_json = true;

		@Comment("Lets the bot say things via command.")
		public boolean say = true;

		@Comment("Welcomes new members to the server.")
		public boolean welcome_message = true;
		@Comment("The channel to send welcome messages to.")
		public String welcome_channel = "welcome";

		@Comment("""
			Répondez s'il vous plaît. Allows users to create RSVP events,
			other users can join these and all get pinged when it starts!""")
		public boolean rsvp = true;
	}
	@Override
	public void validate() {
		functions.welcome_channel = Util.channelCheck.apply(functions.welcome_channel);
	}
}
