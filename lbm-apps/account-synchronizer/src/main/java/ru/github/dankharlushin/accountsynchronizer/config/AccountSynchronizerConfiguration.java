package ru.github.dankharlushin.accountsynchronizer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.github.dankharlushin.accountsynchronizer.master.DisposableSynchronizationMaster;
import ru.github.dankharlushin.accountsynchronizer.master.ScheduledSynchronizationMaster;
import ru.github.dankharlushin.accountsynchronizer.worker.SynchronizationWorker;
import ru.github.dankharlushin.lbmlib.data.config.DataLibConfig;
import ru.github.dankharlushin.lbmlib.data.service.UserService;

@Configuration
@EnableScheduling
@EnableConfigurationProperties
@Import({DataLibConfig.class})
public class AccountSynchronizerConfiguration {

    @Bean
    @ConditionalOnProperty(value = "account-synchronizer.synchronization.mode", havingValue = "disposable", matchIfMissing = true)
    public DisposableSynchronizationMaster disposableSynchronizationWorker(final SynchronizationWorker synchronizationWorker) {
        return new DisposableSynchronizationMaster(synchronizationWorker);
    }

    @Bean
    @ConditionalOnProperty(value = "account-synchronizer.synchronization.mode", havingValue = "scheduled")
    public ScheduledSynchronizationMaster scheduledSynchronizationWorker(final SynchronizationWorker synchronizationWorker) {
        return new ScheduledSynchronizationMaster(synchronizationWorker);
    }

    @Bean
    public SynchronizationWorker synchronizationWorker(final UserService userService,
                                                       @Value("${account-synchronizer.synchronization.group-name}")
                                                       final String groupName) {
        return new SynchronizationWorker(userService, groupName);
    }
}
