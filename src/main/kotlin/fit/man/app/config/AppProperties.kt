package fit.man.app.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "fit-man", ignoreInvalidFields = true)
data class AppProperties(
    val activityScheduler: ActivityScheduler =
        ActivityScheduler(
            fixedRate = "PT1M",
            initialDelay = "PT30S",
            batchSize = 1,
            maxSpeed = 60f,
            threadPoolSize = 2,
            timeout = 5,
        ),
) {
    data class ActivityScheduler(
        val fixedRate: String,
        val initialDelay: String,
        val batchSize: Int,
        val maxSpeed: Float,
        val threadPoolSize: Int,
        val timeout: Int,
    )
}
