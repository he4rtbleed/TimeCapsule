package com.jj.timecapsule;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class CapsuleWriterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capsule_writer);

        // 드로어 레이아웃 설정
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        LinearLayout drawerContent = findViewById(R.id.drawer_content);
        drawerContent.setOnClickListener(v -> drawerLayout.closeDrawers());
    }
}
