package com.superfamous.superphotopicker.views.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mrjodev.photopicker.R;
import com.mrjodev.photopicker.adapters.RcvAdapterForWorker;
import com.mrjodev.photopicker.model.WorkerModel;

import java.util.ArrayList;
import java.util.List;

public class RecyclerTestActivity extends AppCompatActivity {

    RecyclerView rvWorker;
    RcvAdapterForWorker adapterWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_test);

        init();
        loadList();
    }

    private void init(){
        initViews();
        adapterWorker = new RcvAdapterForWorker(this);
        rvWorker.setAdapter(adapterWorker);

        //레이아웃매니저 설정
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvWorker.setLayoutManager(linearLayoutManager);
    }


    //뷰<>레이아웃 매칭
    private void initViews(){
        rvWorker = (RecyclerView)findViewById(R.id.activityRecyclerTest_rv);
    }

    //서버에서 목록불러오고 list로 결과받기
    private void loadList(){

        //서버에서 결과 받고 model로 파싱된 결과 받았다고 가정
        List<WorkerModel> listWorkers = new ArrayList<WorkerModel>();
        for(int i = 0; i < 40; i++){
            WorkerModel m = new WorkerModel();
            m.address = "주소입니다 " + i;
            m.name = "이름입니다 " + i;
            listWorkers.add(m);
        }

        //adapter랑 연결
        adapterWorker.setList(listWorkers);
        //adapterWorker.notifyDataSetChanged(); //첨엔 필요없음

    }



}
