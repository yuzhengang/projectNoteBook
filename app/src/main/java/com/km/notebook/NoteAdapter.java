package com.km.notebook;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NoteAdapter extends BaseAdapter<NoteAdapter.ViewHolder>{


    private List<NoteBean> mDataList;

    public NoteAdapter(Context context) {
        super(context);
    }



    public void notifyDataSetChanged(List<NoteBean> dataList) {
        this.mDataList = dataList;
        super.notifyDataSetChanged();
    }


    public  List<NoteBean>   getData(  ){
        return  mDataList;
    }
    @NonNull
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_note_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder viewHolder, int position) {
        viewHolder.setData(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView  noteTitleTv,noteDateTv;

        public ViewHolder(View itemView) {
            super(itemView);
            noteTitleTv=itemView.findViewById(R.id.noteTitleTv);
            noteDateTv=itemView.findViewById(R.id.noteDateTv);
        }
        public void setData(NoteBean noteBean) {
            noteTitleTv.setText(noteBean.getTitle());
            SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
            Date date= null;
            try {
                date =   DataUtils.getDateWithDateString(noteBean.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                if(DataUtils.IsToday(noteBean.getDate())){
                    noteDateTv.setText("今天 "+sdf.format(date));
                }else if(DataUtils.IsYesterday(noteBean.getDate())){
                    noteDateTv.setText("昨天 "+sdf.format(date));
                }else{
                    noteDateTv.setText(noteBean.getDate());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
