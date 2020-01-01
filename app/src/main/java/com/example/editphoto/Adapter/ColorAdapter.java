package com.example.editphoto.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.editphoto.R;

import java.util.ArrayList;
import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    Context context;
    List<Integer> colorList;
    ColorAdapterListener listener;

    public ColorAdapter(Context context, ColorAdapterListener listener) {
        this.context = context;
        this.colorList = genColorList();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.color_item, parent, false);
        return new ColorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        holder.color_section.setCardBackgroundColor(colorList.get(position));
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder{

        CardView color_section;
        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            color_section = itemView.findViewById(R.id.color_section);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onColorSelected(colorList.get(getAdapterPosition()));
                }
            });
        }
    }

    private List<Integer> genColorList() {
        List <Integer> colorList = new ArrayList<>();
        colorList.add(Color.parseColor("#000000"));
        colorList.add(Color.parseColor("#5f9ea0"));
        colorList.add(Color.parseColor("#7ac5cd"));
        colorList.add(Color.parseColor("#5b6d5e"));
        colorList.add(Color.parseColor("#b7dbbc"));
        colorList.add(Color.parseColor("#fdaa60"));
        colorList.add(Color.parseColor("#cfd850"));
        colorList.add(Color.parseColor("#e84723"));
        colorList.add(Color.parseColor("#c7c3f5"));
        colorList.add(Color.parseColor("#2451c4"));
        colorList.add(Color.parseColor("#32c78f"));
        colorList.add(Color.parseColor("#6200e1"));
        colorList.add(Color.parseColor("#056664"));
        colorList.add(Color.parseColor("#ebe12a"));
        colorList.add(Color.parseColor("#a9fb0c"));


        return colorList;
    }

    public interface ColorAdapterListener{
        void onColorSelected(int color);
    }
}
