package com.km.notebook;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.km.notebook.searchview.seach.KylinSearchView;
import com.km.notebook.searchview.seach.OnSearchFocusListener;
import com.km.notebook.searchview.seach.OnSearchListener;
import com.unistrong.yang.zb_permission.ZbPermission;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.List;

public class MainActivity extends BaseActivity {

    private static final int REQUEST_CONTACT = 1001;
    SwipeRecyclerView  recyclerView;
    NoteAdapter   noteAdapter;
    Toolbar toolbar;
    ImageView  noDataIv;
    KylinSearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchView = (KylinSearchView) findViewById(R.id.sv_default);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView=findViewById(R.id.recyclerView);
        noDataIv=findViewById(R.id.noDataIv);
        setSupportActionBar(toolbar);
        Button fab = (Button) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheckW= ContextCompat.checkSelfPermission(MainActivity.this,    Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int permissionCheckR= ContextCompat.checkSelfPermission(MainActivity.this,    Manifest.permission.READ_EXTERNAL_STORAGE);
                if(permissionCheckR==1&&permissionCheckW==1){
                    Intent  intent=new Intent(MainActivity.this,EditNoteBookActivity.class);
                    startActivity(intent);
                }else{
                    ZbPermission.with(MainActivity.this)
                            .addRequestCode(REQUEST_CONTACT)
                            .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .request(new ZbPermission.ZbPermissionCallback() {
                                @Override
                                public void permissionSuccess(int i) {
                                    Intent  intent=new Intent(MainActivity.this,EditNoteBookActivity.class);
                                    startActivity(intent);
                                }
                                @Override
                                public void permissionFail(int i) {

                                }
                            });
                }
            }
        });

        noteAdapter=new NoteAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacesItemDecoration(DensityUtil.dip2px(this,10)));
        recyclerView.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(MainActivity.this);
                deleteItem.setBackgroundColor(Color.parseColor("#FF3D39"))
                        .setText("删除")
                        .setTextColor(Color.WHITE)
                        .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                        .setWidth(170);
                rightMenu.addMenuItem(deleteItem);
            }
        });
        recyclerView.setOnItemMenuClickListener(new OnItemMenuClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
                showDeleteInfoDlg(menuBridge,adapterPosition);
            }
        });
        recyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int adapterPosition) {
                 Intent  intent=new Intent(MainActivity.this,EditNoteBookActivity.class);
                 intent.putExtra("NOTE_INFO_ID",noteAdapter.getData().get(adapterPosition).getId());
                 intent.putExtra("IS_UPDATE",true);
                 startActivity(intent);
            }
        });
        recyclerView.setAdapter(noteAdapter);
        searchView.setOnSearchListener(new OnSearchListener() {
            @Override
            public void search(String content) {
                if(TextUtils.isEmpty(content)){
                    Toast.makeText(MainActivity.this,"请输入搜索内容",Toast.LENGTH_SHORT).show();
                    return;
                }
                loadDataBySearch(content);
            }
            @Override
            public void searchContentListener(String content) {
                 if(!TextUtils.isEmpty(content)){
                     loadDataBySearch(content);
                 }else{
                     loadDatas();
                 }
             }
        });
    }



    public   void   showDeleteInfoDlg(final SwipeMenuBridge menuBridge, final int  index){
        AlertDialog aldg;
        AlertDialog.Builder adBd=new AlertDialog.Builder(MainActivity.this);
        adBd.setTitle("温馨提示");
        adBd.setIcon(R.mipmap.ic_launcher);
        adBd.setMessage("确定要删除本条笔记吗？");
        adBd.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                menuBridge.closeMenu();
                NoteBean noteBean=noteAdapter.getData().get(index);
                noteAdapter.notifyItemRemoved(which);
                NoteDaoUtil.deleteNote(noteBean.getId());
                loadDatas();
            }
        });
        adBd.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recyclerView.smoothCloseMenu();
            }
        });
        aldg=adBd.create();
        aldg.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        loadDatas();
    }

    public   void   loadDatas(  ){
        List<NoteBean>  noteBeanList=NoteDaoUtil.queryAll();
        toolbar.setTitle(noteBeanList.size()+"条笔记");
        noteAdapter.notifyDataSetChanged(noteBeanList);
        if(noteAdapter.getData().size()==0){
            noDataIv.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else{
            noDataIv.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


    public   void   loadDataBySearch(String  title){
        List<NoteBean>  noteBeanList=NoteDaoUtil.queryNoteByTitle(title);
        toolbar.setTitle(noteBeanList.size()+"条笔记");
        noteAdapter.notifyDataSetChanged(noteBeanList);
        if(noteAdapter.getData().size()==0){
            noDataIv.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else{
            noDataIv.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        recyclerView.smoothCloseMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //写一个menu的资源文件.然后创建就行了.
   //   getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
