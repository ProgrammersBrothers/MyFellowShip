package com.mygame.myfellowship.activity;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mygame.myfellowship.BaseActivity;
import com.mygame.myfellowship.R;
import com.mygame.myfellowship.bean.Urls;
import com.mygame.myfellowship.http.AjaxCallBack;

public class BasicInfoActivity extends BaseActivity {

	ViewGroup viewGroup;
	private OnClickListener getVerifyCodeListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			getFinalHttp().get(Urls.verify_code, new AjaxCallBack<String>(){

				@Override
				public void onSuccess(String t) {
					super.onSuccess(t);
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
				}
			});
		}
	};
	
	@Override
	public void onCreate(Bundle saveInstanced){
//		setContentView(R.layout.act_basic_info);
		
		viewGroup = (ViewGroup) findViewById(R.id.llParent);
		addRegisterView();
	}

	private void addRegisterView() {
		 View regiterView = View.inflate(this, R.layout.register_verify, null);
		 EditText etUserName = (EditText) regiterView.findViewById(R.id.editText1);
		 EditText etVerify = (EditText) regiterView.findViewById(R.id.editText2);
		 Button btnVerify = (Button)regiterView.findViewById(R.id.button1);
		 viewGroup.addView(regiterView);
		 
		 btnVerify.setOnClickListener(getVerifyCodeListener);
		 
	}
}
