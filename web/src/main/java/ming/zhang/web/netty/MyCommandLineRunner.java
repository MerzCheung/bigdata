package ming.zhang.web.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ming.zhang.web.zookeeper.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class MyCommandLineRunner implements ApplicationRunner {

    @Value("${websocket.url}")
    private String websocketUrl;

    @Autowired
    private ServiceRegistry serviceRegistry;

    @Autowired
    private MyWebSocketChannelHandler myWebSocketChannelHandler;


    @Override
    public void run(ApplicationArguments args) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            //开启服务端
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(eventLoopGroup,workGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(myWebSocketChannelHandler);
            System.out.println("服务端开启等待客户端连接..");
            Channel channel = serverBootstrap.bind(Integer.valueOf("11111")).sync().channel();

            //注册服务
            if (serviceRegistry != null){
                serviceRegistry.registry(websocketUrl);
            }
            channel.closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //退出程序
            eventLoopGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
