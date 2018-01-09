package com.huoyancloud.sms;

public class SmsTest {

    public static void main(String[] args) {
        SmsClient client = new SmsClient("6e836d16aeafa2", "30750fdea49360a3ab31285ea7e48d60");
        client.sendSms("18618134755", "火眼云", "SMS_111710105", "{\"code\":111}");
    }
}
