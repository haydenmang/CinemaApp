package com.example.cinemaapp.ui.movie.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class BlobView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float offset = 0f;
    private float offset2 = 0f;

    public BlobView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null); // cần cho BlurMaskFilter
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();

        offset += 0.008f;
        offset2 += 0.005f;

        // Blob 1 - góc trên trái, màu gold mờ
        float x1 = (float)(w * 0.15 + Math.sin(offset) * w * 0.08);
        float y1 = (float)(h * 0.15 + Math.cos(offset * 0.7) * h * 0.06);
        float r1 = w * 0.45f;
        paint.setShader(new RadialGradient(x1, y1, r1,
            new int[]{0x22C8922A, 0x11F0C040, 0x00000000},
            new float[]{0f, 0.5f, 1f},
            Shader.TileMode.CLAMP));
        canvas.drawCircle(x1, y1, r1, paint);

        // Blob 2 - góc dưới phải, màu tím mờ
        float x2 = (float)(w * 0.85 + Math.cos(offset2) * w * 0.07);
        float y2 = (float)(h * 0.80 + Math.sin(offset2 * 0.8) * h * 0.05);
        float r2 = w * 0.5f;
        paint.setShader(new RadialGradient(x2, y2, r2,
            new int[]{0x1A7B2FBE, 0x0FC8922A, 0x00000000},
            new float[]{0f, 0.5f, 1f},
            Shader.TileMode.CLAMP));
        canvas.drawCircle(x2, y2, r2, paint);

        // Blob 3 - giữa màn hình, ánh sáng xuyên kính
        float x3 = w * 0.5f;
        float y3 = (float)(h * 0.45 + Math.sin(offset * 0.5) * h * 0.03);
        float r3 = w * 0.35f;
        paint.setShader(new RadialGradient(x3, y3, r3,
            new int[]{0x0FFFFFFF, 0x00000000},
            new float[]{0f, 1f},
            Shader.TileMode.CLAMP));
        canvas.drawCircle(x3, y3, r3, paint);

        postInvalidateDelayed(32); // ~30fps đủ cho blob
    }
}
