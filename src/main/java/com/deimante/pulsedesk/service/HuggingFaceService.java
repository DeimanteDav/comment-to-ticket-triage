package com.deimante.pulsedesk.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

@Service
public class HuggingFaceService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final tools.jackson.databind.ObjectMapper objectMapper = new ObjectMapper();

    @Value("${huggingface.api.token}")
    private String hfToken;

    private static final String URL = "https://router.huggingface.co/v1/chat/completions";
    private static final String MODEL = "Qwen/Qwen3-8B";

    private String callAI(String userPrompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(hfToken);

            ObjectNode body = objectMapper.createObjectNode();
            body.put("model", MODEL);
            body.put("max_tokens", 150);

            ObjectNode thinking = objectMapper.createObjectNode();
            thinking.put("type", "disabled");
            body.set("thinking", thinking);

            ArrayNode messages = objectMapper.createArrayNode();
            ObjectNode msg = objectMapper.createObjectNode();
            msg.put("role", "user");
            msg.put("content", userPrompt);
            messages.add(msg);
            body.set("messages", messages);

            HttpEntity<String> request = new HttpEntity<>(
                    objectMapper.writeValueAsString(body), headers
            );

            ResponseEntity<String> response = restTemplate.postForEntity(URL, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.path("choices").get(0)
                        .path("message").path("content")
                        .asText().trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean shouldCreateTicket(String commentText) {
        String prompt =
                "You are a support triage assistant. Does the following user comment " +
                        "describe a technical problem, bug, billing issue, or account issue " +
                        "that requires a support ticket? Reply with ONLY 'YES' or 'NO'.\n\n" +
                        "Comment: " + commentText;

        String result = callAI(prompt);

        if (result != null) {
            return result.toUpperCase().startsWith("YES");
        }

        return false;
    }

    public String[] generateTicketData(String commentText) {
        String prompt =
                "You are a support ticket generator. Analyze the comment below and respond " +
                        "with EXACTLY 4 lines, nothing else:\n" +
                        "Line 1: A short ticket title (max 10 words)\n" +
                        "Line 2: Category — one of: bug, feature, billing, account, other\n" +
                        "Line 3: Priority — one of: low, medium, high\n" +
                        "Line 4: A one-sentence summary of the issue\n\n" +
                        "Comment: " + commentText;

        String result = callAI(prompt);

        if (result != null) {
            String[] lines = result.split("\\n");
            if (lines.length >= 4) {
                return new String[]{
                        lines[0].trim(),
                        lines[1].trim(),
                        lines[2].trim(),
                        lines[3].trim()
                };
            }
        }

        return new String[]{
                "Support Ticket",
                "other",
                "medium",
                commentText.length() > 100 ? commentText.substring(0, 100) + "..." : commentText
        };
    }
}