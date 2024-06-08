package com.jj.timecapsule;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

// 패키지 및 import 문은 생략

public class CapsuleWriterActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_READ_STORAGE = 3;
    private static final int PERMISSIONS_REQUEST_MANAGE_STORAGE = 4;

    // 구글맵 관련 변수
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;

    // 위치 정보 저장 변수
    private Location lastKnownLocation;
    private LatLng selectedLocation;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private boolean locationPermissionGranted;

    // 사진 이름 저장 변수
    private String selectedImageName;
    private String selectedImagePath;

    // UI 변수
    BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private EditText editTextTitle;
    private EditText editTextContent;
    private EditText editTextOtherUserEmails; // 다른 사용자 이메일 입력 필드 추가
    private Button buttonPickImage;
    private Button buttonPickDate;
    private Button buttonSaveCapsule;
    private String selectedDate;
    private String token;

    // 위치 권한 요청 메서드
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    // 저장소 권한 요청 메서드
    private void getStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, PERMISSIONS_REQUEST_MANAGE_STORAGE);
            } else {
                openImagePicker();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_READ_STORAGE);
            } else {
                openImagePicker();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                updateLocationUI();
            }
        } else if (requestCode == PERMISSIONS_REQUEST_READ_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "파일 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSIONS_REQUEST_MANAGE_STORAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    openImagePicker();
                } else {
                    Toast.makeText(this, "파일 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            selectedImagePath = getRealPathFromURI(selectedImageUri);
            if (selectedImagePath != null) {
                File file = new File(selectedImagePath);
                selectedImageName = file.getName();
                Toast.makeText(this, "선택된 이미지: " + selectedImageName, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 맵 준비된 경우 이벤트핸들러
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        // 위치권한 먼저 획득
        getLocationPermission();

        // "현재 위치" 버튼 활성화
        updateLocationUI();

        // 디바이스의 현재 위치 가져오기
        getDeviceLocation();
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                selectedLocation = latLng; // 사용자가 선택한 위치를 저장
                map.clear(); // 기존 마커 제거
                map.addMarker(new MarkerOptions().position(latLng).title("저장 위치")); // 새로운 마커 추가
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng)); // 카메라를 클릭한 위치로 이동

                // 저장 되었는지 확인
                if (selectedLocation != null) {
                    Toast.makeText(CapsuleWriterActivity.this, "선택한 위치: " + selectedLocation.latitude + ", " + selectedLocation.longitude, Toast.LENGTH_SHORT).show();
                    Log.d("CapsuleWriterActivity", "선택한 위치: " + selectedLocation.latitude + ", " + selectedLocation.longitude);
                }
            }
        });
    }

    // 위치 UI 업데이트 메서드
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setZoomGesturesEnabled(true);
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    // 디바이스 위치 가져오기
    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // 현재 위치로 카메라 이동
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_capsule_writer);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // BottomSheetBehavior 설정
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        editTextOtherUserEmails = findViewById(R.id.editTextOtherUserEmails); // 다른 사용자 이메일 입력 필드 초기화
        buttonPickImage = findViewById(R.id.buttonPickImage);
        buttonPickDate = findViewById(R.id.buttonPickDate);
        buttonSaveCapsule = findViewById(R.id.buttonSaveCapsule);

        // SharedPreferences에서 저장된 이메일 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("com.jj.timecapsule.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", null);

        if (token == null) {
            // 이메일 정보가 없으면 로그인 화면으로 이동
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        buttonPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CapsuleWriterActivity", "Checking storage permission");
                getStoragePermission();
            }
        });

        buttonPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        buttonSaveCapsule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCapsule();
            }
        });
    }

    // 갤러리 열기
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    // URI에서 실제 경로를 가져오는 메서드
    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            try {
                cursor.moveToFirst();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                String path = cursor.getString(column_index);
                return path;
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    // 날짜 선택기 열기
    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                        Toast.makeText(CapsuleWriterActivity.this, "선택된 날짜: " + selectedDate, Toast.LENGTH_SHORT).show();
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveCapsule() {
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();
        String otherUserEmails = editTextOtherUserEmails.getText().toString().trim(); // 다른 사용자 이메일 가져오기

        Log.d("CapsuleWriterActivity", "Title: " + title);
        Log.d("CapsuleWriterActivity", "Content: " + content);
        Log.d("CapsuleWriterActivity", "Selected location: " + selectedLocation);
        Log.d("CapsuleWriterActivity", "Selected date: " + selectedDate);
        Log.d("CapsuleWriterActivity", "Selected image path: " + selectedImagePath);
        Log.d("CapsuleWriterActivity", "user_email: " + token);
        Log.d("CapsuleWriterActivity", "other_user_emails: " + otherUserEmails); // 로그에 다른 사용자 이메일 추가

        if (title.isEmpty() || content.isEmpty() || selectedLocation == null || selectedDate == null || selectedImagePath == null) {
            Toast.makeText(this, "모든 필드를 작성해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 서버에 타임캡슐 데이터 전송
        uploadCapsule(title, content, selectedImagePath, selectedLocation.latitude, selectedLocation.longitude, selectedDate, token);

        // 다른 사용자들에게 타임캡슐 복사 전송
        if (!otherUserEmails.isEmpty()) {
            String[] otherEmails = otherUserEmails.split(",");
            for (String email : otherEmails) {
                email = email.trim();
                if (!email.isEmpty()) {
                    uploadCapsule(title, content, selectedImagePath, selectedLocation.latitude, selectedLocation.longitude, selectedDate, email);
                }
            }
        }
    }

    private void uploadCapsule(String title, String content, String imagePath, double latitude, double longitude, String date, String userEmail) {
        String serverUrl = "http://sm-janela.p-e.kr/insert_timecapsule.php";

        // 보낼 데이터를 저장할 Map 생성
        Map<String, String> stringParams = new HashMap<>();
        stringParams.put("title", title);
        stringParams.put("text_content", content);
        stringParams.put("latitude", String.valueOf(latitude));
        stringParams.put("longitude", String.valueOf(longitude));
        stringParams.put("target_date", date);
        stringParams.put("user_email", userEmail);

        if (imagePath != null) {
            try {
                File file = new File(imagePath);
                Log.d("uploadCapsule", "파일 경로: " + imagePath);
                Log.d("uploadCapsule", "파일 크기: " + file.length());

                FileInputStream fis = new FileInputStream(file);
                byte[] bytes = new byte[(int) file.length()];
                int bytesRead = fis.read(bytes);
                fis.close();

                Log.d("uploadCapsule", "읽은 바이트 수: " + bytesRead);

                if (bytesRead == -1) {
                    throw new IOException("파일을 읽는 동안 오류가 발생했습니다.");
                }

                String encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT);
                stringParams.put("image_content", encodedImage);
                Log.d("uploadCapsule", "이미지 인코딩 성공");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("uploadCapsule", "이미지 인코딩 중 오류: " + e.getMessage());
                Toast.makeText(this, "이미지 인코딩 중 오류가 발생했습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // 파일 전송 요청 객체 생성[결과를 String으로 받음]
        SimpleMultiPartRequest smpr = new SimpleMultiPartRequest(Request.Method.POST, serverUrl, stringParams, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("CapsuleWriterActivity", "Server response: " + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.getString("status");
                    if (status.equals("success")) {
                        Toast.makeText(CapsuleWriterActivity.this, "타임캡슐이 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        finish(); // 액티비티 종료
                    } else {
                        Toast.makeText(CapsuleWriterActivity.this, "타임캡슐 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CapsuleWriterActivity.this, "응답 파싱 중 오류 발생", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("CapsuleWriterActivity", "Error: " + error.getMessage());
                Toast.makeText(CapsuleWriterActivity.this, "타임캡슐 저장에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
        // 서버에 데이터 보내고 응답 요청
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(smpr);
    }
}
