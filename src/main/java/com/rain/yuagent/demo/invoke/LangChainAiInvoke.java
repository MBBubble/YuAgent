package com.rain.yuagent.demo.invoke;

import dev.langchain4j.community.model.dashscope.WanxImageModel;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.output.Response;

public class LangChainAiInvoke {

    public static void main(String[] args) {

//        ChatLanguageModel qwenModel = QwenChatModel.builder()
//                .apiKey(TestApiKey.API_KEY)
//                .modelName("qwen-max")
//                .build();
//        String chatOut = qwenModel.chat("你好，你是谁？");
//        System.out.println(chatOut);

        WanxImageModel wanxImageModel = WanxImageModel.builder()
                .modelName("wanx2.1-t2i-plus")
                .apiKey(TestApiKey.API_KEY)
                .build();
        Response<Image> response = wanxImageModel.generate("一只布偶猫，脖子前挂着一个小铃铛。");
        System.out.println(response.content().url());

    }
}
