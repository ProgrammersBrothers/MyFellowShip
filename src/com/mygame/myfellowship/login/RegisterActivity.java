package com.mygame.myfellowship.login;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mygame.myfellowship.BaseActivity;
import com.mygame.myfellowship.FriendListActivity;
import com.mygame.myfellowship.Question;
import com.mygame.myfellowship.R;
import com.mygame.myfellowship.SelfDefineApplication;
import com.mygame.myfellowship.SelfDefineApplication.LocationListener;
import com.mygame.myfellowship.bean.CfgCommonType;
import com.mygame.myfellowship.bean.Constant;
import com.mygame.myfellowship.bean.Response;
import com.mygame.myfellowship.bean.Urls;
import com.mygame.myfellowship.gps.MyLocation;
import com.mygame.myfellowship.http.AjaxCallBack;
import com.mygame.myfellowship.http.AjaxParams;
import com.mygame.myfellowship.log.MyLog;
import com.mygame.myfellowship.struct.StructBaseUserInfo;
import com.mygame.myfellowship.struct.StructFriendListShowContent;
import com.mygame.myfellowship.utils.AssetUtils;
import com.mygame.myfellowship.utils.CharacterParse;
import com.mygame.myfellowship.utils.ToastHelper;
import com.mygame.myfellowship.utils.WheelViewUtil;
import com.mygame.myfellowship.utils.WheelViewUtil.OnCfgWheelListener;
import com.mygame.myfellowship.utils.WheelViewUtil.OnWheelViewListener;


/**
 * 注册界面，先注册账号和密码，接着填写信息
 * @author lenovo
 */
public class RegisterActivity extends BaseActivity implements OnClickListener{

