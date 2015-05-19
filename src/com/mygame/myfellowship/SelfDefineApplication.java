package com.mygame.myfellowship;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import cn.smssdk.SMSSDK;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.mygame.myfellowship.bean.Constant;
import com.mygame.myfellowship.gps.MyLocation;
import com.mygame.myfellowship.http.FinalHttp;
import com.mygame.myfellowship.info.User;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.pgyersdk.crash.PgyCrashManager;

public class SelfDefineApplication extends Application {
	private static int DISK_IMAGECACHE_SIZE = 1024*1024*10;
	private static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;
	private static int DISK_IMAGECACHE_QUALITY = 100;  //PNG is lossless so quality is ignored but must be provided
	
	

	public static boolean finishLogin;
	private static SelfDefineApplication application;
	private static FinalHttp finalHttp;
	//定位参数
	private MyLocation myLocation = new MyLocation();
	private LocationClient mLocClient;
	
	public static SelfDefineApplication getInstance() {
		if(application == null){
			application = new SelfDefineApplication();
		}
		return application;
	}

	public User getUser() {
		return null;
	}

	public FinalHttp getFinalHttp() {
		if(finalHttp == null){
			finalHttp = new FinalHttp();
		}
		return finalHttp;
	}
		
	public interface LocationListener{
		public void onReceiveLocation(MyLocation myLocation);
	};
	
	LocationListener listener;
	
	private class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location != null){
				myLocation.setLatitude(location.getLatitude());
				myLocation.setLongitude(location.getLongitude());
				myLocation.setDetailAddress("longtitude:" + location.getLatitude() + ",latitude" + location.getLongitude());
				if(mLocClient.isStarted()){
					mLocClient.stop();
					mLocClient.unRegisterLocationListener(this);
				}
				Log.d("huwei", "地理位置更新，纬度 = " + location.getLatitude()+"，经度 = "+location.getLongitude());
				//得到经纬度
				if(listener != null){
					listener.onReceiveLocation(myLocation);
				}
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		finalHttp = getFinalHttp();
		SDKInitializer.initialize(this);//百度地图初始化
		SMSSDK.initSDK(this, getString(R.string.sharesdk_sms_app_key), getString(R.string.sharesdk_sms_app_secret));
//        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
//        JPushInterface.init(this);     		// 初始化 JPush
        //如果使用美国节点，请加上这行代码 AVOSCloud.useAVCloudUS();
//        AVOSCloud.initialize(this, "5mim4lf83bmmk9n3gqnxnxeb2mlj5zmqdztcj92jf3l8mbo3", "xq1za9q5d9efrqkts0a14sxs0zry7zhzm34bd6ihxqfu49gs");
//        AVObject testObject = new AVObject("TestObject");
//        testObject.put("foo", "bar");
//        testObject.saveInBackground();
		
		// 定位初始化
        PgyCrashManager.register(this,Constant.PgyerAPPID);// 集成蒲公英sdk应用的appId
        initImageLoader(getApplicationContext());
	}
	public static void initImageLoader(Context context) {
		// Create default options which will be used for every
		// displayImage(...) call if no options will be passed to this method
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.build();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPoolSize(3)// default
				.threadPriority(Thread.NORM_PRIORITY - 1)// default
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.memoryCacheSizePercentage(13) // default
				.defaultDisplayImageOptions(defaultOptions)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	public void startLocation(Context context, LocationListener listener) {
		locationInit(context, listener);
		if(!mLocClient.isStarted()){
			mLocClient.start();
		}
	}
	
	private void locationInit(Context context, LocationListener listener) {
		MyLocationListenner myListener = new MyLocationListenner();
		mLocClient = new LocationClient(context);
		if(mLocClient != null){
			this.listener = listener;
			mLocClient.registerLocationListener(myListener);
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);// 打开gps
			option.setCoorType("bd09ll"); // 设置坐标类型
			option.setScanSpan(1000);
			mLocClient.setLocOption(option);
		} else {
			listener.onReceiveLocation(null);
		}
		
	}

}
