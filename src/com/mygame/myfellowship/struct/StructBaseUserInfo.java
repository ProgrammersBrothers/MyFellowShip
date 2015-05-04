package com.mygame.myfellowship.struct;

import java.util.List;

public class StructBaseUserInfo{
    private String sex;              //性别 1、男 2、女
    private String age;               //年龄 
    private String stature;          //身高
    private String IfHaveChildren;     //是否有孩子 1、有小孩 2、没小孩
    private String IfMindHaveChildren;    //是否介意有孩子  1.介意 2、不介意
    private String SubstanceNeeds;  		//物质要求  1、有房 2、没房
    private String InLovePeriod;      //恋爱期限  1、闪婚，2、三个月左右，3、半年左右，4、 一年左右，5、不着急结婚。
    private String Faith;      //信仰
    private List<String>  Coordinates;   //坐标  经度，纬度
    private String SpareTime;        	                 //空余时间  二进制表示00000001,共八位二进制数，前七位代表星期，比如第一位是1代表星期一，第二位为1代表星期二。
    private String MBTI;          //MBTI性格类型  1、NT ，2、NF ，3、SJ ，4、SP 
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getStature() {
		return stature;
	}
	public void setStature(String stature) {
		this.stature = stature;
	}
	public String getIfHaveChildren() {
		return IfHaveChildren;
	}
	public void setIfHaveChildren(String ifHaveChildren) {
		IfHaveChildren = ifHaveChildren;
	}
	public String getIfMindHaveChildren() {
		return IfMindHaveChildren;
	}
	public void setIfMindHaveChildren(String ifMindHaveChildren) {
		IfMindHaveChildren = ifMindHaveChildren;
	}
	public String getSubstanceNeeds() {
		return SubstanceNeeds;
	}
	public void setSubstanceNeeds(String substanceNeeds) {
		SubstanceNeeds = substanceNeeds;
	}
	public String getInLovePeriod() {
		return InLovePeriod;
	}
	public void setInLovePeriod(String inLovePeriod) {
		InLovePeriod = inLovePeriod;
	}
	public String getFaith() {
		return Faith;
	}
	public void setFaith(String faith) {
		Faith = faith;
	}
	public List<String> getCoordinates() {
		return Coordinates;
	}
	public void setCoordinates(List<String> coordinates) {
		Coordinates = coordinates;
	}
	public String getSpareTime() {
		return SpareTime;
	}
	public void setSpareTime(String spareTime) {
		SpareTime = spareTime;
	}
	public String getMBTI() {
		return MBTI;
	}
	public void setMBTI(String mBTI) {
		MBTI = mBTI;
	}
}