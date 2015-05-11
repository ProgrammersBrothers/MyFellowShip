package com.mygame.myfellowship.view.wheelview;

import java.util.List;

import com.mygame.myfellowship.R;
import com.mygame.myfellowship.bean.CfgCommonType;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
 

/**
 * /** HeadAdapter
 */
public class TimeWheelAdapter extends AbstractWheelAdapter {
    // Image size
    final int IMAGE_WIDTH = 50;
    final int IMAGE_HEIGHT = 50;
    public List<CfgCommonType> timeDateList = null;
    private LayoutInflater inflater;
    private Context context;

    /**
     * Constructor
     */
    public TimeWheelAdapter(List<CfgCommonType> timeDateList, Context context)
    {
        this.context = context;
        this.timeDateList = timeDateList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemsCount() {
        return timeDateList.size();
    }

    @Override
    public View getItem(int index, View cachedView, ViewGroup parent) {
        if (cachedView == null) {
            cachedView = inflater.inflate(R.layout.head_select_layout, null);
        }
        TextView textView1 = (TextView) cachedView.findViewById(R.id.textView1);
        textView1.setText(timeDateList.get(index).getName());

        return cachedView;
    }
}
