package ming.zhang.web.domain;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Message {

	//接收者
	public String to;
	//发送的文本
	public String text;
	//消息类型
	public String type;

}
