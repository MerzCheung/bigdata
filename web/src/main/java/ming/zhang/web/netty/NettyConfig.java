package ming.zhang.web.netty;


import io.netty.channel.Channel;
import ming.zhang.web.util.SocketSendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NettyConfig {

    private static SocketSendUtil socketSendUtil;

    @Autowired
    public void setSocketSendUtil(SocketSendUtil socketSendUtil) {
        NettyConfig.socketSendUtil = socketSendUtil;
    }


    /**
     * 存储每一个客户端接入进来的对象
     */
//    public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static final Map<String, Channel> MAP = new ConcurrentHashMap<>();

    static void remove(Channel channel) {
        for (Map.Entry<String, Channel> entry : MAP.entrySet()) {
            if (entry.getValue() == channel) {
                String key = entry.getKey();
                MAP.remove(key);
            }
        }
    }
}
