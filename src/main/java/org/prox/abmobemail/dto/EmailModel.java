package org.prox.abmobemail.dto;

import lombok.Data;

@Data
public class EmailModel {
    private String typeMesseage;
    private String subject;
    private String from;
    private String fromFw;
    private String sentDate;
    private String appId;
    private String appName;

    public EmailModel() {
    }

    public EmailModel(String typeMesseage, String subject, String from, String fromFw, String sentDate, String appId, String appName) {
        this.typeMesseage = typeMesseage;
        this.subject = subject;
        this.from = from;
        this.fromFw = fromFw;
        this.sentDate = sentDate;
        this.appId = appId;
        this.appName = appName;
    }

    @Override
    public String toString() {
        return "  [ ----------------- " + typeMesseage + "-----------------] \n" +
                "Subject: " + subject + "\n" +
                "From: " + from + "\n" +
                "FromFw: " + fromFw + "\n" +
                "Sent Date: " + sentDate + "\n" +
                "App ID: " + appId + "\n" +
                "App Name: " + appName + "\n";
    }
}
