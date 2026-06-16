package com.grade.controller;

import com.grade.common.Result;
import com.grade.entity.Score;
import com.grade.exception.BusinessException;
import com.grade.mapper.ScoreMapper;
import com.grade.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scores")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private ScoreMapper scoreMapper;

    @GetMapping
    public Result<List<Score>> list(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String semester) {

        List<Score> list;
        if (courseId != null && semester != null && !semester.trim().isEmpty()) {
            list = scoreService.listByCourseIdAndSemester(courseId, semester.trim());
        } else if (courseId != null) {
            list = scoreService.listByCourseId(courseId);
        } else {
            list = scoreMapper.selectListWithDetails();
        }
        return Result.success(list);
    }

    @PostMapping
    public Result<Void> save(@RequestBody List<Score> scores) {
        if (scores == null || scores.isEmpty()) {
            throw new BusinessException("成绩数据不能为空");
        }
        for (Score score : scores) {
            if (score.getStudentId() == null) throw new BusinessException("学生ID不能为空");
            if (score.getCourseId() == null) throw new BusinessException("课程ID不能为空");
            if (score.getScore() == null) throw new BusinessException("成绩分数不能为空");
            if (score.getSemester() == null || score.getSemester().trim().isEmpty()) throw new BusinessException("学期不能为空");
            if (score.getExamType() == null || score.getExamType().trim().isEmpty()) score.setExamType("期末考试");
        }
        scoreService.saveBatchScores(scores);
        return Result.success("录入成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Score score) {
        Score exist = scoreService.getById(id);
        if (exist == null) throw new BusinessException("成绩记录不存在");
        score.setId(id);
        scoreService.updateById(score);
        return Result.success("修改成功", null);
    }

    @GetMapping("/course/{courseId}")
    public Result<List<Score>> listByCourseId(@PathVariable Long courseId) {
        return Result.success(scoreService.listByCourseId(courseId));
    }

    @GetMapping("/all")
    public Result<List<Score>> listAll() {
        return Result.success(scoreMapper.selectListWithDetails());
    }
}
