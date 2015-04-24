package com.mygame.myfellowship.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mygame.myfellowship.Constant;
import com.mygame.myfellowship.LoginModel;
import com.mygame.myfellowship.MainActivity;
import com.mygame.myfellowship.R;
import com.mygame.myfellowship.ToastHelper;
import com.mygame.myfellowship.bean.Response;

/**
 * @discription 登录接口回调处理过程
 * @author tom
 */
public class LoginCallBack extends LoadingCallBack<String> {

	Button btnLoad;
	LoginModel user;
	Login activity;
	ProgressDialog pDialog;
	Boolean isBackLogin;

	public LoginCallBack(Boolean isBackLogin, Button btnLoad, LoginModel user,
			Login context, boolean isShowLoading) {
		super(context);
		if(isShowLoading){
			setShowDialog();
		}
		this.btnLoad = btnLoad;
		this.user = user;
		this.activity = context;
		this.isBackLogin = isBackLogin;
	}

	@Override
	public void onStart() {
		setDialogMessage(activity.getString(R.string.loginggg));
		super.onStart();
	}

	@Override
	public void onSuccess(String result) {
		super.onSuccess(result);
		Response<LoginModel> response = new Gson().fromJson(result,
				new TypeToken<Response<LoginModel>>() {
				}.getType());

		if (!isBackLogin) {
			btnLoad.setEnabled(true);
			btnLoad.setText(R.string.login);
		}
		
		try {
			long loginTime = System.currentTimeMillis() - activity.startTime;
			if (loginTime < Constant.MIN_LOGINTIME) {
				Thread.sleep(Constant.MIN_LOGINTIME - loginTime);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			ToastHelper.ToastLg("网络异常，请检查网络设置", activity);
		}

		if ("C0000".equals(response.getCode())) {
//			activity.saveUser();
//			UserUtils.saveUserInfo(activity, response.getResponse());
			user = response.getResponse();
			// 设置了城市和意向
		} else {
			activity.onFindView(false);
			ToastHelper.ToastLg(response.getMessage(), activity);
		}
	}

	@Override
	public void onFailure(Throwable t, int errorNo, String strMsg) {
		super.onFailure(t, errorNo, strMsg);

		activity.onFindView(false);
		ToastHelper.ToastLg(strMsg, activity);
	}
}
