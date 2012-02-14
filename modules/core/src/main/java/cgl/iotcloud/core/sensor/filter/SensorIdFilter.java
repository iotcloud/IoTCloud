package cgl.iotcloud.core.sensor.filter;

import cgl.iotcloud.core.IoTCloud;
import cgl.iotcloud.core.sensor.AbstractSensorFilter;
import cgl.iotcloud.core.sensor.FilterCriteria;
import cgl.iotcloud.core.sensor.SCSensor;
import cgl.iotcloud.core.sensor.Sensor;
import cgl.iotcloud.core.sensor.SensorException;

import java.util.ArrayList;
import java.util.List;

public class SensorIdFilter extends AbstractSensorFilter{
    public SensorIdFilter(IoTCloud iotCloud) {
        super(iotCloud);
    }

    @Override
    public List<SCSensor> filter(FilterCriteria filterCriteria) throws SensorException {
        List<SCSensor> list = iotCloud.getSensorCatalog().getSensors();

        List<SCSensor> filteredList = new ArrayList<SCSensor>();
        String id = filterCriteria.get("id");

        if (id == null) {
            handleException("id attribute should be present in the Filter Definition");
        }

        for (SCSensor s : list) {
            if (s.getId().equals(id)) {
                filteredList.add(s);
            }
        }

        return filteredList;
    }
}
