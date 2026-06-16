package com.grade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grade.entity.Score;
import com.grade.mapper.ScoreMapper;
import com.grade.service.ScoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 成绩Service实现类
 */
@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {

    @Override
    public List<Score> listByCourseId(Long courseId) {
        return baseMapper.selectListByCourseId(courseId);
    }

    @Override
    public List<Score> listByStudentId(Long studentId) {
        return baseMapper.selectListByStudentId(studentId);
    }

    @Override
    public List<Score> listByCourseIdAndSemester(Long courseId, String semester) {
        return baseMapper.selectListByCourseIdAndSemester(courseId, semester);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatchScores(List<Score> scores) {
        if (scores == null || scores.isEmpty()) {
            return true;
        }

        // 构建查询条件：批量查询已存在的记录
        List<Score> existScores = lambdaQuery()
                .in(Score::getStudentId, scores.stream().map(Score::getStudentId).collect(Collectors.toList()))
                .in(Score::getCourseId, scores.stream().map(Score::getCourseId).collect(Collectors.toList()))
                .in(Score::getExamType, scores.stream().map(Score::getExamType).collect(Collectors.toList()))
                .list();

        // 按 studentId+courseId+examType 建立索引
        Map<String, Score> existMap = existScores.stream()
                .collect(Collectors.toMap(
                        s -> s.getStudentId() + "_" + s.getCourseId() + "_" + s.getExamType(),
                        s -> s,
                        (a, b) -> a
                ));

        // 分别执行insert或update
        for (Score score : scores) {
            String key = score.getStudentId() + "_" + score.getCourseId() + "_" + score.getExamType();
            Score exist = existMap.get(key);

            if (exist != null) {
                exist.setScore(score.getScore());
                exist.setSemester(score.getSemester());
                updateById(exist);
            } else {
                save(score);
            }
        }
        return true;
    }
}
