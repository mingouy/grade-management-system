package com.grade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.grade.entity.Score;

import java.util.List;

/**
 * 成绩Service接口
 */
public interface ScoreService extends IService<Score> {

    /**
     * 根据课程ID查询成绩列表
     *
     * @param courseId 课程ID
     * @return 成绩列表
     */
    List<Score> listByCourseId(Long courseId);

    /**
     * 根据学生ID查询成绩列表
     *
     * @param studentId 学生ID
     * @return 成绩列表
     */
    List<Score> listByStudentId(Long studentId);

    /**
     * 根据课程ID和学期查询成绩列表
     *
     * @param courseId 课程ID
     * @param semester 学期
     * @return 成绩列表
     */
    List<Score> listByCourseIdAndSemester(Long courseId, String semester);

    /**
     * 批量录入成绩
     *
     * @param scores 成绩列表
     * @return 是否成功
     */
    boolean saveBatchScores(List<Score> scores);

    /**
     * 查询所有成绩（含学生和课程信息）
     *
     * @return 成绩列表
     */
    List<Score> listAllWithDetails();
}
