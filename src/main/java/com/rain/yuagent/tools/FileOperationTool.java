package com.rain.yuagent.tools;

import cn.hutool.core.io.FileUtil;
import com.rain.yuagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 文件操作工具
 * 读
 * 写
 */
public class FileOperationTool {

    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR +"/file";

    /**
     * 读文件
     * @param fileName 文件名
     * @return 返回结果
     */
    @Tool(description = "Read content from a file")
    public String readFile(@ToolParam(description = "Name of file to read") String fileName) {
        String filePath = FILE_DIR + "/" + fileName;
        try {
            return FileUtil.readUtf8String(filePath);
        } catch (Exception e) {
            return "Error reading file " + fileName;
        }
    }

    /**
     * 写文件
     * @param fileName 保存的文件名
     * @param content 写的内容
     * @return 返回结果
     */
    @Tool(description = "Write content to a file")
    public String writeFile(@ToolParam(description = "Name of file to write") String fileName,
                            @ToolParam(description = "Content to write to the file") String content) {
        String filePath = FILE_DIR + "/" + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content, filePath);
            return "Success writing to file " + fileName;
        } catch (Exception e) {
            return "Error writing file " + fileName;
        }

    }

}
