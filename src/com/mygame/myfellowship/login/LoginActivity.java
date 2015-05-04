package com.mygame.myfellowship.login;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.mygame.myfellowship.BaseActivity;
import com.mygame.myfellowship.R;
import com.mygame.myfellowship.SelfDefineApplication;
import com.mygame.myfellowship.bean.Constant.Preference;
import com.mygame.myfellowship.bean.Constant;
import com.mygame.myfellowship.bean.Urls;
import com.mygame.myfellowship.http.AjaxCallBack;
import com.mygame.myfellowship.http.AjaxParams;
import com.mygame.myfellowship.struct.StructBaseUserInfo;
import com.mygame.myfellowship.utils.AssetUtils;
import com.mygame.myfellowship.utils.SecurityMD5Util;
import com.mygame.myfellowship.utils.ToastHelper;

/**
 * 过渡界面/登录界面
 * 
 * @author tom
 * 
 */
public class LoginActivity extends BaseActivity {

	// 用户名，密码框
	private EditText etUname, etPwd;
	private Button btnLoad;
	long startTime;
	String uname, pwd;

	// 判断缓存用户名和密码是否存在显示过渡元素或者手动登录元素
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 第一次使用进入引导页
//		if (!preferences.getBoolean(Preference.SHOW_GUIDE, false)) {
//			Intent intent = new Intent(this, Guide.class);
//			intent.putExtra(Extra.BOOLEAN_KEY, true);
//			startActivity(intent);
//			finish();
//		} else {
			onFindView(true);
//		}
	}

	public void onFindView(boolean isLoginFail) {
		uname = preferences.getString(Preference.UNAME, null);
		pwd = preferences.getString(Preference.PWD, null);
		if (!TextUtils.isEmpty(uname) && !TextUtils.isEmpty(pwd) && isLoginFail) {
			setContentView(R.layout.activity_welcom);
//			 默认登陆
			hideInput();
			startTime = System.currentTimeMillis();
			login(uname, pwd, true, false);
		} else {
			setContentView(R.layout.act_login);
			setTitle(getString(R.string.login));
			etUname = (EditText) findViewById(R.id.etUname);
			etPwd = (EditText) findViewById(R.id.etPwd);
			btnLoad = (Button) findViewById(R.id.btnLoad);
			
			addTextWatcher(etUname, etPwd);
			
//			getFinalHttp().configTimeout(10*1000);
			
			etPwd.setText(pwd);
			etUname.setText(uname);
			if (!TextUtils.isEmpty(uname)) {
				etUname.setSelection(uname.length());
			}
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
//		MyLog.i("33333333333333333", pwd);
//		preferences.edit().putString(Preference.UNAME, uname).commit();
//		preferences.edit().putString(Preference.PWD, pwd).commit();
//		String pwdsss = preferences.getString(Preference.PWD, "");
//		MyLog.i("444444444444", pwdsss);
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
	 * 跳转到注册界面
	 * 
	 * @param view
	 */
	public void onRegisterClick(final View view) {
		Intent intent = new Intent(this, BasicInfoActivity.class);
		startActivity(intent);
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
		uname = preferences.getString(Constant.USER_NAME, null);
		pwd = preferences.getString(Constant.USER_PWD, null);
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

		if (uname.length() > 16 || pwd.length() < 6) {
			ToastHelper.ToastSht(R.string.pwd_length_limit, getActivity());
			return;
		}
		
		btnLoad.setText("登录中");
		saveUser();
		login(uname, pwd, false, true);
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
		params.put("buss", "getUserid");
		params.put("username",uname);
		params.put("password",pwd);
//		final LoginCallBack callback = new LoginCallBack(isBackLogin, btnLoad, user, LoginActivity.this, isShowLoading);
		getFinalHttp().post(Urls.login, params, new AjaxCallBack<String>(){

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
//				callback.parseData(t);
				parseData(t);
				cancelRequestDialog();
			}
			private void parseData(String t) {
				try {
					JSONObject json = new JSONObject(t);
					if("00".equals(json.get("respCode"))) {
						ToastHelper.ToastLg("登录成功!", getApplicationContext());
						String userId = json.getString("respMsg");
						Log.i("--tom", "userId:" + userId);
						preferences.edit().putString(Constant.USER_ID, userId).commit();
					}
					else if("E01".equals(json.get("respCode"))){
						ToastHelper.ToastLg(json.getString("respMsg"), getApplicationContext());
					}
					else if("E02".equals(json.get("respCode"))){
						ToastHelper.ToastLg(json.getString("respMsg"), getApplicationContext());
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
