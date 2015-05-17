package com.mygame.myfellowship.bean;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

public class Constant {

	public static final long MIN_LOGINTIME = 0;
	public static class Preference {

		public static final String LOGIN_USER = "LOGIN_USER";
		public static final String UNAME = "UNAME";
		public static final String PWD = "PWD";

		public static SharedPreferences getSharedPreferences(Context context) {
			return context.getSharedPreferences("MyFellowShipSharePref", Context.MODE_PRIVATE);
		}
	}
	public static String USER_NAME = "username";
	public static String NICK_NAME = "nickname";
	public static String USER_PWD = "password";
	public static String USER_ID = "userid";
	public static String Sex = "Sex";
	public static String Age = "Age";//代表出生日期
	public static String Height = "Height";
	public static String IfChild = "IfChild";
	public static String IfMind = "IfMind";
	public static String Address = "Address";//坐标
	public static String Coord = "Coord";
	public static String ThingAsk = "ThingAsk";
	public static String MarryNum = "MarryNum";
	public static String Freetime = "Freetime";
	public static String Nature = "Nature";//性格
	public static String Faith1 = "Faith1";
	public static String Faith2 = "Faith2";
	public static String Faith3 = "Faith3";
	public static String Faith = "Faith";
	public static String USER_INFO = "USER_INFO";
	public static final String PgyerAPPID="3a05e1af690dbc648dd964b293ddc7a2";// 集成蒲公英sdk应用的appId

}
