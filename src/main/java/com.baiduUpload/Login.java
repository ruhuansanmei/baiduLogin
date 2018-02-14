package com.baiduUpload;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.google.gson.Gson;

import java.io.*;
import java.util.*;

public class Login {
    static String bduss = "";
    static String u = "";
    static String ptoken = "";
    static String stoken = "";

    public static void main(String[] args) {
        Login login = new Login();
        login.baiduLogin();

        File file = new File("config.txt");
        if (!file.exists()) {
            file.mkdir();
        }

        try(FileOutputStream fileOutput = new FileOutputStream(file, true)) {
            FileInputStream fileInput = new FileInputStream(file);
            fileInput.available();

            fileOutput.write('\n');
            fileOutput.write(bduss.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 百度账号登录
     *
     * @return
     */

    public String baiduLogin() {
        LoginUtil loginUtil = new LoginUtil();

        Scanner input = new Scanner(System.in);
        System.out.print("输入账号密码：");
        String userName = input.next();
        String password = input.next();
        Map<String, Object> map = loginUtil.initHttpClient();

        String traceId = (String) map.get("traceid");
        String body = (String) map.get("body");
        HttpRequest request = (HttpRequest) map.get("request");

        Map<String, String> parameters = new HashMap<String, String>();

        parameters.put("traceid", traceId);
        parameters.put("username", userName);

        String codeString = loginUtil.checkCode(body, request);
        String verifyCode = "";
        if (!codeString.equals("")) {
            verifyCode = loginUtil.getVerification(codeString);
        }

        parameters.put("codestring", codeString);
        parameters.put("verifycode", verifyCode);

        String pubKey = loginUtil.getRSAKeyAndPubKey(request);
        if (pubKey == null) {
            System.out.println("获取密钥失败！");
        }
        parameters.put("pubkey", pubKey);
        String time = loginUtil.getServerTime();
        password = baiduEncoder.getEncrypt(password + time, pubKey);
        parameters.put("password", password);
        parameters.put("servertime", time);
        String cookie = login(parameters, request);

        return cookie;
    }

    private void getCookie(Map<String, String> parameters, HttpRequest request) {
        //发送登录请求
        String requestUrl = "https://wappass.baidu.com/wp/api/login?tt=" + new Date().getTime();
        HttpResponse response = null;
        String codeString = "";
        String verifyCode = "";
        boolean isFail = true;
        LoginUtil loginUtil = new LoginUtil();

        do {
            Map map2 = loginUtil.wrapBaiduLoginPostParameters(parameters);
            request = request.post(requestUrl);
            request.setFollowRedirects(true);
            request.header("Accept", "application/json");
            request.header("Content-Type", "application/x-www-form-urlencoded", true);
            request.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
            request.header("Referer", "https://wappass.baidu.com/");
            request.header("X-Requested-With", "XMLHttpRequest");
            request.header("Connection", "keep-alive");
            request.header("Cookie", "FP_UID=1d6a6d71daaf380db753b1fbdfbef5b1", false);
            request.charset("UTF-8");
            request.form(map2);
            response = request.execute();
            String body = response.body();
            String gotoUrl = "";

            Gson gson = new Gson();
            JsonMethod jsonMethod = gson.fromJson(body, JsonMethod.class);
            String error_no = jsonMethod.getErrInfo().getNo();

            if (error_no.equals("0")) {
                isFail = false;
            } else if (error_no.equals("500001") || error_no.equals("500002")) {   //输入校验码
                codeString = jsonMethod.getData().getCodeString();
                verifyCode = loginUtil.getVerification(codeString);
                parameters.put("codestring", codeString);
                parameters.put("verifycode", verifyCode);
            } else if (error_no.equals("400023") || error_no.equals("400101")) {  //手机或邮箱验证
                gotoUrl = jsonMethod.getData().getGotoUrl();
                String http = loginUtil.verificationIdentity(gotoUrl, request);
                response = request.get(http).execute();
                isFail = false;
            } else {
                System.out.println(jsonMethod.getErrInfo().getMsg());
            }
        } while (isFail);

        loginUtil.checkBaiduLoginResult(response);

        String body = response.body();
        Gson gson = new Gson();
        JsonMethod jsonMethod = gson.fromJson(body, JsonMethod.class);
        bduss = "BDUSS=" + jsonMethod.getData().getBduss();
        u = jsonMethod.getData().getU();
    }

    private String login(Map<String, String> parameters, HttpRequest request) {
        getCookie(parameters, request);
        request = request.post("https://pan.baidu.com/wap/home");
        request.setMaxRedirectCount(10);
        request.header("Connection", "keep-alive");
        request.header("User-Agent", "Mozilla/5.0 (SymbianOS/9.4; Series60/5.0 NokiaN97-1/20.0.019; Profile/MIDP-2.1 Configuration/CLDC-1.1) AppleWebKit/525 (KHTML, like Gecko) BrowserNG/7.1.18124");
        request.header("Upgrade-Insecure-Requests", "1");
        request.header("Accept", "*/*");
        request.cookie("BAIDUID=EC9599E66C95CD1B05F4E5A260BF6297:FG=1");
        request.cookie(bduss);
        HttpResponse response = request.execute();

        String body = response.body();
        System.out.println(body);
        System.out.println(u);

        response.close();

        return null;
    }
}
