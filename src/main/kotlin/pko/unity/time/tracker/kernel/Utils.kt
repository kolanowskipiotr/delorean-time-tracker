package pko.unity.time.tracker.kernel

import org.apache.commons.lang3.StringUtils
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors

class Utils {
    companion object {

        val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault())
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(ZoneId.systemDefault())

        fun formatTime(instatnt: Instant?): String? {
            if (instatnt != null) {
                return TIME_FORMATTER.format(instatnt)
            }
            return StringUtils.EMPTY
        }

        fun buildDateTimeInstant(date: LocalDate, time: Instant): Instant? {
            return date.atStartOfDay().atZone(ZoneId.systemDefault())
                .withHour(time.atZone(ZoneId.systemDefault()).hour)
                .withMinute(time.atZone(ZoneId.systemDefault()).minute)
                .toInstant()
        }

        fun buildDateTimeInstant(date: LocalDate, time: String): Instant? {
            val timeParts =
                Arrays.stream(time.trim { it <= ' ' }.split(":".toRegex()).toTypedArray())
                    .map { it: String -> it.toInt() }
                    .collect(Collectors.toList())
            return date.atStartOfDay().atZone(ZoneId.systemDefault())
                .withHour(timeParts[0])
                .withMinute(timeParts[1])
                .toInstant()
        }

        fun buildDateTimeInstantEndOfDay(date: LocalDate): Instant? {
            return date.atStartOfDay().atZone(ZoneId.systemDefault())
                .withHour(23)
                .withMinute(59)
                .toInstant()
        }
    }
}