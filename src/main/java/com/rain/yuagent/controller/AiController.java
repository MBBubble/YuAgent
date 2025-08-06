package com.rain.yuagent.controller;

import com.rain.yuagent.agent.MyManus;
import com.rain.yuagent.app.LoveApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private LoveApp loveApp;

    @Resource
    private ChatModel dashscopeChatModel;

    @Resource
    private ToolCallback[] toolCallbacks;

    @GetMapping("/loveApp/chat/sync")
    public String doChatWithLoveAppSync(String message, String chatId){
        return loveApp.doChat(message, chatId);
    }

    /**
     * sse的第一种方式
     * @param message
     * @param chatId
     * @return 返回一个 Flux 对象，泛型为 String，这种方式参数里需要添加 produces = MediaType.TEXT_EVENT_STREAM_VALUE
     */
    @GetMapping(value = "/loveApp/chat/sse/one", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSSEOne(String message, String chatId){
       return loveApp.doChatByStream(message, chatId);
    }

    /**
     * sse的第二种方式
     * @param message
     * @param chatId
     * @return 返回一个 Flux 对象，泛型指定为 ServerSentEvent，这种方式可以省略参数中的 produces = MediaType.TEXT_EVENT_STREAM_VALUE
     */
    @GetMapping(value = "/loveApp/chat/sse/two")
    public Flux<ServerSentEvent<String>> doChatWithLoveAppSSETwo(String message, String chatId){
        return loveApp.doChatByStream(message, chatId)
                .map(s -> ServerSentEvent.<String>builder().data(s).build());
    }

    /**
     * 使用 SseEmitter ，持续返回消息
     * @param message
     * @param chatId
     * @return 返回一个 SseEmitter 对象，通过 send 方法持续向 SseEmitter 发送消息
     */
    @GetMapping("/loveApp/chat/sse/sseEmitter")
    public SseEmitter doChatWithLoveAppSSESseEmitter(String message, String chatId){
        // 创建一个具有超时时间的 sseEmitter
        SseEmitter sseEmitter = new SseEmitter(18000L);
        // 获取 Flux 数据流并直接订阅
        loveApp.doChatByStream(message, chatId)
                .subscribe(
                        // 处理每条消息
                        item -> {
                    try {
                        sseEmitter.send(item);
                    } catch (IOException e) {
                        // 如果异常 通过 completeWithError 进行处理
                        sseEmitter.completeWithError(e);
                    }
                },
                        sseEmitter::completeWithError,sseEmitter::complete
                );
        return sseEmitter;
    }

    /**
     *  流式调用 Manus 超级智能体
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus (String message){
        MyManus myManus = new MyManus(toolCallbacks, dashscopeChatModel);
        return myManus.runStream(message);
    }

}
