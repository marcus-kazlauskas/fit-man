package fit.man.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fit-man", ignoreInvalidFields = true)
public record GlobalProperties(
        ActivityScheduler activityScheduler
) {
    public record ActivityScheduler(
            String fixedRate,
            int batchSize,
            float maxSpeed
    ) {}
}
