package ming.zhang.boot_hdfs;

import ming.zhang.boot_hdfs.config.HdfsConfig;
import ming.zhang.boot_hdfs.util.HdfsUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BootHdfsApplicationTests {

    @Test
    void contextLoads() {
        HdfsConfig config = new HdfsConfig("192.168.146.154", "8020", "root");
//        String source = "D:\\bigdata\\boot_hdfs\\src\\main\\resources\\test_file\\test02.txt";
//        String destination = "hdfs://192.168.146.154:8020/test/test02.txt";
//        HdfsUtil.upload(config, source, destination);
        String source = "hdfs://192.168.146.154:8020/test/test02.txt";
        String destination = "D:\\bigdata3\\boot_hdfs\\src\\main\\resources\\test_file\\test03.txt";
        HdfsUtil.download(config, source, destination);
    }

}
