# Funtility

The Funtility module contains fun utility functions and commands that aren't 
necessarily useful, but still nice to play around with.

### Bean
No explanation

### Haiku
A Haiku is a traditional Japanese form of poetry, consisting of three lines.
The first line has five syllables, the second seven, and the third five again.

::: warning A SMALL DISCLAIMER...
This module makes a grave assumption on how many syllables a word contains, it won't always
be correct and can't always be correct due to differences in accents and pronunciations.  
On top of this, it tends to find Haiku's in extremely long sentences, without there really
being a concrete "poem".
:::
::: tip
The module tries to parse some "common" abbreviations into their full forms, if you're missing
an abbreviation, either contribute to the [`HaikuConstants.kt`](https://github.com/Tywrap-Studios/Krafter/edit/master/src/main/kotlin/org/tywrapstudios/krafter/extensions/funtility/HaikuConstants.kt)
file or manually add them using the provided [config file](/config/ext-funtility#haiku-abbreviations).
:::

### Starboard
A starboard is a special channel that members can post messages in once it receives a lot of positive
reactions, in this case, a literal emoji reaction to the message in a public chat using `⭐`.

Once a message enters the starboard it won't be removed from it, even when the reaction count
were to go below the minimum again.
