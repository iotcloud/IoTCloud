package cgl.iotcloud.core.sensor.filter;

import cgl.iotcloud.core.IoTCloud;
import cgl.iotcloud.core.sensor.AbstractSensorFilter;
import cgl.iotcloud.core.sensor.FilterCriteria;
import cgl.iotcloud.core.sensor.SCSensor;
import cgl.iotcloud.core.sensor.Sensor;
import cgl.iotcloud.core.sensor.SensorException;

import java.util.ArrayList;
import java.util.List;

/**
 * Filter Sensors based on the type attribute of a filter
 */
public class SensorTypeFilter extends AbstractSensorFilter {
    public SensorTypeFilter(IoTCloud iotCloud) {
        super(iotCloud);
    }

    @Override
    public List<SCSensor> filter(FilterCriteria filterCriteria) throws SensorException {
        List<SCSensor> list = iotCloud.getSensorCatalog().getSensors();

        String type = filterCriteria.get("type");

        if (type == null) {
            handleException("type attribute should be present in the Filter Definition");
        }

        List<SCSensor> filteredList = new ArrayList<SCSensor>();
        for (SCSensor s : list) {
            if (s.getType().equals(type)) {
                filteredList.add(s);
            }
        }

        return filteredList;
    }
}
