package ming.zhang.web.kafka;

import lombok.extern.slf4j.Slf4j;
import ming.zhang.web.domain.Message;
import ming.zhang.web.util.SocketSendUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author merz
 * @Description:
 */
@Slf4j
@Component
public class KafkaConsumer {

    @Autowired
    private SocketSendUtil socketSendUtil;

    /**
     * 警告
     * @param consumer
     */
    @KafkaListener(topics = "warnings")
    public void warnings(ConsumerRecord<?, ?> consumer) {
        Message warnings = Message.builder().to("123").type("warnings").text(consumer.value().toString()).build();
        socketSendUtil.sendMsg(warnings);
        log.info("{} - {}:{}", consumer.topic(), consumer.key(), consumer.value());
    }

    /**
     * 报警
     * @param consumer
     */
    @KafkaListener(topics = "alerts")
    public void alerts(ConsumerRecord<?, ?> consumer) {
        Message warnings = Message.builder().to("123").type("alerts").text(consumer.value().toString()).build();
        socketSendUtil.sendMsg(warnings);
        log.info("{} - {}:{}", consumer.topic(), consumer.key(), consumer.value());
    }

    /**
     * 温度日志
     * @param consumer
     */
    @KafkaListener(topics = "input")
    public void input(ConsumerRecord<?, ?> consumer) {
        Message warnings = Message.builder().to("123").type("input").text(consumer.value().toString()).build();
        socketSendUtil.sendMsg(warnings);
        log.info("{} - {}:{}", consumer.topic(), consumer.key(), consumer.value());
    }
}
