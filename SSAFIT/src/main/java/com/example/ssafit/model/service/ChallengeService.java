package com.example.ssafit.model.service;

import com.example.ssafit.model.dto.User.Challenge;
import com.example.ssafit.model.dto.User.ChallengeSummary;

import java.time.LocalDate;

public interface ChallengeService {

    /**
     * 사용자의 챌린지 스트릭 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 현재 스트릭, 최장 스트릭, 스트릭 캘린더를 포함한 요약 정보
     */
    ChallengeSummary getChallengeStreak(int userId);

    /**
     * 챌린지 기록을 생성합니다.
     *
     * @param userId 사용자 ID
     * @param recordDate 기록 날짜
     * @return 생성된 챌린지 객체
     */
    Challenge createChallengeRecord(int userId, LocalDate recordDate);

    /**
     * 사용자가 해당 날짜에 이미 챌린지 기록이 있는지 확인합니다.
     *
     * @param userId 사용자 ID
     * @param recordDate 기록 날짜
     * @return 기록 존재 여부
     */
    boolean hasRecordForDate(int userId, LocalDate recordDate);
}