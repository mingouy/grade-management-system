package com.grade.controller;

import com.grade.common.Result;
import com.grade.entity.Score;
import com.grade.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/grade-analysis")
public class GradeAnalysisController {

    @Autowired private ScoreService scoreService;

    @GetMapping("/distribution")
    public Result<Map<String, Object>> distribution(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String semester) {
        List<Score> scores = getScores(courseId, semester);
        Map<String, Object> result = new HashMap<>();

        long excellent = scores.stream().filter(s -> s.getScore() != null && s.getScore().doubleValue() >= 90).count();
        long good = scores.stream().filter(s -> s.getScore() != null && s.getScore().doubleValue() >= 80 && s.getScore().doubleValue() < 90).count();
        long medium = scores.stream().filter(s -> s.getScore() != null && s.getScore().doubleValue() >= 70 && s.getScore().doubleValue() < 80).count();
        long pass = scores.stream().filter(s -> s.getScore() != null && s.getScore().doubleValue() >= 60 && s.getScore().doubleValue() < 70).count();
        long fail = scores.stream().filter(s -> s.getScore() != null && s.getScore().doubleValue() < 60).count();

        result.put("excellent", excellent);
        result.put("good", good);
        result.put("medium", medium);
        result.put("pass", pass);
        result.put("fail", fail);
        result.put("total", scores.size());

        if (!scores.isEmpty()) {
            double avg = scores.stream().filter(s -> s.getScore() != null)
                .mapToDouble(s -> s.getScore().doubleValue()).average().orElse(0);
            result.put("avgScore", new BigDecimal(avg).setScale(1, RoundingMode.HALF_UP).toString());
            long passCount = scores.stream().filter(s -> s.getScore() != null && s.getScore().doubleValue() >= 60).count();
            result.put("passRate", new BigDecimal((double) passCount / scores.size() * 100).setScale(1, RoundingMode.HALF_UP).toString());
        } else {
            result.put("avgScore", "0.0");
            result.put("passRate", "0.0");
        }

        return Result.success(result);
    }

    @GetMapping("/ranking")
    public Result<List<Map<String, Object>>> ranking(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String semester) {
        List<Score> scores = getScores(courseId, semester);

        Map<Long, List<Score>> studentScores = scores.stream()
            .filter(s -> s.getStudentId() != null)
            .collect(Collectors.groupingBy(Score::getStudentId));

        List<Map<String, Object>> ranking = new ArrayList<>();
        studentScores.forEach((studentId, studentScoreList) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("studentId", studentId);
            item.put("studentName", studentScoreList.get(0).getStudentName());
            item.put("studentNo", studentScoreList.get(0).getStudentNo());

            double totalPoints = 0;
            double totalCredits = 0;
            for (Score s : studentScoreList) {
                if (s.getScore() != null && s.getCredit() != null) {
                    totalPoints += getGradePoint(s.getScore().doubleValue()) * s.getCredit().doubleValue();
                    totalCredits += s.getCredit().doubleValue();
                }
            }
            double gpa = totalCredits > 0 ? totalPoints / totalCredits : 0;
            double avg = studentScoreList.stream().filter(s -> s.getScore() != null)
                .mapToDouble(s -> s.getScore().doubleValue()).average().orElse(0);

            item.put("gpa", new BigDecimal(gpa).setScale(2, RoundingMode.HALF_UP).toString());
            item.put("avgScore", new BigDecimal(avg).setScale(1, RoundingMode.HALF_UP).toString());
            item.put("courseCount", studentScoreList.size());
            ranking.add(item);
        });

        ranking.sort((a, b) -> Double.compare(Double.parseDouble((String) b.get("gpa")), Double.parseDouble((String) a.get("gpa"))));

        return Result.success(ranking);
    }

    @GetMapping("/failing")
    public Result<List<Map<String, Object>>> failingStudents() {
        List<Score> allScores = scoreService.listAllWithDetails();

        Map<Long, List<Score>> studentScores = allScores.stream()
            .filter(s -> s.getStudentId() != null && s.getScore() != null && s.getScore().doubleValue() < 60)
            .collect(Collectors.groupingBy(Score::getStudentId));

        List<Map<String, Object>> failingList = new ArrayList<>();
        studentScores.forEach((studentId, failScores) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("studentId", studentId);
            item.put("studentName", failScores.get(0).getStudentName());
            item.put("studentNo", failScores.get(0).getStudentNo());
            item.put("failCount", failScores.size());

            List<Map<String, Object>> courses = new ArrayList<>();
            failScores.forEach(s -> {
                Map<String, Object> course = new HashMap<>();
                course.put("courseName", s.getCourseName());
                course.put("score", s.getScore());
                course.put("semester", s.getSemester());
                courses.add(course);
            });
            item.put("courses", courses);
            failingList.add(item);
        });

        failingList.sort((a, b) -> Integer.compare((int) b.get("failCount"), (int) a.get("failCount")));

        return Result.success(failingList);
    }

    @GetMapping("/semester-comparison")
    public Result<List<Map<String, Object>>> semesterComparison() {
        List<Score> allScores = scoreService.listAllWithDetails();

        Map<String, List<Score>> bySemester = allScores.stream()
            .filter(s -> s.getSemester() != null)
            .collect(Collectors.groupingBy(Score::getSemester));

        List<Map<String, Object>> result = new ArrayList<>();
        bySemester.forEach((semester, semesterScores) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("semester", semester);
            item.put("count", semesterScores.size());

            double avg = semesterScores.stream().filter(s -> s.getScore() != null)
                .mapToDouble(s -> s.getScore().doubleValue()).average().orElse(0);
            item.put("avgScore", new BigDecimal(avg).setScale(1, RoundingMode.HALF_UP).toString());

            long passCount = semesterScores.stream().filter(s -> s.getScore() != null && s.getScore().doubleValue() >= 60).count();
            item.put("passRate", new BigDecimal((double) passCount / semesterScores.size() * 100).setScale(1, RoundingMode.HALF_UP).toString());

            long excellent = semesterScores.stream().filter(s -> s.getScore() != null && s.getScore().doubleValue() >= 90).count();
            item.put("excellentRate", new BigDecimal((double) excellent / semesterScores.size() * 100).setScale(1, RoundingMode.HALF_UP).toString());

            result.add(item);
        });

        result.sort((a, b) -> ((String) a.get("semester")).compareTo((String) b.get("semester")));
        return Result.success(result);
    }

    private List<Score> getScores(Long courseId, String semester) {
        if (courseId != null) {
            if (semester != null && !semester.isEmpty()) {
                return scoreService.listByCourseIdAndSemester(courseId, semester);
            }
            return scoreService.listByCourseId(courseId);
        }
        return scoreService.listAllWithDetails();
    }

    private double getGradePoint(double score) {
        if (score >= 90) return 4.0;
        if (score >= 85) return 3.7;
        if (score >= 82) return 3.3;
        if (score >= 78) return 3.0;
        if (score >= 75) return 2.7;
        if (score >= 72) return 2.3;
        if (score >= 68) return 2.0;
        if (score >= 64) return 1.5;
        if (score >= 60) return 1.0;
        return 0.0;
    }
}
