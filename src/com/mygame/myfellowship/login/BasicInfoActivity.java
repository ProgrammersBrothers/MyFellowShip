package com.mygame.myfellowship.login;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mygame.myfellowship.BaseActivity;
import com.mygame.myfellowship.Question;
import com.mygame.myfellowship.R;
import com.mygame.myfellowship.bean.Constant;
import com.mygame.myfellowship.bean.Response;
import com.mygame.myfellowship.bean.Urls;
import com.mygame.myfellowship.http.AjaxCallBack;
import com.mygame.myfellowship.utils.AssetUtils;

public class BasicInfoActivity extends BaseActivity {

	RadioGroup group;
	private List<Question> requestList;
	private int currentQId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_container);
		setTitle("答题");
		
		initView();
		
		requestBasicTopic();
	}
	
	
	private void requestBasicTopic() {
		 String t = AssetUtils.getDataFromAssets(this, "question.txt");
		 parseBasicTopic(t);
	}


	private void parseBasicTopic(String t) {
		 
		Response<List<Question>> response = new Gson().fromJson(t, 
				
				new TypeToken<Response<List<Question>>>(){}.getType());
		
		if(response.getResult(this)){
			requestList = response.getResponse();
			currentQId = 0;
			mHandler.sendEmptyMessage(0);
		}
	}


	private void initView() {
		
		 group = (RadioGroup)findViewById(R.id.rgroup);
		 
		 addRightBtn(R.string.next_topic, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int checkId = group.getCheckedRadioButtonId();
				
				if(currentQId < requestList.size() - 1){
					saveAnsers(checkId);
					currentQId ++;
					mHandler.sendEmptyMessage(currentQId);
				} else {
					showTestEMBI();
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
			preferences.edit().putString(Constant.Sex, checkId + "").commit();
		} else if("0002".equals(qId)){ // 年龄
			preferences.edit().putString(Constant.Age, curQ.getAnswers().get(checkId)).commit();
		} else if("0003".equals(qId)){ // 身高
			preferences.edit().putString(Constant.Height, curQ.getAnswers().get(checkId)).commit();
		} else if("0004".equals(qId)){ // 小孩
			preferences.edit().putString(Constant.IfChild, checkId + "").commit();
		} else if("0005".equals(qId)){ // 匹配对象有孩子?
			preferences.edit().putString(Constant.IfMind, checkId + "").commit();
		} else if("0006".equals(qId)){ // 物质要求
			preferences.edit().putString(Constant.ThingAsk, checkId + "").commit();
		} else if("0007".equals(qId)){ // 谈恋爱时间期限
			preferences.edit().putString(Constant.MarryNum, curQ.getAnswers().get(checkId)).commit();
		} else if("0008".equals(qId)){ // 你看好中国的经济吗
			preferences.edit().putString(Constant.Faith1, checkId + "").commit();
		} else if("0009".equals(qId)){
			preferences.edit().putString(Constant.Faith2, checkId + "").commit();
		} else if("0010".equals(qId)){
			// 读取faith1，faith2，去三个最多的
			preferences.edit().putString(Constant.Faith, checkId + "").commit();
		} else if("1002".equals(qId)){ // qId以1开头的，都可以算作MBAI性格测试题 .. 后续继续保存数据，并计算性格测试结果
			// todo @胡威，加油
		}
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
			}
		}).create().show();
	}


	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg); 
			int curId = msg.what;
			initRadioGroupView(curId);
		}
		
	};
	 
	@Override
	protected void onDestroy() {
		super.onDestroy(); 
	}


	protected void initRadioGroupView(int curId) {
		Question q = requestList.get(curId);
		int id = 0;
		group.removeAllViews();
		group.clearCheck();
		for(String answer : q.getAnswers()){
			RadioButton rBtn = (RadioButton) View.inflate(this, R.layout.radio_button, null);
			rBtn.setId(id+1);
			rBtn.setText(answer);
			group.addView(rBtn);
			id++;
		}
	}
}

