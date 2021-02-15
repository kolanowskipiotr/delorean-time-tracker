package pko.unity.time.tracker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class UnityTimeTrackerApplication

fun main(args: Array<String>) {
    runApplication<UnityTimeTrackerApplication>(*args)
}
