package ru.github.dankharlushin.accountsynchronizer.master;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import ru.github.dankharlushin.accountsynchronizer.worker.SynchronizationWorker;


public class DisposableSynchronizationMaster implements ApplicationRunner {

    private final SynchronizationWorker synchronizationWorker;

    public DisposableSynchronizationMaster(final SynchronizationWorker synchronizationWorker) {
        this.synchronizationWorker = synchronizationWorker;
    }

    @Override
    public void run(final ApplicationArguments args) {
        synchronizationWorker.work();
    }
}
