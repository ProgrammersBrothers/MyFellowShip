package com.mygame.myfellowship;

import android.app.Application;
import cn.jpush.android.api.JPushInterface;
import cn.smssdk.SMSSDK;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
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
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        //如果使用美国节点，请加上这行代码 AVOSCloud.useAVCloudUS();
        AVOSCloud.initialize(this, "5mim4lf83bmmk9n3gqnxnxeb2mlj5zmqdztcj92jf3l8mbo3", "xq1za9q5d9efrqkts0a14sxs0zry7zhzm34bd6ihxqfu49gs");
        AVObject testObject = new AVObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
	}
	
	
	

}
