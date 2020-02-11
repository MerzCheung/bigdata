package ming.zhang.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author merz
 * @Description:
 */
@Component
public class KafkaProducter {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, String value) {
        kafkaTemplate.send(topic, value);
    }
}
