package com.rain.yuagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@SpringBootTest
class MyManusTest {

    @Resource
    private MyManus myManus;

    @Test
    void MyManusTest() {
        String userPrompt = """
                我的另一半居住在深圳市南山区，请帮我找到 10 公里内合适的约会地点，
                并结合一些网络图片，制定一份详细的约会计划，
                并以 PDF 格式输出""";
        String answer = myManus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }

    @Test
    void MyManusStreamTest() {
        String userPrompt = """
                我的另一半居住在深圳市南山区，请帮我找到 10 公里内合适的约会地点，
                并结合一些网络图片，制定一份详细的约会计划，
                并以 PDF 格式输出""";
        SseEmitter sseEmitter = myManus.runStream(userPrompt);
        Assertions.assertNotNull(sseEmitter);
    }

}