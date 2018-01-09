package com.huoyancloud.sms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SmsClient {

    private String accessKeyId = "";

    private String accessKeySecret = "";

    private String model = "";

    private String domain = "";

    public SmsClient(String accessKeyId, String accessKeySecret) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.model = "sms";
        init();
    }

    public SmsClient(String accessKeyId, String accessKeySecret, String model) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.model = model;
        init();
    }

    private void init() {
        this.domain = "b.dev.huoyancloud.com/api/" +
                this.model +
                "/";
    }

    private String genUrl(HashMap<String, String> params, boolean security, String accessKeyId, String accessKeySecret, String domain) {
        if (accessKeyId == null || accessKeyId.equals("")) {
            accessKeyId = this.accessKeyId;
        }
        if (accessKeySecret == null || accessKeySecret.equals("")) {
            accessKeySecret = this.accessKeySecret;
        }
        if (domain == null || domain.equals("")) {
            domain = this.domain;
        }
        if ((accessKeyId == null || accessKeyId.equals("")) || (accessKeySecret == null || accessKeySecret.equals("")) || (domain == null || domain.equals(""))) {
            return null;
        }
        HashMap<String, String> paras = new HashMap<String, String>();
        paras.put("SignatureMethod", "HMAC-SHA1");
        paras.put("SignatureNonce", UUID.randomUUID().toString());
        paras.put("AccessKeyId", accessKeyId);
        paras.put("SignatureVersion", "1.0");
        SimpleDateFormat df = new SimpleDateFormat();
        paras.put("Timestamp", df.format(new Date()));
        paras.putAll(params);
        if (paras.containsKey("Signature"))
            paras.remove("Signature");
        TreeMap<String, String> sortParas = new TreeMap<String, String>();
        sortParas.putAll(paras);
        Iterator<String> it = sortParas.keySet().iterator();
        StringBuilder sortQueryStringTmp = new StringBuilder();
        while (it.hasNext()) {
            String key = it.next();
            sortQueryStringTmp.append(URLEncoder.encode(key)).append("=").append(URLEncoder.encode(paras.get(key))).append("&");
        }
        String sortedQueryString = sortQueryStringTmp.toString();
        System.out.println(sortedQueryString);
        String sign = sha1(sortedQueryString + accessKeySecret);
        String schema = "http";
        if (security) {
            schema = "https";
        }
        return schema + "://" + domain + "?" + sortQueryStringTmp + "Signature=" + sign;
    }


    public String sendSms(String phoneNumber, String signName, String templateCode, String templateParam, String outId) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("PhoneNumbers", phoneNumber);
        params.put("SignName", signName);
        params.put("TemplateCode", templateCode);
        params.put("TemplateParam", templateParam);
        params.put("OutId", outId);
        params.put("Action", "SendSms");
        return request(params);
    }

    public String querySendDetails(String phoneNumber, String bizId, String sendDate, String pageSize, String currentPage) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("PhoneNumbers", phoneNumber);
        params.put("BizId", bizId);
        params.put("SendDate", sendDate);
        params.put("PageSize", pageSize);
        params.put("CurrentPage", currentPage);
        params.put("Action", "QuerySendDetails");
        return request(params);
    }

    private String request(HashMap<String, String> params) {
        return request(params, false, "", "", "");
    }

    private String request(HashMap<String, String> params, boolean security, String accessKeyId, String accessKeySecret, String domain) {
        String url = genUrl(params, security, accessKeyId, accessKeySecret, domain);
        if (url == null || url.equals("")) {
            return "配置错误";
        }
        URL obj;
        try {
            obj = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "参数错误";
        }
        HttpURLConnection con;
        try {
            con = (HttpURLConnection) obj.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return "网络异常";
        }
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        try {
            int responseCode = con.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (in == null) {
            return null;
        }
        String inputLine;
        StringBuilder response = new StringBuilder();
        try {
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    private static String sha1(String content) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-1");
            digest.update(content.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String shaHex = Integer.toHexString(aMessageDigest & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
