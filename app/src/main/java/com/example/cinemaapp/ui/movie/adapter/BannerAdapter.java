package com.example.cinemaapp.ui.movie.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final List<Integer> bannerImages;

    public BannerAdapter(List<Integer> bannerImages) {
        this.bannerImages = bannerImages;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new BannerViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        if (bannerImages.isEmpty()) {
            return;
        }
        int imageResId = bannerImages.get(position % bannerImages.size());
        Glide.with(holder.itemView.getContext())
                .load(imageResId)
                .into((ImageView) holder.itemView);
    }

    @Override
    public int getItemCount() {
        return bannerImages.isEmpty() ? 0 : Integer.MAX_VALUE;
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
