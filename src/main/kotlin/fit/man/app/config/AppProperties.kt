package fit.man.app.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "fit-man", ignoreInvalidFields = true)
data class AppProperties(
    @get:JvmName("activityScheduler")
    val activityScheduler: ActivityScheduler = ActivityScheduler(
        fixedRate = "PT1M",
        initialDelay = "PT30S",
        batchSize = 1,
        maxSpeed = 60f,
        threadPoolSize = 2,
        timeout = 5
    )
) {
    data class ActivityScheduler(
        @get:JvmName("fixedRate") val fixedRate: String,
        @get:JvmName("initialDelay") val initialDelay: String,
        @get:JvmName("batchSize") val batchSize: Int,
        @get:JvmName("maxSpeed") val maxSpeed: Float,
        @get:JvmName("threadPoolSize") val threadPoolSize: Int,
        @get:JvmName("timeout") val timeout: Int
    )
}
