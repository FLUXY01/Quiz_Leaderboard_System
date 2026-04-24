package org.example.model;

public class LeaderBoardEntry {
    private String participant;
    private int totalScore;

    public LeaderBoardEntry(String participant, int totalScore) {
        this.participant = participant;
        this.totalScore = totalScore;
    }

    public String getParticipant() { return participant; }
    public int getTotalScore() { return totalScore; }
}