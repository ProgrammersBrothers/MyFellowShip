package com.mygame.myfellowship.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mygame.myfellowship.BaseActivity;
import com.mygame.myfellowship.Question;
import com.mygame.myfellowship.R;
import com.mygame.myfellowship.bean.Constant;
import com.mygame.myfellowship.bean.Response;
import com.mygame.myfellowship.bean.Urls;
import com.mygame.myfellowship.gps.LocationUtil;
import com.mygame.myfellowship.gps.MyLocation;
import com.mygame.myfellowship.http.AjaxCallBack;
import com.mygame.myfellowship.struct.StructBaseUserInfo;
import com.mygame.myfellowship.utils.AssetUtils;
import com.mygame.myfellowship.utils.CharacterParse;
import com.mygame.myfellowship.utils.ToastHelper;

public class BasicInfoActivity extends BaseActivity {

	RadioGroup group;
	TextView tvQuestion;
	private List<Question> requestList;
	private int currentQId;
	
	final int HANDLE_BASETOPIC = 0x1001;//基本信息
	final int HANDLE_MBTI = 0x1002;//MBTI性格测试
	
	CharacterParse mCharacterParse;
	private int questionType = 1;//题目类型 1、代表课选择的基本信息 2、MBTI性格测试题  3、空余时间  4  、坐标
	private String mMBTIbigType = "";
	//定位参数
	private MyLocation myLocation = new MyLocation();
	private LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	
	//用户基本信息结构体
	StructBaseUserInfo mStructBaseUserInfo = new StructBaseUserInfo();
	
