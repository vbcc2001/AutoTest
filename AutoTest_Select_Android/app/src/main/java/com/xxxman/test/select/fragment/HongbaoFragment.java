package com.xxxman.test.select.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xxxman.test.select.R;
import com.xxxman.test.select.util.BaseThread;
import com.xxxman.test.select.util.ShellUtil;
import com.xxxman.test.select.util.UiautomatorThread;

import java.io.IOException;
import java.util.List;

public class HongbaoFragment extends Fragment {

    private static final String TAG = HongbaoFragment.class.getName();

    public static HongbaoFragment newInstance() {
        HongbaoFragment fragment = new HongbaoFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_hongbao,container,false);
        return view;
    }
    @Override
    public void onViewCreated(View view,Bundle bundle){
        Button runBtn = (Button) view.findViewById(R.id.runBtn);
        //绑定运行按钮
        runBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiautomatorThread thread = new UiautomatorThread("SelectHB");
            }
        });
        Button runBtn1 = (Button) view.findViewById(R.id.runBtn1);
        //绑定运行按钮
        runBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiautomatorThread thread = new UiautomatorThread("ClickHB");
            }
        });
    }
}