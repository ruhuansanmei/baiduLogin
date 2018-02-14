package com.baiduUpload;

import cn.hutool.core.text.StrBuilder;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileReader;
import java.math.BigInteger;

public class baiduEncoder {

    /**
     * 生成gid
     * @return
     */
    /*public static String getGid() {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        String path = "F:/project/BaiDuUpload/src/main/java/com.baiduUpload/element.js";
        String guid = null;
        try {
            engine.eval(new FileReader(path));
            if (engine instanceof Invocable) {
                Invocable invocable = (Invocable) engine;
                Methods baiduEncoder = invocable.getInterface(Methods.class);
                guid = baiduEncoder.gidRandom();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return guid;
    }*/

    /**
     * RSA加密密码
     *
     * @param pwd
     * @param pubkey
     * @return
     */
    public static String getEncrypt(String pwd, String pubkey) {
        pwd = baiduEncoder.getReverse(pwd);
        BigInteger bigInteger = new BigInteger(pubkey, 16);
        int a = 0x10001;
        BigInteger big = new BigInteger(String.valueOf(a));
        byte[] bytes = pwd.getBytes();
        BigInteger bigPw = new BigInteger(bytes);
        BigInteger bi = bigPw.modPow(big, bigInteger);
        String s = bi.toString(16);

        return s;
    }

    /**
     * 返回颠倒的字符串
     * @param reverse
     * @return
     */
    public static String getReverse(String reverse) {
        StringBuffer stringBuffer = new StringBuffer(reverse);
        reverse = stringBuffer.reverse().toString();

        return reverse;
    }
}
