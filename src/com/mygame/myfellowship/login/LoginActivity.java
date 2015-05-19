package com.mygame.myfellowship.login;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mygame.myfellowship.BaseActivity;
import com.mygame.myfellowship.FriendListActivity;
import com.mygame.myfellowship.Question;
import com.mygame.myfellowship.R;
import com.mygame.myfellowship.SelfDefineApplication;
import com.mygame.myfellowship.bean.Constant.Preference;
import com.mygame.myfellowship.bean.Constant;
import com.mygame.myfellowship.bean.Response;
import com.mygame.myfellowship.bean.Urls;
import com.mygame.myfellowship.http.AjaxCallBack;
import com.mygame.myfellowship.http.AjaxParams;
import com.mygame.myfellowship.struct.StructBaseUserInfo;
import com.mygame.myfellowship.utils.AssetUtils;
import com.mygame.myfellowship.utils.SecurityMD5Util;
import com.mygame.myfellowship.utils.ToastHelper;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;
import com.pgyersdk.update.PgyUpdateManager;

/**
 * 过渡界面/登录界面
 * 
 * @author tom
 * 
 */
public class LoginActivity extends BaseActivity {

	// 用户名，密码框
	private EditText etUname, etPwd;
	private Button btnLoad,mButtonLogin;
	long startTime;
	String uname, pwd;

	// 判断缓存用户名和密码是否存在显示过渡元素或者手动登录元素
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		PgyUpdateManager.register(this,Constant.PgyerAPPID);// 集成蒲公英sdk应用的appId
		onFindView(true);
	}

	public void onFindView(boolean isLoginFail) {
		uname = preferences.getString(Preference.UNAME, null);
		pwd = preferences.getString(Preference.PWD, null);
		setContentView(R.layout.act_login);
		addRightBtn(R.string.sisn_up, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(intent);
			}
		});
		addBackBtn(R.string.welcome, new OnClickListener() {
			
			@Override
			public void onClick(View v) { 
				
			}
		});
		etUname = (EditText) findViewById(R.id.etUname);
		etPwd = (EditText) findViewById(R.id.etPwd);
		btnLoad = (Button) findViewById(R.id.btnLoad);
		mButtonLogin = (Button) findViewById(R.id.ButtonLogin);
		
		addTextWatcher(etUname, etPwd);
		
		etPwd.setText(pwd);
		etUname.setText(uname);
		if (!TextUtils.isEmpty(uname)) {
			etUname.setSelection(uname.length());
		}
	}

	private void addTextWatcher(EditText etUname2, EditText etPwd2) {
		 etUname2.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) { }
			
			@Override
			public void afterTextChanged(Editable s) {
				if(TextUtils.isEmpty(s.toString())){
					preferences.edit().putString(Preference.UNAME, null);
				}
			}
		});
		 
		 etPwd2.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) { }
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) { }
				
				@Override
				public void afterTextChanged(Editable s) {
					if(TextUtils.isEmpty(s.toString())){
						preferences.edit().putString(Preference.PWD, null);
					}
				}
			});
	}

	public void saveUser() {
		preferences.edit().putString(Preference.UNAME, uname).commit();
		preferences.edit().putString(Preference.PWD, pwd).commit();
	}

	// 跳转到忘记密码界面
	public void onSkipToFindPwd(View view) {
//		startActivity(new Intent(this, FindPasswordBack.class));
	}

	public void onHideInputClick(View view) {
//		hideInput();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (SelfDefineApplication.finishLogin) {
			SelfDefineApplication.finishLogin = false;
			finish();
		}
		super.onResume();
