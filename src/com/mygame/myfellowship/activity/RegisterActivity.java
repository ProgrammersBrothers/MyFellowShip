package com.mygame.myfellowship.activity;

import android.os.Bundle;

import com.mygame.myfellowship.BaseActivity;
import com.mygame.myfellowship.R;

public class RegisterActivity extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 请求所有的答题(包括当前答题题号)
		// 显示当前的答题
		// 提交保存当前答题的题号
		
		setContentView(R.layout.register_verify);
		initView(); 
	}
	
	private void initView() {
		
	}
	
}
