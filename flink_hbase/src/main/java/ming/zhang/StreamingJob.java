package ming.zhang;

import ming.zhang.events.MonitoringEvent;
import ming.zhang.events.TemperatureAlert;
import ming.zhang.events.TemperatureEvent;
import ming.zhang.events.TemperatureWarning;
import ming.zhang.serialization.InputEventSerializer;
import ming.zhang.serialization.MonitoringEventDeserializer;
import ming.zhang.serialization.TemperatureAlertSerializer;
import ming.zhang.serialization.TemperatureWarningSerializer;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.IngestionTimeExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author merz
 * @Description:
 */
public class StreamingJob {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.146.154:9092");
        properties.setProperty("group.id", "test-consumer-group");

        FlinkKafkaConsumer<MonitoringEvent> consumer = new FlinkKafkaConsumer<>("hello", new MonitoringEventDeserializer(), properties);
        consumer.setStartFromGroupOffsets();
        DataStream<MonitoringEvent> inputEventStream = env.addSource(consumer).returns(MonitoringEvent.class).assignTimestampsAndWatermarks(new IngestionTimeExtractor<>());
        inputEventStream.print();
        Pattern<MonitoringEvent, ?> warningPattern = Pattern.<MonitoringEvent>begin("first")
                .subtype(TemperatureEvent.class)
                .where(new IterativeCondition<TemperatureEvent>() {
                    private static final long serialVersionUID = -6301755149429716724L;

                    @Override
                    public boolean filter(TemperatureEvent value, Context<TemperatureEvent> ctx) throws Exception {
                        return value.getTemperature() >= 100;
                    }
                })
                .next("second")
                .subtype(TemperatureEvent.class)
                .where(new IterativeCondition<TemperatureEvent>() {
                    private static final long serialVersionUID = 2392863109523984059L;

                    @Override
                    public boolean filter(TemperatureEvent value, Context<TemperatureEvent> ctx) throws Exception {
                        return value.getTemperature() >= 100;
                    }
                })
                .within(Time.seconds(10));

        // 从我们的警告模式创建一个模式流
        PatternStream<MonitoringEvent> tempPatternStream = CEP.pattern(
                inputEventStream.keyBy("rackID"),
                warningPattern);

        // 为每个匹配的警告模式生成温度警告
        DataStream<TemperatureWarning> warnings = tempPatternStream.select(
                (Map<String, List<MonitoringEvent>> pattern) -> {
                    TemperatureEvent first = (TemperatureEvent) pattern.get("first").get(0);
                    TemperatureEvent second = (TemperatureEvent) pattern.get("second").get(0);

                    return new TemperatureWarning(first.getRackID(), (first.getTemperature() + second.getTemperature()) / 2);
                }
        );


        // 警报模式:在20秒的时间间隔内出现两个连续的温度警告
        Pattern<TemperatureWarning, ?> alertPattern = Pattern.<TemperatureWarning>begin("first")
                .next("second")
                .within(Time.seconds(20));

        // 从我们的警报模式创建一个模式流
        PatternStream<TemperatureWarning> alertPatternStream = CEP.pattern(
                warnings.keyBy("rackID"),
                alertPattern);

        // 只有当第二次温度警告的平均温度高于第一次警告的温度时，才会发出温度警报
        DataStream<TemperatureAlert> alerts = alertPatternStream.flatSelect(
                (Map<String, List<TemperatureWarning>> pattern, Collector<TemperatureAlert> out) -> {
                    TemperatureWarning first = pattern.get("first").get(0);
                    TemperatureWarning second = pattern.get("second").get(0);

                    if (first.getAverageTemperature() < second.getAverageTemperature()) {
                        out.collect(new TemperatureAlert(first.getRackID()));
                    }
                },
                TypeInformation.of(TemperatureAlert.class));

        // 将警告和警报事件发送到kafka
        warnings.addSink(new FlinkKafkaProducer<>(
                "192.168.146.154:9092",
                "warnings",
                new TemperatureWarningSerializer()
        ));
        alerts.addSink(new FlinkKafkaProducer<>(
                "192.168.146.154:9092",
                "alerts",
                new TemperatureAlertSerializer()
        ));
        inputEventStream.addSink(new FlinkKafkaProducer<>(
                "192.168.146.154:9092",
                "input",
                new InputEventSerializer()
        ));
//        inputEventStream.addSink(new HBaseWriter());
        env.execute("flink_hbase");
    }
}
