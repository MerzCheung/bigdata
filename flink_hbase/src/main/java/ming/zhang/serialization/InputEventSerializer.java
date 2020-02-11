package ming.zhang.serialization;

import com.alibaba.fastjson.JSON;
import ming.zhang.events.MonitoringEvent;
import org.apache.flink.api.common.serialization.SerializationSchema;

/**
 * @author merz
 * @Description:
 */
public class InputEventSerializer implements SerializationSchema<MonitoringEvent> {

    @Override
    public byte[] serialize(MonitoringEvent temperatureEvent) {
        return JSON.toJSONBytes(temperatureEvent);
    }
}
