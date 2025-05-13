package com.example.ssafit.model.dto;

import java.time.LocalDateTime;

public class Inbody {
    private int inbodyId;
    private int userId;
    private double weight;
    private double height;
    private double bmi;
    private double muscleMass;
    private double bodyFat;
    private LocalDateTime uploadedAt;
    private LocalDateTime updatedAt;

    public int getInbodyId() {
        return inbodyId;
    }

    public void setInbodyId(int inbodyId) {
        this.inbodyId = inbodyId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public double getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(double bodyFat) {
        this.bodyFat = bodyFat;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public double getMuscleMass() {
        return muscleMass;
    }

    public void setMuscleMass(double musscleMass) {
        this.muscleMass = musscleMass;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Inbody{" +
                "inbodyId=" + inbodyId +
                ", userId=" + userId +
                ", weight=" + weight +
                ", height=" + height +
                ", bmi=" + bmi +
                ", muscleMass=" + muscleMass +
                ", bodyFat=" + bodyFat +
                ", uploadedAt=" + uploadedAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
