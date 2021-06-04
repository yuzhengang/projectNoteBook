package com.km.notebook;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SearchControlPopupWindow {
    private Context context;
    public PopupWindow popupWindow;
    private View mView;
    ImageView  upSearchIv,downSearchIv;
    public TextView  insexSearchTv;
    SearchControlListener  searchControlListener;
    public   void  setSearchControlListener(   SearchControlListener  searchControlListener ){
        this.searchControlListener=searchControlListener;
    }



    public SearchControlPopupWindow(Context context) {
        this.context = context;
        createPopupWindow();
    }






    interface    SearchControlListener{

        void    upSearch(  );

        void    downSearch( );

    }



    private void createPopupWindow() {
        popupWindow = new PopupWindow(context);
        mView = LayoutInflater.from(context).inflate(R.layout.layout_search_control, null);
        popupWindow.setContentView(mView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(false);
        popupWindow.setWidth(DensityUtil.dip2px(context,50));
        popupWindow.setHeight(DensityUtil.dip2px(context,120));
        popupWindow.setAnimationStyle(R.style.Animation);
        popupWindow.setClippingEnabled(false);
        upSearchIv=mView.findViewById(R.id.upSearchIv);
        downSearchIv=mView.findViewById(R.id.downSearchIv);
        insexSearchTv=mView.findViewById(R.id.insexSearchTv);
        upSearchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(searchControlListener!=null){
                  searchControlListener.upSearch();
              }
            }
        });
        downSearchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(searchControlListener!=null){
                   searchControlListener.downSearch();
               }
            }
        });
    }

    public void show(View parent) {
        popupWindow.showAtLocation(parent, Gravity.RIGHT , 0, 0);
    }
}
