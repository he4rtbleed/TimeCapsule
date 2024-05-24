package com.jj.timecapsule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("com.jj.timecapsule.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);

//        String token = sharedPreferences.getString("TOKEN", null);
//        if (token == null) {
//            Log.d(TAG, "Token is null, redirecting to LoginActivity");
//            try {
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);
//                finish();
//            } catch (Exception e) {
//                Log.e(TAG, "Error starting LoginActivity", e);
//            }
//            return; // onCreate 메서드를 여기서 종료하여 이후 코드가 실행되지 않도록 함
//        }

        // 닉네임과 회원ID 설정
        TextView textViewUserInfo = findViewById(R.id.textViewUserInfo);
        textViewUserInfo.setText("닉네임(회원id)");

        // 로그아웃 버튼 설정
        Button buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("TOKEN");
            editor.apply();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // RecyclerView 설정
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTimeCapsules);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<TimeCapsule> timeCapsuleList = new ArrayList<>();
        timeCapsuleList.add(new TimeCapsule("타임캡슐 1", "23/05/22"));
        timeCapsuleList.add(new TimeCapsule("타임캡슐 2", "24/05/22"));
        timeCapsuleList.add(new TimeCapsule("타임캡슐 3", "25/05/22"));
        timeCapsuleList.add(new TimeCapsule("타임캡슐 4", "26/05/22"));
        timeCapsuleList.add(new TimeCapsule("타임캡슐 4", "26/05/22"));
        timeCapsuleList.add(new TimeCapsule("타임캡슐 4", "26/05/22"));
        timeCapsuleList.add(new TimeCapsule("타임캡슐 4", "26/05/22"));
        timeCapsuleList.add(new TimeCapsule("타임캡슐 4", "26/05/22"));
        timeCapsuleList.add(new TimeCapsule("타임캡슐 4", "26/05/22"));
        timeCapsuleList.add(new TimeCapsule("타임캡슐 4", "26/05/22"));
        timeCapsuleList.add(new TimeCapsule("타임캡슐 4", "26/05/22"));
        timeCapsuleList.add(new TimeCapsule("타임캡슐 4", "26/05/22"));
        timeCapsuleList.add(new TimeCapsule("타임캡슐 4", "26/05/22"));
        timeCapsuleList.add(new TimeCapsule("타임캡슐 4", "26/05/22"));
        timeCapsuleList.add(new TimeCapsule("타임캡슐 4", "26/05/22"));
        timeCapsuleList.add(new TimeCapsule("타임캡슐 4", "26/05/22"));
        TimeCapsuleAdapter adapter = new TimeCapsuleAdapter(timeCapsuleList);
        recyclerView.setAdapter(adapter);

        // "추억 보관하기" 버튼 설정
        Button buttonAddMemory = findViewById(R.id.buttonAddMemory);
        buttonAddMemory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CapsuleWriterActivity.class);
            startActivity(intent);
        });

        // "추억 불러오기" 버튼 설정
        Button buttonRefreshMemory = findViewById(R.id.buttonRefreshMemory);
        buttonRefreshMemory.setOnClickListener(v -> {
            // TODO: 추억 불러오기 기능 구현
        });
    }
}