//		PgyFeedbackShakeManager.register(this, Constant.PgyerAPPID);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		PgyFeedbackShakeManager.unregister();
	}
	/**
	 * 请求登陆的相关接口
	 * 
	 * @param name
	 *            用户名
	 * @param pwd
	 *            加密后的密码
	 * @return 请求登陆参数
	 */
	private AjaxParams getLoginParams(String name, String pwd) {
		AjaxParams parameters = new AjaxParams();
		parameters.put("userName", name);
		parameters.put("password", pwd);
		parameters.put("pwd", this.pwd);
		parameters.put("appType", "android");
		return parameters;
	}

	/**
	 * 登录
	 * 
	 * @param view
	 */
	public void onLoginClick(final View view) {
		hideInput();
		uname = ((EditText) findViewById(R.id.etUname)).getText().toString()
				.trim();
		pwd = ((EditText) findViewById(R.id.etPwd)).getText().toString().trim();
//		uname = preferences.getString(Constant.USER_NAME, null);
//		pwd = preferences.getString(Constant.USER_PWD, null);
		if (TextUtils.isEmpty(uname)) {
			ToastHelper.ToastLg(R.string.username_empty, this);
			return;
		}

		if (TextUtils.isEmpty(pwd)) {
			ToastHelper.ToastLg(R.string.pwd_empty, this);
			return;
		}

		// 用户名长度限制16位以内
		if (uname.length() > 16) {
			ToastHelper.ToastSht(R.string.username_limit, getActivity());
			return;
		}

/*		if (uname.length() > 16 || pwd.length() < 6) {
			ToastHelper.ToastSht(R.string.pwd_length_limit, getActivity());
			return;
		}
		*/
		btnLoad.setText("登录中");
		saveUser();
		login(uname, pwd, false, true);
	}
	
	//基本用户信息解析
	void parseUserBaseInfo(StructBaseUserInfo l_StructBaseUserInfo ){
		preferences.edit().putString(Constant.USER_ID, l_StructBaseUserInfo.getUserid()).commit();
		preferences.edit().putString(Constant.NICK_NAME, l_StructBaseUserInfo.getNickname()).commit();
		preferences.edit().putString(Constant.Sex,l_StructBaseUserInfo.getSex()).commit();

		preferences.edit().putString(Constant.Age,l_StructBaseUserInfo.getBirthday()).commit();

		preferences.edit().putString(Constant.Height, l_StructBaseUserInfo.getStature()).commit();

//		preferences.edit().putString(Constant.IfChild,l_StructBaseUserInfo.getIfHaveChildren()).commit();
//	
//		preferences.edit().putString(Constant.IfMind, l_StructBaseUserInfo.getIfMindHaveChildren()).commit();
//
//		preferences.edit().putString(Constant.ThingAsk,l_StructBaseUserInfo.getSubstanceNeeds()).commit();
//
//		preferences.edit().putString(Constant.MarryNum, l_StructBaseUserInfo.getInLovePeriod()).commit();
//
//		preferences.edit().putString(Constant.Faith,l_StructBaseUserInfo.getFaith()).commit();
		
		Set<String> siteno = new HashSet<String>(); 
		if(l_StructBaseUserInfo.getCoordinates() != null){
			for(int i=0;i<l_StructBaseUserInfo.getCoordinates().size();i++){
				siteno.add(l_StructBaseUserInfo.getCoordinates().get(i));
			}
		}
		preferences.edit().putStringSet(Constant.Address,siteno).commit();
		
//		preferences.edit().putString(Constant.Freetime,l_StructBaseUserInfo.getSpareTime()).commit();
		
		preferences.edit().putString(Constant.Nature,l_StructBaseUserInfo.getMBTI()).commit();
		
		
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), FriendListActivity.class);
		startActivity(intent);
	}
	/**
	 * 登录
	 * 
	 * @param uname
	 *            用户名
	 * @param pwd
	 *            加密后的密码
	 * @param isShowLoading
	 *            是否显示loading框
	 */
	private void login(String uname, String pwd, boolean isBackLogin, boolean isShowLoading) {
		// 不需要获取公钥了。
		String encryptPwd = null;
		try {
			encryptPwd = SecurityMD5Util.getInstance().MD5Encode(pwd);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (TextUtils.isEmpty(encryptPwd)) {
//				ToastHelper.ToastLg(R.string.encrypt_fail, this);
				return;
			}
		}
		
		AjaxParams params = new AjaxParams();
		params.put("username",uname);
		params.put("password",pwd);
		
		String login = Urls.getUrlAppendPath(Urls.login, new BasicNameValuePair("username",uname),
				new BasicNameValuePair("password",pwd));
//		final LoginCallBack callback = new LoginCallBack(isBackLogin, btnLoad, user, LoginActivity.this, isShowLoading);
		getFinalHttp().get(login, new AjaxCallBack<String>(){

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
//				callback.parseData(t);
				parseData(t);
				cancelRequestDialog();
			}
			private void parseData(String t) {
					Response<StructBaseUserInfo> response = new Gson().fromJson(t, 
							new TypeToken<Response<StructBaseUserInfo>>(){}.getType());
					if(response.getResult()){
						StructBaseUserInfo aa = response.getResponse();
						
						parseUserBaseInfo(aa);
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
/*		getFinalHttp().get(Urls.login, getLoginParams(uname, encryptPwd),
				new LoginCallBack(isBackLogin, btnLoad, user, Login.this, isShowLoading));
		String logResult = AssetUtils.getDataFromAssets(this, "login.txt");*/
		
		
		
	}

}
