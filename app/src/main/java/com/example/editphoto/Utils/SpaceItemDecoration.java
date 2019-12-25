package com.example.editphoto.Utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if(parent.getChildAdapterPosition(view) == state.getItemCount())
        {
            outRect.left = space;
            outRect.right = 0;
        }
        else
        {
            outRect.right = space;
            outRect.left = 0;
        }
    }
}
