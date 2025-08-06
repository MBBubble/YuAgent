package com.rain.imagesearchmcpserver.tool;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ImageSearchToolTest {

    @Resource
    private ImageSearchTool imageSearchTool;

    @Test
    void searchImage() {
        String searchImage = imageSearchTool.searchImage("cat");
        Assertions.assertNotNull(searchImage);
    }
}