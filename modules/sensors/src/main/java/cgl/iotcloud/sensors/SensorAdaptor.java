package cgl.iotcloud.sensors;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.ManagedLifeCycle;
import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.broker.JMSSender;
import cgl.iotcloud.core.broker.JMSSenderFactory;
import cgl.iotcloud.core.message.update.MessageToUpdateFactory;
import cgl.iotcloud.core.message.update.UpdateMessage;
import cgl.iotcloud.core.sensor.Sensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SensorAdaptor {
    private static Logger log = LoggerFactory.getLogger(RegistrationWSClient.class);

    private Sensor sensor = null;

    private RegistrationClient client = null;

    private JMSSender heartBeatSender = null;

    private ScheduledExecutorService fScheduler;

    public SensorAdaptor(String url) {
        client = new RegistrationWSClient(url + "/soap/services/SensorRegistrationService");
    }

    public SensorAdaptor(String host, int port, boolean rest) {
        if (rest) {
            client = new RegistrationHttpClient(host, port, false);
        } else {
            client = new RegistrationWSClient("http://" + host + ":" + port);
        }

        fScheduler = Executors.newScheduledThreadPool(1);
    }

    public SensorAdaptor(String host, int port) {
        this(host, port, false);
    }

    public void registerSensor(String name, String type, String sensorClass) {                
        try {
            Class c = Class.forName(sensorClass);            
            Object o = c.newInstance();
            if (o instanceof Sensor) {
                Sensor s = (Sensor) o;
                s.setName(name);
                s.setType(type);
                client.registerSensor(s);                
                
                sensor = s;
            }
        } catch (ClassNotFoundException e) {
            handleException("Failed to locate the class: " + sensorClass, e);
        } catch (InstantiationException e) {
            handleException("Failed to intialize the class: " + sensorClass, e);
        } catch (IllegalAccessException e) {
            handleException("Access restricted for the class: " + sensorClass, e);
        }
    }

    public void registerSensor(Sensor sensor) {
        client.registerSensor(sensor);               

        this.sensor = sensor;
    }

    public void start() {
        if (sensor instanceof ManagedLifeCycle)  {
            ((ManagedLifeCycle) sensor).init();
        }
        if (sensor.getUpdateEndpoint() != null) {
            heartBeatSender = new JMSSenderFactory().create(sensor.getUpdateEndpoint());
            heartBeatSender.setMessageFactory(new MessageToUpdateFactory());
            
            heartBeatSender.init();
            heartBeatSender.start();

            fScheduler.scheduleAtFixedRate(new HeartBeatTask(), 1000, 15000, TimeUnit.MILLISECONDS);
        }
    }

    public void stop() {
        fScheduler.shutdown();

        try {
            fScheduler.awaitTermination(15000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            handleException("Interruption while awaiting termination of heartbeat scheduler", e);
        }

        client.unRegisterSensor(sensor);

        if (sensor instanceof ManagedLifeCycle) {
            ((ManagedLifeCycle) sensor).destroy();
        }
    }

    private class HeartBeatTask implements Runnable {
        @Override
        public void run() {
            UpdateMessage updateMessage = new UpdateMessage(sensor.getId());
            updateMessage.addUpdate(Constants.Updates.STATUS, Constants.Updates.ALIVE);
            heartBeatSender.send(updateMessage);
        }
    }

    protected static void handleException(String s, Exception e) {
        log.error(s, e);
        throw new SCException(s, e);
    }

    protected static void handleException(String s) {
        log.error(s);
        throw new SCException(s);
    }
}
