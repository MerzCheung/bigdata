package ming.zhang.serialization;

import com.alibaba.fastjson.JSON;
import ming.zhang.events.TemperatureAlert;
import org.apache.flink.api.common.serialization.SerializationSchema;

/**
 * @author merz
 * @Description:
 */
public class TemperatureAlertSerializer implements SerializationSchema<TemperatureAlert> {

    @Override
    public byte[] serialize(TemperatureAlert temperatureAlert) {
        return JSON.toJSONBytes(temperatureAlert);
    }
}
