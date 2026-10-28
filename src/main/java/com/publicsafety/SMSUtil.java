package com.publicsafety;

public class SMSUtil {

    public static boolean sendSMS(String phoneNumber, String messageText) {
        System.out.println("==================================================");
        System.out.println("[SMS GATEWAY DISPATCHED]");
        System.out.println("TO: " + phoneNumber);
        System.out.println("MESSAGE: " + messageText);
        System.out.println("STATUS: 200 OK (Simulated Success)");
        System.out.println("==================================================");
        return true;
    }
} 