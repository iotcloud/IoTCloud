package cgl.iotcloud.core.sensor;

import cgl.iotcloud.core.Filter;
import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.IoTCloud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractSensorFilter implements Filter {
    protected Logger log;

    protected IoTCloud iotCloud;

    public AbstractSensorFilter(IoTCloud iotCloud) {
        log = LoggerFactory.getLogger(this.getClass());

        this.iotCloud = iotCloud;
    }

    public abstract List<SCSensor> filter(FilterCriteria filterCriteria) throws SensorException;

    protected void handleException(String s) throws SensorException {
        log.error(s);
        throw new SensorException(s);
    }
}
