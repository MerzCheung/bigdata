package ming.zhang.serialization;

import com.alibaba.fastjson.JSON;
import ming.zhang.events.TemperatureWarning;
import org.apache.flink.api.common.serialization.SerializationSchema;

/**
 * @author merz
 * @Description:
 */
public class TemperatureWarningSerializer implements SerializationSchema<TemperatureWarning> {

    @Override
    public byte[] serialize(TemperatureWarning temperatureWarning) {
        return JSON.toJSONBytes(temperatureWarning);
    }
}
