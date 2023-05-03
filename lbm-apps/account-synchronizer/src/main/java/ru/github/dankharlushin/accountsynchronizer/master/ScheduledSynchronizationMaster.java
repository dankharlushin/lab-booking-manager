package ru.github.dankharlushin.accountsynchronizer.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import ru.github.dankharlushin.accountsynchronizer.worker.SynchronizationWorker;

public class ScheduledSynchronizationMaster {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledSynchronizationMaster.class);

    private final SynchronizationWorker synchronizationWorker;

    public ScheduledSynchronizationMaster(final SynchronizationWorker synchronizationWorker) {
        this.synchronizationWorker = synchronizationWorker;
    }

    @Scheduled(fixedDelayString = "${account-synchronizer.synchronization.scheduled.fixed-delay-ms}")
    public void synchronize() {
        synchronizationWorker.work();
    }
}
