package com.mygame.myfellowship;

import com.mygame.myfellowship.http.FinalHttp;
import com.mygame.myfellowship.info.User;
import android.app.Application;

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
	}
	
	
	

}
