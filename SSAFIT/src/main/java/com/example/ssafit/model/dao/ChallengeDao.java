package com.example.ssafit.model.dao;

import com.example.ssafit.model.dto.user.Challenge;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface ChallengeDao {
    /**
     * 사용자 ID로 모든 챌린지 기록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 챌린지 기록 목록
     */
    List<Challenge> getChallengesByUserId(int userId);

    /**
     * 사용자 ID와 날짜로 특정 챌린지 기록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @param recordDate 기록 날짜
     * @return 챌린지 기록 또는 null
     */
    Challenge getChallengeByUserIdAndDate(@Param("userId") int userId, @Param("recordDate") LocalDate recordDate);

    /**
     * 새로운 챌린지 기록을 삽입합니다.
     *
     * @param challenge 삽입할 챌린지 객체
     * @return 삽입된 행 수
     */
    int insertChallenge(Challenge challenge);
}