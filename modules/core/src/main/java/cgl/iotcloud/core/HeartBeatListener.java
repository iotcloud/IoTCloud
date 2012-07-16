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

    private List<List<HeartBeat>> faultySensors = new ArrayList<List<HeartBeat>>();
    
    private List<HeartBeat> aliveSensors = new ArrayList<HeartBeat>();

    public HeartBeatListener(SensorCatalog catalog) {
        this.catalog = catalog;
        
        task = new CleanupTask();

        fScheduler = Executors.newScheduledThreadPool(1);
    }

    public boolean onUpdateMessage(SensorMessage message) {
        if (message instanceof UpdateMessage) {
            UpdateMessage updateMessage = (UpdateMessage) message;

            String status = updateMessage.getUpdate(Constants.Updates.STATUS);
            if (status.equalsIgnoreCase(Constants.Updates.ALIVE)) {
                String id = updateMessage.getId();

                
                
                return true;
            }
        }
        return false;
    }

    @Override
    public void init() {
        // put all the sensors to the fixed queue
        for (SCSensor scSensor : catalog.getSensors()) {
            aliveSensors.add(new HeartBeat(System.currentTimeMillis(), scSensor.getId()));
        }
        
        fScheduler.scheduleWithFixedDelay(task, 1000, 10000, TimeUnit.MILLISECONDS);
    }

    /**
     * Remove a sensor from the system so that we no longer keep track of it
     *
     * @param scSensor sensor to be removed
     */
    public void removeSensor(SCSensor scSensor) {
        HeartBeat h = new HeartBeat(0, scSensor.getId());
        aliveSensors.remove(h);

        for (List<HeartBeat> list : faultySensors) {
            list.remove(h);
        }
    }

    /**
     * Add a sensor so that we can keep track of it
     *
     * @param scSensor keep track of the sensor
     */
    public void addSensor(SCSensor scSensor) {
        aliveSensors.add(new HeartBeat(System.currentTimeMillis(), scSensor.getId()));
    }

    @Override
    public void destroy() {
        if (!fScheduler.isShutdown()) {
            fScheduler.shutdown();
        }
    }
    
    private void sensorAlive(String id) {
        HeartBeat heartBeat = new HeartBeat(0, id);
        for (List<HeartBeat> sensorList : faultySensors) {
            if (sensorList.contains(heartBeat)) {
                // remove the sensor from the list
                sensorList.remove(heartBeat);
            }
        }
    }

    private class CleanupTask implements Runnable {
        @Override
        public void run() {

        }
    }
    
    private class HeartBeat {
        private String id;
        
        private long time;

        private HeartBeat(long time, String id) {
            this.time = time;
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public long getTime() {
            return time;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            HeartBeat heartBeat = (HeartBeat) o;

            return !(id != null ? !id.equals(heartBeat.id) : heartBeat.id != null);

        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }
    
}
