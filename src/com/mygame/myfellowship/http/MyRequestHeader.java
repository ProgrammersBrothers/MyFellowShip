package com.mygame.myfellowship.http;

import org.apache.http.HttpRequest;

import android.content.Context;
import android.text.TextUtils;

import com.mygame.myfellowship.SelfDefineApplication; 



public class MyRequestHeader extends BaseReqeustHeader{ 

    public MyRequestHeader(Context context)
    {
        super(context); 
    }

    @Override
    public String getUid() {
		return null;
    }

    public static String getToken() {
    	if(SelfDefineApplication.getInstance().getUser() != null){
    		return SelfDefineApplication.getInstance().getUser().getToken();
    	}
    	return null;
    }

    @Override
    public String getLongitude() {
		return null;
    }

    @Override
    public String getLatitude() {
		return null;
    }

    @Override
    public String getPushToken() {
		return null;
    }

    @Override
    public String getSex() {
		return null;
    }

    @Override
    public String getLang() {
		return null;
    }
    
	public static String getUserName() {
		if(SelfDefineApplication.getInstance().getUser() != null){
			return SelfDefineApplication.getInstance().getUser().getUserName();
		}
		return null;
	}
    
    public static void addHeaders(HttpRequest request, AbsReqeustHeader headers) {
		if (!TextUtils.isEmpty(getUserName())) {
			request.addHeader("p", getUserName());
		}
		if (!TextUtils.isEmpty(getToken())) {
			request.addHeader("token", getToken());
		}
    }

}
