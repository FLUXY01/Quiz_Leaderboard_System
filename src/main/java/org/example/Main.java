package org.example;

import org.example.model.LeaderBoardEntry;
import org.example.service.QuizService;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            String regNo = "RA2311003010846";

            QuizService service = new QuizService();

            List<LeaderBoardEntry> leaderboard = service.process(regNo);

            System.out.println("\nLeaderboard:");
            for (LeaderBoardEntry e : leaderboard) {
                System.out.println(e.getParticipant() + " -> " + e.getTotalScore());
            }

            service.submit(regNo, leaderboard);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}