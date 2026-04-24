package org.example.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.*;
import org.example.util.HttpUtil;

import java.util.*;

public class QuizService {

    private static final String BASE_URL = "https://devapigw.vidalhealthtpa.com/srm-quiz-task";

    private final ObjectMapper mapper = new ObjectMapper();

    public List<LeaderBoardEntry> process(String regNo) throws Exception {

        Set<String> seen = new HashSet<>();
        Map<String, Integer> scores = new HashMap<>();

        for (int i = 0; i < 10; i++) {

            String url = BASE_URL + "/quiz/messages?regNo=" + regNo + "&poll=" + i;
            String response = HttpUtil.get(url);

            //System.out.println("POLL " + i + " RESPONSE: " + response); This is for debugging i used it when the error was coming to check the get response

            if (response == null || !response.trim().startsWith("{")) {
                System.out.println("Skipping invalid response...");
                if (i < 9) Thread.sleep(5000);
                continue;
            }

            APIResponse apiResponse = mapper.readValue(response, APIResponse.class);

            if (apiResponse.getEvents() != null) {
                for (Event e : apiResponse.getEvents()) {

                    String key = e.getRoundId() + "_" + e.getParticipant();

                    if (seen.add(key)) {
                        scores.put(
                                e.getParticipant(),
                                scores.getOrDefault(e.getParticipant(), 0) + e.getScore()
                        );
                    }
                }
            }

            if (i < 9) Thread.sleep(5000);
        }

        List<LeaderBoardEntry> leaderboard = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            leaderboard.add(new LeaderBoardEntry(entry.getKey(), entry.getValue()));
        }

        leaderboard.sort((a, b) -> b.getTotalScore() - a.getTotalScore());

        return leaderboard;
    }

    public void submit(String regNo, List<LeaderBoardEntry> leaderboard) throws Exception {

        Map<String, Object> request = new HashMap<>();
        request.put("regNo", regNo);

        List<Map<String, Object>> list = new ArrayList<>();

        for (LeaderBoardEntry e : leaderboard) {
            Map<String, Object> obj = new HashMap<>();
            obj.put("participant", e.getParticipant());
            obj.put("totalScore", e.getTotalScore());
            list.add(obj);
        }

        request.put("leaderboard", list);

        String json = mapper.writeValueAsString(request);

        String response = HttpUtil.post(BASE_URL + "/quiz/submit", json);

        System.out.println(
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                        mapper.readTree(response)
                )
        );
    }
}