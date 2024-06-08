package com.jj.timecapsule;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimeCapsuleAdapter extends RecyclerView.Adapter<TimeCapsuleAdapter.TimeCapsuleViewHolder> {

    private List<TimeCapsule> timeCapsuleList;
    private Context context;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public TimeCapsuleAdapter(Context context, List<TimeCapsule> timeCapsuleList) {
        this.context = context;
        this.timeCapsuleList = timeCapsuleList;
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @NonNull
    @Override
    public TimeCapsuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_capsule_item, parent, false);
        return new TimeCapsuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeCapsuleViewHolder holder, int position) {
        TimeCapsule timeCapsule = timeCapsuleList.get(position);
        holder.textViewTitle.setText(timeCapsule.getTitle());
        holder.textViewDate.setText(timeCapsule.getTargetDate());

        boolean isLocked = isDateLocked(timeCapsule.getTargetDate());
        if (isLocked) {
            holder.itemView.setEnabled(false); // 잠긴 상태로 비활성화
            holder.imageViewLock.setVisibility(View.VISIBLE); // 자물쇠 아이콘 보이기
        } else {
            holder.itemView.setEnabled(true); // 열 수 있는 상태로 활성화
            holder.imageViewLock.setVisibility(View.GONE); // 자물쇠 아이콘 숨기기

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 위치 권한 확인
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 현재 위치 가져오기
                    fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location currentLocation = task.getResult();
                            if (currentLocation != null) {
                                float[] results = new float[1];
                                Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                                        timeCapsule.getLatitude(), timeCapsule.getLongitude(), results);
                                float distanceInMeters = results[0];

                                if (distanceInMeters <= 20) {
                                    // 거리가 20미터 이내이면 CapsuleDetailActivity로 이동하면서 데이터 전달
                                    Intent intent = new Intent(v.getContext(), CapsuleDetailActivity.class);
                                    intent.putExtra("timeCapsule", timeCapsule);
                                    v.getContext().startActivity(intent);
                                } else {
                                    // 거리가 멀면 토스트 메시지 출력
                                    Toast.makeText(context, "캡슐을 열 수 있는 위치가 아닙니다. 남은 거리: " + Math.round(distanceInMeters) + "m", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return timeCapsuleList.size();
    }

    // 날짜가 지났는지 확인하는 메소드
    private boolean isDateLocked(String openDate) {
        try {
            Date date = dateFormat.parse(openDate);
            return date != null && date.after(new Date());
        } catch (ParseException e) {
            e.printStackTrace();
            return true; // 오류 발생 시 잠금 상태로 처리
        }
    }

    static class TimeCapsuleViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDate;
        ImageView imageViewLock;

        public TimeCapsuleViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            imageViewLock = itemView.findViewById(R.id.imageViewLock);
        }
    }
}
