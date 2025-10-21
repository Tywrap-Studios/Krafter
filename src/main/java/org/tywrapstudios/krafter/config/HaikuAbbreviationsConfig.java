package org.tywrapstudios.krafter.config;

import org.tywrapstudios.blossombridge.api.config.ConfigClass;

import java.util.HashMap;
import java.util.Map;

import blue.endless.jankson.Comment;

public class HaikuAbbreviationsConfig implements ConfigClass {

	@Comment("Map of abbreviations for haiku syllable counting. Key is the abbreviation, value is the full form.")
	public Map<String, String> abbreviations = new HashMap<>();

	@Override
	public void validate() {

	}
}
