package ming.zhang.web.zookeeper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author merz
 * @Description:
 * @date 2018/10/25 12:06
 */
@Component
public class ClientUtil {

    @Autowired
    private ServiceDiscovery serviceDiscovery;

    public Object getAddress() {
        return serviceDiscovery.discory();
    }
}
