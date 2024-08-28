package com.example.demo.helpers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {
    private final String customField;

    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.customField = request.getParameter("customField");
    }

    public String getCustomField() {
        return customField;
    }
}
