package com.xxxman.autotest.shell;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UpdateActivity extends AppCompatActivity implements OnScrollListener {

    private ListView listView;
    private static final String TAG = UpdateActivity.class.getName();
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private MyAdapter adapter;
    private MyHandler myHandler;
    private long mTaskId = 0;
    private DownloadManager downloadManager;
    private String downloadPath ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.list_view);
        adapter = new MyAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(this);
        myHandler = new MyHandler();
        UpdateThread thread = new UpdateThread();
        thread.start();
    }
    /**
     * 接受消息，处理消息 ，此Handler会与当前主线程一块运行
     * */
    class MyHandler extends Handler {
        public MyHandler() {}

        public MyHandler(Looper L) {
            super(L);
        }

        // 子类必须重写此方法，接受数据
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle b = msg.getData();
            if(b.getString("error_num")==""){
                UpdateActivity.this.adapter.notifyDataSetChanged();
            }else{
                Toast.makeText(getApplicationContext(), "查询失败,请稍后重新。"+b.getString("error_num")+"："+b.getString("error_msg"), Toast.LENGTH_LONG).show();
            }

        }
    }
    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
//        Toast.makeText(this, "更新中...123", Toast.LENGTH_LONG).show();
//        mLastItem = mFirstVisibleItem + mVisibleItemCount - 1;
//        if (adapter.getCount() > mCount) {
//            mListView.removeFooterView(mLoadLayout);
//        }
//        UpdateThread thread = new UpdateThread();
//        thread.start();
    }
    @Override
    public void onScroll(AbsListView view, int mFirstVisibleItem, int mVisibleItemCount, int mTotalItemCount) {
//        Toast.makeText(this, "更新中...", Toast.LENGTH_LONG).show();
//        UpdateThread thread = new UpdateThread();
//        thread.start();
    }
    class UpdateThread extends Thread {

        @Override
        public void run() {
            super.run();
            MyConnection my  = new MyConnection();
            String url = Constant.URL();
            String context = "{\"function\":\"F100012\",\"user\":{\"id\":\"1\",\"session\":\"123\"},\"content\":{}}";
            Map<String, String> parms = new HashMap<>();
            parms.put("jsonContent", context);
            Log.d(TAG, "http请求" + context);
            String rs = my.getContextByHttp(url, parms);
            Log.d(TAG, "http请求结果" + rs);
            Gson gson = new GsonBuilder().serializeNulls().create();
            try{
                HttpResult requestParameter = (HttpResult) gson.fromJson(rs, new TypeToken<HttpResult>() {}.getType());
                Log.d(TAG, "error" + requestParameter.getErrorNo()+":"+requestParameter.getErrorInfo());
                Log.d(TAG, "list" + "("+requestParameter.getList().size()+"):"+requestParameter.getList());
                if(requestParameter.getErrorNo()=="" && requestParameter.getList().size()>0){
                    list = requestParameter.getList();
                    Message msg = new Message();
                    Bundle b = new Bundle();// 存放数据
                    b.putString("error_num",requestParameter.getErrorNo());
                    b.putString("error_msg",requestParameter.getErrorInfo());
                    msg.setData(b);
                    UpdateActivity.this.myHandler.sendMessage(msg); // 向Handler发送消息，更新UI
                }
            }catch (Exception e){
                Message msg = new Message();
                Bundle b = new Bundle();// 存放数据
                b.putString("error_num","-999");
                b.putString("error_msg","网络错误！");
                msg.setData(b);
                UpdateActivity.this.myHandler.sendMessage(msg); // 向Handler发送消息，更新UI
            }
        }


    }
    class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        public MyAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return (long)list.get(arg0).get("id");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int i = position;
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item, null);
                holder.img = (ImageView)convertView.findViewById(R.id.img);
                holder.version = (TextView)convertView.findViewById(R.id.version);
                holder.remark = (TextView)convertView.findViewById(R.id.remark);
                holder.down_btn = (Button)convertView.findViewById(R.id.down_btn);
                convertView.setTag(holder);

            }else {
                holder = (ViewHolder)convertView.getTag();
            }
            if("助手".equals(list.get(position).get("type"))){
                holder.img.setBackgroundResource(R.drawable.i1);
            }else if ("流程".equals(list.get(position).get("type"))){
                holder.img.setBackgroundResource(R.drawable.i2);
            }else{
                holder.img.setBackgroundResource(R.drawable.i3);
            }
            holder.version.setText((String)list.get(position).get("version"));
            holder.remark.setText("更新时间："+(String)list.get(position).get("update_time"));
            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showInfo(i);
                }
            });
            return convertView;
        }
    }
    public final class ViewHolder{
        public ImageView img;
        public TextView version;
        public TextView remark;
        public Button down_btn;
    }
    /**
     * listview中点击按键弹出对话框
     */
    public void showInfo(final int i){
        new AlertDialog.Builder(this)
            .setTitle((String)list.get(i).get("version"))
            .setMessage((String)list.get(i).get("remark"))
            .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    download(i);
                }
            }).show();
    }
    public void download(int i){
        String versionUrl = (String) list.get(i).get("url");
        Log.d(TAG, "url=" + versionUrl);
        String versionName = (String) list.get(i).get("version");

        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(versionUrl));
        request.setAllowedOverRoaming(false);//漫游网络是否可以下载

        //设置文件类型，可以在下载结束后自动打开该文件
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(versionUrl));
        request.setMimeType(mimeString);

        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);

        //设置通知栏标题
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
//        request.setTitle("下载");
//        request.setDescription("花椒助手下载");
//        request.setAllowedOverRoaming(false);

        //sdcard的目录下的download文件夹，必须设置
        request.setDestinationInExternalPublicDir("/", versionName);
        //request.setDestinationInExternalFilesDir(),也可以自己制定下载路径

        //将下载请求加入下载队列
        downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
        mTaskId = downloadManager.enqueue(request);
        try {
            String path = Environment.getExternalStorageDirectory().getCanonicalPath();
            downloadPath = path+"/"+versionName;
        } catch (IOException e) {
            e.printStackTrace();
        }

        //注册广播接收者，监听下载状态
        this.registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

