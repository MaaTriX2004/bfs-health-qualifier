package com.bajaj.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class WebhookRunner implements CommandLineRunner {

    @Autowired
    private RestTemplate restTemplate;

    // ==================================================================================
    // SQL SOLUTION FOR QUESTION 2
    // ==================================================================================
    // Logic Breakdown:
    // 1. We JOIN Department, Employee, and Payments tables.
    // 2. We FILTER for payments > 70000.
    // 3. We GROUP BY Department.
    // 4. We calculate AVG Age using TIMESTAMPDIFF(YEAR, DOB, CURDATE()).
    // 5. We create the String List using GROUP_CONCAT, ordered by Name.
    // 6. We use SUBSTRING_INDEX to keep only the first 10 names.
    // 7. We ORDER BY Department ID Descending.
    // ==================================================================================
    private static final String SQL_SOLUTION_Q2 = 
        "SELECT " +
        "    d.DEPARTMENT_NAME, " +
        "    AVG(TIMESTAMPDIFF(YEAR, e.DOB, CURDATE())) AS AVERAGE_AGE, " +
        "    SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) ORDER BY e.FIRST_NAME, e.LAST_NAME SEPARATOR ', '), ', ', 10) AS EMPLOYEE_LIST " +
        "FROM DEPARTMENT d " +
        "JOIN EMPLOYEE e ON d.DEPARTMENT_ID = e.DEPARTMENT " +
        "JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID " +
        "WHERE p.AMOUNT > 70000 " +
        "GROUP BY d.DEPARTMENT_ID, d.DEPARTMENT_NAME " +
        "ORDER BY d.DEPARTMENT_ID DESC;";

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> APP STARTED: Processing Task...");

        // ---------------------------------------------------------------
        // STEP 1: GENERATE WEBHOOK
        // ---------------------------------------------------------------
        String registerUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        // Using REG12348 (Even number) to ensure we get Question 2 logic requirements
        Map<String, String> regBody = new HashMap<>();
        regBody.put("name", "Nipun Poswal");
        regBody.put("regNo", "REG12348"); 
        regBody.put("email", "nipun@example.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> regRequest = new HttpEntity<>(regBody, headers);

        try {
            System.out.println(">>> Sending Registration Request...");
            ResponseEntity<Map> response = restTemplate.postForEntity(registerUrl, regRequest, Map.class);
            
            Map<String, Object> responseBody = response.getBody();
            
            if (responseBody == null) {
                System.out.println(">>> Error: Null response from registration.");
                return;
            }

            String webhookUrl = (String) responseBody.get("webhookUrl");
            String accessToken = (String) responseBody.get("accessToken");

            System.out.println(">>> Received Webhook URL: " + webhookUrl);
            System.out.println(">>> Received Token: " + accessToken);

            // ---------------------------------------------------------------
            // STEP 2: SUBMIT SOLUTION
            // ---------------------------------------------------------------
            
            HttpHeaders submitHeaders = new HttpHeaders();
            submitHeaders.setContentType(MediaType.APPLICATION_JSON);
            // Add the token to headers
            submitHeaders.set("Authorization", accessToken); 

            Map<String, String> submitBody = new HashMap<>();
            submitBody.put("finalQuery", SQL_SOLUTION_Q2);

            HttpEntity<Map<String, String>> submitRequest = new HttpEntity<>(submitBody, submitHeaders);

            System.out.println(">>> Submitting SQL Solution...");
            
            // Use the Webhook URL received in Step 1
            // If it's null (rare error case), use the fallback URL from the PDF
            String finalUrl = (webhookUrl != null) ? webhookUrl : "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
            
            ResponseEntity<String> submitResponse = restTemplate.postForEntity(finalUrl, submitRequest, String.class);
            
            System.out.println(">>> ====================================");
            System.out.println(">>> FINAL RESPONSE: " + submitResponse.getStatusCode());
            System.out.println(">>> BODY: " + submitResponse.getBody());
            System.out.println(">>> ====================================");

        } catch (Exception e) {
            System.err.println(">>> ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}