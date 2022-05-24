package com.tang.utils;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;

import java.util.Collections;

/**
 * 代码生成器
 * @author tang
 * @date 2022/5/23 15:54
 * @desc
 */
public class GeneratorUtils {
    public static void main(String[] args) {
            generator();
    }
    public static void generator(){
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/tang?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8",
                "root", "123456")
                .globalConfig(builder -> {
                    builder.author("tang") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("C:\\小白代码\\SpringBoot-vue\\src\\main\\java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.tang") // 设置父包名
                            .moduleName(null) // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.mapper.xml, "C:\\小白代码\\SpringBoot-vue\\src\\main\\resources\\mapper")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.entityBuilder().enableLombok();
                    builder.controllerBuilder().enableHyphenStyle()
                            .enableRestStyle(); // 开启生成@RestController 控制器
                    builder.addInclude("sys_user") // 设置需要生成的表名
                            .addTablePrefix("t_", "sys_"); // 设置过滤表前缀
                })
//                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