	private void locationInit() {
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
	}
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location != null){
				myLocation.setLatitude(location.getLatitude());
				myLocation.setLongitude(location.getLongitude());
				if(myLocation.getLatitude() == 0.0 && myLocation.getLongitude() == 0.0){
				}else{
					//得到经纬度
				}
				Log.d("huwei", "地理位置更新，纬度 = " + location.getLatitude()+"，经度 = "+location.getLongitude());
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_container);
		setTitle("答题");
		mCharacterParse = new CharacterParse();
		initView();
		locationInit();
		if(!mLocClient.isStarted()){
			mLocClient.start();
		}
		requestBasicTopic();
	}
	
	
	private void requestBasicTopic() {
		 String t = AssetUtils.getDataFromAssets(this, "question.txt");
		 questionType = 1;
		 parseBasicTopic(t);
	}

	//解析题目json
	private void parseBasicTopic(String t) {
		Response<List<Question>> response = new Gson().fromJson(t, 
				
				new TypeToken<Response<List<Question>>>(){}.getType());
		if(response.getResult(this)){
			requestList = response.getResponse();
			currentQId = 0;
			Message msg = new Message();
			msg.what = HANDLE_BASETOPIC;
			msg.arg1 = currentQId;
			mHandler.sendMessage(msg);
		}
	}


	private void initView() {
		
		 group = (RadioGroup)findViewById(R.id.rgroup);
		 tvQuestion = (TextView)findViewById(R.id.tvQuestion);
		 
		 addRightBtn(R.string.next_topic, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int checkId = group.getCheckedRadioButtonId();
				if(checkId <= 0){
					ToastHelper.ToastLg("没有选择答案", getApplicationContext());
					return;
				}
				//基本可选择信息填写
				if(questionType == 1){
					if(currentQId < requestList.size() - 1){
						saveAnsers(checkId-1);
						currentQId ++;
						Message msg = new Message();
						msg.what = HANDLE_BASETOPIC;
						msg.arg1 = currentQId;
						mHandler.sendMessage(msg);	
					}else{
						showTestEMBI();
					}

				} 
				else if(questionType == 2){
					if(currentQId < requestList.size() - 1){
						saveAnsers(checkId-1);
						currentQId ++;
						Message msg = new Message();
						msg.what = HANDLE_BASETOPIC;
						msg.arg1 = currentQId;
						mHandler.sendMessage(msg);	
					}else{
						mMBTIbigType = mCharacterParse.getCharacterType();
						mStructBaseUserInfo.setMBTI(mMBTIbigType);
					}
						
				}
			}
		});
	}


	/**
	 * 保存答案
	 * @param checkId
	 */
	protected void saveAnsers(int checkId) {
		Question curQ = requestList.get(currentQId);
		String qId = curQ.getQuesionId();
		if("0001".equals(qId)){ // 性别
			preferences.edit().putString(Constant.Sex,curQ.getAnswerstype().get(checkId)).commit();
			mStructBaseUserInfo.setSex(curQ.getAnswerstype().get(checkId));
		} else if("0002".equals(qId)){ // 年龄
			preferences.edit().putString(Constant.Age,curQ.getAnswerstype().get(checkId)).commit();
			mStructBaseUserInfo.setAge(curQ.getAnswerstype().get(checkId));
		} else if("0003".equals(qId)){ // 身高
			preferences.edit().putString(Constant.Height, curQ.getAnswerstype().get(checkId)).commit();
			mStructBaseUserInfo.setStature(curQ.getAnswerstype().get(checkId));
		} else if("0004".equals(qId)){ // 小孩
			preferences.edit().putString(Constant.IfChild, curQ.getAnswerstype().get(checkId)).commit();
			mStructBaseUserInfo.setIfHaveChildren(curQ.getAnswerstype().get(checkId));
		} else if("0005".equals(qId)){ // 匹配对象有孩子?
			preferences.edit().putString(Constant.IfMind, curQ.getAnswerstype().get(checkId)).commit();
			mStructBaseUserInfo.setIfMindHaveChildren(curQ.getAnswerstype().get(checkId));
		} else if("0006".equals(qId)){ // 物质要求
			preferences.edit().putString(Constant.ThingAsk, curQ.getAnswerstype().get(checkId)).commit();
			mStructBaseUserInfo.setSubstanceNeeds(curQ.getAnswerstype().get(checkId));
		} else if("0007".equals(qId)){ // 谈恋爱时间期限
			preferences.edit().putString(Constant.MarryNum, curQ.getAnswerstype().get(checkId)).commit();
			mStructBaseUserInfo.setInLovePeriod(curQ.getAnswerstype().get(checkId));
		} else if("0008".equals(qId)){ // 你看好中国的经济吗
			preferences.edit().putString(Constant.Faith1, curQ.getAnswerstype().get(checkId)).commit();
		} else if("0009".equals(qId)){
			preferences.edit().putString(Constant.Faith2, curQ.getAnswerstype().get(checkId)).commit();
		} else if("0010".equals(qId)){
			// 读取faith1，faith2，去三个最多的
			preferences.edit().putString(Constant.Faith3, curQ.getAnswerstype().get(checkId)).commit();
			String Faith1 = preferences.getString(Constant.Faith1, "A");
			String Faith2 = preferences.getString(Constant.Faith2, "A");
			String Faith3 = preferences.getString(Constant.Faith3, "A");
			String FaithType = getFaithType(Faith1, Faith2, Faith3);
			mStructBaseUserInfo.setFaith(FaithType);
		} 
		// qId以1开头的，都可以算作MBAI性格测试题 .. 后续继续保存数据，并计算性格测试结果
		if(questionType == 2){
			int typeInt = mCharacterParse.MTBITypeToInt(curQ.getAnswerstype().get(checkId));
			mCharacterParse.setCharacterAndNum(typeInt);
		}
		//保存坐标
		if(questionType == 4){
			List<String> coordinates = new ArrayList<String>();
			coordinates.add(Double.toString(myLocation.getLongitude()));
			coordinates.add(Double.toString(myLocation.getLatitude()));
			mStructBaseUserInfo.setCoordinates(coordinates);
		}
				
		
	}
	//计算信仰类型
	String getFaithType(String Faith1,String Faith2,String Faith3){
		int a = 0;
		int b = 0;
		if(Faith1.equals("A")){
			a++;
		}else{
			b++;
		}
		if(Faith2.equals("A")){
			a++;
		}else{
			b++;
		}
		if(Faith3.equals("A")){
			a++;
		}else{
			b++;
		}
		return (a>b)?"A":"B";
	}
	protected void requestMBAIQuestion() {
		getFinalHttp().post(Urls.question_info, new AjaxCallBack<String>(){

			@Override
			public void onStart() {
				super.onStart();
				showReqeustDialog(R.string.requestion_topic);
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				questionType = 2;
				parseBasicTopic(t);
				cancelRequestDialog();
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				cancelRequestDialog();
			}
			
		});
	}


	protected void showTestEMBI() {
		 new AlertDialog.Builder(this).setTitle("提示")
		 .setMessage("基本信息答完，继续测试MBAI?")
		 .setPositiveButton("前往", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				 requestMBAIQuestion();
//				String t = AssetUtils.getDataFromAssets(getApplicationContext(), "question.txt");
//				parseBasicTopic(t);
			}
		}).create().show();
	}


	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg); 
			int curId = msg.what;
			switch (curId) {
			case HANDLE_BASETOPIC:
				BasicInfoRadioGroupView(msg.arg1);
				break;
			default:
				break;
			}
			
		}
		
	};
	 
	@Override
	protected void onDestroy() {
		super.onDestroy(); 
		if(mLocClient.isStarted()){
			mLocClient.stop();
		}
		mLocClient.unRegisterLocationListener(myListener);
	}

	//下一个题目显示
	protected void BasicInfoRadioGroupView(int curId) {
		Question q = requestList.get(curId);
		int id = 0;
		group.removeAllViews();
		group.clearCheck();
		tvQuestion.setText(q.getQuestion());
		for(String answer : q.getAnswers()){
			RadioButton rBtn = (RadioButton) View.inflate(this, R.layout.radio_button, null);
			rBtn.setId(id+1);
			rBtn.setText(answer);
			group.addView(rBtn);
			id++;
		}
	}
	void testJson(){
		StructBaseUserInfo mStructBaseUserInfo = new StructBaseUserInfo();
		mStructBaseUserInfo.setAge("12");
		mStructBaseUserInfo.setFaith("dfd");
		List<String> coordinates = new ArrayList<String>();
		coordinates.add("24.56");
		coordinates.add("108.2325");
		mStructBaseUserInfo.setCoordinates(coordinates);
		
		String getjson = new Gson().toJson(mStructBaseUserInfo);
		Log.i("huwei", getjson);
	}
}

