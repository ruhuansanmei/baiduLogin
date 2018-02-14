package com.baiduUpload;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.http.HttpBase;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.google.gson.Gson;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginUtil {
    String token;
    String type;
    String u;
    /**
     * 初始化cookie
     * @return
     */
    public static Map<String, Object> initHttpClient() {

        String url = "https://wappass.baidu.com/";
        HttpRequest request = HttpUtil.createGet(url);
        request.header("Accept", "*/*");
        request.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
        HttpResponse response = request.execute();
        String body = response.body();
        String traceId = response.headerList("Trace-Id").get(0);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("traceid", traceId);
        map.put("request", request);
        map.put("body", body);

        return map;
    }

    /**
     * 百度登录获得RSA加密公钥和Pubkey
     *
     * @param request
     * @return
     */

    public String getRSAKeyAndPubKey(HttpRequest request) {
        String baseUrl = "https://wappass.baidu.com/static/touch/js/login_d9bffc9.js";
        request = request.get(baseUrl);
        HttpResponse response = request.execute();
        String body = response.body();
        Pattern pattern = Pattern.compile(",rsa:\"(.*?)\",error:");
        Matcher matcher = pattern.matcher(body);
        String pubKey = "";
        if (matcher.find()) {
            pubKey = matcher.group(1).trim();
        }

        return pubKey;

    }

    /**
     * 通过Cookies检查百度账号是否登录成功
     *
     * @param response
     * @return
     */

    public String checkBaiduLoginResult(HttpResponse response) {
        List list = response.headerList("Set-Cookie");
        String cookie = "";
        boolean loginResult = false;

        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                cookie = cookie + ";" + list.get(i).toString().split(";")[0];
            }
            cookie = cookie.substring(1, cookie.length());
        }

        if (cookie != null) {
            int index = cookie.indexOf("BDUSS");
            if (index >= 0) {
                int firstIndex = cookie.indexOf("=", index) + 1;
                int lastIndex = cookie.indexOf(";", firstIndex);
                String bduss = cookie.substring(firstIndex, lastIndex);
                System.out.println(bduss);
                loginResult = true;
            }
        }

        if (loginResult) {
            System.out.println("登录成功！");
        } else {
            System.out.println("登录失败！");
        }

        return cookie;
    }

    /**
     * 显示校验码，用户输入校验码
     * @param codeString
     * @return
     */
    public String getVerification(String codeString) {
        String http = "https://wappass.baidu.com/cgi-bin/genimage?" + codeString;
        System.out.println(http);
        Scanner input = new Scanner(System.in);
        System.out.print("输入校验码：");
        String verifyCode = input.next();
        return verifyCode;
    }

    /**
     * 身份验证
     * @param gotoUrl
     * @param request
     * @return
     */
    public String verificationIdentity(String gotoUrl, HttpRequest request) {
        request = request.get(gotoUrl);
        HttpResponse response = request.execute();
        String body = response.body();
        String rawPhone = null;
        String rawEmail = null;
        String rawTokenUType = null;

        Matcher matcher = Pattern.compile("<p class=\"verify-type-li-tiptop\">(.*?)</p>\\s+<p class=\"verify-type-li-tipbottom\">通过手机验证码验证身份</p>").matcher(body);
        if (matcher.find()) {
            rawPhone = matcher.group(1);
        }
        matcher = Pattern.compile("<p class=\"verify-type-li-tiptop\">(.*?)</p>\\s+<p class=\"verify-type-li-tipbottom\">通过邮箱验证码验证身份</p>").matcher(body);
        if (matcher.find()) {
            rawEmail = matcher.group(1);
        }

        matcher = Pattern.compile("<li class=\"verify-type-li clearfix\" data-location=\"(.*?)\"").matcher(body);
        if (matcher.find()) {
            rawTokenUType = matcher.group(1);
        }

        String[] tokens = rawTokenUType.split("[?&=]");
        for (int i = 0; i < tokens.length; i++) {
            String s = tokens[i];

            if (s.equals("token")) {
                token = tokens[i + 1];
            }

            if (s.equals("type")) {
                type = tokens[i + 1];
            }

            if (s.equals("u")) {
                u = tokens[i + 1];
            }
        }

        try {

            if (!(rawEmail.length() > 0)) {
                System.out.println("没有找到手机号！");
            }

            if (!(rawPhone.length() > 0)) {
                System.out.println("没有找到邮箱！");
            }
        } catch (Exception e) {
            e.getMessage();
        }

        int code = sendCodeToUser(request);
        while (code == -1) {
            code = sendCodeToUser(request);
        }

        String http = verificationCode(request);
        if (http == null) {
            System.out.println("请重新输入!");
            http = verificationCode(request);
        }

        return http;
    }

    /**
     * 发送手机或邮箱验证码
     * @param request
     * @return
     */
    public int sendCodeToUser(HttpRequest request) {
        String http = "https://wappass.baidu.com/passport/authwidget?action=send&tpl=&type=%s&token=%s&from=" +
                "&skin=&clientfrom=&adapter=2&updatessn=&bindToSmsLogin=&upsms=&finance=";

        http = String.format(http, type, token);
        request = request.get(http);
        HttpResponse response = request.execute();
        String body = response.body();

        Matcher matcher = Pattern.compile("<p class=\"mod-tipinfo-subtitle\">\\s+(.*?)\\s+</p>").matcher(body);
        if (matcher.find()) {
            System.out.println(matcher.group(1));
            return 0;
        }

        return -1;
    }

    /**
     * 输入验证码
     * @param request
     * @return
     */
    private String verificationCode(HttpRequest request) {
        String http = "https://wappass.baidu.com/passport/authwidget?v=1501743656994&vcode=%s&token=%s&u=%s" +
                "&action=check&type=%s&tpl=&skin=&clientfrom=&adapter=2&updatessn=&bindToSmsLogin=" +
                "&isnew=&card_no=&finance=&callback=jsonp1";

        Scanner input = new Scanner(System.in);
        System.out.print("输入校验码：");
        String vcode = input.next();
        http = String.format(http, vcode, token, u, type);

        request = request.get(http);
        request.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
        request.header("Connection", "keep-alive");
        request.header("Host", "wappass.baidu.com");
        request.header("Pragma", "no-cache");
        request.header("Upgrade-Insecure-Requests", "1");

        HttpResponse response = request.execute();
        String body = response.body();

        Gson gson = new Gson();
        JsonMethod jsonMethod = gson.fromJson(body, JsonMethod.class);
        String no = jsonMethod.getErrInfo().getNo();

        if (no.equals("-2")) {
            System.out.println(jsonMethod.getErrInfo().getMsg());
            return null;
        }

        return jsonMethod.getData().getU();
    }

    /**
     * 登录检查是否输入校验码
     * @param body
     * @param request
     * @return
     */
    public String checkCode(String body, HttpRequest request) {
        Matcher matcher = Pattern.compile("<input type=\"hidden\" id=\"login-vcodestr\" name=\"vcodestr\" value=\"(.*?)\"/>").matcher(body);
        if (matcher.find()) {
            String codeString = matcher.group(1).trim();
            return codeString;
        }

        return "";
    }

    /**
     * 封装登录参数
     * @param parameters
     */
    public Map<String, String> wrapBaiduLoginPostParameters(Map<String, String> parameters) {
        Map<String, String> parameterMap = new HashMap<String, String>();
        String time = parameters.get("servertime");
        String password = parameters.get("password");
        String verifyCode = parameters.get("verifycode");
        parameterMap.put("username", parameters.get("username"));
        parameterMap.put("password", password);
        parameterMap.put("verifycode", verifyCode);
        parameterMap.put("vcodestr", parameters.get("codestring"));
        parameterMap.put("action", "login");
        parameterMap.put("u", "https%3A%2F%2Fpan.baidu.com%2Fwap%2Fwelcome%3FrealName%3D1%26uid%3D1518520710367_361%26traceid%3DE334D903");
        parameterMap.put("tpl", "netdisk");
        parameterMap.put("tn", "");
        parameterMap.put("pu", "");
        parameterMap.put("ssid", "");
        parameterMap.put("from", "");
        parameterMap.put("bd_page_type", "");
        parameterMap.put("uid", "1518520710367_361");
        parameterMap.put("type", "");
        parameterMap.put("regtype", "");
        parameterMap.put("subpro", "");
        parameterMap.put("adapter", "0");
        parameterMap.put("skin", "default_v2");
        parameterMap.put("regist_mode", "");
        parameterMap.put("login_share_strategy", "");
        parameterMap.put("client", "");
        parameterMap.put("clientfrom", "");
        parameterMap.put("connect", "0");
        parameterMap.put("bindToSmsLogin", "");
        parameterMap.put("isphone", "0");
        parameterMap.put("loginmerge", "1");
        parameterMap.put("getpassUrl", "/passport/getpass?clientfrom=&adapter=0&ssid=&from=&authsite=1&bd_page_type=&uid=1518520710367_361&pu=&tpl=netdisk&u=https://pan.baidu.com/wap/welcome%3FrealName%3D1%26uid%3D1518520710367_361%26traceid%3DE334D903&type=&bdcm=d8160febcb1349549023dd54564e9258d0094ae0&tn=&regist_mode=&login_share_strategy=&subpro=&skin=default_v2&client=&connect=0&smsLoginLink=1&loginLink=&bindToSmsLogin=&overseas=1&is_voice_sms=&subpro=&traceid=E334D903&hideSLogin=&forcesetpwd=&nousername=&regdomestic=1");
        parameterMap.put("dv", "tk0.408376350146535171516806245342@oov0QqrkqfOuwaCIxUELn3oYlSOI8f51tbnGy-nk3crkqfOuwaCIxUou2iobENoYBf51tb4Gy-nk3cuv0ounk5vrkBynGyvn1QzruvN6z3drLJi6LsdFIe3rkt~4Lyz5ktfn1Qlrk5v5D5fOuwaCIxUobJWOI3~rkt~4Lyi5kBfni0vrk8~n15fOuwaCIxUobJWOI3~rkt~4Lyz5DQfn1oxrk0v5k5eruvN6z3drLneFYeVEmy-nk3c-qq6Cqw3h7CChwvi5-y-rkFizvmEufyr1By4k5bn15e5k0~n18inD0b5D8vn1Tyn1t~nD5~5T__ivmCpA~op5gr-wbFLhyFLnirYsSCIAerYnNOGcfEIlQ6I6VOYJQIvh515f51tf5DBv5-yln15f5DFy5myl5kqf5DFy5myvnktxrkT-5T__Hv0nq5myv5myv4my-nWy-4my~n-yz5myz4Gyx4myv5k0f5Dqirk0ynWyv5iTf5DB~rk0z5Gyv4kTf5DQxrkty5Gy-5iQf51B-rkt~4B__");
        parameterMap.put("countrycode", "");
        parameterMap.put("mobilenum", "undefined");
        parameterMap.put("servertime", time);
        parameterMap.put("gid", "5FC3A37-C5D5-40C8-A7DC-CA1E2D0E867D");
        parameterMap.put("logLoginType", "wap_loginTouch");
        parameterMap.put("FP_UID", "1d6a6d71daaf380db753b1fbdfbef5b1");
        parameterMap.put("traceid", "E334D903");

        return parameterMap;

    }

    public String getServerTime() {
        String http = "https://wappass.baidu.com/wp/api/security/antireplaytoken";

        HttpRequest request = HttpUtil.createGet(http);
        String body = request.execute().body();
        Matcher matcher = Pattern.compile(",\"time\":\"(.*?)\"").matcher(body);
        String time;
        if (matcher.find()) {
            time = matcher.group(1);
            return time;
        }
        time = "e362bacbae";
        return time;
    }
}
