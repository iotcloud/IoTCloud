package cgl.iotcloud.core;

import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.update.UpdateMessage;
import cgl.iotcloud.core.sensor.SCSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
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
    private static Logger log = LoggerFactory.getLogger(HeartBeatListener.class);

    /** Information about the registered sensors */
    private SensorCatalog catalog;
    /** A task to un-register the sensors that we haven't heard from a while */
    private CleanupTask task;

    private final long maxFaultyTime;

    private final long factor;

    private final ScheduledExecutorService fScheduler;

    private final List<HeartBeat> faultySensors = new ArrayList<HeartBeat>();
    
    private final List<HeartBeat> aliveSensors = new ArrayList<HeartBeat>();

    private final IoTCloud ioTCloud;

    public HeartBeatListener(SensorCatalog catalog, IoTCloud ioTCloud) {
        this(catalog, ioTCloud, 120000, 2);
    }

    public HeartBeatListener(SensorCatalog catalog, IoTCloud ioTCloud, long maxFaultyTime, long factor) {
        this.maxFaultyTime = maxFaultyTime;
        this.factor = factor;
        this.ioTCloud = ioTCloud;
        this.catalog = catalog;

        fScheduler = Executors.newScheduledThreadPool(1);
        task = new CleanupTask();
    }

    public boolean onUpdateMessage(SensorMessage message) {
        if (message instanceof UpdateMessage) {
            UpdateMessage updateMessage = (UpdateMessage) message;

            String status = updateMessage.getUpdate(Constants.Updates.STATUS);
            if (status.equalsIgnoreCase(Constants.Updates.ALIVE)) {
                String id = updateMessage.getId();
                if (log.isDebugEnabled()) {
                    log.debug("Received a Heartbeat from the sensor ID: " + id);
                }
                // mark the sensor as alive
                sensorAlive(id);
                
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
     * @param id sensor to be removed
     */
    public void removeSensor(String id) {
        HeartBeat h = new HeartBeat(0, id);
        aliveSensors.remove(h);

        faultySensors.remove(h);
    }

    /**
     * Add a sensor so that we can keep track of it
     *
     * @param id keep track of the sensor
     */
    public void addSensor(String id) {
        aliveSensors.add(new HeartBeat(System.currentTimeMillis(), id));
    }

    @Override
    public void destroy() {
        if (!fScheduler.isShutdown()) {
            fScheduler.shutdown();

            try {
                fScheduler.awaitTermination(15000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                handleException("Interruption while awaiting termination of heartbeat scheduler", e);
            }
        }
    }
    
    private void sensorAlive(String id) {
        HeartBeat heartBeat = new HeartBeat(System.currentTimeMillis(), id);
        boolean found = false;

        // first see weather the sensor is in alive list
        for (HeartBeat h : aliveSensors) {
            if (h.equals(heartBeat)) {
                h.setTime(System.currentTimeMillis());
                found = true;
            }
        }

        // if not in alive list it can be in the faulty sensors list
        if (!found) {

            if (faultySensors.contains(heartBeat)) {
                // remove the sensor from the list
                faultySensors.remove(heartBeat);

                aliveSensors.add(heartBeat);
                found = true;
            }
        }

        // if not found in any of the lists, add it to the alive list
        if (!found) {
            aliveSensors.add(new HeartBeat(System.currentTimeMillis(), id));
        }
    }

    private class CleanupTask implements Runnable {
        @Override
        public void run() {
            Iterator<HeartBeat> it = aliveSensors.iterator();

            while (it.hasNext()) {
                HeartBeat h = it.next();
                long duration = System.currentTimeMillis() - h.getTime();
                // we have the time limit exceeded
                if (duration > h.getNextFaultyTime()) {
                    // put the sensor as it is to the faulty list
                    it.remove();

                    h.setNextFaultyTime(h.getNextFaultyTime() * factor);
                    faultySensors.add(h);

                }
            }

            Iterator<HeartBeat> itf = faultySensors.iterator();
            while (itf.hasNext()) {
                HeartBeat h = itf.next();

                long duration = System.currentTimeMillis() - h.getTime();
                // we have the time limit exceeded
                if (duration > h.getNextFaultyTime() && duration < maxFaultyTime) {
                    h.setNextFaultyTime(h.getNextFaultyTime() * factor);

                    faultySensors.add(h);
                } else if (duration > h.getNextFaultyTime() && duration > maxFaultyTime) {
                    // remove the sensor from our lists
                    itf.remove();
                    // un-register the sensor
                    ioTCloud.unRegisterSensor(h.getId());
                }
            }
        }
    }
    
    private class HeartBeat {
        private String id;
        
        private long time;

        private long nextFaultyTime = 30000;

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

        public void setTime(long time) {
            this.time = time;
        }

        public long getNextFaultyTime() {
            return nextFaultyTime;
        }

        public void setNextFaultyTime(long nextFaultyTime) {
            this.nextFaultyTime = nextFaultyTime;
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

    protected static void handleException(String s, Exception e) {
        log.error(s, e);
        throw new SCException(s, e);
    }
    
}
