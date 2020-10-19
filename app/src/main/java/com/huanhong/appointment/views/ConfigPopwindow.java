package com.huanhong.appointment.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.huanhong.appointment.R;
import com.huanhong.appointment.utils.SharedPreferencesUtils;

/**
 * Created by 坎坎.
 * Date: 2019/7/2
 * Time: 15:46
 * describe:
 */
public class ConfigPopwindow {

    private static final String deviceType = "deviceType"; // 1 灯光设备 2其他设备
    private static final String areaType = "areaType"; // 1 会议室  2 工位  3 健身
    private static final String freeLightType = "freeLightType";  // 1 绿色  2红色  3黄色  4 关闭
    private static final String ingLightType = "ingLightType";
    private static final String seatLightType = "seatLightType";
    private static final String equipmentLightType = "equipmentLightType";

    public static int getDeviceType() {
        return SharedPreferencesUtils.readIntData(deviceType);
    }

    public static int getAreaType() {
        return SharedPreferencesUtils.readIntData(areaType);
    }

    public static int getFreeLightType() {
        return SharedPreferencesUtils.readIntData(freeLightType);
    }

    public static int getIngLightType() {
        return SharedPreferencesUtils.readIntData(ingLightType);
    }

    public static int getSeatLightType() {
        return SharedPreferencesUtils.readIntData(seatLightType);
    }

    public static int getEquipmentLightType() {
        return SharedPreferencesUtils.readIntData(equipmentLightType);
    }

    private final int[] deviceIds = {R.id.rb_device_light,R.id.rb_device_others};
    private final int[] areaIds = {R.id.rb_area_meeting,R.id.rb_area_seat,R.id.rb_area_equipment};
    private final int[] freeIds = {R.id.rb_light_free_green,R.id.rb_light_free_red,R.id.rb_light_free_yellow,R.id.rb_light_free_close};
    private final int[] ingIds = {R.id.rb_light_ing_green,R.id.rb_light_ing_red,R.id.rb_light_ing_yellow,R.id.rb_light_ing_close};
    private final int[] seatIds = {R.id.rb_seat_green,R.id.rb_seat_red,R.id.rb_seat_yellow,R.id.rb_seat_close};
    private final int[] equipmentIds = {R.id.rb_equipment_green,R.id.rb_equipment_red,R.id.rb_equipment_yellow,R.id.rb_equipment_close};

    private PopupWindow popupWindow;
    public ConfigPopwindow(Context context, View view) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_config, null);
         popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        popupWindow.setBackgroundDrawable(new ColorDrawable(0xc8000000));
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);

        View tv_confirm = contentView.findViewById(R.id.tv_confirm);


        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        initConfig(contentView);
    }

    private RadioGroup rg_device, rg_area, rg_light_free, rg_light_ing, rg_light_seat, rg_light_equipment;

    private void initConfig(View contentView) {
        rg_device = contentView.findViewById(R.id.rg_device);
        rg_area = contentView.findViewById(R.id.rg_area);
        rg_light_free = contentView.findViewById(R.id.rg_light_free);
        rg_light_ing = contentView.findViewById(R.id.rg_light_ing);
        rg_light_seat = contentView.findViewById(R.id.rg_light_seat);
        rg_light_equipment = contentView.findViewById(R.id.rg_light_equipment);

        rg_device.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_device_light:
                        SharedPreferencesUtils.addData(deviceType, 0);
                        break;
                    case R.id.rb_device_others:
                        SharedPreferencesUtils.addData(deviceType, 1);
                        break;
                }
            }
        });
        rg_area.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_area_meeting:
                        SharedPreferencesUtils.addData(areaType, 0);
                        break;
                    case R.id.rb_area_seat:
                        SharedPreferencesUtils.addData(areaType, 1);
                        break;
                    case R.id.rb_area_equipment:
                        SharedPreferencesUtils.addData(areaType, 2);
                        break;
                }

            }
        });
        rg_light_free.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_light_free_green:
                        SharedPreferencesUtils.addData(freeLightType, 0);
                        break;
                    case R.id.rb_light_free_red:
                        SharedPreferencesUtils.addData(freeLightType, 1);
                        break;
                    case R.id.rb_light_free_yellow:
                        SharedPreferencesUtils.addData(freeLightType, 2);
                        break;
                    case R.id.rb_light_free_close:
                        SharedPreferencesUtils.addData(freeLightType, 3);
                        break;
                }
            }
        });
        rg_light_ing.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_light_ing_green:
                        SharedPreferencesUtils.addData(ingLightType, 0);
                        break;
                    case R.id.rb_light_ing_red:
                        SharedPreferencesUtils.addData(ingLightType, 1);
                        break;
                    case R.id.rb_light_ing_yellow:
                        SharedPreferencesUtils.addData(ingLightType, 2);
                        break;
                    case R.id.rb_light_ing_close:
                        SharedPreferencesUtils.addData(ingLightType, 3);
                        break;
                }
            }
        });
        rg_light_seat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_seat_green:
                        SharedPreferencesUtils.addData(seatLightType, 0);
                        break;
                    case R.id.rb_seat_red:
                        SharedPreferencesUtils.addData(seatLightType, 1);
                        break;
                    case R.id.rb_seat_yellow:
                        SharedPreferencesUtils.addData(seatLightType, 2);
                        break;
                    case R.id.rb_seat_close:
                        SharedPreferencesUtils.addData(seatLightType, 3);
                        break;
                }
            }
        });
        rg_light_equipment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_equipment_green:
                        SharedPreferencesUtils.addData(equipmentLightType, 0);
                        break;
                    case R.id.rb_equipment_red:
                        SharedPreferencesUtils.addData(equipmentLightType, 1);
                        break;
                    case R.id.rb_equipment_yellow:
                        SharedPreferencesUtils.addData(equipmentLightType, 2);
                        break;
                    case R.id.rb_equipment_close:
                        SharedPreferencesUtils.addData(equipmentLightType, 3);
                        break;
                }
            }
        });
        rg_device.check(deviceIds[getDeviceType()]);
        rg_area.check(areaIds[getAreaType()]);
        rg_light_free.check(freeIds[getFreeLightType()]);
        rg_light_ing.check(ingIds[getIngLightType()]);
        rg_light_seat.check(seatIds[getSeatLightType()]);
        rg_light_equipment.check(equipmentIds[getEquipmentLightType()]);

    }

}
