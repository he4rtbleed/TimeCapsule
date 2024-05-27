package com.jj.timecapsule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

public class LoginActivity extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private EditText editTextLoginUserId;
    private EditText editTextLoginPassword;
    private Button buttonLogin;
    private Button buttonSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_page);

        editTextLoginUserId = findViewById(R.id.editTextLoginUserId);
        editTextLoginPassword = findViewById(R.id.editTextLoginPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignup = findViewById(R.id.buttonSignup);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextLoginUserId.getText().toString();
                String password = editTextLoginPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "회원ID 혹은 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    new LoginUser().execute(email, password);
                }
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private class LoginUser extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String userId = params[0];
            String password = params[1];

            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("email", userId);
            postDataParams.put("password", password);

            return performPostCall("http://202.31.230.66/login.php", postDataParams);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                if (status.equals("success")) {
                    // 로그인 성공 시 메인 액티비티 이동
                    SharedPreferences sharedPreferences = getSharedPreferences("com.jj.timecapsule.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("TOKEN", editTextLoginUserId.getText().toString());
                    editor.apply();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
            }
        }

        private String performPostCall(String requestURL, HashMap<String, String> postDataParams) {
            URL url;
            String response = "";
            try {
                url = new URL(requestURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                Log.d("HTTPResponseCode", String.valueOf(responseCode)); // HTTP 응답 코드 로그 출력

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }
        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }
    }
}
