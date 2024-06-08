package com.jj.timecapsule;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUserId;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextName;
    private EditText editTextAge;
    private Spinner editTextGender;
    private EditText editTextNickname;
    private EditText editTextPhone;
    private Button buttonRegister;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

        editTextUserId = findViewById(R.id.editTextUserId);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextAge = findViewById(R.id.editTextAge);
        editTextGender = findViewById(R.id.spinnerGender);
        editTextNickname = findViewById(R.id.editTextNickname);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonRegister = findViewById(R.id.buttonRegister);

        // 회원 ID 입력 제한 설정 (영소문자 + 숫자, 최대 10자)
        InputFilter[] userIdFilters = new InputFilter[2];
        userIdFilters[0] = new InputFilter.LengthFilter(10); // 최대 10자
        userIdFilters[1] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    char character = source.charAt(i);
                    if (!Character.isLowerCase(character) && !Character.isDigit(character)) {
                        return "";
                    }
                }
                return null;
            }
        };
        editTextUserId.setFilters(userIdFilters);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String userId = editTextUserId.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();
        String name = editTextName.getText().toString();
        String age = editTextAge.getText().toString();
        String gender = editTextGender.getSelectedItem().toString();
        String nickname = editTextNickname.getText().toString();
        String phone = editTextPhone.getText().toString();

        if (!password.equals(confirmPassword)) {
            // 비밀번호와 비밀번호 확인이 같지 않을 경우
            Toast.makeText(getApplicationContext(), "비밀번호가 일치 하지 않습니다.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "비밀번호가 일치하지 않습니다.");
        }
        else if (userId.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                name.isEmpty() || age.isEmpty() || gender.isEmpty() || nickname.isEmpty() || phone.isEmpty()) {
            Toast.makeText(getApplicationContext(), "모든 항목을 채워주세요.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "모든 항목을 채워주세요.");
        }
        else {
            new RegisterUser().execute(userId, password, name, age, gender, nickname, phone);
        }
    }

    private class RegisterUser extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String password = params[1];
            String name = params[2];
            String age = params[3];
            String gender = params[4];
            String nickname = params[5];
            String phone = params[6];

            try {
                URL url = new URL("http://sm-janela.p-e.kr/Register.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                Log.d(TAG, "연결 성공: " + url.toString());

                HashMap<String, String> postDataParams = new HashMap<>();
                postDataParams.put("email", email);
                postDataParams.put("password", password);
                postDataParams.put("name", name);
                postDataParams.put("age", age);
                postDataParams.put("gender", gender);
                postDataParams.put("nickname", nickname);
                postDataParams.put("phone", phone);

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();

                Log.d(TAG, "데이터 전송: " + postDataParams.toString());

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
                return sb.toString();

            } catch (Exception e) {
                Log.e(TAG, "오류 발생", e);
                e.printStackTrace();
                return null;
            }
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

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "서버 응답: " + result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");

                if (status.equals("success")) {
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                Log.e(TAG, "JSON 파싱 오류", e);
                e.printStackTrace();
                Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
