package com.huanhong.appointment;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 坎坎.
 * Date: 2019/7/9
 * Time: 16:34
 * describe:
 */
public class RangeBar extends RelativeLayout {
    private String[] times = {"02:00", "04:00", "06:00", "08:00", "10:00", "12:00",
            "14:00", "16:00", "18:00", "20:00", "22:00"};
    private LayoutInflater inflater;
    private View left_line;
    private Context context;

    public RangeBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflater = LayoutInflater.from(context);
        this.context = context ;
        init();
    }

    private void init() {
        addView(getBgView());
        addView(getTimeText());
    }

    private View getBgView() {
        View view = new View(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(960, 50);
        view.setLayoutParams(lp);
        view.setBackgroundResource(R.drawable.range_bar_bg);
        return view;
    }

    private View getTimeText() {
        View view = inflater.inflate(R.layout.ll_time, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(960, LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lp);
        left_line= view.findViewById(R.id.left_line);
        return view;
    }

    private List<View> rangViews = new ArrayList<>();
    private View curr = null;

    public void setTimeRangeList(List<TimeBean> beans){
        if(rangViews.size()>0){
            for (View rangView : rangViews) {
                removeView(rangView);
            }
        }
        rangViews.clear();
        if(beans!=null&&beans.size()>0){
            for (TimeBean bean : beans) {
                setTimeRange(bean,0);
            }
        }
    }

    private View line ;
    private void initLine(){
        line = new View(context);
        LayoutParams lp = new LayoutParams(1, 50);
        line.setLayoutParams(lp);
        line.setBackgroundResource(R.color.yellow);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            line.setTranslationZ(100f);
        }
        addView(line);
    }
    public void setCurrentTime(String time){
        if(line == null){
            initLine();
        }
        if (time.contains(":")) {
            String[] starts = time.split(":");
            int startH = Integer.parseInt(starts[0]);
            int startM = Integer.parseInt(starts[1]);
            int shw = (startH / 2) * itemWidth  ;
            int smw = (int) (((startM + (startH%2) * 60  )/120f)*itemWidth);
            int startLeft = shw + smw;
            RelativeLayout.LayoutParams lp = (LayoutParams) line.getLayoutParams();
            lp.leftMargin = startLeft;
            line.setLayoutParams(lp);
        }
    }
    int itemWidth = 80 ;
    public void setTimeRange( TimeBean bean ,int flag) {
        if (bean.start.contains(":") && bean.end.contains(":")) {
            String[] starts = bean.start.split(":");
            int startH = Integer.parseInt(starts[0]);
            int startM = Integer.parseInt(starts[1]);
            int shw = (startH / 2) * itemWidth  ;
            int smw = (int) (((startM + (startH%2) * 60  )/120f)*itemWidth);
            int startLeft = shw + smw;

            String[] ends = bean.end.split(":");
            int endH = Integer.parseInt(ends[0]);
            int endM = Integer.parseInt(ends[1]);
            int ehw = (endH / 2) * itemWidth  ;
            int emw = (int) (((endM + (endH%2) * 60  )/120f)*itemWidth);
            int endLeft = ehw + emw;

            int width  =  endLeft  - startLeft;

            View rangView = new View(context);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, 50);
            lp.leftMargin = startLeft;
            rangView.setLayoutParams(lp);
            if(flag==0){
                rangView.setBackgroundResource(R.color.red);
                rangViews.add(rangView);
            }else if(flag==1)  {
                if(curr!=null){
                    removeView(curr);
                    curr= null;
                }
                rangView.setBackgroundResource(R.color.green);
                curr = rangView;
            }
            addView(rangView);
        }
    }

    public static class TimeBean{
        private String start, end;
        public TimeBean(String start, String end) {
            this.start = start;
            this.end = end;
        }
        public String getStart() {
            return start;
        }
        public void setStart(String start) {
            this.start = start;
        }
        public String getEnd() {
            return end;
        }
        public void setEnd(String end) {
            this.end = end;
        }
    }
}
