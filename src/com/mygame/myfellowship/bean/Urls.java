package com.mygame.myfellowship.bean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.NameValuePair;

import android.text.TextUtils;
import android.util.Log;

public class Urls {
	
	public static final String SERVER_IP = "http://192.168.1.103:8080/loveon/service";
//	public static final String SERVER_IP = "http://204.152.218.57:8080/loveon/service";
//	public static final String SERVER_IP = "http://118.193.163.126:3306/loveon/service";

	public static String question_info = SERVER_IP + "?buss=getQueGroup";

	public static String register = SERVER_IP + "?buss=reg";
	
	public static String getuser = SERVER_IP + "?buss=getUser";
	
	public static String login = SERVER_IP + "?buss=getUserid";
	
	public static String ImageUrl = SERVER_IP + "?buss=getImageurl";
	
	
	public static String getUrlAppendPath(String url, NameValuePair... parmas) {
        StringBuilder sb = new StringBuilder(url);
        if (parmas != null) {
            if (!url.endsWith("&")) {
                sb.append("&");
            }
            for (int i = 0; i < parmas.length; i++) {
                String key = parmas[i].getName();
                String value = parmas[i].getValue();
                if (TextUtils.isEmpty(value)) {
                    value = "";
                    continue;
                }
                try {
                    value = URLEncoder.encode(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                sb.append(key).append("=").append(value);
                if (i != parmas.length - 1) {
                    sb.append("&");
                }
            }
        }
        Log.e("URL", sb.toString());
        return sb.toString();
    }

}
