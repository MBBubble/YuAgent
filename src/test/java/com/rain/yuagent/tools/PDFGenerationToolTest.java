package com.rain.yuagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class PDFGenerationToolTest {

    @Test
    public void testGeneratePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "小白.pdf";
        String content = "my name is zhouxiaobai,i am learning of ai project name in codefather.cn";
        String result = tool.generatePDF(fileName, content);
        assertNotNull(result);
    }
}
