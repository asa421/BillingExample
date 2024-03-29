package su.salut.billingexample.extensions.android.time

import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

@Deprecated("Затычка для api < 26", ReplaceWith("java.time.Duration.Duration"))
class Duration {
    companion object {
        /**
         * Obtains a `Duration` from a text string such as `PnDTnHnMn.nS`.
         *
         *
         * This will parse a textual representation of a duration, including the
         * string produced by `toString()`. The formats accepted are based
         * on the ISO-8601 duration format `PnDTnHnMn.nS` with days
         * considered to be exactly 24 hours.
         *
         *
         * The string starts with an optional sign, denoted by the ASCII negative
         * or positive symbol. If negative, the whole period is negated.
         * The ASCII letter "P" is next in upper or lower case.
         * There are then four sections, each consisting of a number and a suffix.
         * The sections have suffixes in ASCII of "D", "H", "M" and "S" for
         * days, hours, minutes and seconds, accepted in upper or lower case.
         * The suffixes must occur in order. The ASCII letter "T" must occur before
         * the first occurrence, if any, of an hour, minute or second section.
         * At least one of the four sections must be present, and if "T" is present
         * there must be at least one section after the "T".
         * The number part of each section must consist of one or more ASCII digits.
         * The number may be prefixed by the ASCII negative or positive symbol.
         * The number of days, hours and minutes must parse to an `long`.
         * The number of seconds must parse to an `long` with optional fraction.
         * The decimal point may be either a dot or a comma.
         * The fractional part may have from zero to 9 digits.
         *
         *
         * The leading plus/minus sign, and negative values for other units are
         * not part of the ISO-8601 standard.
         *
         *
         * Examples:
         * <pre>
         * "PT20.345S" -- parses as "20.345 seconds"
         * "PT15M"     -- parses as "15 minutes" (where a minute is 60 seconds)
         * "PT10H"     -- parses as "10 hours" (where an hour is 3600 seconds)
         * "P2D"       -- parses as "2 days" (where a day is 24 hours or 86400 seconds)
         * "P2DT3H4M"  -- parses as "2 days, 3 hours and 4 minutes"
         * "P-6H3M"    -- parses as "-6 hours and +3 minutes"
         * "-P6H3M"    -- parses as "-6 hours and -3 minutes"
         * "-P-6H+3M"  -- parses as "+6 hours and -3 minutes"
        </pre> *
         *
         * @param text  the text to parse, not null
         * @return the parsed duration, not null
         * @throws RuntimeException if the text cannot be parsed to a duration
         */
        @Throws(RuntimeException::class)
        fun parse(text: CharSequence): Long {
            /** The pattern for parsing. */
            val pattern = Pattern.compile(
                "([-+]?)P(?:([-+]?[0-9]+)D)?" +
                        "(T(?:([-+]?[0-9]+)H)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)(?:[.,]([0-9]{0,9}))?S)?)?",
                Pattern.CASE_INSENSITIVE
            )

            val secondsPerDay = 86400
            val secondsPerHour = 3600
            val secondsPerMinute = 60

            Objects.requireNonNull(text, "text")
            val matcher: Matcher = pattern.matcher(text)
            if (matcher.matches()) {
                // check for letter T but no time sections
                if ("T" != matcher.group(3)) {
                    val dayMatch = matcher.group(2)
                    val hourMatch = matcher.group(4)
                    val minuteMatch = matcher.group(5)
                    val secondMatch = matcher.group(6)
                    if (dayMatch != null || hourMatch != null || minuteMatch != null || secondMatch != null) {
                        val daysAsSecs = parseNumber(dayMatch, secondsPerDay, "days")
                        val hoursAsSecs = parseNumber(hourMatch, secondsPerHour, "hours")
                        val minsAsSecs = parseNumber(minuteMatch, secondsPerMinute, "minutes")
                        val seconds = parseNumber(secondMatch, 1, "seconds")
                        return try {
                            Math.addExact(
                                daysAsSecs,
                                Math.addExact(hoursAsSecs, Math.addExact(minsAsSecs, seconds))
                            )
                        } catch (ex: ArithmeticException) {
                            throw (RuntimeException("Text cannot be parsed to a Duration: overflow").initCause(
                                ex
                            ))
                        }
                    }
                }
            }
            throw RuntimeException("Text cannot be parsed to a Duration")
        }

        private fun parseNumber(parsed: String?, multiplier: Int, errorText: String): Long {
            return try {
                // regex limits to [-+]?[0-9]+
                when (parsed.isNullOrBlank()) {
                    true -> 0
                    false -> Math.multiplyExact(parsed.toLong(), multiplier.toLong())
                }
            } catch (ex: NumberFormatException) {
                throw (RuntimeException("Text cannot be parsed to a Duration: $errorText").initCause(
                    ex
                ))
            } catch (ex: java.lang.ArithmeticException) {
                throw (RuntimeException("Text cannot be parsed to a Duration: $errorText").initCause(
                    ex
                ))
            }
        }
    }
}