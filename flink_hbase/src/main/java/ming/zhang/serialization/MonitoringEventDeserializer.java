package ming.zhang.serialization;

import com.alibaba.fastjson.JSON;
import ming.zhang.events.MonitoringEvent;
import ming.zhang.events.TemperatureEvent;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

/**
 * @author merz
 * @Description:
 */
public class MonitoringEventDeserializer implements DeserializationSchema<MonitoringEvent> {
    @Override
    public MonitoringEvent deserialize(byte[] message) throws IOException {
        return JSON.parseObject(message, TemperatureEvent.class);
    }

    @Override
    public boolean isEndOfStream(MonitoringEvent monitoringEvent) {
        return false;
    }

    @Override
    public TypeInformation<MonitoringEvent> getProducedType() {
        return null;
    }
}
