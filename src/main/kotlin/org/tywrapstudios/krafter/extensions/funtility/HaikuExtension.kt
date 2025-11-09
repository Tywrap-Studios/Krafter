@file:Suppress("MagicNumber", "ReturnCount")

package org.tywrapstudios.krafter.extensions.funtility

import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.event.message.MessageCreateEvent
import dev.kordex.core.DISCORD_PINK
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.event
import org.tywrapstudios.krafter.LOGGING
import org.tywrapstudios.krafter.haikuAbbreviationsConfig

class HaikuExtension : Extension() {
	override val name: String = "krafter.haiku"

	init {
		ABBREVIATIONS.putAll(haikuAbbreviationsConfig().abbreviations)
	}

	override suspend fun setup() {
		event<MessageCreateEvent> {
			action {
				if (event.message.author?.isBot == true) return@action

				val content = event.message.content

				val haiku = detectHaiku(content)

				if (haiku != null) {
					event.message.channel.createEmbed {
						description = haiku.lines.joinToString("\n\n", transform = {
							val upperCaseFirstLetter = it.replaceFirstChar { char -> char.uppercaseChar() }
							"*$upperCaseFirstLetter*"
						})

						color = DISCORD_PINK

						footer {
							text = "- A Haiku by ${event.message.author?.username ?: "Unknown"}"
						}
					}
				}
			}
		}
	}

	/**
	 * Scans the provided text for a haiku (5-7-5 syllable thing).
	 * The algorithm only detects haiku's that use the entire message from start to finish (I think).
	 *
	 * @param text The message content to analyze
	 * @return A Haiku object if found, null otherwise
	 */
	private fun detectHaiku(text: String): Haiku? {
		val words = text.trim()
			.replace(Regex("[^a-zA-Z0-9\\s'-]"), "")
			.split(Regex("\\s+"))
			.filter { it.isNotBlank() }
			.map { ABBREVIATIONS.getOrElse(it) { it } }

		if (words.isEmpty()) return null

		val targetPattern = listOf(5, 7, 5)

		for (startIdx in words.indices) {
			val result = tryBuildHaiku(words, targetPattern, startIdx)
			// Only accept if we found a valid haiku AND it uses all words in the message
			// No weird Frankensteining of haiku's from partial messages
			if (result != null && result.usedAllWords) {
				return result.haiku
			}
		}

		return null
	}

	/**
	 * Attempts to build a haiku starting from a specific word index.
	 * Greedily consumes words to match each line's syllable requirement.
	 *
	 * @param words The list of words from the message
	 * @param pattern The syllable pattern to match ([5, 7, 5])
	 * @return A HaikuResult object if successful, null if the pattern cannot be matched
	 */
	private fun tryBuildHaiku(words: List<String>, pattern: List<Int>, startIdx: Int = 0): HaikuResult? {
		val lines = mutableListOf<String>()
		var currentWordIdx = startIdx

		for (targetSyllables in pattern) {
			val line = mutableListOf<String>()
			var syllableCount = 0

			while (currentWordIdx < words.size && syllableCount < targetSyllables) {
				val word = words[currentWordIdx]
				val wordSyllables = countSyllables(word)

				if (syllableCount + wordSyllables <= targetSyllables) {
					line.add(word)
					syllableCount += wordSyllables
					currentWordIdx++
				} else {
					break
				}
			}

			if (syllableCount != targetSyllables) return null

			lines.add(line.joinToString(" "))
		}

		return if (lines.size == 3) {
			HaikuResult(
				haiku = Haiku(lines),
				usedAllWords = currentWordIdx == words.size
			)
		} else {
			null
		}
	}

	/**
	 * Counts the number of syllables in a word using English language heuristics.
	 * This is an approximation as perfect syllable counting can depend on accents, regions and whatnot.
	 * Also, sometimes things get flagged as having more or fewer syllables
	 * than they actually do (e.g., "fire" can get seen as one syllable, and "bothered" can be three
	 * (obviously I fixed these specific examples, hard to have examples of things
	 * that are wrong if you immediately fix them)).
	 *
	 * Basic "rules":
	 * 1. Count the vowel groups (consecutive vowels count as one "syllable")
	 * 2. Some silent vowel groups at the end don't count if there are other syllables (e.g., "make": silent 'e')
	 * 3. Correct the aforementioned silent groups for certain endings that should count (e.g., "table": the 'le' ending)
	 * 4. Every word has at least 1 syllable (this is for when you have weird inputs)
	 *
	 * @param word The word to count syllables for
	 * @return The estimated number of syllables (minimum 1)
	 */
	private fun countSyllables(word: String): Int {
		// Only empty words have no syllables
		if (word.isEmpty()) return 0

		val lowerWord = word.lowercase().replace(Regex("[^a-z]"), "")
		// Words that were previously full but now empty had at least one syllable!
		// (Rule 4)
		if (lowerWord.isEmpty()) return 1

		var count = 0
		var previousWasVowel = false

		for (i in lowerWord.indices) {
			val char = lowerWord[i]
			val isVowel = char in "aeiouy"

			if (isVowel && !previousWasVowel) {
				count++
			}

			previousWasVowel = isVowel
		}

		/**
		 * Helper function to add back a syllable if the word ends with a specific
		 * consonant + ending combination.
		 *
		 * @param consonantIndex The index from the end of the word where the consonant should be
		 * @param ending The ending string to check for
		 */
		fun forceAdd(ending: String) {
			if (lowerWord.length >= (ending.length + 1) &&
				lowerWord[lowerWord.length - (ending.length + 1)] !in "aeiouy" &&
				lowerWord.endsWith(ending)
			) {
				count++
			}
		}

		/**
		 * Helper function to remove a syllable if the word ends with a specific ending.
		 *
		 * @param ending The ending string to check for
		 */
		fun forceRemove(ending: String) {
			if (lowerWord.length >= (ending.length + 1) &&
				lowerWord[lowerWord.length - (ending.length + 1)] !in "aeiouy" &&
				lowerWord.endsWith(ending)
			) {
				count--
			}
		}

		// Handle the endings rule
		if (count > 1) {
			// e.g. "make", "bake", "time"
			forceRemove("e")
			// e.g. "bothered", "catered"
			forceRemove("ered")
			// e.g. "flames", "makes"
			forceRemove("es")
			// e.g. "table", "bottle"
			forceAdd("le")
		}

		val result = maxOf(count, 1)
		LOGGING.debug("Haiku: $word = $result syllables (counted $count before maxOf)")
		return result
//		return maxOf(count, 1)
	}

	data class Haiku(val lines: List<String>)

	private data class HaikuResult(val haiku: Haiku, val usedAllWords: Boolean)
}
