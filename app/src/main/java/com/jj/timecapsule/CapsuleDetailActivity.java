package com.jj.timecapsule;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CapsuleDetailActivity extends AppCompatActivity {

    private static final String TAG = "CapsuleDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capsule_detail);

        TimeCapsule timeCapsule = (TimeCapsule) getIntent().getSerializableExtra("timeCapsule");

        TextView textViewTitle = findViewById(R.id.textViewTitle);
        TextView textViewContent = findViewById(R.id.textViewContent);
        TextView textViewDate = findViewById(R.id.textViewDate);
        ImageView imageView = findViewById(R.id.imageView);
        Button buttonBack = findViewById(R.id.buttonBack);

        textViewTitle.setText(timeCapsule.getTitle());
        textViewContent.setText(timeCapsule.getTextContent());
        textViewDate.setText(timeCapsule.getCreatedAt());

        if (timeCapsule.getImageContent() != null && !timeCapsule.getImageContent().isEmpty()) {
            byte[] imageBytes = Base64.decode(timeCapsule.getImageContent(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageView.setImageBitmap(bitmap);
        } else {
            Log.e(TAG, "Image content is null or empty.");
        }

        // 돌아가기 버튼 클릭 리스너 설정
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 메인 액티비티로 이동
                Intent intent = new Intent(CapsuleDetailActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
