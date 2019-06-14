package com.hr.techlabapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ItemAdapter {
    class ItemViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView textView;

        ItemViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.My_Items);
        }

    }
}