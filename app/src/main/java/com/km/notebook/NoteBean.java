package com.km.notebook;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

@Entity
public class NoteBean implements Parcelable {

    @Id(autoincrement = true)
    Long id;
    @Unique
    private String  title;
    private String  content;
    private String  date;
    private String isSaveTxt;

    protected NoteBean(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        title = in.readString();
        content = in.readString();
        date = in.readString();
        isSaveTxt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(date);
        dest.writeString(isSaveTxt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NoteBean> CREATOR = new Creator<NoteBean>() {
        @Override
        public NoteBean createFromParcel(Parcel in) {
            return new NoteBean(in);
        }

        @Override
        public NoteBean[] newArray(int size) {
            return new NoteBean[size];
        }
    };

    public String getIsSaveTxt() {
        return isSaveTxt;
    }

    public void setIsSaveTxt(String isSaveTxt) {
        this.isSaveTxt = isSaveTxt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @Generated(hash = 568682706)
    public NoteBean(Long id, String title, String content, String date, String isSaveTxt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.isSaveTxt = isSaveTxt;
    }

    @Generated(hash = 451626881)
    public NoteBean() {
    }



}
