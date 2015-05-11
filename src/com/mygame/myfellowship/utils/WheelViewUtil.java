package com.mygame.myfellowship.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mygame.myfellowship.R;
import com.mygame.myfellowship.bean.CfgCommonType;
import com.mygame.myfellowship.view.wheelview.DateWheelAdapter;
import com.mygame.myfellowship.view.wheelview.OnWheelScrollListener;
import com.mygame.myfellowship.view.wheelview.TimeWheelAdapter;
import com.mygame.myfellowship.view.wheelview.WheelView;

/**
 * 仿照ios7滑动控件
 * 
 * @author longke
 * 
 */
@SuppressLint("SimpleDateFormat")
public class WheelViewUtil {
    static PopupWindow distancePopupWindow = null;
    private static int yearIndex;
    private static int monthIndex;
    private static int dayIndex;
    private static List<String> yearList;
    private static List<String> monthList;
    private static List<String> dayList;
    static int indexYear;
    static int indexMouth;
    static int indexDay;
    static int index1;
    static int selectYear;
    static int selectMouth;
    static int itemLeft = 0;
    static int itemRight = 0;
    static Date date;
    static DateWheelAdapter dayAdapter;
    static DateWheelAdapter mouthAdapter;
    static CfgCommonType cct = null;
    String content = null;
    private static String[] YEAES;
    private static String[] MOUTHS;
    private static String[] DAYS_31;
    public final static String[] AGES = new String[] { "不限", "18", "19", "20", "21", "22", "23", "24", "25", "26",
            "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44",
            "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62",
            "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80" };

    /**
     * 
     * 回调接口
     * 
     */
    public static interface OnWheelViewListener {
        public void Confirm(String select, int index);

        public void doubleConfirm(String selectLeft, String selectRight);

        // 选择日期时，如果回传参数全部为-1，表示至今
        public void dateConfirm(String selectyear, String selectmonth, String selectday, int year, int month, int day);
    }

    /**
     * 
     * 回调接口
     * 
     */
    public static interface OnCfgWheelListener {
        public void Confirm(CfgCommonType select, int index);

        public void CustomSalayConfirm(String min, String max);
    }

