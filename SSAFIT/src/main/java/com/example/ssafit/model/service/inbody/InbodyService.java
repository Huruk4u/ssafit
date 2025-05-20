package com.example.ssafit.model.service.inbody;

import com.example.ssafit.model.dto.Inbody;
import java.util.List;

public interface InbodyService {
    void updateInbodyData(Inbody data);
    Inbody findLatestByUserId(int userId);
}