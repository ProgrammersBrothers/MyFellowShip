package com.mygame.myfellowship.struct;

import java.util.List;

public class StructBaseUserInfo{
	public  String userid = "";
    private String sex = "";              //性别 1、男 2、女
    private String age = "";               //年龄       备注：根据出生日期自动计算
    private String birthday="";            //出生年月      例如：1989-07-07
    private String stature = "";          //身高           例如： 150
    private String IfHaveChildren = "";     //是否有孩子 1、有小孩 2、没小孩
    private String IfMindHaveChildren = "";    //是否介意有孩子  1.介意 2、不介意
    private String SubstanceNeeds = "";  		//物质要求  1、有房 2、没房
    private String InLovePeriod = "";      //恋爱期限  1、闪婚，2、三个月左右，3、半年左右，4、 一年左右，5、不着急结婚。
    private String Faith = "";      //信仰    A或者B
    private List<String> Coordinates;   //坐标  经度，纬度     例如：[90.274776,27.596344]
    private String SpareTime = "";   //空余时间  二进制表示00000001,共八位二进制数，前七位代表星期，比如第一位是1代表星期一，第二位为1代表星期二。
    private String MBTI = "";          //MBTI性格类型  1、NT ，2、NF ，3、SJ ，4、SP
    private String nickname="";  //昵称
    private String meter=""; //体重     例如：60
    private String vipType = "";//诚意      1，免费会员  2，普通会员 3，白金会员 4，钻石会员 5 门票会员
    private String hobby="";//嗜好      1，认同 2，否定 3，中立
    private String marrigestatus="";  //婚恋状态     1，未婚 2，离婚
    private String userimage="";  //图片路径   
    private String email=""; //邮箱
    
    
	public String getSubstranceNeeds() {
		return SubstanceNeeds;
	}
	public void setSubstranceNeeds(String substranceNeeds) {
		SubstanceNeeds = substranceNeeds;
	}
	
	
	public String getUserimage() {
		return userimage;
	}
	public void setUserimage(String userimage) {
		this.userimage = userimage;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getStature() {
		return stature;
	}
	public void setStature(String stature) {
		this.stature = stature;
	}
//	public String getIfHaveChildren() {
//		return IfHaveChildren;
//	}
//	public void setIfHaveChildren(String ifHaveChildren) {
//		IfHaveChildren = ifHaveChildren;
//	}
//	public String getIfMindHaveChildren() {
//		return IfMindHaveChildren;
//	}
//	public void setIfMindHaveChildren(String ifMindHaveChildren) {
//		IfMindHaveChildren = ifMindHaveChildren;
//	}
//	public String getSubstanceNeeds() {
//		return SubstanceNeeds;
//	}
//	public void setSubstanceNeeds(String substanceNeeds) {
//		SubstanceNeeds = substanceNeeds;
//	}
//	public String getInLovePeriod() {
//		return InLovePeriod;
//	}
//	public void setInLovePeriod(String inLovePeriod) {
//		InLovePeriod = inLovePeriod;
//	}
//	public String getFaith() {
//		return Faith;
//	}
//	public void setFaith(String faith) {
//		Faith = faith;
//	}
	public List<String> getCoordinates() {
		return Coordinates;
	}
	public void setCoordinates(List<String> coordinates) {
		Coordinates = coordinates;
	}
//	public String getSpareTime() {
//		return SpareTime;
//	}
//	public void setSpareTime(String spareTime) {
//		SpareTime = spareTime;
//	}
	public String getMBTI() {
		return MBTI;
	}
	public void setMBTI(String mBTI) {
		MBTI = mBTI;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public void setEmial(String email) {
		this.email = email;
	}
	public void setHasChild(String string) {
		this.IfHaveChildren = string;
	}
	public void setOHasChild(String string) {
		this.IfHaveChildren = string;
	}
	public void setMarryTime(String string) {
		this.InLovePeriod = string;
	}
	public void setMessHabit(String string) {
		this.hobby  = string;
	}
	public void setWeight(String weight) {
		this.meter = weight;
	}
	public void setFreeTime(String freeTime) {
		this.SpareTime = freeTime;
	}
	public void setMarryStatus(String string) {
		this.marrigestatus = string;
	}
}