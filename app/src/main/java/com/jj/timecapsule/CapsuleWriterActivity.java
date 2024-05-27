package com.jj.timecapsule;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

public class CapsuleWriterActivity extends AppCompatActivity implements OnMapReadyCallback {
    // 구글맵 관련 변수
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;

    // 위치권한 관련 변수
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // 위치 정보 저장 변수
    private Location lastKnownLocation;

    // 사용자가 선택한 위치를 저장할 변수
    private LatLng selectedLocation;

    // UI 변수
    BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

    // 위치 권한 요청 메서드
    private void getLocationPermission() {
        // 위치 권한 요청. 권한 요청 결과는 onRequestPermissionsResult 콜백으로 처리됨
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // 요청이 취소된 경우 결과 배열은 비어 있음
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }

    // 맵 준비된 경우 이벤트 핸들러
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        // 위치권한 먼저 획득
        getLocationPermission();

        // "현재 위치" 버튼 활성화
        updateLocationUI();

        // 장치의 현재 위치를 파악하고 지도의 위치를 설정
        getDeviceLocation();

        // 지도 클릭하여 위치 저장
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
            // 위치 권한 관련 예외 처리
        }
    }

    // 장치의 현재 위치 가져오기
    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // 요청이 성공하고 위치 정보가 있는 경우
                            lastKnownLocation = task.getResult();
                            // 지도의 카메라를 현재 위치로 이동
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            // 위치 정보가 없는 경우 기본 위치로 이동
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            // 위치 권한 관련 예외 처리
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
    }
}
