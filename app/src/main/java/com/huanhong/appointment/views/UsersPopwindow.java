package com.huanhong.appointment.views;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huanhong.appointment.R;
import com.huanhong.appointment.adapter.CommonAdapter;
import com.huanhong.appointment.adapter.ViewHolder;
import com.huanhong.appointment.bean.Staff;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 坎坎.
 * Date: 2019/7/2
 * Time: 15:46
 * describe:
 */
public class UsersPopwindow {
    public UsersPopwindow(Context context, View view, final List<Staff.User> list , final ConfirmCallback confirmCallback){
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_list,null);
        final PopupWindow  popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        popupWindow.setBackgroundDrawable(new ColorDrawable(0xc8000000));
        popupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);

        RecyclerView recycler_user =contentView.findViewById(R.id.recycler_user);
        View tv_cancel =contentView.findViewById(R.id.tv_cancel);
        View tv_confirm =contentView.findViewById(R.id.tv_confirm);
        recycler_user.setLayoutManager(new LinearLayoutManager(context));

        final boolean[] bs = new boolean[list.size()];

        recycler_user.setAdapter(new CommonAdapter<Staff.User>(context,list,R.layout.item_str) {
            @Override
            public void convert(final ViewHolder holder, final List<Staff.User> t) {
                TextView tv_ = holder.getView(R.id.tv_);
                final CheckBox ch_ = holder.getView(R.id.ch_);
                tv_.setText(t.get(holder.getRealPosition()).userName);
                ch_.setChecked(t.get(holder.getRealPosition()).check);
                bs[holder.getRealPosition()] = t.get(holder.getRealPosition()).check;

                ch_.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        bs[holder.getRealPosition()] = isChecked;
                    }
                });

            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectlist.clear();
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).check = bs[i];
                    if(list.get(i).check){
                        selectlist.add(list.get(i));
                    }
                }
                if(confirmCallback!=null){
                    confirmCallback.confirm(selectlist);
                }
                popupWindow.dismiss();
            }
        });
    }
    private List<Staff.User> selectlist  = new ArrayList<>();
    public interface ConfirmCallback{
        void confirm(List<Staff.User> selectlist);
    }
}
