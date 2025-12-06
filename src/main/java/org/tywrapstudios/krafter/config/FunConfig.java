package org.tywrapstudios.krafter.config;

import org.tywrapstudios.blossombridge.api.config.ConfigClass;

import blue.endless.jankson.Comment;

public class FunConfig implements ConfigClass {

	@Comment("Whether to enable the Fun feature.")
	public boolean enabled = false;

	@Comment("People who administer Fun functions.")
	public AdministratorList administrators = new AdministratorList();

	public Functions functions = new Functions();

	public static class Functions {

		@Comment("Bean. Who knows what it does.")
		public boolean bean = false;

		@Comment("""
			A Haiku is a traditional form of Japanese consisting of three lines,
			with the first and last lines having five syllables and the middle
			line having seven syllables.

			This function looks for accidental Haiku's in chat messages.""")
		public boolean haiku = false;

		@Comment("Allow users to star messages and put them on a dedicated channel.")
		public boolean star_board = false;

		@Comment("The channel to put starred messages in.")
		public String star_channel = "";

		@Comment("The minimum amount of stars required to be featured on the board. (>=)")
		public int min_stars = 10;
	}

	@Override
	public void validate() {
		functions.star_channel = Util.channelCheck.apply(functions.star_channel);
	}
}
