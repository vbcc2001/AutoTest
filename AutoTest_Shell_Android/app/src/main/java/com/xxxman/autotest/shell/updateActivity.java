package com.xxxman.autotest.shell;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class updateActivity extends AppCompatActivity {

    private ListView listView;
    private static final String TAG = updateActivity.class.getName();
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private SimpleAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.list_view);
        adapter = new SimpleAdapter(updateActivity.this,getData(),R.layout.list_item,
                new String[]{"version","remark","url"},
                new int[]{R.id.title,R.id.info,R.id.img});
        listView.setAdapter(adapter);
        UpdateThread thread = new UpdateThread();
        thread.start();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int count, long arg3) {
                    adapter.notifyDataSetChanged();
                    Toast.makeText(updateActivity.this, "正在刷新", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private List<Map<String, Object>> getData() {
        return list;
    }
    class UpdateThread extends Thread {

        @Override
        public void run() {
            super.run();
            MyConnection my  = new MyConnection();
            String url = Constant.URL;
            String context = "{\"function\":\"F100012\",\"user\":{\"id\":\"1\",\"session\":\"123\"},\"content\":{}}";
            Map<String, String> parms = new HashMap<>();
            parms.put("jsonContent", context);
            Log.d(TAG, "http请求" + context);
            String rs = my.getContextByHttp(url, parms);
            Log.d(TAG, "http请求结果" + rs);
            Gson gson = new GsonBuilder().serializeNulls().create();
            HttpResult requestParameter = (HttpResult) gson.fromJson(rs, new TypeToken<HttpResult<Map<String,String>>>() {}.getType());
            Log.d(TAG, "error" + requestParameter.getError().getNum()+":"+requestParameter.getError().getMsg());
            Log.d(TAG, "list" + "("+requestParameter.getList().size()+"):"+requestParameter.getList());
            if(requestParameter.getError().getNum()=="" && requestParameter.getList().size()>0){
                //list = requestParameter.getList();
                //adapter.notifyDataSetChanged();
            }
        }
    }
}
