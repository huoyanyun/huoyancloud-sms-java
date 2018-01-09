package com.huoyancloud.sms;

public class SmsTest {

    /**
     * 请将参数替换成自己的
     * templateParam JSON字符串,如"{\"code\":\"1234\"}"
     */
    public static void main(String[] args) {
        SmsClient client = new SmsClient("XXXX", "XXX");
        String res = client.sendSms("XXXX", "XXXX", "XXXX", "XXXX", "XXX");
        System.out.println(res);
    }
}