    /**
     * 根据id获取可修改的arraylist
     * 
     * @param arrayList
     * @return 可修改的arraylist
     */
    public static List<String> getArrayList(int arrayListId, Context context) {
        return new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(arrayListId)));
    }

    /**
     * 日期控件 可选日期，精确到日。显示出来的日期是包括默认日期以及之前的日期 年份，从默认时间的年份倒推80年，当然，默认时间必须大于80年
     * 
     * @param context
     *            -- 上下文
     * @param view
     *            -- 视图
     * @param listener
     *            -- 点击监听器
     * @param defaultDate
     *            -- 默认时间 格式: 2014-1-1
     * @param isShowNow
     *            -- 是否显示"至今"按钮
     * @return 显示出来的带滑动视图
     */
    public static View showWheelView(final Context context, View view, final OnWheelViewListener listener,
            String defaultDate, String title, boolean isShowNow) {

        disMissThisView();

        MOUTHS = context.getResources().getStringArray(R.array.months);
        DAYS_31 = context.getResources().getStringArray(R.array.days_31);
        YEAES = context.getResources().getStringArray(R.array.years);
        View outerView = LayoutInflater.from(context).inflate(R.layout.date_wheel_view, null);

        final WheelView yearWheel = (WheelView) outerView
                .findViewById(R.id.yearwheel);
        final WheelView mouthWheel = (WheelView) outerView
                .findViewById(R.id.mouthwheel);
        final WheelView dayWheel = (WheelView) outerView
                .findViewById(R.id.daywheel);
        TextView tvCancel = (TextView) outerView.findViewById(R.id.tvCancel);
        TextView tvConfirm = (TextView) outerView.findViewById(R.id.tvConfirm);
        TextView tvContent = (TextView) outerView.findViewById(R.id.tvTitle);
        TextView tvNow = (TextView) outerView.findViewById(R.id.tvNow);

        if (isShowNow) {
            tvNow.setVisibility(View.VISIBLE);
        }
        monthList = new ArrayList<String>(Arrays.asList(MOUTHS));
        dayList = new ArrayList<String>(Arrays.asList(DAYS_31));

        if (yearList != null) {
            yearList.clear();
        }
        yearList = new ArrayList<String>();
        try {
            String[] defaultDates = defaultDate.split("-");
            int defautYear = Integer.parseInt(defaultDates[0]);

            // 从开始年份给yearList添加"xxxx年"
            for (int startYear = defautYear - 79; startYear <= defautYear; startYear++) {
                String currentYear = startYear + "年";
                yearList.add(currentYear);
            }

            // 去掉结束日期之后的数据
            initData(defaultDates[0], defaultDates[1], defaultDates[2]);
        } catch (NumberFormatException ex) {
            throw ex;
        }

        final DateWheelAdapter yearAdapter = new DateWheelAdapter(yearList, context);
        DateWheelAdapter mouthAdapter = new DateWheelAdapter(monthList, context);
        dayAdapter = new DateWheelAdapter(dayList, context);

        yearWheel.setViewAdapter(yearAdapter);
        mouthWheel.setViewAdapter(mouthAdapter);
        dayWheel.setViewAdapter(dayAdapter);
        yearWheel.setCurrentItem(yearIndex);
        mouthWheel.setCurrentItem(monthIndex);
        dayWheel.setCurrentItem(dayIndex);

        tvNow.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v) {
                distancePopupWindow.dismiss();
                listener.dateConfirm("-1", "-1", "-1", -1, -1, -1);
            }
        });

        tvCancel.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0) {
                distancePopupWindow.dismiss();
            }
        });
        tvConfirm.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0) {

                distancePopupWindow.dismiss();
                int year = yearWheel.getCurrentItem();
                int month = mouthWheel.getCurrentItem();
                int day = dayWheel.getCurrentItem();
                Log.i("choose date", "year:" + year + ",month:" + month + ",day:" + day);
                logList(yearList);
                try {
                    listener.dateConfirm(yearList.get(year), monthList.get(month), dayList.get(day), yearIndex,
                            monthIndex, dayIndex);
                } catch (Exception e) {
                    listener.dateConfirm("-1", "-1", "-1", -1, -1, -1);
                }
            }

            private void logList(List<String> list) {
                if (list != null) {
                    Log.i("choose date", "yearList:" + list.size());
                } else {
                    Log.i("choose date", "yearList is empty!");
                }
            }
        });
        if (TextUtils.isEmpty(title)) {
            tvContent.setVisibility(View.INVISIBLE);
        } else {
            tvContent.setText(title);
        }
        yearWheel.addScrollingListener(new OnWheelScrollListener()
        {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {

                yearIndex = wheel.getCurrentItem();
                String year = yearList.get(yearWheel.getCurrentItem()).replace("年", "");

                String month = monthList.get(mouthWheel.getCurrentItem()).replace("月", "");
                if (Integer.parseInt(month) == 2) {
                    if (isLeapYear(year)) {
                        // 是闰年
                        if (dayAdapter.timeDateList.size() != 29) {
                            dayList = new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(
                                    R.array.days_29)));
                            dayAdapter = new DateWheelAdapter(dayList, context);
                            dayWheel.setViewAdapter(dayAdapter);
                            if (dayIndex > 28) {
                                dayWheel.setCurrentItem(0);
                                dayIndex = 0;
                            } else {
                                dayWheel.setCurrentItem(dayIndex);
                            }
                        }
                    } else {
                        // 28 不是闰年
                        if (dayAdapter.timeDateList.size() != 28) {

                            dayAdapter = new DateWheelAdapter(dayList, context);
                            dayWheel.setViewAdapter(dayAdapter);
                            if (dayIndex > 27) {
                                dayWheel.setCurrentItem(0);
                                dayIndex = 0;
                            } else {
                                dayWheel.setCurrentItem(dayIndex);
                            }
                        }
                    }
                }

            }
        });
        mouthWheel.addScrollingListener(new OnWheelScrollListener()
        {

            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {

                // TODO Auto-generated method stub
                monthIndex = wheel.getCurrentItem();
                String year = yearList.get(yearWheel.getCurrentItem()).replace("年", "");
                String month = monthList.get(mouthWheel.getCurrentItem()).replace("月", "");
                int i = Integer.parseInt(month);
                if (i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10 || i == 12) {
                    // 31
                    if (dayAdapter.timeDateList.size() != 31) {
                        dayList = Arrays.asList(context.getResources().getStringArray(R.array.days_31));
                        dayAdapter = new DateWheelAdapter(dayList, context);
                        dayWheel.setViewAdapter(dayAdapter);
                        dayWheel.setCurrentItem(dayIndex);
                    }
                } else if (i == 2) {
                    if (isLeapYear(year)) {
                        // 29
                        if (dayAdapter.timeDateList.size() != 29) {
                            dayList = Arrays.asList(context.getResources().getStringArray(R.array.days_29));
                            dayAdapter = new DateWheelAdapter(dayList, context);
                            dayWheel.setViewAdapter(dayAdapter);
                            if (dayIndex > 28) {
                                dayWheel.setCurrentItem(0);
                                dayIndex = 0;
                            } else {
                                dayWheel.setCurrentItem(dayIndex);
                            }
                        }
                    } else {
                        // 28
                        if (dayAdapter.timeDateList.size() != 28) {
                            dayList = Arrays.asList(context.getResources().getStringArray(R.array.days_28));
                            dayAdapter = new DateWheelAdapter(dayList, context);
                            dayWheel.setViewAdapter(dayAdapter);
                            if (dayIndex > 27) {
                                dayWheel.setCurrentItem(0);
                                dayIndex = 0;
                            } else {
                                dayWheel.setCurrentItem(dayIndex);
                            }
                        }
                    }
                } else {
                    // 30
                    if (dayAdapter.timeDateList.size() != 30) {
                        dayList = Arrays.asList(context.getResources().getStringArray(R.array.days_30));
                        dayAdapter = new DateWheelAdapter(dayList, context);
                        dayWheel.setViewAdapter(dayAdapter);
                        if (dayIndex > 29) {
                            dayWheel.setCurrentItem(0);
                            dayIndex = 0;
                        } else {
                            dayWheel.setCurrentItem(dayIndex);
                        }
                    }
                }

            }
        });
        dayWheel.addScrollingListener(new OnWheelScrollListener()
        {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                dayIndex = wheel.getCurrentItem();
            }
        });

        disMissThisView();

        distancePopupWindow = new PopupWindow(outerView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
        outerView.setFocusableInTouchMode(true);

        ColorDrawable cd = new ColorDrawable(Color.argb(136, 0, 0, 0));
        distancePopupWindow.setBackgroundDrawable(cd);
        distancePopupWindow.setOutsideTouchable(true);

        distancePopupWindow.setAnimationStyle(R.anim.popuwindow);
        distancePopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        return outerView;
    }

    /**
     * 日期控件 可选日期，精确到日。可设置显示的开始日期和结束日期
     * 
     * @param context
     *            -- 上下文
     * @param view
     *            -- 视图
     * @param contents
     *            -- 内容
     * @param listener
     *            -- 点击监听器
     * @param title
     *            -- 标题
     * @param year
     *            -- 当前选中的年
     * @param mouth
     *            -- 当前选中的月
     * @param day
     *            -- 当前选中的日
     * @param startTime
     *            -- 起始时期 (格式:2014年-2月-2日) -- 只用到年份
     * @param endTime
     *            -- 结束时期 (格式:2014年-2月-2日) -- 只用到年份
     * @param isShowNow
     *            -- 是否显示"至今"
     * @return 显示出来的带滑动视图
     */
    public static View showWheelView(final Context context, View view, final OnWheelViewListener listener,
            String title, String year, String mouth, final String day, String startDate, String endDate,
            boolean isShowNow) {

        disMissThisView();

        MOUTHS = context.getResources().getStringArray(R.array.months);
        DAYS_31 = context.getResources().getStringArray(R.array.days_31);
        YEAES = context.getResources().getStringArray(R.array.years);
        View outerView = LayoutInflater.from(context).inflate(R.layout.date_wheel_view, null);

        final WheelView yearWheel = (WheelView) outerView
                .findViewById(R.id.yearwheel);
        final WheelView mouthWheel = (WheelView) outerView
                .findViewById(R.id.mouthwheel);
        final WheelView dayWheel = (WheelView) outerView
                .findViewById(R.id.daywheel);
        TextView tvCancel = (TextView) outerView.findViewById(R.id.tvCancel);
        TextView tvConfirm = (TextView) outerView.findViewById(R.id.tvConfirm);
        TextView tvContent = (TextView) outerView.findViewById(R.id.tvTitle);
        TextView tvNow = (TextView) outerView.findViewById(R.id.tvNow);

        if (isShowNow) {
            tvNow.setVisibility(View.VISIBLE);
        }
        monthList = new ArrayList<String>(Arrays.asList(MOUTHS));
        dayList = new ArrayList<String>(Arrays.asList(DAYS_31));
        yearList = new ArrayList<String>(Arrays.asList(YEAES));

        deleteBeforeDate(startDate);
        deleteAfterDate(endDate);
        // 去掉结束日期之后的数据
        initData(year, mouth, day);

        final DateWheelAdapter yearAdapter = new DateWheelAdapter(yearList, context);
        final DateWheelAdapter mouthAdapter = new DateWheelAdapter(monthList, context);
        dayAdapter = new DateWheelAdapter(dayList, context);

        yearWheel.setViewAdapter(yearAdapter);
        mouthWheel.setViewAdapter(mouthAdapter);
        dayWheel.setViewAdapter(dayAdapter);
        yearWheel.setCurrentItem(yearIndex);
        mouthWheel.setCurrentItem(monthIndex);
        dayWheel.setCurrentItem(dayIndex);

        tvNow.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v) {
                distancePopupWindow.dismiss();
                listener.dateConfirm("-1", "-1", "-1", -1, -1, -1);
            }
        });

        tvCancel.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0) {
                distancePopupWindow.dismiss();
            }
        });
        tvConfirm.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0) {

                distancePopupWindow.dismiss();
                int year = yearWheel.getCurrentItem();
                int month = mouthWheel.getCurrentItem();
                int day = dayWheel.getCurrentItem();
                Log.i("choose date", "year:" + year + ",month:" + month + ",day:" + day);
                logList(yearList);
                try {
                    listener.dateConfirm(yearList.get(year), monthList.get(month), dayList.get(day), yearIndex,
                            monthIndex, dayIndex);
                } catch (Exception e) {
                    listener.dateConfirm("-1", "-1", "-1", -1, -1, -1);
                }
            }

            private void logList(List<String> list) {
                if (list != null) {
                    Log.i("choose date", "yearList:" + list.size());
                } else {
                    Log.i("choose date", "yearList is empty!");
                }
            }
        });
        if (TextUtils.isEmpty(title)) {
            tvContent.setVisibility(View.INVISIBLE);
        } else {
            tvContent.setText(title);
        }
        yearWheel.addScrollingListener(new OnWheelScrollListener()
        {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {

                yearIndex = wheel.getCurrentItem();
                String year = yearList.get(yearWheel.getCurrentItem()).replace("年", "");

                String month = monthList.get(mouthWheel.getCurrentItem()).replace("月", "");
                if (Integer.parseInt(month) == 2) {
                    if (isLeapYear(year)) {
                        // 是闰年
                        if (dayAdapter.timeDateList.size() != 29) {
                            dayList = new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(
                                    R.array.days_29)));
                            dayAdapter = new DateWheelAdapter(dayList, context);
                            dayWheel.setViewAdapter(dayAdapter);
                            if (dayIndex > 28) {
                                dayWheel.setCurrentItem(0);
                                dayIndex = 0;
                            } else {
                                dayWheel.setCurrentItem(dayIndex);
                            }
                        }
                    } else {
                        // 28 不是闰年
                        if (dayAdapter.timeDateList.size() != 28) {

                            dayAdapter = new DateWheelAdapter(dayList, context);
                            dayWheel.setViewAdapter(dayAdapter);
                            if (dayIndex > 27) {
                                dayWheel.setCurrentItem(0);
                                dayIndex = 0;
                            } else {
                                dayWheel.setCurrentItem(dayIndex);
                            }
                        }
                    }
                }

            }
        });
        mouthWheel.addScrollingListener(new OnWheelScrollListener()
        {

            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {

                // TODO Auto-generated method stub
                monthIndex = wheel.getCurrentItem();
                String year = yearList.get(yearWheel.getCurrentItem()).replace("年", "");
                String month = monthList.get(mouthWheel.getCurrentItem()).replace("月", "");
                int i = Integer.parseInt(month);
                if (i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10 || i == 12) {
                    // 31
                    if (dayAdapter.timeDateList.size() != 31) {
                        dayList = Arrays.asList(context.getResources().getStringArray(R.array.days_31));
                        dayAdapter = new DateWheelAdapter(dayList, context);
                        dayWheel.setViewAdapter(dayAdapter);
                        dayWheel.setCurrentItem(dayIndex);
                    }
                } else if (i == 2) {
                    if (isLeapYear(year)) {
                        // 29
                        if (dayAdapter.timeDateList.size() != 29) {
                            dayList = Arrays.asList(context.getResources().getStringArray(R.array.days_29));
                            dayAdapter = new DateWheelAdapter(dayList, context);
                            dayWheel.setViewAdapter(dayAdapter);
                            if (dayIndex > 28) {
                                dayWheel.setCurrentItem(0);
                                dayIndex = 0;
                            } else {
                                dayWheel.setCurrentItem(dayIndex);
                            }
                        }
                    } else {
                        // 28
                        if (dayAdapter.timeDateList.size() != 28) {
                            dayList = Arrays.asList(context.getResources().getStringArray(R.array.days_28));
                            dayAdapter = new DateWheelAdapter(dayList, context);
                            dayWheel.setViewAdapter(dayAdapter);
                            if (dayIndex > 27) {
                                dayWheel.setCurrentItem(0);
                                dayIndex = 0;
                            } else {
                                dayWheel.setCurrentItem(dayIndex);
                            }
                        }
                    }
                } else {
                    // 30
                    if (dayAdapter.timeDateList.size() != 30) {
                        dayList = Arrays.asList(context.getResources().getStringArray(R.array.days_30));
                        dayAdapter = new DateWheelAdapter(dayList, context);
                        dayWheel.setViewAdapter(dayAdapter);
                        if (dayIndex > 29) {
                            dayWheel.setCurrentItem(0);
                            dayIndex = 0;
                        } else {
                            dayWheel.setCurrentItem(dayIndex);
                        }
                    }
                }

            }
        });
        dayWheel.addScrollingListener(new OnWheelScrollListener()
        {

            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                dayIndex = wheel.getCurrentItem();
            }
        });

        disMissThisView();

        distancePopupWindow = new PopupWindow(outerView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
        outerView.setFocusableInTouchMode(true);

        ColorDrawable cd = new ColorDrawable(Color.argb(136, 0, 0, 0));
        distancePopupWindow.setBackgroundDrawable(cd);
        distancePopupWindow.setOutsideTouchable(true);

        distancePopupWindow.setAnimationStyle(R.anim.popuwindow);
        distancePopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        return outerView;
    }

    private static void disMissThisView() {
        if (distancePopupWindow != null && distancePopupWindow.isShowing()) {
            distancePopupWindow.dismiss();
            distancePopupWindow = null;
        }
    }

    /**
     * 删除年份、月份、日列表中指定日期之前的数据
     * 
     * @param startDate
     *            -- 指定日期
     * @return
     */
    private static void deleteBeforeDate(String startDate) {
        if (TextUtils.isEmpty(startDate)) {
            return;
        }
        // 去掉开始日期之前的数据
        String[] startDateArray = startDate.split("-");
        // 越界 -1
        int yearStartIdx = yearList.indexOf(startDateArray[0]);
        for (int i = 0; i < yearStartIdx; i++) {
            yearList.remove(i);
        }
    }

    /**
     * 删除年份、月份、日列表中指定日期之前的数据
     * 
     * @param startDate
     *            -- 指定日期
     * @return
     */
    private static void deleteAfterDate(String startDate) {
        if (TextUtils.isEmpty(startDate)) {
            return;
        }
        // 去掉指定日期之后的数据
        String[] startDateArray = startDate.split("-");
        // 越界 -1
        int yearEndIdx = yearList.indexOf(startDateArray[0]);
        for (int i = yearList.size() - 1; i > yearEndIdx; i--) {
            yearList.remove(i);
        }
        // int monthEndIdx = monthList.indexOf(startDateArray[1]);
        // for(int i = monthList.size() - 1; i > monthEndIdx; i--){
        // monthList.remove(i);
        // }
        // int dayEndIdx = monthList.indexOf(startDateArray[2]);
        // for(int i = dayList.size() - 1; i > dayEndIdx; i--){
        // dayList.remove(i);
        // }
    }

    /**
     * 删除今天之前的数据
     * 
     * @param startDate
     *            -- 指定日期
     * @return
     */
    private static void deleteAfterTotal(String startDate) {
        if (TextUtils.isEmpty(startDate)) {
            return;
        }
        // 去掉指定日期之后的数据
        String[] startDateArray = startDate.split("-");
        // 越界 -1
        int yearEndIdx = yearList.indexOf(startDateArray[0] + "年");
        for (int i = yearList.size() - 1; i > yearEndIdx; i--) {
            yearList.remove(i);
        }
        /*
         * if(Integer.parseInt(startDateArray[1])<10){
         * startDateArray[1].replace("0", ""); }
         * if(Integer.parseInt(startDateArray[2])<10){
         * startDateArray[2].replace("0", ""); } int monthEndIdx =
         * monthList.indexOf(startDateArray[1]+"月"); for(int i =
         * monthList.size() - 1; i > monthEndIdx; i--){ monthList.remove(i); }
         * int dayEndIdx = monthList.indexOf(startDateArray[2]+"日"); for(int i =
         * dayList.size() - 1; i > dayEndIdx; i--){ dayList.remove(i); }
         */
    }

    /**
     * 日期控件
     * 
     * @param context
     * @param view
     * @param contents
     * @param listener
     * @param title
     * @param index
     * @return
     */
    public static View showWheelView(final Context context, View view, final OnWheelViewListener listener,
            String title, String year, String mouth, final String day) {

        disMissThisView();

        MOUTHS = context.getResources().getStringArray(R.array.months);
        DAYS_31 = context.getResources().getStringArray(R.array.days_31);
        YEAES = context.getResources().getStringArray(R.array.years);
        View outerView = LayoutInflater.from(context).inflate(R.layout.date_wheel_view, null);

        final WheelView yearWheel = (WheelView) outerView
                .findViewById(R.id.yearwheel);
        final WheelView mouthWheel = (WheelView) outerView
                .findViewById(R.id.mouthwheel);
        final WheelView dayWheel = (WheelView) outerView
                .findViewById(R.id.daywheel);
        RelativeLayout titleLayout=(RelativeLayout) outerView.findViewById(R.id.title_layout);
        TextView tvCancel = (TextView) outerView.findViewById(R.id.tvCancel);
        TextView tvConfirm = (TextView) outerView.findViewById(R.id.tvConfirm);
        TextView tvContent = (TextView) outerView.findViewById(R.id.tvTitle);
        monthList = Arrays.asList(MOUTHS); // 月份会随着闰月和 非闰月变化。但保持不变
        dayList = Arrays.asList(DAYS_31);
        yearList = getArrayList(R.array.years, context); // 年份list可以修改的。有时限定时间不超过今年
                                                         // ，所以重新new一个arraylist
        initData(year, mouth, day);

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String endDate = sDateFormat.format(new java.util.Date());

        deleteAfterTotal(endDate);
        final DateWheelAdapter yearAdapter = new DateWheelAdapter(yearList, context);
        mouthAdapter = new DateWheelAdapter(monthList, context);
        dayAdapter = new DateWheelAdapter(dayList, context);

        yearWheel.setViewAdapter(yearAdapter);
        mouthWheel.setViewAdapter(mouthAdapter);
        dayWheel.setViewAdapter(dayAdapter);
        yearWheel.setCurrentItem(yearIndex);
        mouthWheel.setCurrentItem(monthIndex);
        dayWheel.setCurrentItem(dayIndex);
        tvCancel.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0) {
                distancePopupWindow.dismiss();
            }
        });
        titleLayout.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View arg0) {
            }
        });
        outerView.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View arg0) {
                distancePopupWindow.dismiss();
            }
        });
        tvConfirm.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0) {

                distancePopupWindow.dismiss();
                int year = yearWheel.getCurrentItem();
                int month = mouthWheel.getCurrentItem();
                int day = dayWheel.getCurrentItem();
                String dayStr = "1";

                // 防止天数越界
                if (dayList.size() > 0 && day < dayList.size()) {
                    dayStr = dayList.get(day);
                }
                listener.dateConfirm(yearList.get(year), monthList.get(month), dayStr, yearIndex, monthIndex, dayIndex);
            }
        });
        if (TextUtils.isEmpty(title)) {
            tvContent.setVisibility(View.INVISIBLE);
        } else {
            tvContent.setText(title);
        }
        yearWheel.addScrollingListener(new OnWheelScrollListener()
        {

            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {

                yearIndex = wheel.getCurrentItem();
                String year = yearList.get(yearWheel.getCurrentItem()).replace("年", "");

                String month = monthList.get(mouthWheel.getCurrentItem()).replace("月", "");
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String endDate = sDateFormat.format(new java.util.Date());
                String[] startDateArray = endDate.split("-");
                if (year.equals(startDateArray[0])) {
                    if (Integer.parseInt(startDateArray[1]) < 10) {
                        startDateArray[1].replace("0", "");
                    }
                    
                    int monthEndIdx = monthList.indexOf(startDateArray[1] + "月");
                    for (int i = monthList.size() - 1; i > monthEndIdx; i--) {
                        monthList.remove(i);
                    }
                    
                    mouthAdapter = new DateWheelAdapter(monthList, context);
                    mouthWheel.setViewAdapter(mouthAdapter);
                    mouthWheel.setCurrentItem(monthIndex);
                    
                    return;

                } /*else {
                    monthList = Arrays.asList(MOUTHS);
                    dayList = Arrays.asList(DAYS_31);
                    mouthAdapter = new DateWheelAdapter(monthList, context);
                    mouthWheel.setViewAdapter(mouthAdapter);
                    mouthWheel.setCurrentItem(monthIndex);
                    dayAdapter = new DateWheelAdapter(dayList, context);
                    dayWheel.setViewAdapter(dayAdapter);
                    dayWheel.setCurrentItem(dayIndex);
                }*/
                if (Integer.parseInt(month) == 2) {
                    if (isLeapYear(year)) {
                        // 是闰年
                        if (dayAdapter.timeDateList.size() != 29) {
                            dayList = getArrayList(R.array.days_29, context); // Arrays.asList(context.getResources().getStringArray(R.array.days_29));
                            dayAdapter = new DateWheelAdapter(dayList, context);
                            dayWheel.setViewAdapter(dayAdapter);
                            if (dayIndex > 28) {
                                dayWheel.setCurrentItem(0);
                                dayIndex = 0;
                            } else {
                                dayWheel.setCurrentItem(dayIndex);
                            }
                        }
                    } else {
                        // 28 不是闰年
                        if (dayAdapter.timeDateList.size() != 28) {
                            dayList = getArrayList(R.array.days_28, context); // Arrays.asList(context.getResources().getStringArray(R.array.days_28));
                            dayAdapter = new DateWheelAdapter(dayList, context);
                            dayWheel.setViewAdapter(dayAdapter);
                            if (dayIndex > 27) {
                                dayWheel.setCurrentItem(0);
                                dayIndex = 0;
                            } else {
                                dayWheel.setCurrentItem(dayIndex);
                            }
                        }
                    }
                }
                

            }
        });
        mouthWheel.addScrollingListener(new OnWheelScrollListener()
        {

            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                try {
                    monthIndex = wheel.getCurrentItem();
                    String year = yearList.get(yearWheel.getCurrentItem()).replace("年", "");
                    String month = monthList.get(mouthWheel.getCurrentItem()).replace("月", "");
                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String endDate = sDateFormat.format(new java.util.Date());
                    String[] startDateArray = endDate.split("-");
                    if (Integer.parseInt(startDateArray[1]) < 10) {
                        startDateArray[1].replace("0", "");
                    }
                    if (Integer.parseInt(startDateArray[2]) < 10) {
                        startDateArray[2].replace("0", "");
                    }
                    if (year.equals(startDateArray[0])&&month.endsWith(startDateArray[1])) {
                        
                       
                        int dayEndIdx = dayList.indexOf(startDateArray[2] + "日");
                        for (int i = dayList.size() - 1; i > dayEndIdx; i--) {
                            dayList.remove(i);
                        }
                        
                        dayAdapter = new DateWheelAdapter(dayList, context);
                        dayWheel.setViewAdapter(dayAdapter);
                        dayWheel.setCurrentItem(dayIndex);
                        return;
                        
                    }
                    int i = Integer.parseInt(month);
                    if (i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10 || i == 12) {
                        // 31
                        if (dayAdapter.timeDateList.size() != 31) {
                            dayList = getArrayList(R.array.days_31, context); // Arrays.asList(context.getResources().getStringArray(R.array.days_31));
                            dayAdapter = new DateWheelAdapter(dayList, context);
                            dayWheel.setViewAdapter(dayAdapter);
                            dayWheel.setCurrentItem(dayIndex);
                        }
                    } else if (i == 2) {
                        if (isLeapYear(year)) {
                            // 29
                            if (dayAdapter.timeDateList.size() != 29) {
                                dayList = getArrayList(R.array.days_29, context); // Arrays.asList(context.getResources().getStringArray(R.array.days_29));
                                dayAdapter = new DateWheelAdapter(dayList, context);
                                dayWheel.setViewAdapter(dayAdapter);
                                if (dayIndex > 28) {
                                    dayWheel.setCurrentItem(0);
                                    dayIndex = 0;
                                } else {
                                    dayWheel.setCurrentItem(dayIndex);
                                }
                            }
                        } else {
                            // 28
                            if (dayAdapter.timeDateList.size() != 28) {
                                dayList = getArrayList(R.array.days_28, context); // Arrays.asList(context.getResources().getStringArray(R.array.days_28));
                                dayAdapter = new DateWheelAdapter(dayList, context);
                                dayWheel.setViewAdapter(dayAdapter);
                                if (dayIndex > 27) {
                                    dayWheel.setCurrentItem(0);
                                    dayIndex = 0;
                                } else {
                                    dayWheel.setCurrentItem(dayIndex);
                                }
                            }
                        }
                    } else {
                        // 30
                        if (dayAdapter.timeDateList.size() != 30) {
                            dayList = getArrayList(R.array.days_30, context); // Arrays.asList(context.getResources().getStringArray(R.array.days_30));
                            dayAdapter = new DateWheelAdapter(dayList, context);
                            dayWheel.setViewAdapter(dayAdapter);
                            if (dayIndex > 29) {
                                dayWheel.setCurrentItem(0);
                                dayIndex = 0;
                            } else {
                                dayWheel.setCurrentItem(dayIndex);
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
        dayWheel.addScrollingListener(new OnWheelScrollListener()
        {

            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                dayIndex = wheel.getCurrentItem();
            }
        });

        distancePopupWindow = new PopupWindow(outerView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
        outerView.setFocusableInTouchMode(true);
        ColorDrawable cd = new ColorDrawable(Color.argb(136, 0, 0, 0));
        distancePopupWindow.setBackgroundDrawable(cd);
        distancePopupWindow.setOutsideTouchable(true);
        distancePopupWindow.setAnimationStyle(R.anim.popuwindow);
        distancePopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        return outerView;
    }

    /**
     * 日期控件*只有年和月
     * 
     * @param context
     * @param view
     * @param contents
     * @param listener
     * @param title
     * @param index
     * @return
     */
    public static View showDateWheelView(final Context context, View view, final OnWheelViewListener listener,
            String title, String year, String mouth) {

        disMissThisView();

        MOUTHS = context.getResources().getStringArray(R.array.months);

        YEAES = context.getResources().getStringArray(R.array.years);
        View outerView = LayoutInflater.from(context).inflate(R.layout.date_yearandmouth_wheel_view, null);

        final WheelView yearWheel = (WheelView) outerView
                .findViewById(R.id.yearwheel);
        final WheelView mouthWheel = (WheelView) outerView
                .findViewById(R.id.mouthwheel);

        TextView tvCancel = (TextView) outerView.findViewById(R.id.tvCancel);
        TextView tvConfirm = (TextView) outerView.findViewById(R.id.tvConfirm);
        TextView tvContent = (TextView) outerView.findViewById(R.id.tvTitle);
        monthList = Arrays.asList(MOUTHS); // 月份会随着闰月和 非闰月变化。但保持不变

        yearList = getArrayList(R.array.years, context); // 年份list可以修改的。有时限定时间不超过今年
                                                         // ，所以重新new一个arraylist
        initData(year, mouth);

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String endDate = sDateFormat.format(new java.util.Date());

        deleteAfterDate(endDate);
        final DateWheelAdapter yearAdapter = new DateWheelAdapter(yearList, context);
        final DateWheelAdapter mouthAdapter = new DateWheelAdapter(monthList, context);
        dayAdapter = new DateWheelAdapter(dayList, context);

        yearWheel.setViewAdapter(yearAdapter);
        mouthWheel.setViewAdapter(mouthAdapter);

        yearWheel.setCurrentItem(yearIndex);
        mouthWheel.setCurrentItem(monthIndex);

        tvCancel.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0) {
                distancePopupWindow.dismiss();
            }
        });
        tvConfirm.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0) {

                distancePopupWindow.dismiss();
                int year = yearWheel.getCurrentItem();
                int month = mouthWheel.getCurrentItem();
                listener.doubleConfirm(yearList.get(year), monthList.get(month));
            }
        });
        if (TextUtils.isEmpty(title)) {
            tvContent.setVisibility(View.INVISIBLE);
        } else {
            tvContent.setText(title);
        }
        yearWheel.addScrollingListener(new OnWheelScrollListener()
        {

            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {

                yearIndex = wheel.getCurrentItem();

            }
        });
        mouthWheel.addScrollingListener(new OnWheelScrollListener()
        {

            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {

                monthIndex = wheel.getCurrentItem();
            }
        });

        distancePopupWindow = new PopupWindow(outerView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
        outerView.setFocusableInTouchMode(true);
        ColorDrawable cd = new ColorDrawable(Color.argb(136, 0, 0, 0));
        distancePopupWindow.setBackgroundDrawable(cd);
        distancePopupWindow.setOutsideTouchable(true);
        distancePopupWindow.setAnimationStyle(R.anim.popuwindow);
        distancePopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        return outerView;
    }

    /**
     * 数据初始化
     * 
     * @param year
     * @param month
     * @param day
     */
    @SuppressLint("SimpleDateFormat")
    public static void initData(String year, String month, String day) {
        // TODO Auto-generated method stub

        if (TextUtils.isEmpty(year)) {
            yearIndex = 33; // 默认取中间值
            monthIndex = 5;
            dayIndex = 0;
        } else {
            yearIndex = yearList.indexOf(year + "年");
            monthIndex = monthList.indexOf(month + "月");
            dayIndex = dayList.indexOf(day + "日");
        }

        if (yearIndex == -1) {
            yearIndex = 33; // 默认取中间值
        }
        if (monthIndex == -1) {
            monthIndex = 5;
        }
        if (dayIndex == -1) {
            dayIndex = 0;
        }

    }

    /**
     * 数据初始化
     * 
     * @param year
     * @param month
     * 
     */
    @SuppressLint("SimpleDateFormat")
    public static void initData(String year, String month) {
        // TODO Auto-generated method stub

        if (TextUtils.isEmpty(year)) {
            yearIndex = yearList.size() >> 1; // 默认取中间值
            monthIndex = 0;

        } else {
            yearIndex = yearList.indexOf(year + "年");
            monthIndex = monthList.indexOf(month + "月");

        }

        if (yearIndex == -1) {
            yearIndex = yearList.size() >> 1; // 默认取中间值
        }
        if (monthIndex == -1) {
            monthIndex = 0;
        }

    }

    /**
     * 判断是否是瑞年
     * */
    public static boolean isLeapYear(String str) {
        int year = Integer.parseInt(str);
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }

    /**
     * 单个选择Wheel
     * 
     * 
     * @param context
     * @param view
     * @param contents
     * @param listener
     * @param title
     * @param cotent
     * @return
     */
    @SuppressLint("InflateParams")
    public static View showSingleWheel1(Context context, View view, final List<String> ccts,
            final OnWheelViewListener listener, String title, String content) {

        disMissThisView();

        /*
         * if (distancePopupWindow != null && distancePopupWindow.isShowing()) {
         * return null; }
         */
        View outerView = LayoutInflater.from(context).inflate(R.layout.singlewheel_view, null);
        final WheelView ccwv = (WheelView) outerView
                .findViewById(R.id.ccwvLeft); // 只有一个
        ccwv.setCyclic(false);
        TextView tvCancel = (TextView) outerView.findViewById(R.id.tvCancel);
        TextView tvConfirm = (TextView) outerView.findViewById(R.id.tvConfirm);
        TextView tvContent = (TextView) outerView.findViewById(R.id.tvTitle);
        tvCancel.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0) {
                distancePopupWindow.dismiss();
            }
        });
        tvConfirm.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0) {
                listener.Confirm(ccts.get(ccwv.getCurrentItem()), ccwv.getCurrentItem());
                distancePopupWindow.dismiss();
            }
        });
        if (TextUtils.isEmpty(title)) {
            tvContent.setVisibility(View.INVISIBLE);
        } else {
            tvContent.setText(title);
        }
        ccwv.setViewAdapter(new DateWheelAdapter(ccts, context));
        if (TextUtils.isEmpty(content)) {
            content = ccts.get(0);
            ccwv.setCurrentItem(0);
        } else {
            for (int i = 0; i < ccts.size(); i++) {
                if (ccts.get(i).equals(content)) {
                    content = ccts.get(i);
                    ccwv.setCurrentItem(i);
                }
            }

        }
        outerView.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View arg0) {
                distancePopupWindow.dismiss();
            }
        });
        distancePopupWindow = new PopupWindow(outerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
        outerView.setFocusableInTouchMode(true);
        ColorDrawable cd = new ColorDrawable(Color.argb(136, 0, 0, 0));
        distancePopupWindow.setBackgroundDrawable(cd);
        distancePopupWindow.setOutsideTouchable(true);
        distancePopupWindow.setAnimationStyle(R.anim.popuwindow);
        distancePopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        return outerView;
    }
    

    /**
     * 单个选择Wheel
     * 
     * 
     * @param context
     * @param view
     * @param contents
     * @param listener
     * @param title
     * @param cotent
     * @return
     */
    @SuppressLint("InflateParams")
    public static View showSingleWheel(Context context, View view, final List<CfgCommonType> ccts,
            final OnCfgWheelListener listener, String title, String content) {

        disMissThisView();
        /*
         * if (distancePopupWindow != null && distancePopupWindow.isShowing()) {
         * return null; }
         */
        View outerView = LayoutInflater.from(context).inflate(R.layout.singlewheel_view, null);
        final WheelView ccwv = (WheelView) outerView
                .findViewById(R.id.ccwvLeft); // 只有一个
        ccwv.setCyclic(false);
        TextView tvCancel = (TextView) outerView.findViewById(R.id.tvCancel);
        TextView tvConfirm = (TextView) outerView.findViewById(R.id.tvConfirm);
        TextView tvContent = (TextView) outerView.findViewById(R.id.tvTitle);
        RelativeLayout titleLayout=(RelativeLayout) outerView.findViewById(R.id.title_layout);
        titleLayout.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View arg0) {
                
            }
        });
        tvCancel.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0) {
                distancePopupWindow.dismiss();
            }
        });
        tvConfirm.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0) {
                listener.Confirm(ccts.get(ccwv.getCurrentItem()), ccwv.getCurrentItem());
                distancePopupWindow.dismiss();
            }
        });
        if (TextUtils.isEmpty(title)) {
            tvContent.setVisibility(View.INVISIBLE);
        } else {
            tvContent.setText(title);
        }
        ccwv.setViewAdapter(new TimeWheelAdapter(ccts, context));
        if (TextUtils.isEmpty(content)) {
            cct = ccts.get(0);
            ccwv.setCurrentItem(0);
        } else {
            for (int i = 0; i < ccts.size(); i++) {
                if (ccts.get(i).getName().equals(content)) {
                    cct = ccts.get(i);
                    ccwv.setCurrentItem(i);
                }
            }

        }
        outerView.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View arg0) {
                distancePopupWindow.dismiss();
            }
        });
        distancePopupWindow = new PopupWindow(outerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
        outerView.setFocusableInTouchMode(true);
        ColorDrawable cd = new ColorDrawable(Color.argb(136, 0, 0, 0));
        distancePopupWindow.setBackgroundDrawable(cd);
        distancePopupWindow.setOutsideTouchable(true);
        distancePopupWindow.setAnimationStyle(R.anim.popuwindow);
        distancePopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        return outerView;
    }

    /**
     * 自定义薪水选择控件弹框
     * 
     * 
     * @param context
     * @param view
     * @param contents
     * @param listener
     * @param title
     * @param cotent
     * @return
     */
    @SuppressLint("InflateParams")
    public static View showCustomSalary(Context context, View view, final List<CfgCommonType> ccts,
            final OnCfgWheelListener listener, String title, String content) {

        disMissThisView();

        View outerView = LayoutInflater.from(context).inflate(R.layout.custom_salary_wheel_view, null);
        final WheelView ccwv = (WheelView) outerView
                .findViewById(R.id.ccwvLeft); // 只有一个
        ccwv.setCyclic(false);
        TextView tvConfirm = (TextView) outerView.findViewById(R.id.tvConfirm);
        final EditText minSalary = (EditText) outerView.findViewById(R.id.min_salary_et);
        final EditText maxSalary = (EditText) outerView.findViewById(R.id.max_salary_et);
        tvConfirm.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0) {
                if (!TextUtils.isEmpty(minSalary.getText().toString().trim())
                        && !TextUtils.isEmpty(maxSalary.getText().toString().trim())) {
                    listener.CustomSalayConfirm(minSalary.getText().toString(), maxSalary.getText().toString().trim());
                } else {
                    listener.Confirm(ccts.get(ccwv.getCurrentItem()), ccwv.getCurrentItem());
                }

                distancePopupWindow.dismiss();
            }
        });

        ccwv.setViewAdapter(new TimeWheelAdapter(ccts, context));
        if (TextUtils.isEmpty(content)) {
            cct = ccts.get(0);
            ccwv.setCurrentItem(0);
        } else {
            for (int i = 0; i < ccts.size(); i++) {
                if (ccts.get(i).getName().equals(content)) {
                    cct = ccts.get(i);
                    ccwv.setCurrentItem(i);
                }
            }

        }
        distancePopupWindow = new PopupWindow(outerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
        outerView.setFocusableInTouchMode(true);
        ColorDrawable cd = new ColorDrawable(Color.argb(136, 0, 0, 0));
        distancePopupWindow.setBackgroundDrawable(cd);
        distancePopupWindow.setOutsideTouchable(true);
        distancePopupWindow.setAnimationStyle(R.anim.popuwindow);
        distancePopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        return outerView;
    }

    /**
     * 两个选择Wheel
     * 
     * @param context
     * @param view
     * @param contents
     * @param listener
     * @param title
     * @param cotent
     * @return
     */
    @SuppressLint("InflateParams")
    public static View showDoubleWheel(Context context, View view, final OnWheelViewListener listener,
            final List<String> cctsLeft, final List<String> cctsRight, String title, String leftContent,
            String rightContent) {

        disMissThisView();

        View outerView = LayoutInflater.from(context).inflate(R.layout.doublewheel_view, null);
        final WheelView ccwvLeft = (WheelView) outerView
                .findViewById(R.id.leftwheel);
        ccwvLeft.setCyclic(false);
        final WheelView ccwvRight = (WheelView) outerView
                .findViewById(R.id.rightwheel);
        ccwvRight.setCyclic(false);
        TextView tvCancel = (TextView) outerView.findViewById(R.id.tvCancel);
        TextView tvConfirm = (TextView) outerView.findViewById(R.id.tvConfirm);
        TextView tvContent = (TextView) outerView.findViewById(R.id.tvTitle);
        tvCancel.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0) {
                distancePopupWindow.dismiss();
            }
        });
        tvConfirm.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0) {
                listener.doubleConfirm(cctsLeft.get(ccwvLeft.getCurrentItem()),
                        cctsRight.get(ccwvRight.getCurrentItem()));
                distancePopupWindow.dismiss();
            }
        });
        if (TextUtils.isEmpty(title)) {
            tvContent.setVisibility(View.INVISIBLE);
        } else {
            tvContent.setText(title);
        }
        ccwvLeft.setViewAdapter(new DateWheelAdapter(cctsLeft, context));
        if (TextUtils.isEmpty(leftContent)) {
            leftContent = cctsLeft.get(0);
            ccwvLeft.setCurrentItem(0);
        } else {
            for (int i = 0; i < cctsLeft.size(); i++) {
                if (cctsLeft.get(i).equals(leftContent)) {
                    leftContent = cctsLeft.get(i);
                    ccwvLeft.setCurrentItem(i);
                }
            }

        }
        ccwvRight.setViewAdapter(new DateWheelAdapter(cctsRight, context));
        if (TextUtils.isEmpty(rightContent)) {
            rightContent = cctsRight.get(0);
            ccwvRight.setCurrentItem(0);
        } else {
            for (int i = 0; i < cctsRight.size(); i++) {
                if (cctsRight.get(i).equals(rightContent)) {
                    rightContent = cctsRight.get(i);
                    ccwvRight.setCurrentItem(i);
                }
            }

        }
        distancePopupWindow = new PopupWindow(outerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
        outerView.setFocusableInTouchMode(true);
        ColorDrawable cd = new ColorDrawable(Color.argb(136, 0, 0, 0));
        distancePopupWindow.setBackgroundDrawable(cd);
        distancePopupWindow.setOutsideTouchable(true);
        distancePopupWindow.setAnimationStyle(R.anim.popuwindow);
        distancePopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        return outerView;
    }
}
