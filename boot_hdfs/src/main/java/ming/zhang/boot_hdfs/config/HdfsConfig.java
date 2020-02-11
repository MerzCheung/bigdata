package ming.zhang.boot_hdfs.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author merz
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HdfsConfig {

    // hdfs 服务器地址
    private String hostname;
    // hdfs 服务器端口
    private String port;
    // hdfs 服务器账户
    private String username;
}
