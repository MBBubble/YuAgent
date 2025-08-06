package com.rain.yuagent.agent;

import com.rain.yuagent.agent.model.AgentState;

/**
 * ReAct (Reasoning and Acting) 模式的代理抽象类，Base的具体实现
 * 实现了思考（Think）-行动（Act）的循环模式
 */
public abstract class ReActAgent extends BaseAgent {

    /**
     * 思考是否执行下一步
     *
     * @return 是否需要进行下一步 true：需要； false：无需
     */
    public abstract Boolean think();

    /**
     * 具体执行
     * @return
     */
    public abstract String act();

    /**
     * 执行单个步骤： 思考（Thing）+行动（Act）
     * @return
     */
    @Override
    public String step() {
        try {
            Boolean thinkResult = think();
            if (thinkResult) {
                // 需要行动
                return act();
            } else {
                // 无需行动，更改状态
                setState(AgentState.FINISHED);
                return "思考完成 - 无需行动";
            }
        } catch (Exception e){
            // 记录异常日志
            e.printStackTrace();
            return "步骤执行失败：" + e.getMessage();
        }
    }
}
