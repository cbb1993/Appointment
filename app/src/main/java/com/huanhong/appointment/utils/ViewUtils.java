package com.huanhong.appointment.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ViewUtils {
    /**
     * 重新计算ListView的高度，解决ScrollView和ListView两个View都有滚动的效果，在嵌套使用时起冲突的问题
     *
     * @param listView
     */
    public static void setListViewHeight(ListView listView) {

        // 获取ListView对应的Adapter

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static void setListViewHeight(ListView listView, int count) {

        // 获取ListView对应的Adapter

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;

        if (listAdapter.getCount() < count)
            count = listAdapter.getCount();

        for (int i = 0, len = count; i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (count - 1));
        listView.setLayoutParams(params);
    }

    public static int getGridViewHeight(GridView gridView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return 0;
        }
        int totalHeight = 0;
        int count = listAdapter.getCount();
        int numCoulums = gridView.getNumColumns();
        if (numCoulums <= 0) {
            numCoulums = 3;
        }
        int len = count / (numCoulums + 1) + 1;
        for (int i = 0; i < count; i += numCoulums) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, gridView);
            measureView(listItem); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            totalHeight += (gridView.getVerticalSpacing() * (len - 1));
        }
        totalHeight += gridView.getPaddingTop() + gridView.getPaddingBottom();

        return totalHeight;
    }

    public static void setGridViewHeight(GridView gridView) {
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = getGridViewHeight(gridView);
        gridView.setLayoutParams(params);
    }

    public static void measureView(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                , View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    }

    /**
     * 4.4之下返回0,4.4以上正常，因为4.4以下不支持沉浸式
     */
    public static int getStatusBarHeightWhenTranslucentStatusBar(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return 0;
        } else return getStatusBarHeight(context);
    }

    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz
                    .getField("status_bar_height")
                    .get(object)
                    .toString());
            statusBarHeight = context
                    .getResources()
                    .getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    public static Bitmap getViewShot(View view) {
        if (view == null) {
            return null;
        }
        ;
        // 获取当前视图的view
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);

        // 获取状态栏高度
        int width = view.getWidth();
        int height = view.getHeight();

        Bitmap scrBmp = null;
        try {
            // 去掉标题栏的截图
            scrBmp = Bitmap.createBitmap(view.getDrawingCache(), 0, 0,
                    width, height);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();

        return scrBmp;
    }

    public static void setSystemUiGone(View view) {
        view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public static void showSystemUiVisible(View view) {
        view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


    public static void hideBottomUIMenu(Activity context) {
        if(Build.VERSION.SDK_INT >= 19) {
            View decorView = context.getWindow().getDecorView();

            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;

            decorView.setSystemUiVisibility(uiOptions);

        }
    }

    // 获得锁屏的设置
    public static WindowManager.LayoutParams getLockLayoutParams() {
        WindowManager.LayoutParams mLayoutParams =new  WindowManager.LayoutParams();
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        mLayoutParams.flags = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return mLayoutParams;
    }
}
