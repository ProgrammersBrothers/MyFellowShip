package com.mygame.myfellowship;

import android.app.Application;
import cn.smssdk.SMSSDK;

import com.baidu.mapapi.SDKInitializer;
import com.mygame.myfellowship.http.FinalHttp;
import com.mygame.myfellowship.info.User;

public class SelfDefineApplication extends Application {

	public static boolean finishLogin;
	private static SelfDefineApplication application;
	private static FinalHttp finalHttp;
	
	public static SelfDefineApplication getInstance() {
		if(application == null){
			application = new SelfDefineApplication();
		}
		return application;
	}

	public User getUser() {
		return null;
	}

	public FinalHttp getFinalHttp() {
		if(finalHttp == null){
			finalHttp = new FinalHttp();
		}
		return finalHttp;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		finalHttp = getFinalHttp();
		SDKInitializer.initialize(this);//百度地图初始化
		SMSSDK.initSDK(this, getString(R.string.sharesdk_sms_app_key), getString(R.string.sharesdk_sms_app_secret));
	}
	
	
	

}
