package com.mygame.myfellowship.login;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.mygame.myfellowship.BaseActivity;
import com.mygame.myfellowship.R;

public class BasicInfoActivity extends BaseActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_container);
		setTitle("答题");
	}
	
	
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg); 
		}
		
	};
	 
	@Override
	protected void onDestroy() {
		super.onDestroy(); 
	}
}

