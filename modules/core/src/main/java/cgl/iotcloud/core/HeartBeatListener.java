package cgl.iotcloud.core;

import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.update.UpdateMessage;
import cgl.iotcloud.core.sensor.SCSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * We will listen for the heartbeats sent by the sensors to figure out weather they are alive,
 * The heartbeat interval will be sent to the sensor when they register to the sensor cloud.
 * A sensor should send a status message periodically over the update channel to indicate its aliveness,
 */
public class HeartBeatListener implements ManagedLifeCycle {
    private Logger log = LoggerFactory.getLogger(HeartBeatListener.class);

    /** Information about the registered sensors */
    private SensorCatalog catalog;
    /** A task to un-register the sensors that we haven't heard from a while */
    private CleanupTask task;

    private final ScheduledExecutorService fScheduler;

    private List<List<SCSensor>> heartBeatListener = new ArrayList<List<SCSensor>>();

    public HeartBeatListener(SensorCatalog catalog) {
        this.catalog = catalog;
        
        task = new CleanupTask();

        fScheduler = Executors.newScheduledThreadPool(1);
    }

    public boolean onUpdateMessage(SensorMessage message) {
        if (message instanceof UpdateMessage) {

        }
        return false;
    }

    @Override
    public void init() {
        fScheduler.scheduleWithFixedDelay(task, 1000, 10000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void destroy() {

    }

    private class CleanupTask implements Runnable {
        @Override
        public void run() {

        }
    }
    
}
