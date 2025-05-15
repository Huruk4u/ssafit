package com.example.ssafit.model.service;

import com.example.ssafit.model.dto.Inbody;
import java.util.List;

public interface InbodyService {
    void updateInbodyData(Inbody data);
    List<String> recommendTagsByInbody(int userId);
}