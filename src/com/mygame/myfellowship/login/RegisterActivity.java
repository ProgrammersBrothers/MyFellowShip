package com.mygame.myfellowship.login;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.mygame.myfellowship.BaseActivity;
import com.mygame.myfellowship.R;
import com.mygame.myfellowship.bean.Constant;
import com.mygame.myfellowship.http.AjaxParams;
import com.mygame.myfellowship.utils.ToastHelper;

public class RegisterActivity extends BaseActivity{

	protected static final int INPUT_PWD = 1;
	protected static final int SEND_MSG_ERROR = 2;
	protected static final int SEND_MSG_SUCCESS = 3;
	protected static final int SEND_MSG_VERIFY_SUCCESS = 4;
	protected static final int VERIRY_CODE_ERROR = 5;
	Button btnRegister;
	Button btnVerify;
	private EditText phonEditText, phoneVerify;
	private EditText etPwd, etConfirmPwd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_verify);
		setTitle("注册");
		initView();
		SMSSDK.registerEventHandler(event);
	}
	
	
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int event =msg.arg1;
			int result=  msg.arg2;
			Object data = msg.obj;
			
			if (result == SMSSDK.RESULT_COMPLETE) {
				//短信注册成功后，返回MainActivity,然后提示新好友
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
					ToastHelper.ToastSht(R.string.verify_code_success ,getApplicationContext());
//					textView2.setText("提交验证码成功");
					requestRegister();
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					ToastHelper.ToastSht(R.string.verify_code_success, getApplicationContext());
//					textView2.setText("验证码已经发送");
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
		
	};
	
	EventHandler event = new EventHandler(){
		
		  @Override
          public void afterEvent(int event, int result, Object data) {
			  
			  Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				mHandler.sendMessage(msg);
		  }
	};
	
	
	
	private void initView() {
		btnVerify = (Button) findViewById(R.id.button1);
		phonEditText = (EditText)findViewById(R.id.phonEditText);
		phoneVerify = (EditText)findViewById(R.id.phoneVerify);
		etPwd = (EditText)findViewById(R.id.etPwd);
		etConfirmPwd = (EditText)findViewById(R.id.etConfirmPwd);
		
		btnVerify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				// 获取支持发送验证码的国家列表
				// 目前中国肯定支持的，所以就不验证中国了。
//				SMSSDK.getSupportedCountries();
				
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
		});
		
		addRightBtn(R.string.complete, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
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
				
				
				showReqeustDialog(R.string.sending_data);
				 // 提交验证码，成功，就跳到基本信息界面
				SMSSDK.submitVerificationCode("86", phone, verifyCode);
			}
		});
	}

	protected void requestRegister() {
		AjaxParams params = new AjaxParams();
		params.put("phone", phoneVerify.getText().toString());
		params.put("password", etConfirmPwd.getText().toString());
		preferences.edit().putString(Constant.USER_NAME, phoneVerify.getText().toString());
		preferences.edit().putString(Constant.USER_PWD, etPwd.getText().toString());
		
//		getFinalHttp().post(Urls.register, params, new AjaxCallBack<String>(){
//
//			@Override
//			public void onSuccess(String t) {
//				super.onSuccess(t);
				cancelRequestDialog();
				ToastHelper.ToastSht(R.string.register_success, getActivity());
				startActivity(new Intent(RegisterActivity.this, BasicInfoActivity.class));
				finish();
//			}
//
//			@Override
//			public void onFailure(Throwable t, int errorNo, String strMsg) {
//				super.onFailure(t, errorNo, strMsg);
//				ToastHelper.ToastLg(strMsg, getActivity());
//				cancelRequestDialog();
//			}
//		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 取消注册短信接收码
		SMSSDK.unregisterEventHandler(event);
	}
	
	
	
	
}
