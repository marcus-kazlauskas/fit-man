package fit.man.app.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor
import java.util.concurrent.ThreadPoolExecutor

@Configuration
class AppThreadPoolConfig {
    @Bean(name = ["analysisExecutor"])
    fun analysisExecutor(appProperties: AppProperties): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.setCorePoolSize(appProperties.activityScheduler.threadPoolSize)
        executor.setMaxPoolSize(appProperties.activityScheduler.threadPoolSize)
        executor.setQueueCapacity(appProperties.activityScheduler.batchSize * 4)
        executor.setThreadNamePrefix("AnalysisExecutor-")
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(appProperties.activityScheduler.timeout * 4)
        executor.setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
        return executor
    }
}
