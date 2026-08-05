package fit.man.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fit-man", ignoreInvalidFields = true)
public record AppProperties(
        ActivityScheduler activityScheduler
) {
    public record ActivityScheduler(
            String fixedRate,
            String initialDelay,
            int batchSize,
            float maxSpeed,
            int threadPoolSize,
            int timeout
    ) {}

    public AppProperties {
        if (activityScheduler == null) {
            activityScheduler = new ActivityScheduler(
                    "PT1M",
                    "PT30S",
                    1,
                    60,
                    2,
                    5
            );
        }
    }
}
