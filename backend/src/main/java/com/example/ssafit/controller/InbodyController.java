package com.example.ssafit.controller;

import com.example.ssafit.model.dto.Inbody;
import com.example.ssafit.model.service.inbody.InbodyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api_inbody")
@CrossOrigin(origins = "*")
public class InbodyController {

    private final InbodyService inbodyService;

    public InbodyController(InbodyService inbodyService) {
        this.inbodyService = inbodyService;
    }

    /** 최신 인바디 한 건 조회 */
    @GetMapping("/latest/{userId}")
    public ResponseEntity<Inbody> getLatestInbody(@PathVariable int userId, Principal principal) {
        //String username = principal.getName();
        Inbody latest = inbodyService.findLatestByUserId(userId);

        // 이건 오류까진 아니니까 exception 처리 따로 안 함.
        if (latest == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(latest);
    }
}