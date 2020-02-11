package ming.zhang.web.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import ming.zhang.web.domain.Message;
import ming.zhang.web.util.SocketSendUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 接受/处理/响应客户端websocke请求的核心业务处理类
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class MyWebSockeHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker webSocketServerHandshaker;

    @Value("${websocket.url}")
    private String websocketUrl;

    @Value("${server.address}")
    private String address;

    @Autowired
    private SocketSendUtil socketSendUtil;


    //客户端与服务端创建链接的时候调用
    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        log.info("客户端与服务端连接开启");
    }

    //客户端与服务端断开连接的时候调用
    @Override
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        context.close();
        NettyConfig.remove(context.channel());
        log.info("客户端与服务端连接断开");
    }

    //服务端接收客户端发送过来的数据结束之后调用
    @Override
    public void channelReadComplete(ChannelHandlerContext context) throws Exception {
        context.flush();
    }

    //工程出现异常的时候调用
    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable throwable) throws Exception {
        NettyConfig.remove(context.channel());
        throwable.printStackTrace();
        context.close();
    }

    //服务端处理客户端websocke请求的核心方法
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        //处理客户端向服务端发起的http握手请求
        if (o instanceof FullHttpRequest) {
            handHttpRequest(channelHandlerContext, (FullHttpRequest) o);
        } else if (o instanceof WebSocketFrame) {//处理websocket链接业务
            handWebSocketFrame(channelHandlerContext, (WebSocketFrame) o);
        }
    }

    /**
     * 处理客户端与服务端之间的websocket业务
     *
     * @param context
     * @param webSocketFrame
     */
    private void handWebSocketFrame(ChannelHandlerContext context, WebSocketFrame webSocketFrame) {
        if (webSocketFrame instanceof CloseWebSocketFrame) {//判断是否是关闭websocket的指令
            webSocketServerHandshaker.close(context.channel(), (CloseWebSocketFrame) webSocketFrame.retain());
        }
        if (webSocketFrame instanceof PingWebSocketFrame) {//判断是否是ping消息
            context.channel().write(new PongWebSocketFrame(webSocketFrame.content().retain()));
            return;
        }
        if (!(webSocketFrame instanceof TextWebSocketFrame)) {//判断是否是二进制消息
            return;
        }
        //返回应答消息
        //获取客户端向服务端发送的消息
        String request = ((TextWebSocketFrame) webSocketFrame).text();
        log.info("服务端收到客户端的消息：{}, 当前时间：{}", request, System.currentTimeMillis());
        try {
            Message msg = JSONObject.parseObject(request, Message.class);
            socketSendUtil.sendMsg(msg);
        } catch (Exception e) {
            log.info("客户端消息转化失败》》》》》》》");
        }
    }

    /**
     * 处理客户端向服务端发起http握手请求业务
     *
     * @param context
     * @param fullHttpRequest
     */
    private void handHttpRequest(ChannelHandlerContext context, FullHttpRequest fullHttpRequest) throws IOException, InterruptedException {
        //判断是否http握手请求
        if (!fullHttpRequest.getDecoderResult().isSuccess() || !("websocket".equals(fullHttpRequest.headers().get("Upgrade")))) {
            sendHttpResponse(context, fullHttpRequest, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        Map<String, String> parmMap = new RequestParser(fullHttpRequest).parse(); // 将GET, POST所有请求参数转换成Map对象
        log.info("http握手：parmMap:{}", parmMap);
        String account = parmMap.get("account");
        String auth = parmMap.get("auth");
        if (StringUtils.isNotEmpty(account) && StringUtils.isNotEmpty(auth)) {
            WebSocketServerHandshakerFactory webSocketServerHandshakerFactory = new WebSocketServerHandshakerFactory(websocketUrl, null, false);
            webSocketServerHandshaker = webSocketServerHandshakerFactory.newHandshaker(fullHttpRequest);
            if (webSocketServerHandshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(context.channel());
            } else {
                webSocketServerHandshaker.handshake(context.channel(), fullHttpRequest);
            }
            NettyConfig.MAP.put(account, context.channel());
        } else {
            log.info("参数错误");
            context.close();
        }
    }

    /**
     * 服务端想客户端发送响应消息
     *
     * @param context
     * @param fullHttpRequest
     * @param defaultFullHttpResponse
     */
    private void sendHttpResponse(ChannelHandlerContext context, FullHttpRequest fullHttpRequest, DefaultFullHttpResponse defaultFullHttpResponse) {
        if (defaultFullHttpResponse.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(defaultFullHttpResponse.getStatus().toString(), CharsetUtil.UTF_8);
            defaultFullHttpResponse.content().writeBytes(buf);
            buf.release();
        }
        //服务端向客户端发送数据
        ChannelFuture future = context.channel().writeAndFlush(defaultFullHttpResponse);
        if (defaultFullHttpResponse.getStatus().code() != 200) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
