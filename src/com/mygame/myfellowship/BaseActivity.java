package com.mygame.myfellowship;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import cn.jpush.android.api.JPushInterface;

import com.avos.avoscloud.AVAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mygame.myfellowship.bean.Constant.Preference;
import com.mygame.myfellowship.http.FinalHttp;
import com.mygame.myfellowship.info.User;
import com.mygame.myfellowship.log.MyLog;
import com.mygame.myfellowship.view.RequestDialog;
import com.mygame.myfellowship.view.SelfDefineActionBar;
import com.mygame.myfellowship.view.SelfDefineActionBar.IProvideActionBar;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author 
 * 
 */
public class BaseActivity extends FragmentActivity implements
		IProvideActionBar {
	public static String tag = BaseActivity.class.getSimpleName();
	public SharedPreferences preferences;
	public User user;
	public ImageLoader imageLoader = ImageLoader.getInstance();
	private FinalHttp http;
	protected SelfDefineActionBar actionBar;
	protected RequestDialog requestDialog;

	@Override
	public SelfDefineActionBar getSelfDefActionBar() {
		actionBar = (SelfDefineActionBar) findViewById(R.id.tkActionBar);
		return actionBar;
	}
	
	public Activity getActivity() {
		return this;
	}

	/**
	 * 显示对话框
	 * @param strId
	 */
	public void showReqeustDialog(int strId){
		if(requestDialog == null){
			requestDialog = new RequestDialog(this);
		}
		requestDialog.setCancelable(false);
		requestDialog.setMessage(getString(strId));
		
		if(!requestDialog.isShowing()){
			requestDialog.show();
		}
	}
	
	/**
	 * 取消对话框
	 */
	public void cancelRequestDialog(){
		if(requestDialog != null && requestDialog.isShowing()){
			requestDialog.cancel();
		}
	}
	
	
	/**
	 * 添加标题
	 * 
	 * @param title
	 *            -- 标题
	 * @param listener
	 *            -- 监听事件
	 */
	@Override
	public void setTitle(CharSequence title, OnClickListener listener) {
		getSelfDefActionBar();
		if (actionBar != null) {
			actionBar.setTitle(title, listener);
		}
	}
	
	@Override
	public void setTitle(int strId) {
		getSelfDefActionBar();
		if (actionBar != null) {
			actionBar.setTitle(strId, null);
		}
	}
	@Override
	public void setTitle(CharSequence title) {
		getSelfDefActionBar();
		if (actionBar != null) {
			actionBar.setTitle(title, null);
		}
	}
 
 
	/**
	 * 添加顶部标题栏左侧返回按钮
	 * 
	 * @param listener
	 *            如果为null，就pass Activity activity
	 */
	protected void addBackBtn(View.OnClickListener listener) {
		getSelfDefActionBar();
		if (actionBar != null) {
			if (listener == null) {
				listener = new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				};
			}
			actionBar.addBackText(R.string.back, listener);
		}
	}

	/**
	 * 添加顶部标题栏左侧返回按钮
	 * 
	 * @param listener
	 */
	protected void addBackImage(int drawId, OnClickListener listener) {
		getSelfDefActionBar();
		if (actionBar != null) {
			if (listener == null) {
				listener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				};
			}
			actionBar.addBackImage(drawId, listener);
		}
	}
	
	/**
	 * 添加顶部标题栏左侧返回按钮
	 * 
	 * @param listener
	 *            如果为null，就pass Activity activity
	 */
	protected void addRightBtn(int strId, OnClickListener listener) {
		getSelfDefActionBar();
		if (actionBar != null) {
			if (listener == null) {
				listener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				};
			}
			actionBar.addRightText(strId, listener);
		}
	}

	/**
	 * 添加顶部标题栏左侧返回按钮
	 * 
	 * @param listener
	 */
	protected void addRightImage(int drawId, OnClickListener listener) {
		getSelfDefActionBar();
		if (actionBar != null) {
			if (listener == null) {
				listener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				};
			}
			actionBar.addRightImage(drawId, listener);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AVAnalytics.trackAppOpened(getIntent());
		tag = getClass().getSimpleName();
		// 禁止横屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		if (null == preferences) {
			preferences = Preference.getSharedPreferences(this);
		} 
		getFinalHttp();
	}

	public FinalHttp getFinalHttp() {
		if (http == null) {
			http = SelfDefineApplication.getInstance().getFinalHttp();
		}
		return http;
	}
	
	/**
	 * 获取当前登录用户信息
	 */
	protected void getCurrentUser() {
		try {
			user = new Gson().fromJson(
					preferences.getString(Preference.LOGIN_USER, null),
					new TypeToken<User>() {
					}.getType());
		} catch (Exception e) {
			MyLog.e(tag, getString(R.string.login_userinfo_not_exist));
		}
	}

	/**
	 * 隐藏输入法
	 */
	protected void hideInput() {
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
//
//	/**
//	 * 返回按钮
//	 * 
//	 * @param view
//	 */
//	public void onBackClick(View view) {
//		this.finish();
//	}
//
	/**
	 * 如果使用actionbar，需要主动调用一下这个方法，把actionbar容器的的id传入
	 */
	@Override
	public void setupSelfDefineActionBar(int resId) {
		actionBar = (SelfDefineActionBar) findViewById(resId);
	}

	/**
	 * 隐藏覆盖在整个fragment上的空视图--表示没有数据的情况，可以动态添加一个自定义视图
	 * 
	 * @param rootView
	 *            -- fragment的根视图
	 */
//	public void hideEmptyView() {
//		View rootView = getWindow().getDecorView();
//		View emptyLayout = rootView.findViewById(R.id.rlEmptyView);
//		if (null != emptyLayout) {
//			emptyLayout.setVisibility(View.GONE);
//		}
//	}
//
//	public void setLoadingView() {
//		View rootView = getWindow().getDecorView();
//		RelativeLayout llEmptyLayout = (RelativeLayout) rootView
//				.findViewById(R.id.rlEmptyView);
//		if (null != llEmptyLayout) {
//			llEmptyLayout.setVisibility(View.VISIBLE);
//			if (llEmptyLayout.getChildCount() >= 1) {
//				llEmptyLayout.removeAllViews();
//			}
//			View emptyView = View.inflate(getActivity(),
//					R.layout.empty_text_view, null);
//			TextView emptyText = (TextView) emptyView
//					.findViewById(R.id.tvEmpty);
//			emptyText.setText("加载中，请稍后...");
//			llEmptyLayout.addView(emptyView);
//		}
//	}
//
//	/**
//	 * 设置整个视图没有数据的情况，可以动态添加一个自定义视图
//	 * 
//	 * @param layoutId
//	 *            -- 空视图的布局视图（注意会被放在全屏幕的LinearLayout中。)
//	 */
//	public void setEmptyView(int strId, int topDrawId) {
//		View rootView = getWindow().getDecorView();
//		RelativeLayout llEmptyLayout = (RelativeLayout) rootView
//				.findViewById(R.id.rlEmptyView);
//		if (null != llEmptyLayout) {
//			llEmptyLayout.setVisibility(View.VISIBLE);
//			if (llEmptyLayout.getChildCount() >= 1) {
//				llEmptyLayout.removeAllViews();
//			}
//			View emptyView = View.inflate(getActivity(),
//					R.layout.empty_call_log, null);
//			TextView emptyText = (TextView) emptyView
//					.findViewById(R.id.tvEmpty);
//			emptyText.setCompoundDrawablesWithIntrinsicBounds(0, topDrawId, 0,
//					0);
//			emptyText.setText(strId);
//			llEmptyLayout.addView(emptyView);
//		}
//	}
//
//	public Dialog mDialog = null;// 等待对话框

	@Override
	public void setTitle(int strId, OnClickListener listener) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(this);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(this);
	}
