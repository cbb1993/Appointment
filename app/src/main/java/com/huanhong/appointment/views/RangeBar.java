package com.huanhong.appointment.views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huanhong.appointment.R;

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
    private Context context;
    private int height = 50;

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
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(itemWidth*12, height);
        view.setLayoutParams(lp);
        view.setBackgroundResource(R.drawable.range_bar_bg);
        return view;
    }

    private View getTimeText() {
        View view = inflater.inflate(R.layout.ll_time, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(itemWidth*12, LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lp);
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
            int i = 1;
            for (TimeBean bean : beans) {
                setTimeRange(bean,i++,0);
            }
        }
    }

    private ImageView line ;
    private void initLine(){
        line = new ImageView(context);
        LayoutParams lp = new LayoutParams(16, 16);
        lp.topMargin =42;
        line.setLayoutParams(lp);
        line.setImageResource(R.mipmap.icon_current);
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
            lp.leftMargin = startLeft-8;
            line.setLayoutParams(lp);
        }
    }
    int itemWidth = 120 ;
    public void setTimeRange( TimeBean bean ,int tag,int flag) {
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

            final View rangView = new View(context);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
            lp.leftMargin = startLeft;
            rangView.setLayoutParams(lp);
            if(flag==0){
                if(bean.state==1){
                    rangView.setBackgroundResource(R.color.meet_blue);
                }else {
                    rangView.setBackgroundResource(R.color.meet_grey);
                }
                rangViews.add(rangView);
            }else if(flag==1)  {
                if(curr!=null){
                    removeView(curr);
                    curr= null;
                }
                rangView.setBackgroundResource(R.color.green);
                curr = rangView;
            }
            rangView.setTag(tag);

            rangView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listner!=null){
                        Object tag  =  rangView.getTag();
                        if(tag!=null){
                            int p = Integer.parseInt(tag.toString()) - 1;
                            listner.click(p);
                        }
                    }
                }
            });

            addView(rangView);
        }
    }

    public static class TimeBean{
        private String start, end;
        private int state; //  1 正常  2 已结束
        public TimeBean(String start, String end ,int state) {
            this.start = start;
            this.end = end;
            this.state = state;
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
        public int getState() {
            return state;
        }
    }

    private MeetClickListner listner ;

    public void setMeetClickListner(MeetClickListner listner) {
        this.listner = listner;
    }

    public interface MeetClickListner{
        void click(int p);
    }
}
