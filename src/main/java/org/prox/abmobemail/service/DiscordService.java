package org.prox.abmobemail.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class DiscordService {

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendNotification(String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("content", content);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);


        String webhookUrl = "https://discord.com/api/webhooks/1253230025520713748/pFPNWgaxIErCt_YQud38u9IkcCYYOb1FZNHnqTIJQtzM3mgbZI4biMGtZblYzfPcnyN0";

        ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Message sent successfully");
        } else {
            System.out.println("Error sending message: " + response.getStatusCode());
        }
    }
}
