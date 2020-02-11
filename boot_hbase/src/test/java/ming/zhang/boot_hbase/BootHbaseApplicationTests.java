package ming.zhang.boot_hbase;

import ming.zhang.boot_hbase.service.impl.HBaseService;
import org.apache.hadoop.hbase.client.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class BootHbaseApplicationTests {

    @Autowired
    private HBaseService hBaseService;

    @Test
    void contextLoads() {
//        List<Result> t1 = hBaseService.getRowKeyAndColumn("t1", "2d8a3b16-98a1-4440-a882-7a037bb6b0b0", "2d8a3b16-98a1-4440-a882-7a037bb6b0b0", null, null);
//        System.out.println(t1);
        List<String> rowKeys = new ArrayList<>();
        rowKeys.add("1");
        List<Result> listRowkeyData = hBaseService.getListRowkeyData("student", rowKeys, "info", "name");
        listRowkeyData.forEach(item -> {
            System.out.println(item);
        });
    }

}
