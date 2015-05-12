package com.mygame.myfellowship.bean;

public class Urls {
	
	public static final String SERVER_IP = "http://204.152.218.57:8080/loveon/service";
//	public static final String SERVER_IP = "http://192.168.1.101:8080/loveon/service";

	public static String question_info = SERVER_IP + "?buss=getQueGroup";

	public static String register = SERVER_IP + "?buss=reg";
	
	public static String getuser = SERVER_IP + "?buss=getUser";
	
	public static String login = SERVER_IP + "?buss=getUserid";

}
