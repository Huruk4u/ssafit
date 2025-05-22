package com.example.ssafit.model.dto.user;

import com.example.ssafit.model.dto.Badge;
import com.example.ssafit.model.dto.Inbody;

import java.util.List;

public class UserProfileResponseForm {
    private ChallengeSummary challengeSummary;
    private List<Inbody> inbody;
    private List<Badge> badges;
    private Badge badge;
    private String firstExercise;
    private String secondExercise;
    private String thirdExercise;

    public UserProfileResponseForm(ChallengeSummary challengeSummary, List<Inbody> inbody, List<Badge> badges, Badge badge, String firstExercise, String secondExercise, String thirdExercise) {
        this.challengeSummary = challengeSummary;
        this.inbody = inbody;
        this.badges = badges;
        this.badge = badge;
        this.firstExercise = firstExercise;
        this.secondExercise = secondExercise;
        this.thirdExercise = thirdExercise;
    }

    public void setChallengeSummary(ChallengeSummary challengeSummary) {
        this.challengeSummary = challengeSummary;
    }

    public void setInbody(List<Inbody> inbody) {
        this.inbody = inbody;
    }

    public void setBadges(List<Badge> badges) {
        this.badges = badges;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public ChallengeSummary getChallengeSummary() {
        return challengeSummary;
    }

    public List<Inbody> getInbody() {
        return inbody;
    }

    public List<Badge> getBadges() {
        return badges;
    }

    public Badge getBadge() {
        return badge;
    }

    public void setFirstExercise(String firstExercise) {
        this.firstExercise = firstExercise;
    }
    public void setSecondExercise(String secondExercise) {
        this.secondExercise = secondExercise;
    }
    public void setThirdExercise(String thirdExercise) {
        this.thirdExercise = thirdExercise;
    }
    public String getFirstExercise() {
        return firstExercise;
    }
    public String getSecondExercise() {
        return secondExercise;
    }
    public String getThirdExercise() {
        return thirdExercise;
    }

    @Override
    public String toString() {
        return "UserProfileResponseForm{" +
                "challengeSummary=" + challengeSummary +
                ", inbody=" + inbody +
                ", badges=" + badges +
                ", badge=" + badge +
                '}';
    }
}
