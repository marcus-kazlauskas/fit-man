package fit.man.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AppThreadPoolConfig {
    @Bean(name = "analysisExecutor")
    public Executor analysisExecutor(AppProperties appProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(appProperties.activityScheduler().threadPoolSize());
        executor.setMaxPoolSize(appProperties.activityScheduler().threadPoolSize());
        executor.setQueueCapacity(appProperties.activityScheduler().batchSize() * 4);
        executor.setThreadNamePrefix("AnalysisExecutor-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(appProperties.activityScheduler().timeout() * 4);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}