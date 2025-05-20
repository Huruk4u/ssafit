package com.example.ssafit.model.dto.user;

import com.example.ssafit.model.dto.Badge;
import com.example.ssafit.model.dto.Inbody;

import java.util.List;

public class UserProfileResponseForm {
    private ChallengeSummary challengeSummary;
    private List<Inbody> inbody;
    private List<Badge> badges;
    private Badge badge;

    public UserProfileResponseForm(ChallengeSummary challengeSummary, List<Inbody> inbody, List<Badge> badges, Badge badge) {
        this.challengeSummary = challengeSummary;
        this.inbody = inbody;
        this.badges = badges;
        this.badge = badge;
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
}
