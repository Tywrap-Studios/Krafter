package org.tywrapstudios.krafter.config;

import java.util.Objects;
import java.util.function.Function;

class Util {
	static Function<String, String> channelCheck = (t) -> {
		if(!Objects.equals(t, "new") && !t.matches("[a-z\\-]+") && !t.isEmpty()) return "";
		return t;
	};
}
