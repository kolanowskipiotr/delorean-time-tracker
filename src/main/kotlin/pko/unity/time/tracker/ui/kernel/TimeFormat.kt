package pko.unity.time.tracker.ui.kernel

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.StringUtils.EMPTY
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TimeFormat {
    var TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    fun getTimeString(instatnt: Instant?): String? {
        if(instatnt != null) {
            return TIME_FORMATTER.format(instatnt)
        }
        return EMPTY
    }
}
