package com.example.ourpro.expert;

public class Expert {
    private String expert;
    private String status;



    private String expertId;

    public Expert() {} // нужен для Firebase

    public Expert(String expert, String status) {
        this.expert = expert;
        this.status = status;
    }

    public String getExpert() {
        return expert;
    }

    public String getStatus() {
        return status;
    }

    public String getExpertId() {
        return expertId;
    }

    public void setExpertId(String expertId) {
        this.expertId = expertId;
    }
}
