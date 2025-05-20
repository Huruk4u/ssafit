package com.example.ssafit.model.dao;

import com.example.ssafit.model.dto.Inbody;

import java.util.List;

public interface InbodyDao {

    void updateInbody(Inbody inbody);

    Inbody findLatestInbodyByUserId(int userId);

    List<Inbody> findInbodyListByUserId(int userId);
}