//	/**
//	 * 弹出提示对话框
//	 */
//	public void showRequestDialog(String tipText) {
//		try {
//			if (mDialog != null) {
//				mDialog.dismiss();
//				mDialog = null;
//			}
//			mDialog = DialogFactory.creatRequestDialog(this, tipText);
//			mDialog.show();
//		} catch (Exception e) {
//
//		}
//	}
//
//	/**
//	 * 弹出提示对话框
//	 */
//	public void showRequestDialog(Context context, String tipText) {
//		if (mDialog != null) {
//			mDialog.dismiss();
//			mDialog = null;
//		}
//		mDialog = DialogFactory.creatRequestDialog(context, tipText);
//		mDialog.show();
//	}
//
//	/**
//	 * 弹出提示对话框
//	 */
//	public void showRequestDialog(int resId) {
//		if (mDialog != null) {
//			mDialog.dismiss();
//			mDialog = null;
//		}
//		mDialog = DialogFactory.creatRequestDialog(this, getResources()
//				.getString(resId));
//		mDialog.show();
//	}

//	/**
//	 * 隐藏提示对话框
//	 */
//	public void canceRequestDialog() {
//		try {
//			if (mDialog != null) {
//				if (mDialog.isShowing()) {
//					mDialog.dismiss();
//					mDialog = null;
//				}
//			}
//		} catch (Exception e) {
//
//		}
//	}

	/**
	 * 重置ListView加载中状态
	 */
//	public void resetListViewEmpty() {
//		TextView emptyText = (TextView) findViewById(R.id.tvEmpty);
//		ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
//		emptyText.setText("加载中，请稍候...");
//		pbLoading.setVisibility(View.VISIBLE);
//	}

//	/**
//	 * ListView加载完成
//	 * 
//	 * @param tip
//	 */
//	public void setListViewEmpty(String tip) {
//		TextView emptyText = (TextView) findViewById(R.id.tvEmpty);
//		ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
//		if (null == emptyText || pbLoading == null)
//			return;
//		emptyText.setText(tip);
//		if (!TextUtils.isEmpty(tip)) {
//			Drawable top = getResources().getDrawable(
//					R.drawable.icon_no_calllog);
//			emptyText.setCompoundDrawablesWithIntrinsicBounds(null, top, null,
//					null);
//		} else {
//			emptyText.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
//					null);
//		}
//		pbLoading.setVisibility(View.GONE);
//	}
//
//	@Override
//	public void onAutoInstall() {
//		// TODO Auto-generated method stub
//		String app_name = getResources().getString(R.string.app_name) + ".apk";
//
//		File test = new File(new FileCache(this).getFileDirCache(), app_name);
//		if (null != test && test.exists()) {
//			Uri uri = Uri.fromFile(test);
//			Intent intent = new Intent(Intent.ACTION_VIEW);
//			intent.setDataAndType(uri,
//					"application/vnd.android.package-archive");
//			startActivity(intent);
//		}
//	}
}
