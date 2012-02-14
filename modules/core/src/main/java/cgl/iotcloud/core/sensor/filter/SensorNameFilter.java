package cgl.iotcloud.core.sensor.filter;

import cgl.iotcloud.core.IoTCloud;
import cgl.iotcloud.core.sensor.AbstractSensorFilter;
import cgl.iotcloud.core.sensor.FilterCriteria;
import cgl.iotcloud.core.sensor.SCSensor;
import cgl.iotcloud.core.sensor.Sensor;
import cgl.iotcloud.core.sensor.SensorException;

import java.util.ArrayList;
import java.util.List;

public class SensorNameFilter extends AbstractSensorFilter {

    public SensorNameFilter(IoTCloud iotCloud) {
        super(iotCloud);
    }

    @Override
    public List<SCSensor> filter(FilterCriteria filterCriteria) throws SensorException {
        List<SCSensor> list = iotCloud.getSensorCatalog().getSensors();

        List<SCSensor> filteredList = new ArrayList<SCSensor>();

        String name = filterCriteria.get("name");

        if (name == null) {
            handleException("name attribute should be present in the Filter Definition");
        }

        for (SCSensor s : list) {
            if (s.getName().equals(name)) {
                filteredList.add(s);
            }
        }

        return filteredList;
    }
}
