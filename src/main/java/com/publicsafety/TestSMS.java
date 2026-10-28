package com.publicsafety;

public class TestSMS {
    public static void main(String[] args) {
        // Test sending directly to your registered Fast2SMS number
        boolean result = SMSUtil.sendSMS("7680837657", "TEST ALERT: Fast2SMS direct test!");
        System.out.println("Result: " + result);
    }
}
