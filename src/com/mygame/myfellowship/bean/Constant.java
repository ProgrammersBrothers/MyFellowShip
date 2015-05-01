package com.mygame.myfellowship.bean;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

public class Constant {

	public static final long MIN_LOGINTIME = 0;
	public static class Preference {

		public static final String LOGIN_USER = null;
		public static final String UNAME = null;
		public static final String PWD = null;

		public static SharedPreferences getSharedPreferences(Context context) {
			return context.getSharedPreferences("MyFellowShipSharePref", Context.MODE_PRIVATE);
		}
	}
	public static String USER_NAME = "user_name";
	public static String USER_PWD = "user_pwd";
	public static String Sex = "Sex";
	public static String Age = "Age";
	public static String Height = "Height";
	public static String IfChild = "IfChild";
	public static String IfMind = "IfMind";
	public static String Address = "Address";
	public static String Coord = "Coord";
	public static String ThingAsk = "ThingAsk";
	public static String MarryNum = "MarryNum";
	public static String Freetime = "Freetime";
	public static String Faith = "Faith";
	public static String Nature = "Nature";
	

}
