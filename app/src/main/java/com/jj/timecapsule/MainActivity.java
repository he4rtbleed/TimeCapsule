package com.jj.timecapsule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String TAG = "MainActivity";
    private List<TimeCapsule> timeCapsuleList;
    private TimeCapsuleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("com.jj.timecapsule.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);

        String token = sharedPreferences.getString("TOKEN", null);
        if (token == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        TextView textViewUserInfo = findViewById(R.id.textViewUserInfo);
        textViewUserInfo.setText("환영합니다 " + token + "님");

        Button buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("TOKEN");
            editor.apply();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerViewTimeCapsules);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        timeCapsuleList = new ArrayList<>();
        adapter = new TimeCapsuleAdapter(this, timeCapsuleList); // context 추가
        recyclerView.setAdapter(adapter);

        Button buttonAddMemory = findViewById(R.id.buttonAddMemory);
        buttonAddMemory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CapsuleWriterActivity.class);
            startActivity(intent);
        });

        Button buttonRefreshMemory = findViewById(R.id.buttonRefreshMemory);
        buttonRefreshMemory.setOnClickListener(v -> {
            loadTimeCapsules();
        });

        loadTimeCapsules();
    }

    private void loadTimeCapsules() {
        String url = "http://sm-janela.p-e.kr/get_timecapsules.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, "서버 응답: " + response.toString());
                    if (response.getString("status").equals("success")) {
                        JSONArray timecapsules = response.getJSONArray("timeCapsules");
                        timeCapsuleList.clear();
                        for (int i = 0; i < timecapsules.length(); i++) {
                            JSONObject timecapsule = timecapsules.getJSONObject(i);
                            String id = timecapsule.getString("id");
                            String title = timecapsule.getString("title");
                            String textContent = timecapsule.getString("text_content");
                            String imageContent = timecapsule.optString("image_content", null); // 이미지가 없을 때 처리
                            String createdAt = timecapsule.getString("created_at");
                            String targetDate = timecapsule.getString("target_date");
                            double latitude = timecapsule.getDouble("latitude");
                            double longitude = timecapsule.getDouble("longitude");
                            String userEmail = timecapsule.getString("user_email");

                            timeCapsuleList.add(new TimeCapsule(id, title, textContent, imageContent, createdAt, targetDate, latitude, longitude, userEmail));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        String message = response.getString("message");
                        Log.e(TAG, "서버 응답 오류: " + message);
                        Toast.makeText(MainActivity.this, "타임캡슐을 불러오는 데 실패했습니다: " + message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON 파싱 오류: " + e.getMessage());
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "응답을 처리하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "서버 통신 오류: " + error.getMessage());
                error.printStackTrace();
                Toast.makeText(MainActivity.this, "서버와 통신하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}
