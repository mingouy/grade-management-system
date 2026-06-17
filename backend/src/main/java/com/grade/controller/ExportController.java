package com.grade.controller;

import com.alibaba.excel.EasyExcel;
import com.grade.entity.Score;
import com.grade.interceptor.AuthInterceptor;
import com.grade.service.ScoreService;
import com.grade.service.StudentInfoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/export")
public class ExportController {

    @Autowired
    private ScoreService scoreService;
    @Autowired
    private StudentInfoService studentInfoService;

    @GetMapping("/scores")
    public void exportScores(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String semester,
            HttpServletResponse response) throws IOException {
        List<Score> scores;
        if (courseId != null) {
            if (semester != null && !semester.isEmpty()) {
                scores = scoreService.listByCourseIdAndSemester(courseId, semester);
            } else {
                scores = scoreService.listByCourseId(courseId);
            }
        } else {
            scores = scoreService.list();
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = URLEncoder.encode("成绩报表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), Score.class).sheet("成绩报表").doWrite(scores);
    }

    @GetMapping("/my-scores")
    public void exportMyScores(HttpServletResponse response) throws IOException {
        Long userId = AuthInterceptor.getCurrentUserId();
        var student = studentInfoService.getByUserId(userId);
        if (student == null) return;
        List<Score> scores = scoreService.listByStudentId(student.getId());
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = URLEncoder.encode("我的成绩", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), Score.class).sheet("我的成绩").doWrite(scores);
    }
}
