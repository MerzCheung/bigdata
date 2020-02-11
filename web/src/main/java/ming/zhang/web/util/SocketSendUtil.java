package ming.zhang.web.util;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import ming.zhang.web.domain.Message;
import ming.zhang.web.netty.NettyConfig;
import org.springframework.stereotype.Component;

/**
 * @author merz
 * @Description:
 */
@Component
@Slf4j
public class SocketSendUtil {

    public void sendMsg(Message message) {
        Channel channel = NettyConfig.MAP.get(message.getTo());
        if (channel != null && channel.isOpen()) {
            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
        }
    }
}
