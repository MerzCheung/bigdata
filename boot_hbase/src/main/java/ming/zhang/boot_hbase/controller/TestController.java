package ming.zhang.boot_hbase.controller;

import ming.zhang.boot_hbase.service.impl.HBaseService;
import org.apache.hadoop.hbase.client.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.hadoop.hbase.client.HTableInterface;
import java.util.List;

/**
 * @author merz
 * @Description:
 */
@RestController
public class TestController {

    @Autowired
    private HBaseService hBaseService;

    @RequestMapping("/test")
    public void test() {
        List<Result> t1 = hBaseService.getRowKeyAndColumn("t1", "2d8a3b16-98a1-4440-a882-7a037bb6b0b0", "2d8a3b16-98a1-4440-a882-7a037bb6b0b0", null, null);
        System.out.println(t1);
    }
}
