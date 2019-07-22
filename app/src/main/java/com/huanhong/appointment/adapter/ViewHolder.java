package com.huanhong.appointment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;
    public Object obj;

    public ViewHolder(View itemView, int position) {
        super(itemView);
        mConvertView = itemView;
        mPosition = position;
        mViews = new SparseArray<>();
        mConvertView.setTag(this);
    }


    public static ViewHolder get(Context context, View convertView,
                                 ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
            return new ViewHolder(itemView, position);
        } else {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.mPosition = position;
            return holder;
        }
    }


    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }


    public int getRealPosition() {
        return mPosition;
    }


    public void updatePosition(int position) {
        mPosition = position;
    }
}