	protected static final int INPUT_PWD = 1;
	protected static final int SEND_MSG_ERROR = 2;
	protected static final int SEND_MSG_SUCCESS = 3;
	protected static final int SEND_MSG_VERIFY_SUCCESS = 4;
	protected static final int VERIRY_CODE_ERROR = 5;
	protected static final int VERIFY_CODE = 10;
	protected static final int MBTI_TEST = 11;
	protected static final int FINISH_ASK = 12;
	Button btnNext, btnSubmit;
	Button btnVerify;
	EditText phonEditText, phoneVerify;
	EditText etPwd, etConfirmPwd;
	LinearLayout llRigGetInfo, llRigGetVerify;
	LinearLayout llMBTI;
	RadioGroup rgGender;
	TextView tvLocation, tvBirthday, tvMBTI, tvHeight;
	LinearLayout llBirthday;
	EditText etUserName, etEmail;
	StructBaseUserInfo mStructBaseUserInfo = new StructBaseUserInfo();
	List<String> coordinates = new ArrayList<String>();
	private CharacterParse mCharacterParse;
	protected int checkId;
	private List<Question> requestList;
	private ScrollView scrollview;
	private Dialog chooseDlg;
	List<CfgCommonType> highCcts = new ArrayList<CfgCommonType>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_verify);
		setTitle("注册");
		initView();
		SMSSDK.registerEventHandler(event);
		
		initData();
	}
	
	
	private void initData() {
		mCharacterParse = new CharacterParse();
		
		for(int i = 0; i < 50; i++){
			CfgCommonType cfg = new CfgCommonType();
			cfg.setName("" + (150 + i));
			highCcts.add(cfg);
		}
	}

	LocationListener listener = new LocationListener() {
		
		@Override
		public void onReceiveLocation(MyLocation myLocation) {
			coordinates.clear();
			coordinates.add(Double.toString(myLocation.getLongitude()));
			coordinates.add(Double.toString(myLocation.getLatitude()));
			cancelRequestDialog();
			tvLocation.setText(myLocation.getDetailAddress());
		}
	};
	

	private void initLocation() {
		showReqeustDialog(R.string.location);
		SelfDefineApplication.getInstance().startLocation(this, listener);
	}


	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case VERIFY_CODE:
				setMessageVerify(msg);
				break;
			case MBTI_TEST:
				showBMTIDlg(msg);
			case FINISH_ASK:
				ToastHelper.ToastSht("可以提交个人信息了", getActivity());
				break;
			default:
				break;
			}
		}
	};
	
	
	private void showBMTIDlg(Message msg) {
		final Question curQ = (Question) msg.obj;
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.dialog);
		builder.setTitle(curQ.getQuestion()); 
		String[] nItems = new String[]{};
		nItems = curQ.getAnswers().toArray(nItems);
		//  设置多选项
		int checkedItem = 0;
		
		builder.setSingleChoiceItems(nItems, checkedItem, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				chooseDlg.cancel();
				int typeInt = mCharacterParse.MTBITypeToInt(curQ.getAnswerstype().get(which));
				MyLog.i("--tom", "you choose which:" + which);
				mCharacterParse.setCharacterAndNum(typeInt);
				checkId ++;
				
				if(checkId < requestList.size()){
					Message msg = mHandler.obtainMessage();
					msg.what = MBTI_TEST;
					msg.obj = requestList.get(checkId);
					mHandler.sendMessage(msg);
				} else {
					mMBTIbigType = mCharacterParse.getCharacterType();
					mStructBaseUserInfo.setMBTI(mMBTIbigType+"");
					tvMBTI.setText(mMBTIbigType+"");
					mHandler.sendEmptyMessage(FINISH_ASK);
				}
			}
		});
		chooseDlg = builder.create();
		chooseDlg.show();
	}
	
	private void setMessageVerify(Message msg) {
		int event =msg.arg1;
		int result=  msg.arg2;
		Object data = msg.obj;
		
		if (result == SMSSDK.RESULT_COMPLETE) {
			//短信注册成功后，返回MainActivity,然后提示新好友
			if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
				ToastHelper.ToastSht(R.string.please_fill_basic_info, getActivity());
				requestRegister();
			} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
				ToastHelper.ToastSht(R.string.verify_code_success, getApplicationContext());
//						textView2.setText("验证码已经发送");
				cancelRequestDialog();
			}else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//返回支持发送验证码的国家列表
				ToastHelper.ToastSht(R.string.get_country_list_success, getApplicationContext());
				cancelRequestDialog();
			}
		} else {
			((Throwable) data).printStackTrace();
			ToastHelper.ToastSht(R.string.verify_code_error, RegisterActivity.this);
			cancelRequestDialog();
		}
	}
	
	
	EventHandler event = new EventHandler(){
		
		  @Override
          public void afterEvent(int event, int result, Object data) {
			  
			  Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				msg.what = VERIFY_CODE;
				mHandler.sendMessage(msg);
		  }
	};
	private int mMBTIbigType;
	
	
	private void initView() {
		btnVerify = (Button) findViewById(R.id.btnVerify);
		btnNext = (Button) findViewById(R.id.btnNext);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		phonEditText = (EditText)findViewById(R.id.phonEditText);
		phoneVerify = (EditText)findViewById(R.id.phoneVerify);
		etPwd = (EditText)findViewById(R.id.etPwd);
		etConfirmPwd = (EditText)findViewById(R.id.etConfirmPwd);
		llRigGetInfo = (LinearLayout)findViewById(R.id.llRigGetInfo);
		llRigGetVerify = (LinearLayout)findViewById(R.id.llRigGetVerify);
		rgGender = (RadioGroup)findViewById(R.id.rgGender);
		tvLocation = (TextView)findViewById(R.id.tvLocation);
		tvBirthday = (TextView)findViewById(R.id.tvBirthday);
		tvHeight = (TextView)findViewById(R.id.tvHeight);
		llBirthday = (LinearLayout)findViewById(R.id.llBirthday);
		etUserName = (EditText)findViewById(R.id.etUserName);
		etEmail = (EditText)findViewById(R.id.etEmail);
		llMBTI = (LinearLayout)findViewById(R.id.llMBTI);
		tvMBTI = (TextView)findViewById(R.id.tvMBTI);
		scrollview = (ScrollView)findViewById(R.id.scrollview);
		
		llRigGetVerify.setVisibility(View.VISIBLE);
		llRigGetInfo.setVisibility(View.GONE);
		llMBTI.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		tvLocation.setOnClickListener(this);
		btnVerify.setOnClickListener(this); 
		tvBirthday.setOnClickListener(this);
		tvHeight.setOnClickListener(this);
	}
	
	
	/**
	 * 获取验证码
	 */
	private void getVerifyCode() {
		// 获取支持发送验证码的国家列表
		// 目前中国肯定支持的，所以就不验证中国了。
		// SMSSDK.getSupportedCountries();
		String phone = phonEditText.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			ToastHelper.ToastSht(R.string.input_phone_number, getActivity());
			return;
		}
		
		if(phone.length() < 6 || phone.length() > 16){
			ToastHelper.ToastSht(R.string.phone_length_limit, getActivity());
			return;
		}
		
		showReqeustDialog(R.string.send_verify_code);
		SMSSDK.getVerificationCode("86",phonEditText.getText().toString());
	}

	/**
	 * 提交注册账号
	 */
	private void submitVerificationCode() {
		requestRegister();
		String phone = phonEditText.getText().toString().trim();
		String verifyCode = phoneVerify.getText().toString().trim();
		
		String pwd = etConfirmPwd.getText().toString().trim();
		String confirmPwd = etPwd.getText().toString().trim();
		
		if(TextUtils.isEmpty(phone)){
			ToastHelper.ToastSht(R.string.input_phone_number, getActivity());
			return;
		}
		if(TextUtils.isEmpty(verifyCode)){
			ToastHelper.ToastSht(R.string.input_verify_code, getActivity());
			return;
		}
		if(phone.length() < 6 &&  phone.length() > 16){
			ToastHelper.ToastSht(R.string.phone_length_limit, getActivity());
			return;
		}
		
		if(pwd.length() < 6 &&  pwd.length() > 16){
			ToastHelper.ToastSht(R.string.pwd_length_limit, getActivity());
			return;
		}
		
		if(confirmPwd.length() < 6 &&  confirmPwd.length() > 16){
			ToastHelper.ToastSht(R.string.pwd_length_limit, getActivity());
			return;
		}
		
		if(!confirmPwd.equals(pwd)){
			ToastHelper.ToastSht(R.string.pwd_not_equal_confirm_pwd, getActivity());
			return;
		}
		showReqeustDialog(R.string.create_accounting);
		// 提交验证码，成功，就跳到基本信息界面
		SMSSDK.submitVerificationCode("86", phone, verifyCode);
	}
	
	/**
	 * 提交注册账号和密码
	 */
	protected void requestRegister() {
		AjaxParams params = new AjaxParams();
		params.put("username", phonEditText.getText().toString());
		params.put("password", etConfirmPwd.getText().toString());
		String rig = Urls.getUrlAppendPath(Urls.register, new BasicNameValuePair("username", phonEditText.getText().toString()),
				new BasicNameValuePair("password", etConfirmPwd.getText().toString()));
		getFinalHttp().get(rig, new AjaxCallBack<String>(){

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				Response<String> response = new Gson().fromJson(t, 
						new TypeToken<Response<String>>(){}.getType());
				cancelRequestDialog();
				if(response.getResult()){
					preferences.edit().putString(Constant.USER_NAME, phonEditText.getText().toString()).commit();
					preferences.edit().putString(Constant.USER_PWD, etPwd.getText().toString()).commit();
					preferences.edit().putString(Constant.USER_ID, response.getMessage()).commit();
					mStructBaseUserInfo.setUserid(preferences.getString(Constant.USER_ID, response.getMessage()));
					ToastHelper.ToastSht(R.string.register_success, getActivity());
					changeView(true);
					initLocation();
				}else{
					ToastHelper.ToastLg(response.getMessage(), getActivity());
				}

			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				ToastHelper.ToastLg(strMsg, getActivity());
				cancelRequestDialog();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 取消注册短信接收码
		SMSSDK.unregisterEventHandler(event);
	}

	/**
	 * 根据标识显示和隐藏视图
	 * @param showInfo true 显示个人信息，隐藏注册, false 显示注册，隐藏个人信息
	 */
	private void changeView(boolean showInfo) {
		if(showInfo){
			llRigGetInfo.setVisibility(View.VISIBLE);
			llRigGetVerify.setVisibility(View.GONE);
		} else {
			llRigGetInfo.setVisibility(View.GONE);
			llRigGetVerify.setVisibility(View.VISIBLE);
		}
		scrollview.smoothScrollTo(0, 0);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.tvBirthday: // 获取验证码 
			WheelViewUtil.showWheelView(v.getContext(), (View)tvBirthday, ageListener, 
					"1988-01-01", "选择生日", false);
			break;
		case R.id.tvHeight: // 获取验证码 
			WheelViewUtil.showSingleWheel(getActivity(), v, highCcts, highListener, "选择身高", "身高");
			break;
		case R.id.tvLocation: // 获取验证码
			showReqeustDialog(R.string.location);
			SelfDefineApplication.getInstance().startLocation(this, listener);
			break;
		case R.id.btnVerify: // 获取验证码
			getVerifyCode();
			break;
		case R.id.btnNext: // 下一步，提交验证码，成功后再提交手机和密码
			submitVerificationCode();
			break;
		case R.id.btnSubmit: // 提交个人信息
			SubmitAllUserInfo();
			break;
		case R.id.llMBTI:
			String mbti = tvMBTI.getText().toString();
			if (TextUtils.isEmpty(mbti)) {
				// 显示MBTI对话框
				requestMBAIQuestion();
			}
			break;
		}
	}
	
	
	OnWheelViewListener ageListener = new OnWheelViewListener() {
		
		@Override
		public void doubleConfirm(String selectLeft, String selectRight) {
			 
		}
		
		@Override
		public void dateConfirm(String selectyear, String selectmonth,
				String selectday, int year, int month, int day) {
			 tvBirthday.setText(selectyear + "-" + selectmonth + "-" + selectday);
		}
		
		@Override
		public void Confirm(String select, int index) {
		}
	};
	
	
	OnCfgWheelListener highListener = new OnCfgWheelListener() {
		
		@Override
		public void CustomSalayConfirm(String min, String max) {
			 
		}
		@Override
		public void Confirm(CfgCommonType select, int index) {
			tvHeight.setText(select.getName());
		}
	};
	
	
	//解析题目json
	private int parseMbtiTopic(String t) {
		int result = 0;
		Response<List<Question>> response = new Gson().fromJson(t, 
				new TypeToken<Response<List<Question>>>(){}.getType());
		if(response.getResult()){
			requestList = response.getResponse();
			Message msg = new Message();
			msg.what = MBTI_TEST;
			msg.obj = requestList.get(0);
			checkId = 0;
			mHandler.sendMessage(msg);
		}else{
			result = -1;
			ToastHelper.ToastLg(response.getMessage(), getActivity());
		}
		return result;
	}
	
	protected void requestMBAIQuestion() {
		AjaxParams params = new AjaxParams();
		params.put("userid", preferences.getString(Constant.USER_ID, "adcd"));
		getFinalHttp().post(Urls.question_info,params, new AjaxCallBack<String>(){

			@Override
			public void onStart() {
				super.onStart();
				showReqeustDialog(R.string.requestion_topic);
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				cancelRequestDialog();
				parseMbtiTopic(t);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				cancelRequestDialog();
			}
			
		});
	}
	
	
	//提交用户信息
	void SubmitAllUserInfo(){
		
		// 性别
		String gender = "2";
		if(rgGender.getCheckedRadioButtonId() == R.id.rbMale){
			gender = "1";
		}
		mStructBaseUserInfo.setSex(gender);
		
		// 位置
		String detailAdd = tvLocation.getText().toString();
		if(TextUtils.isEmpty(detailAdd)){
			ToastHelper.ToastSht(R.string.please_get_location, getActivity());
			return;
		}
		
		// 位置
		mStructBaseUserInfo.setCoordinates(coordinates);
		
		// 生日 
		String birthday = tvBirthday.getText().toString();
		if(TextUtils.isEmpty(birthday)){
			ToastHelper.ToastSht(R.string.choose_birthday, getActivity());
			return;
		}
		mStructBaseUserInfo.setBirthday(birthday);
		// 身高
		String height = tvHeight.getText().toString();
		if(TextUtils.isEmpty(height)){
			ToastHelper.ToastSht(R.string.please_choose_height, getActivity());
			return;
		}
		mStructBaseUserInfo.setStature(height);
		// 昵称
		String nickName = etUserName.getText().toString();
		if(TextUtils.isEmpty(nickName)){
			ToastHelper.ToastSht(R.string.please_input_nickname, getActivity());
			return;
		}
		mStructBaseUserInfo.setNickname(nickName);
		// 邮箱
		String email = etEmail.getText().toString();
		
		
		if(TextUtils.isEmpty(email)){
			ToastHelper.ToastSht(R.string.please_input_email, getActivity());
			return;
		} else if(!email.contains("@")){
			ToastHelper.ToastSht("请输入合法邮箱", getActivity());
			return;
		}
		
		mStructBaseUserInfo.setEmial(email);
		// MBTI测试题
		String mbti = tvMBTI.getText().toString();
		
		if(TextUtils.isEmpty(mbti)){
			ToastHelper.ToastSht(R.string.get_emti_test, getActivity());
			return;
		}
		
		mMBTIbigType = mCharacterParse.getCharacterType();
		mStructBaseUserInfo.setMBTI(mMBTIbigType+"");
 
		String getjson = new Gson().toJson(mStructBaseUserInfo);
		Log.i("huwei", getjson);
		preferences.edit().putString(Constant.USER_INFO, getjson).commit();
		
		AjaxParams params = new AjaxParams();
		params.put("userMsg", getjson);
		showReqeustDialog(R.string.sending_data);
		getFinalHttp().post(Urls.getuser, params, new AjaxCallBack<String>(){

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				cancelRequestDialog();
				Response<List<StructFriendListShowContent>> response = new Gson().fromJson(t, 
						
						new TypeToken<Response<List<StructFriendListShowContent>>>(){}.getType());
				if(response.getResult()){
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), FriendListActivity.class);
					startActivity(intent);
					finish();
				}else{
					ToastHelper.ToastLg(response.getMessage(), getActivity());
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				ToastHelper.ToastLg(strMsg, getActivity());
				cancelRequestDialog();
			}
		});
		
	}
  
}
