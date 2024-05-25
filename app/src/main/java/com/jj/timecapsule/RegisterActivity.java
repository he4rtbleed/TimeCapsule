package com.jj.timecapsule;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

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
        String email = editTextUserId.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();
        String name = editTextName.getText().toString();
        String age = editTextAge.getText().toString();
        String gender = editTextGender.getSelectedItem().toString();
        String nickname = editTextNickname.getText().toString();
        String phone = editTextPhone.getText().toString();

        if (!password.equals(confirmPassword)) {
            // 비밀번호와 비밀번호 확인이 같지 않을 경우
            return;
        }

    }
}
