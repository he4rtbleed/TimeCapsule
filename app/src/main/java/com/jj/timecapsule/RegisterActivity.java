package com.jj.timecapsule;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextName;
    private EditText editTextAge;
    private EditText editTextGender;
    private EditText editTextNickname;
    private EditText editTextPhone;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

        editTextEmail = findViewById(R.id.editTextUserId);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextAge = findViewById(R.id.editTextAge);
        editTextGender = findViewById(R.id.editTextGender);
        editTextNickname = findViewById(R.id.editTextNickname);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();
        String name = editTextName.getText().toString();
        String age = editTextAge.getText().toString();
        String gender = editTextGender.getText().toString();
        String nickname = editTextNickname.getText().toString();
        String phone = editTextPhone.getText().toString();

        if (!password.equals(confirmPassword)) {
            // 비밀번호와 비밀번호 확인이 같지 않을 경우
<<<<<<< Updated upstream
            return;
=======
            Toast.makeText(getApplicationContext(), "비밀번호가 일치 하지 않습니다.", Toast.LENGTH_LONG).show();
        }
        else if (userId.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
        name.isEmpty() || age.isEmpty() || gender.isEmpty() || nickname.isEmpty() || phone.isEmpty()) {
            Toast.makeText(getApplicationContext(), "모든 항목을 채워주세요.", Toast.LENGTH_LONG).show();
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
                URL url = new URL("http://10.0.2.2/register.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);

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

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
                return sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
>>>>>>> Stashed changes
        }

    }
}
