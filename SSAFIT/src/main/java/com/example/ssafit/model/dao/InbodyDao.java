package com.example.ssafit.model.dao;

import com.example.ssafit.model.dto.Inbody;

public interface InbodyDao {

    void updateInbody(Inbody inbody);

    Inbody findLatestInbodyByUserId(int userId);

}