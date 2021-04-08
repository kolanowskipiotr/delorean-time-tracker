package pko.delorean.time.tracker.kernel

import org.apache.commons.lang3.StringUtils
import pko.delorean.time.tracker.domain.WorkLog
import pko.delorean.time.tracker.kernel.Utils.Companion.buildDateTimeInstant
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.function.BinaryOperator
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


        fun sumDurations(workLogs: Collection<WorkLog>, workDayDate: LocalDate): Long =
            workLogs
                .map{ workLogDuration(it, workDayDate) }
                .sum()


        fun workLogDuration(worklog: WorkLog, workDayDate: LocalDate): Long {
            return worklog.getDuration(
                buildDateTimeInstant(
                    workDayDate,
                    Instant.now().truncatedTo(ChronoUnit.MINUTES)
                )
            )
        }
    }
}