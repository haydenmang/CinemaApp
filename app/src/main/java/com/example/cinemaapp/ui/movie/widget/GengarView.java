package com.example.cinemaapp.ui.movie.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class GengarView extends View {

    private final Paint pFill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pStroke = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float currentOffsetX = 0f;
    private float targetOffsetX = 0f;
    private boolean isBlinking = false;

    public GengarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        pStroke.setStyle(Paint.Style.STROKE);
        pStroke.setAntiAlias(true);
    }

    public void setTypingPhone(int length) {
        isBlinking = false;
        targetOffsetX = (length % 2 == 0) ? -1f : 1f;
        invalidate();
    }

    public void setTypingPassword(boolean typing) {
        isBlinking = typing;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth(), h = getHeight();
        float cx = w / 2f, cy = h / 2f;
        float s = Math.min(w, h) * 0.46f;

        currentOffsetX += (targetOffsetX - currentOffsetX) * 0.12f;

        // === BODY ===
        RadialGradient grad = new RadialGradient(cx, cy - s * 0.05f, s,
            new int[]{Color.parseColor("#1C1C2E"), Color.parseColor("#050508")},
            new float[]{0.3f, 1f}, Shader.TileMode.CLAMP);
        pFill.setShader(grad);
        pFill.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, cy, s, pFill);
        pFill.setShader(null);

        // === MẮT ===
        float lx = cx - s * 0.28f, ly = cy - s * 0.22f;
        float rx = cx + s * 0.28f, ry = cy - s * 0.22f;
        float ew = s * 0.30f, eh = s * 0.22f;

        drawEye(canvas, lx, ly, ew, eh, true, currentOffsetX);
        drawEye(canvas, rx, ry, ew, eh, false, currentOffsetX);

        // === MIỆNG ===
        drawMouth(canvas, cx, cy + s * 0.10f, s);

        if (Math.abs(currentOffsetX - targetOffsetX) > 0.01f) {
            postInvalidateDelayed(16);
        }
    }

    private void drawEye(Canvas canvas, float cx, float cy, float ew, float eh,
                         boolean isLeft, float offsetX) {
        if (isBlinking) {
            // Nhắm - đường cong
            pStroke.setColor(Color.parseColor("#E8192C"));
            pStroke.setStrokeWidth(ew * 0.18f);
            pStroke.setStrokeCap(Paint.Cap.ROUND);
            Path p = new Path();
            p.moveTo(cx - ew * 0.9f, cy + eh * 0.05f);
            p.quadTo(cx, cy + eh * 0.55f, cx + ew * 0.9f, cy + eh * 0.05f);
            canvas.drawPath(p, pStroke);
            return;
        }

        // Tam giác nằm ngang nghiêng kiểu Gengar
        Path eye = new Path();
        if (isLeft) {
            // Mắt trái: nghiêng từ dưới trái lên trên phải
            eye.moveTo(cx - ew, cy + eh * 0.4f);      // góc dưới trái
            eye.lineTo(cx + ew * 0.6f, cy - eh * 0.8f); // đỉnh trên phải
            eye.lineTo(cx + ew, cy + eh * 0.4f);       // góc dưới phải
            eye.quadTo(cx + ew * 0.2f, cy + eh * 0.7f, cx - ew, cy + eh * 0.4f);
        } else {
            // Mắt phải: nghiêng từ dưới phải lên trên trái (đối xứng)
            eye.moveTo(cx + ew, cy + eh * 0.4f);       // góc dưới phải
            eye.lineTo(cx - ew * 0.6f, cy - eh * 0.8f); // đỉnh trên trái
            eye.lineTo(cx - ew, cy + eh * 0.4f);       // góc dưới trái
            eye.quadTo(cx - ew * 0.2f, cy + eh * 0.7f, cx + ew, cy + eh * 0.4f);
        }
        eye.close();

        // Fill đỏ
        pFill.setStyle(Paint.Style.FILL);
        pFill.setColor(Color.parseColor("#E8192C"));
        canvas.drawPath(eye, pFill);

        // Outline đen
        pStroke.setColor(Color.parseColor("#080810"));
        pStroke.setStrokeWidth(ew * 0.12f);
        pStroke.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawPath(eye, pStroke);

        // Pupil tím
        float px = cx + offsetX * ew * 0.30f;
        float py = cy + eh * 0.08f;
        float pr = ew * 0.22f;
        pFill.setColor(Color.parseColor("#7C3AED"));
        canvas.drawCircle(px, py, pr, pFill);

        // Highlight trắng
        pFill.setColor(Color.WHITE);
        canvas.drawCircle(px - pr * 0.38f, py - pr * 0.38f, pr * 0.40f, pFill);
    }

    private void drawMouth(Canvas canvas, float cx, float cy, float s) {
        float mw = s * 0.72f;
        float mh = s * 0.32f;

        // Outline đen dày
        Path outline = new Path();
        outline.moveTo(cx - mw - s*0.04f, cy);
        outline.quadTo(cx, cy + mh + s*0.05f, cx + mw + s*0.04f, cy);
        outline.lineTo(cx + mw + s*0.04f, cy + mh*0.52f);
        outline.quadTo(cx, cy + mh*1.72f, cx - mw - s*0.04f, cy + mh*0.52f);
        outline.close();
        pFill.setColor(Color.parseColor("#080810"));
        pFill.setStyle(Paint.Style.FILL);
        canvas.drawPath(outline, pFill);

        // Fill trắng
        Path mouth = new Path();
        mouth.moveTo(cx - mw, cy + s*0.01f);
        mouth.quadTo(cx, cy + mh, cx + mw, cy + s*0.01f);
        mouth.lineTo(cx + mw, cy + mh*0.48f);
        mouth.quadTo(cx, cy + mh*1.62f, cx - mw, cy + mh*0.48f);
        mouth.close();
        pFill.setColor(Color.WHITE);
        canvas.drawPath(mouth, pFill);

        // Răng tím
        pStroke.setColor(Color.parseColor("#9966CC"));
        pStroke.setStrokeWidth(s * 0.020f);
        pStroke.setStrokeCap(Paint.Cap.BUTT);
        int n = 6;
        for (int i = 1; i <= n; i++) {
            float t = (float) i / (n + 1);
            float tx = cx - mw + mw * 2 * t;
            float topY = cy + mh * (float)(1 - Math.pow(2*t - 1, 2)) * 0.08f + s*0.01f;
            canvas.drawLine(tx, topY, tx, cy + mh * 0.95f, pStroke);
        }
    }
}
