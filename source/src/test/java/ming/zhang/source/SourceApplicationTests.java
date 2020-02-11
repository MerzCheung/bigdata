package ming.zhang.source;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

@Slf4j
@SpringBootTest
class SourceApplicationTests {

    @Autowired
    private KafkaProducter kafkaProducter;

    @Test
    public void json() {
        MonitoringEvent temperatureEvent = new TemperatureEvent(1, 200);
        String s = JSON.toJSONString(temperatureEvent);
        System.out.println(s);
        TemperatureEvent temperatureEvent1 = JSON.parseObject(s, TemperatureEvent.class);
        System.out.println(temperatureEvent1);
    }

    @Test
    void test2() {
        Integer[] arr = {120, 140, 110, 105, 50, 30, 110, 50, 120, 130, 50, 150, 200, 60};
//        Integer[] arr2 = {120, 140, 220, 210, 50, 30, 110, 50, 120, 130, 50, 150, 200, 60};
        for (int i = 0; i < arr.length; i++) {
            MonitoringEvent temperatureEvent1 = new TemperatureEvent(1, arr[i]);
            log.info("发送消息A：{}", JSON.toJSONString(temperatureEvent1));
            kafkaProducter.send("hello", JSON.toJSONString(temperatureEvent1));
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void contextLoads() {
        Random r = new Random();
        while (true) {
            MonitoringEvent temperatureEvent1 = new TemperatureEvent(1, r.nextInt(150));
            log.info("发送消息A：{}", JSON.toJSONString(temperatureEvent1));
            kafkaProducter.send("hello", JSON.toJSONString(temperatureEvent1));

            MonitoringEvent temperatureEvent2 = new TemperatureEvent(2, r.nextInt(150));
            log.info("发送消息B：{}", JSON.toJSONString(temperatureEvent2));
            kafkaProducter.send("hello", JSON.toJSONString(temperatureEvent2));

            MonitoringEvent temperatureEvent3 = new TemperatureEvent(3, r.nextInt(150));
            log.info("发送消息C：{}", JSON.toJSONString(temperatureEvent3));
            kafkaProducter.send("hello", JSON.toJSONString(temperatureEvent3));
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
