package com.example.ssafit.model.service.inbody;

import com.example.ssafit.model.dto.Inbody;
import java.util.List;

public interface InbodyService {
    void updateInbodyData(Inbody data);
    List<String> recommendTagsByInbody(int userId);
}