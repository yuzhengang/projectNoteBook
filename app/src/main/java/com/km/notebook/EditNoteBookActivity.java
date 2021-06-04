package com.km.notebook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.unistrong.yang.zb_permission.Permission;
import com.unistrong.yang.zb_permission.ZbPermission;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ren.qinc.edit.PerformEdit;

public class EditNoteBookActivity extends BaseActivity implements SearchEditText.OnPreDrawListener, SearchControlPopupWindow.SearchControlListener {
    private static final int REQUEST_STORAGE = 1001;
    EditText noteTitleEt;
    SearchEditText noteContentEt;
    TextView noteTotalNumTv, noteTimeTv;
    ImageView noteConfirmIv, noteRedoIv, noteUndoIv, noteBackIv, noteSearchIv;
    PerformEdit mPerformEdit;
    boolean isUpdate = false;
    LinearLayout editMenuLat;
    TextShowTask textShowTask;
    TextSaveTask textSaveTask;
    SearchControlPopupWindow searchControlPopupWindow;
    LoadingDialog loadingDialog;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_edit_notebook);
        noteConfirmIv = findViewById(R.id.noteConfirmIv);
        noteRedoIv = findViewById(R.id.noteRedoIv);
        noteUndoIv = findViewById(R.id.noteUndoIv);
        noteBackIv = findViewById(R.id.noteBackIv);
        noteContentEt = findViewById(R.id.noteContentEt);
        noteTitleEt = findViewById(R.id.noteTitleEt);
        noteTotalNumTv = findViewById(R.id.noteTotalNumTv);
        editMenuLat = findViewById(R.id.editMenuLat);
        noteTimeTv = findViewById(R.id.noteTimeTv);
        noteBackIv = findViewById(R.id.noteBackIv);
        noteSearchIv = findViewById(R.id.noteSearchIv);
        noteContentEt.setOnPreDrawListener(this);
        loadingDialog = new LoadingDialog(EditNoteBookActivity.this);
        loadingDialog.setInterceptBack(false);
        noteBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
     // new TextShowTask(loadingDialog).execute(Environment.getExternalStorageDirectory() + "/zzzz/test.txt");
        mPerformEdit = new PerformEdit(noteContentEt);
        mPerformEdit.setDefaultText("");
        searchControlPopupWindow = new SearchControlPopupWindow(this);
        searchControlPopupWindow.setSearchControlListener(this);
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("HH:mm");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        System.out.println("现在时间：" + sdf.format(date)); // 输出已经格式化的现在时间（24小时
        noteTimeTv.setText("今天: " + sdf.format(date));
        noteRedoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPerformEdit.redo();
            }
        });
        noteSearchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputTitleDialog();
            }
        });
        noteUndoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPerformEdit.undo();
            }
        });
        noteContentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                noteTotalNumTv.setText("共" + s.length() + "字");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        noteConfirmIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = noteTitleEt.getText().toString().trim();
                final String content = noteContentEt.getText().toString().trim();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                final String strDate = sdf.format(c.getTime());
                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(EditNoteBookActivity.this, "请输入标题", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(EditNoteBookActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isUpdate) {
                    if (noteBean.getIsSaveTxt().equals("true")) {
                        if (content.length() != oldLength) {
                            File oldFile = new File(noteBean.getContent());
                            if (oldFile.isFile() && oldFile.exists()) {
                                oldFile.delete();
                            }
                            String path = FileUtils.getRootFilePath();
                            textSaveTask = new TextSaveTask(true,loadingDialog,title, strDate);
                            textSaveTask.execute(path, title + ".txt", content);
                        } else {
                            NoteDaoUtil.updateNote(new NoteBean(noteBean.getId(), title, noteBean.getContent(), strDate, "true"));
                            finish();
                        }
                    } else {
                        if(content.getBytes().length / 1024  < 500){
                            NoteDaoUtil.updateNote(new NoteBean(noteBean.getId(), title, content, strDate, "false"));
                            finish();
                        }else{
                            String path = FileUtils.getRootFilePath();
                            textSaveTask = new TextSaveTask(true,loadingDialog,title, strDate);
                            textSaveTask.execute(path, title + ".txt", content);
                        }
                    }
                } else {
                    if (content.getBytes().length / 1024  < 500) {
                        NoteDaoUtil.insertNote(new NoteBean(null, title, content, strDate, "false"));
                        finish();
                    } else {
                        String path = FileUtils.getRootFilePath();
                        FileUtils.crateFile(path, title + ".txt");
                        textSaveTask = new TextSaveTask(false, loadingDialog,title, strDate);
                        textSaveTask.execute(path, title + ".txt", content);
                    }
                }
            }
        });
        loadData();
    }



    NoteBean noteBean;
    public void loadData() {
      Long      noteBeanId = getIntent().getLongExtra("NOTE_INFO_ID",0);
      noteBean =NoteDaoUtil.queryNoteById(noteBeanId);
        if (noteBean != null) {
            editMenuLat.setVisibility(View.INVISIBLE);
            isUpdate = getIntent().getBooleanExtra("IS_UPDATE", false);
            noteTitleEt.setText(noteBean.getTitle());
            if (noteBean.getIsSaveTxt().equals("true")) {
                String path = noteBean.getContent();
                new TextShowTask(loadingDialog).execute(path);
            } else {
                editMenuLat.setVisibility(View.VISIBLE);
                noteContentEt.setText(noteBean.getContent());
                noteContentEt.setFocusable(true);
                noteContentEt.setFocusableInTouchMode(true);
                noteContentEt.requestFocus();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date date = null;
            try {
                date = DataUtils.getDateWithDateString(noteBean.getDate());
                if (DataUtils.IsToday(noteBean.getDate())) {
                    noteTimeTv.setText("今天 " + sdf.format(date));
                } else if (DataUtils.IsYesterday(noteBean.getDate())) {
                    noteTimeTv.setText("昨天 " + sdf.format(date));
                } else {
                    noteTimeTv.setText(noteBean.getDate());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    List<Integer> searchIndexList = new ArrayList<Integer>();

    private void inputTitleDialog() {
        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("文本查找").setView(inputServer).setNegativeButton("取消", null);
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String inputName = inputServer.getText().toString().trim();
                        String contentStr = noteContentEt.getText().toString();
                        new SearchTask(loadingDialog,contentStr, inputName).execute();
                    }
                });
        builder.show();
    }


    class SearchTask extends AsyncTask<String, Void, SpannableString> {

        String inputName,  contentStr;
        LoadingDialog   loadingDialog;
        public SearchTask(  LoadingDialog   loadingDialog,  String contentStr,String  inputName ){
            this.contentStr=contentStr;
            this.inputName=inputName;
            this.loadingDialog=loadingDialog;
        }
        @Override
        protected SpannableString doInBackground(String... strings) {

            return KeywordUtil.matcherSearchTitle(Color.RED, contentStr, inputName);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            noteContentEt.requestFocus();
            loadingDialog.setLoadingText("正在查找内容...");
            loadingDialog.setSuccessText("查找成功");
            loadingDialog.setFailedText("查找内容为空");
            loadingDialog.show();
        }

        @Override
        protected void onPostExecute(SpannableString spannableString) {
            super.onPostExecute(spannableString);
            noteContentEt.setText(spannableString);
            searchIndexList.clear();
            Integer _search_pos = contentStr.indexOf(inputName);
            while (_search_pos > -1) {
                searchIndexList.add(_search_pos + inputName.length());
                _search_pos = contentStr.indexOf(inputName, _search_pos + 1);
            }
            if (searchIndexList.size() > 0) {
                loadingDialog.loadSuccess();
                noteContentEt.setSelection(searchIndexList.get(0));
                searchControlPopupWindow.show(editMenuLat);
                searchControlPopupWindow.insexSearchTv.setText((searchIndex + 1) + "/" + NoteApplication.num);
            } else {
                loadingDialog.loadFailed();
            }
        }
    }


    @Override
    public void onPreDraw(int bottom) {

    }

    int searchIndex;


    @Override
    public void upSearch() {
        if (searchIndex > 0) {
            searchIndex--;
        }else{
            searchIndex=searchIndexList.size()-1;
        }
        noteContentEt.setSelection(searchIndexList.get(searchIndex));
        searchControlPopupWindow.insexSearchTv.setText((searchIndex + 1) + "/" + NoteApplication.num);
    }

    @Override
    public void downSearch() {
        if (searchIndex < searchIndexList.size() - 1) {
            searchIndex++;
        }else{
            searchIndex=0;
        }
        noteContentEt.setSelection(searchIndexList.get(searchIndex));
        searchControlPopupWindow.insexSearchTv.setText((searchIndex + 1) + "/" + NoteApplication.num);
    }

    private class TextSaveTask extends AsyncTask<String, Integer, String> {

        String title;
        String steDate;
        LoadingDialog  loadingDialog;
        boolean  isUpdate;
        public TextSaveTask( boolean  isUpdate,LoadingDialog  loadingDialog,String title, String steDate) {
            this.title = title;
            this.steDate = steDate;
            this.loadingDialog=loadingDialog;
            this.isUpdate=isUpdate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.setLoadingText("正在保存文本...");
            loadingDialog.setSuccessText("保存成功");
            loadingDialog.setFailedText("保存失败");
            loadingDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.e("正在保存进度", values[0] + "");
        }

        @Override
        protected String doInBackground(String... params) {
            if (FileUtils.writeString2File(params[0], params[1], params[2])) {
                return params[0] + params[1];
            }
            return "";
        }

        @Override
        protected void onPostExecute(String b) {
            super.onPostExecute(b);
            Log.e("TextSaveTask", b);
            if (TextUtils.isEmpty(b)) {
                loadingDialog.loadFailed();
            } else {
                loadingDialog.loadSuccess();
                if(isUpdate){
                    NoteDaoUtil.updateNote(new NoteBean(noteBean.getId(), title, b, steDate, "true"));
                }else{
                    NoteDaoUtil.insertNote(new NoteBean(null, title, b, steDate, "true"));
                }
                finish();
            }
        }
    }


    private int oldLength;

    private class TextShowTask extends AsyncTask<String, Integer, String> {

        LoadingDialog  loadingDialog;

        public  TextShowTask(LoadingDialog loadingDialog ){
            this.loadingDialog=loadingDialog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.setLoadingText("正在读取文本...");
            loadingDialog.setFailedText("读取失败");
            loadingDialog.setSuccessText("读取成功");
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return FileUtils.ReadFile2String(params[0], "utf-8");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (TextUtils.isEmpty(s)) {
                loadingDialog.loadFailed();
            } else {
                loadingDialog.loadSuccess();
                oldLength = s.length();
                noteContentEt.setText(s);
                mPerformEdit.setDefaultText(s);
                noteContentEt.setFocusable(true);
                noteContentEt.setFocusableInTouchMode(true);
                editMenuLat.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (textSaveTask != null && textSaveTask.getStatus() == AsyncTask.Status.RUNNING) {
            textSaveTask.cancel(true);
        }
        if (textShowTask != null && textShowTask.getStatus() == AsyncTask.Status.RUNNING) {
            textShowTask.cancel(true);
        }
    }

    @Override
    public void finish() {
        super.finish();
    }
}
