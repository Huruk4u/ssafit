package com.example.ssafit.model.dto;

import com.example.ssafit.controller.RecommendController;

import java.util.List;

public class RecommendResult {
    public List<String> recommended_parts;
    public double weight;
    public double muscle_mass;
    public double body_fat_mass;
    public double bmi;
    public double body_fat_percentage;

    public RecommendResult() {}

    public List<String> getRecommended_parts() {
        return recommended_parts;
    }

    public void setRecommended_parts(List<String> recommended_parts) {
        this.recommended_parts = recommended_parts;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getMuscle_mass() {
        return muscle_mass;
    }

    public void setMuscle_mass(double muscle_mass) {
        this.muscle_mass = muscle_mass;
    }

    public double getBody_fat_mass() {
        return body_fat_mass;
    }

    public void setBody_fat_mass(double body_fat_mass) {
        this.body_fat_mass = body_fat_mass;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public double getBody_fat_percentage() {
        return body_fat_percentage;
    }

    public void setBody_fat_percentage(double body_fat_percentage) {
        this.body_fat_percentage = body_fat_percentage;
    }

    @Override
    public String toString() {
        return "RecommendResult{" +
                "recommended_parts=" + recommended_parts +
                ", weight=" + weight +
                ", muscle_mass=" + muscle_mass +
                ", body_fat_mass=" + body_fat_mass +
                ", bmi=" + bmi +
                ", body_fat_percentage=" + body_fat_percentage +
                '}';
    }
}
