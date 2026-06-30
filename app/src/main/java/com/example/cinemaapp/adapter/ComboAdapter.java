package com.example.cinemaapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinemaapp.R;
import com.example.cinemaapp.data.model.Combo;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ComboAdapter extends RecyclerView.Adapter<ComboAdapter.ComboViewHolder> {

    private List<Combo> combos;
    private Map<Integer, Integer> quantities = new HashMap<>(); // comboId -> quantity
    private OnComboChangeListener listener;
    private final NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    public interface OnComboChangeListener {
        void onComboChanged(long totalComboPrice, String comboDesc, Map<Integer, Integer> selectedCombos);
    }

    public ComboAdapter(List<Combo> combos, OnComboChangeListener listener) {
        this.combos = combos;
        this.listener = listener;
        if (combos != null) {
            for (Combo c : combos) {
                if (c.id != null) quantities.put(c.id, 0);
            }
        }
    }

    public void setCombos(List<Combo> combos) {
        this.combos = combos;
        this.quantities.clear();
        if (combos != null) {
            for (Combo c : combos) {
                if (c.id != null) quantities.put(c.id, 0);
            }
        }
        notifyDataSetChanged();
        notifyListener();
    }

    @NonNull
    @Override
    public ComboViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_combo, parent, false);
        return new ComboViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComboViewHolder holder, int position) {
        Combo combo = combos.get(position);
        if (combo.id == null) return;
        
        holder.tvName.setText(combo.name != null ? combo.name : "");
        holder.tvDesc.setText(combo.description != null ? combo.description : "");
        holder.tvPrice.setText(fmt.format(combo.price) + "đ");

        int qty = quantities.containsKey(combo.id) ? quantities.get(combo.id) : 0;
        holder.tvQty.setText(String.valueOf(qty));

        holder.btnPlus.setOnClickListener(v -> {
            int currentQty = quantities.containsKey(combo.id) ? quantities.get(combo.id) : 0;
            quantities.put(combo.id, currentQty + 1);
            notifyItemChanged(position);
            notifyListener();
        });

        holder.btnMinus.setOnClickListener(v -> {
            int currentQty = quantities.containsKey(combo.id) ? quantities.get(combo.id) : 0;
            if (currentQty > 0) {
                quantities.put(combo.id, currentQty - 1);
                notifyItemChanged(position);
                notifyListener();
            }
        });
    }

    @Override
    public int getItemCount() {
        return combos != null ? combos.size() : 0;
    }

    private void notifyListener() {
        if (listener == null || combos == null) return;
        long total = 0;
        StringBuilder desc = new StringBuilder();
        Map<Integer, Integer> selected = new HashMap<>();

        for (Combo c : combos) {
            if (c.id != null && quantities.containsKey(c.id)) {
                int qty = quantities.get(c.id);
                if (qty > 0) {
                    total += qty * c.price;
                    desc.append(c.name).append(" x").append(qty).append(" ");
                    selected.put(c.id, qty);
                }
            }
        }
        listener.onComboChanged(total, desc.toString().trim(), selected);
    }

    static class ComboViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvPrice, tvQty;
        Button btnMinus, btnPlus;

        public ComboViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvComboName);
            tvDesc = itemView.findViewById(R.id.tvComboDesc);
            tvPrice = itemView.findViewById(R.id.tvComboPrice);
            tvQty = itemView.findViewById(R.id.tvQty);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }
    }
}
