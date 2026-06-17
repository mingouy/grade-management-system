package com.grade.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.grade.common.Result;
import com.grade.entity.Course;
import com.grade.entity.Score;
import com.grade.entity.StudentInfo;
import com.grade.service.CourseService;
import com.grade.service.ScoreService;
import com.grade.service.StudentInfoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/import")
public class ImportController {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private StudentInfoService studentInfoService;

    @Autowired
    private CourseService courseService;

    @PostMapping("/scores")
    public Result<String> importScores(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        try {
            List<Score> scores = new ArrayList<>();
            EasyExcel.read(file.getInputStream(), Score.class, new ReadListener<Score>() {
                @Override
                public void invoke(Score data, AnalysisContext context) {
                    if (data.getStudentNo() != null && data.getScore() != null) {
                        scores.add(data);
                    }
                }
                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {}
            }).sheet().doRead();

            if (scores.isEmpty()) {
                return Result.error("未读取到有效数据");
            }

            List<StudentInfo> allStudents = studentInfoService.listWithDetails();
            Map<String, Long> studentNoToId = allStudents.stream()
                .filter(s -> s.getStudentNo() != null)
                .collect(Collectors.toMap(StudentInfo::getStudentNo, StudentInfo::getId, (a, b) -> a));

            List<Course> allCourses = courseService.list();
            Map<String, Long> courseCodeToId = allCourses.stream()
                .filter(c -> c.getCourseCode() != null)
                .collect(Collectors.toMap(Course::getCourseCode, Course::getId, (a, b) -> a));

            int validCount = 0;
            for (Score score : scores) {
                Long studentId = studentNoToId.get(score.getStudentNo());
                if (studentId == null) continue;

                Long courseId = null;
                if (score.getCourseCode() != null) {
                    courseId = courseCodeToId.get(score.getCourseCode());
                }
                if (courseId == null) continue;

                score.setStudentId(studentId);
                score.setCourseId(courseId);
                validCount++;
            }

            if (validCount == 0) {
                return Result.error("未匹配到有效的学生或课程");
            }

            List<Score> validScores = scores.stream()
                .filter(s -> s.getStudentId() != null && s.getCourseId() != null)
                .collect(Collectors.toList());

            scoreService.saveBatchScores(validScores);
            return Result.success("成功导入 " + validScores.size() + " 条成绩记录");
        } catch (Exception e) {
            return Result.error("导入失败: " + e.getMessage());
        }
    }

    @GetMapping("/score-template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=score_template.xlsx");
        EasyExcel.write(response.getOutputStream(), Score.class).sheet("成绩导入模板").doWrite(new ArrayList<>());
    }
}
