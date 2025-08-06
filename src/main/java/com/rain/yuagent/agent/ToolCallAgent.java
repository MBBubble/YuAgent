package com.rain.yuagent.agent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.rain.yuagent.agent.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理工具调用的基础代理类，具体实现了 think 和 act 方法，可以用作创建实例的父类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent {

    /**
     * 可用的工具
     */
    private final ToolCallback[] availableTools;

    /**
     * 工具管理器
     */
    private ToolCallingManager toolCallingManager;

    /**
     * 禁用 Spring AI 内置的工具调用机制，自己维护选项和消息上下文
     */
    private final ChatOptions chatOptions;

    /**
     * 保存工具调用的响应结果
     */
    private ChatResponse toolCallChatResponse;

    /**
     * 工具调用信息的响应结果，即需要调用哪些工具
     */
    private ChatResponse  chatResponse;

    public ToolCallAgent(ToolCallback[] availableTools){
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        this.chatOptions = DashScopeChatOptions.builder().build();
    }

    /**
     * 处理当前状态并思考下一步行动
     * @return 是否需要执行相关行动 true：有工具 需要；false：没有工具 不需要
     */
    @Override
    public Boolean think() {
        // 1、提示词校验，拼接用户提示词
        if(StrUtil.isNotBlank(getNextStepPrompt())){
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
        }

        // 2、调用AI大模型，获取工具调用结果
        List<Message> messageList = getMessageList();
        Prompt prompt = new Prompt(messageList, chatOptions);
        try {
            ChatResponse chatResponse = getChatClient()
                    .prompt(prompt)
                    .system(getSystemPrompt())
                    .tools(availableTools)
                    .call()
                    .chatResponse();
            // 保存响应结果
            this.toolCallChatResponse = chatResponse;

            // 3、解析工具的调用结果，拿到需要调用的工具 tools
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            // 获取响应中的工具列表
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            String assistantMessageText = assistantMessage.getText();
            log.info("{}的思考：{}", getName(), assistantMessageText);
            log.info("{}选择了 {} 个工具来使用", getName(), toolCallList.size());
            String toolCallInfo = toolCallList.stream()
                    .map(toolCall -> String.format("工具名称：%s，参数：%s", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info(toolCallInfo);
            // 如果不需要工具，返回 false
            if (toolCallList.isEmpty()){
                getMessageList().add(assistantMessage);
                return false;
            } else {
                // 需要调用工具 返回 true
                return true;
            }
        } catch (Exception e) {
            log.error("{}的思考过程遇到了问题：{}", getName(), e.getMessage());
            getMessageList().add(new AssistantMessage("处理时遇到了错误：" + e.getMessage()));
            return false;
        }
    }

    /**
     * 执行工具调用并处理结果
     * @return
     */
    @Override
    public String act() {
        // 没有工具需要调用
        if (!toolCallChatResponse.hasToolCalls()) {
            return "没有工具需要调用";
        }
        // 有工具需要调用
        Prompt prompt = new Prompt(getMessageList(), chatOptions);
        // 工具管理器执行工具调用
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        // 记录消息上下文
        setMessageList(toolExecutionResult.conversationHistory());
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage)CollUtil.getLast(toolExecutionResult.conversationHistory());
        boolean isDoTerminate = toolResponseMessage.getResponses().stream()
                .anyMatch(toolResponse -> {
                    return toolResponse.name().equals("doTerminate");
                });
        if (isDoTerminate){
            // 调用了终止工具，任务结束
            setState(AgentState.FINISHED);
            log.info("调用了终止工具 doTerminate");
        }
        String results = toolResponseMessage.getResponses().stream()
                .map(response -> "工具 " + response.name() + " 返回的结果：" + response.responseData())
                .collect(Collectors.joining("\n"));
        log.info(results);
        return results;
    }
}
