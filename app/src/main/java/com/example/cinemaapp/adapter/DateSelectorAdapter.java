package com.example.cinemaapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinemaapp.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DateSelectorAdapter extends RecyclerView.Adapter<DateSelectorAdapter.ViewHolder> {

    public interface OnDateClickListener {
        void onDateClick(Calendar date, int position);
    }

    private final List<Calendar> dates;
    private int selectedPosition = 0;
    private final OnDateClickListener listener;

    public void setSelectedPosition(int position) {
        if (position >= 0 && position < dates.size()) {
            int prev = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(prev);
            notifyItemChanged(selectedPosition);
        }
    }

    private static final SimpleDateFormat DAY_NAME_FMT = new SimpleDateFormat("EEE", new Locale("vi"));
    private static final SimpleDateFormat DAY_NUM_FMT  = new SimpleDateFormat("dd", Locale.getDefault());

    public DateSelectorAdapter(List<Calendar> dates, OnDateClickListener listener) {
        this.dates = dates;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_date_selector, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Calendar cal = dates.get(position);
        boolean isSelected = position == selectedPosition;

        String dayName = position == 0 ? "Hôm nay" : DAY_NAME_FMT.format(cal.getTime());
        holder.tvDayName.setText(dayName.toUpperCase());
        holder.tvDayNum.setText(DAY_NUM_FMT.format(cal.getTime()));

        if (isSelected) {
            holder.tvDayName.setTextColor(0xFF00E5FF);
            holder.tvDayNum.setTextColor(0xFFF8F9FA);
            holder.tvDayNum.setBackgroundResource(R.drawable.bg_date_selected);
        } else {
            holder.tvDayName.setTextColor(0xFF8B949E);
            holder.tvDayNum.setTextColor(0xFFF8F9FA);
            holder.tvDayNum.setBackgroundResource(0);
        }

        holder.itemView.setOnClickListener(v -> {
            int prev = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prev);
            notifyItemChanged(selectedPosition);
            listener.onDateClick(cal, selectedPosition);
        });
    }

    @Override
    public int getItemCount() { return dates.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName, tvDayNum;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            tvDayNum  = itemView.findViewById(R.id.tvDayNum);
        }
    }
}
