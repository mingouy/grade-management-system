package com.grade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 成绩管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.grade.mapper")
public class GradeManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(GradeManagementApplication.class, args);
    }
}
