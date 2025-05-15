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
    public List<String> recommendTagsByInbody(int userId) {
        Inbody data = inbodyDao.findLatestInbodyByUserId(userId);
        if (data == null) return Collections.emptyList();

        List<String> tags = new ArrayList<>();

        if (data.getBodyFat() > 25.0) tags.add("복부");
        if (data.getMuscleMass() < 30.0) tags.add("상체");
        if (data.getWeight() > 80.0) tags.add("하체");

        List<String> allTags = Arrays.asList("전신", "상체", "하체", "복부");
        Collections.shuffle(allTags);

        for (String tag : allTags) {
            if (tags.size() >= 3) break;
            if (!tags.contains(tag)) tags.add(tag);
        }

        return tags;
    }

}