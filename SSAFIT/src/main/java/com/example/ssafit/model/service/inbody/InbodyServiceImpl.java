package com.example.ssafit.model.service.inbody;

import com.example.ssafit.model.dao.InbodyDao;
import com.example.ssafit.model.dto.Inbody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InbodyServiceImpl implements InbodyService {

    @Autowired
    private InbodyDao inbodyDao;

    @Override
    public void updateInbodyData(Inbody data) {
        inbodyDao.updateInbody(data);
    }

    @Override
    public Inbody findLatestByUserId(int userId) {
        return inbodyDao.findLatestInbodyByUserId(userId);
    }

    @Override
    public List<Inbody> findInbodyListByUserId(int userId) {
        return inbodyDao.findInbodyListByUserId(userId);
    }

}