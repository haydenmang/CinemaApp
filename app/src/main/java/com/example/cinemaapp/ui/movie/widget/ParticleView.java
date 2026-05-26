package com.example.cinemaapp.ui.movie.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleView extends View {

    private final List<Particle> particles = new ArrayList<>();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();
    private static final int PARTICLE_COUNT = 18;

    public ParticleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        particles.clear();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            particles.add(new Particle(w, h, random));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Particle p : particles) {
            paint.setColor(p.color);
            paint.setAlpha((int) (p.alpha * 255));
            canvas.drawCircle(p.x, p.y, p.radius, paint);
            p.update(getWidth(), getHeight());
        }
        postInvalidateDelayed(16); // ~60fps
    }

    private static class Particle {
        float x, y, radius, speed, alpha;
        int color;
        float alphaDir = -0.005f;

        Particle(int w, int h, Random random) {
            reset(w, h, random, true);
        }

        void reset(int w, int h, Random random, boolean randomY) {
            x = random.nextFloat() * w;
            y = randomY ? random.nextFloat() * h : h + 10;
            radius = 1f + random.nextFloat() * 2.5f;
            speed = 0.3f + random.nextFloat() * 0.7f;
            alpha = 0.04f + random.nextFloat() * 0.18f;
            // Màu gold/trắng ngẫu nhiên
            int[] colors = {0xFFC8922A, 0xFFF0C040, 0xFFFFFFFF, 0x88C8922A, 0x88F0C040};
            color = colors[random.nextInt(colors.length)];
        }

        void update(int w, int h) {
            y -= speed;
            alpha += alphaDir;
            if (alpha <= 0.05f || alpha >= 0.8f) alphaDir = -alphaDir;
            if (y < -10) {
                x = new Random().nextFloat() * w;
                y = h + 10;
            }
        }
    }
}
