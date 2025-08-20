//package com.rain.yuagent.agent;
//
//import com.alibaba.dashscope.app.Application;
//import com.alibaba.dashscope.app.ApplicationParam;
//import com.alibaba.dashscope.app.ApplicationResult;
//import com.alibaba.dashscope.exception.ApiException;
//import com.alibaba.dashscope.exception.InputRequiredException;
//import com.alibaba.dashscope.exception.NoApiKeyException;
//import io.reactivex.Flowable;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
///**
// * 阿里云创建智能体应用，通过程序中进行调用
// */
//@Component
//@Slf4j
//public class YunAgent {
//
//    // 应用ID
//    private static final String APP_ID = "";
//
//    public static void appCall(String userPrompt)
//            throws ApiException, NoApiKeyException, InputRequiredException {
//        ApplicationParam param = ApplicationParam.builder()
//                // 若没有配置环境变量，可用百炼API Key将下行替换为：.apiKey("sk-xxx")。但不建议在生产环境中直接将API Key硬编码到代码中，以减少API Key泄露风险。
//                .apiKey("")
//                .appId(APP_ID)
//                .prompt(userPrompt)
//                .incrementalOutput(true)
//                .build();
//
//        Application application = new Application();
//        // .streamCall（）：流式输出内容
//        Flowable<ApplicationResult> result = application.streamCall(param);
//        result.blockingForEach(data -> {
//            System.out.printf("%s\n",
//                    data.getOutput().getText());
//        });
//    }
//
//}
