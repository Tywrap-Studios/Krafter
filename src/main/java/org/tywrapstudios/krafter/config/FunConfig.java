package org.tywrapstudios.krafter.config;

import org.tywrapstudios.blossombridge.api.config.ConfigClass;

import blue.endless.jankson.Comment;

public class FunConfig implements ConfigClass {

	@Comment("Whether to enable the Fun feature.")
	public boolean enabled = false;

	@Comment("People who administer Fun functions.")
	public AdministratorList administrators = new AdministratorList();

	public Functions functions = new Functions();

	@Override
	public void validate() {

	}

	public static class Functions {

		@Comment("Bean. Who knows what it does.")
		public boolean bean = false;

		@Comment("""
			A Haiku is a traditional form of Japanese consisting of three lines,
			with the first and last lines having five syllables and the middle
			line having seven syllables.

			This function looks for accidental Haiku's in chat messages.""")
		public boolean haiku = false;
	}
}
