package com.rain.yuagent.agent;

import com.rain.yuagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.util.StringUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * 基础类，用于状态管理和执行流程定义
 */
@Data
@Slf4j
public abstract class BaseAgent {

    /**
     * 核心属性
     */
    private String name;

    /**
     * 提示词
     */
    private String systemPrompt;
    private String nextStepPrompt;

    /**
     * 状态
     */
    private AgentState state = AgentState.IDLE;

    /**
     * 最大步骤
     */
    private int maxStep = 10;

    /**
     * 当前步骤
     */
    private int currentStep = 0;

    /**
     * 大模型 LLM
     */
    private ChatClient chatClient;

    /**
     * Memory 记忆需要自主维护会话上下文
     */
    private List<Message> messageList = new ArrayList<>();

    /**
     * 运行代理
     * @param userPrompt
     * @return
     */
    public String run (String userPrompt){
        if (this.state != AgentState.IDLE) {
            throw new RuntimeException("Can't run agent when state is " + AgentState.IDLE);
        }
        if (StringUtil.isEmpty(userPrompt)) {
            throw new RuntimeException("user prompt is empty");
        }
        // 先更改状态
        state = AgentState.RUNNING;
        // 记录消会话上下文
        messageList.add(new UserMessage(userPrompt));
        // 结果列表
        ArrayList<String> results = new ArrayList<>();
        try {
            for (int i = 0; i < maxStep && state != AgentState.FINISHED; i++) {
                int stepNumber = i + 1;
                currentStep = stepNumber;
                String currentStepResult = this.step();
                String result = "Step " + stepNumber + ": " + currentStepResult;
                results.add(result);

                // 是否超出最大步骤
                if (currentStep >= maxStep) {
                    state = AgentState.FINISHED;
                    results.add("Terminated: Reached max steps (" + maxStep + ")");
                }
            }
            return String.join("\n", results);
        } catch (Exception e) {
            // 出现异常，将状态改为 error
            state = AgentState.ERROR;
            log.error("error executing agent", e);
            return "执行错误" + e.getMessage();
        } finally {
            // 3、清理资源
            this.cleanup();
        }
    }

    /**
     * 运行代理，使用流式输出的方式
     * 注意：
     * 使用 CompletableFuture，线程异步处理，避免阻塞主线程
     * @param userPrompt 用户的输入
     * @return SseEmitter
     */
    public SseEmitter runStream (String userPrompt){
        // 创建一个具有超时时间的 SseEmitter
        SseEmitter sseEmitter = new SseEmitter(30000L);
        // 此处需要使用 CompletableFuture，线程异步处理，避免阻塞主线程
        CompletableFuture.runAsync(() -> {
            try {
                if (this.state != AgentState.IDLE) {
                    sseEmitter.send("Can't run agent when state is " + AgentState.IDLE);
                    sseEmitter.complete();
                    return;
                }
                if (StringUtil.isEmpty(userPrompt)) {
                    sseEmitter.send("user prompt is empty");
                    sseEmitter.complete();
                    return;
                }

                // 先更改状态
                state = AgentState.RUNNING;
                // 记录消会话上下文
                messageList.add(new UserMessage(userPrompt));
                try {
                    for (int i = 0; i < maxStep && state != AgentState.FINISHED; i++) {
                        int stepNumber = i + 1;
                        currentStep = stepNumber;
                        // 单步执行
                        String currentStepResult = this.step();
                        // 单步执行结果
                        String result = "Step " + stepNumber + ": " + currentStepResult;
                        // 将单步结果发送给 SseEmitter
                        sseEmitter.send(result);
                    }
                    // 是否超出最大步骤
                    if (currentStep >= maxStep) {
                        state = AgentState.FINISHED;
                        sseEmitter.send("Terminated: Reached max steps (" + maxStep + ")");
                    }

                    // 正常完成时
                    sseEmitter.complete();
                } catch (Exception e) {
                    // 出现异常，将状态改为 error
                    state = AgentState.ERROR;
                    log.error("error executing agent", e);
                    try {
                        sseEmitter.send("执行错误：" + e.getMessage());
                        sseEmitter.complete();
                    } catch (IOException ex) {
                        // 发生异常时
                        sseEmitter.completeWithError(e);
                    }
                } finally {
                    // 3、清理资源
                    this.cleanup();
                }
            } catch (IOException e) {
                sseEmitter.completeWithError(e);
            }
        });
        // 设置超时的回调
        sseEmitter.onTimeout(() -> {
            this.state = AgentState.ERROR;
            this.cleanup();
            log.warn("sse connection time out");
        });
        // 设置完成时的回调
        sseEmitter.onCompletion(() -> {
            if (this.state == AgentState.RUNNING) {
                this.state = AgentState.FINISHED;
            }
            this.cleanup();
            log.info("sse connection completed");
        });
        // 返回 SSEEmitter
        return sseEmitter;
    }

    /**
     * 定义单个步骤
     *
     * @return
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup() {
        // 子类可以重写此方法来清理资源
    }

}