//        //创建下载任务,downloadUrl就是下载链接
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
//        Log.d(TAG, "url=" + downloadUrl);
//        //指定下载路径和下载文件名
//        request.setDestinationInExternalPublicDir("/download/", fileName);
//        //获取下载管理器
//        DownloadManager downloadManager= (DownloadManager) UpdateActivity.this.getSystemService(Context.DOWNLOAD_SERVICE);
//        //将下载任务加入下载队列，否则不会进行下载
//        downloadManager.enqueue(request);
    }
    //广播接受者，接收下载状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkDownloadStatus();//检查下载状态
        }
    };
    //检查下载状态
    private void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);//筛选下载任务，传入任务ID，可变参数
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    Log.d(TAG, ">>>下载暂停");
                case DownloadManager.STATUS_PENDING:
                    Log.d(TAG, ">>>下载延迟");
                case DownloadManager.STATUS_RUNNING:
                    Log.d(TAG, ">>>正在下载");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    Log.d(TAG, ">>>下载完成");
                    Toast.makeText(UpdateActivity.this, "下载完成，请打开根目录进行安装！", Toast.LENGTH_LONG).show();
                    //下载完成安装APK
                    //downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + versionName;
                    Log.d(TAG, "文件路径："+downloadPath);
                    //installAPK(new File(downloadPath));
                    break;
                case DownloadManager.STATUS_FAILED:
                    Log.d(TAG, ">>>下载失败");
                    break;
            }
        }
    }
    //下载到本地后执行安装
    protected void installAPK(File file) {
        if (!file.exists()) return;
        Log.d(TAG, file.exists()+"，文件存在，准备安装");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("content://" + file.toString());
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        //在服务中开启activity必须设置flag,后面解释
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);

//        Intent i=new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(this, "com.xxxman.autotest.shell", file));
//        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        startActivity(i);
    }
}
