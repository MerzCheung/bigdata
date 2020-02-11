package ming.zhang.web.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 初始化链接时候的组件
 */
@Component
public class MyWebSocketChannelHandler extends ChannelInitializer<SocketChannel> {

    @Autowired
    private MyWebSockeHandler handler;

    @Override
    protected void initChannel(SocketChannel e) {
        //websocket协议本身是基于http协议的，所以这边也要使用http解编码器
        e.pipeline().addLast("http-codec",new HttpServerCodec());
        //netty是基于分段请求的，HttpObjectAggregator的作用是将请求分段再聚合,参数是聚合字节的最大长度
        e.pipeline().addLast("aggregator",new HttpObjectAggregator(65536));
        // 添加ChunkedWriteHandler，来向客户端发送HTML5文件，主要用于支持浏览器和服务端进行WebSocket 通信。
        e.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
        e.pipeline().addLast("handler",handler);
    }
}
