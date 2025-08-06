package com.rain.yuagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WebSearchToolTest {

    @Value("${search-api.api-key}")
    private String searchApiKey;

    @Test
    void searchWeb() {
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        String content = "马化腾";
        String searchedWebResult = webSearchTool.searchWeb(content);
        System.out.println("联网搜索结果：" + searchedWebResult);
        assertNotNull(searchedWebResult);
    }
}