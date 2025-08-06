package com.rain.yuagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileOperationToolTest {

    @Test
    void readFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String fileName = "周小白.txt";
        String readFileResult = fileOperationTool.readFile(fileName);
        System.out.println("读取的内容为：" + readFileResult);
        assertNotNull(readFileResult);
    }

    @Test
    void writeFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String content = "我是周小白 哈哈哈";
        String fileName = "周小白.txt";
        String writeFileResult = fileOperationTool.writeFile(fileName, content);
        assertNotNull(writeFileResult);
    }
}