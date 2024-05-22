package com.jj.timecapsule;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class StarryBackgroundView extends View {
    private Paint paint;
    private Paint blurPaint;
    private Random random;
    private int width;
    private int height;
    private LinearGradient gradient;

    public StarryBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        blurPaint = new Paint();
        blurPaint.setMaskFilter(new BlurMaskFilter(18, BlurMaskFilter.Blur.SOLID));
        random = new Random();
        setLayerType(LAYER_TYPE_SOFTWARE, null); // Blur 효과를 위해 하드웨어 가속을 비활성화
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        gradient = new LinearGradient(0, 0, width, height, 0xFF1a1a2e, 0xFF16213e, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 배경 그라데이션
        paint.setShader(gradient);
        canvas.drawRect(0, 0, width, height, paint);
        paint.setShader(null); //그라데이션 다 그렸으니 shader 지움

        // 별 그리기
        paint.setColor(0xFFFFFFFF);
        blurPaint.setColor(0xFFFFFFFF);
        for (int i = 0; i < 100; i++) {
            float x = random.nextFloat() * width;
            float y = random.nextFloat() * height;
            canvas.drawCircle(x, y, 2, paint);
            canvas.drawCircle(x, y, 2, blurPaint);
        }

        // 유성우 그리기
        paint.setColor(0xFFFFFFFF);
        blurPaint.setColor(0xFFFFFFFF);
        for (int i = 0; i < 10; i++) {
            float startX = random.nextFloat() * width;
            float startY = random.nextFloat() * height;
            float stopX = startX + random.nextFloat() * 50;
            float stopY = startY + random.nextFloat() * 50;
            canvas.drawLine(startX, startY, stopX, stopY, paint);
            canvas.drawLine(startX, startY, stopX, stopY, blurPaint);
        }
    }
}
