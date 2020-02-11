package ming.zhang;


import com.alibaba.fastjson.JSONObject;
import ming.zhang.events.MonitoringEvent;
import org.apache.commons.lang3.RandomUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * @author merz
 * @Description:
 */
public class HBaseWriter extends RichSinkFunction<MonitoringEvent> {

    private static org.apache.hadoop.conf.Configuration configuration;
    private static Connection connection = null;
    private static BufferedMutator mutator;
    private static String TABLENAME = "t1";
    private static int count = 0;


    @Override
    public void open(Configuration parameters) throws Exception {
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.master", "192.168.146.151:60000");
        configuration.set("hbase.zookeeper.quorum", "192.168.146.151,192.168.146.152,192.168.146.153");
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        try {
            connection = ConnectionFactory.createConnection(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedMutatorParams params = new BufferedMutatorParams(TableName.valueOf(TABLENAME));
        params.writeBufferSize(2 * 1024 * 1024);
        mutator = connection.getBufferedMutator(params);
    }


    @Override
    public void close() throws IOException {
        if (mutator != null) {
            mutator.flush();
            mutator.close();
        }
        if (connection != null) {
            connection.close();
        }
    }


    @Override
    public void invoke(MonitoringEvent values, Context context) throws Exception {
//        DeviceData deviceData = JSONObject.parseObject(values, DeviceData.class);
//        long unixTimestamp = 0;
//        try {
//            String gatherTime = deviceData.getGatherTime();
//            //毫秒和秒分开处理
//            if (gatherTime.length() > 20) {
//                long ms = Long.parseLong(gatherTime.substring(20, 23));
//                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(gatherTime);
//                unixTimestamp = date.getTime() + ms;
//            } else {
//                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(gatherTime);
//                unixTimestamp = date.getTime();
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        String RowKey = deviceData.getMachID() + String.valueOf(unixTimestamp);
//        String Key = deviceData.getOperationValue();
//        String Value = deviceData.getOperationData();
//        System.out.println("Column Family=f1,  RowKey=" + RowKey + ", Key=" + Key + " ,Value=" + Value);
        Put put = new Put(UUID.randomUUID().toString().getBytes());
        put.addColumn("f1".getBytes(), UUID.randomUUID().toString().getBytes(), values.toString().getBytes());
        mutator.mutate(put);
        //每满500条刷新一下数据
        mutator.flush();
//        if (count >= 500) {
//            mutator.flush();
//            count = 0;
//        }
//        count = count + 1;
    }
}
