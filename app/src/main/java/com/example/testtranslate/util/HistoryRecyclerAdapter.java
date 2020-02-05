package com.example.testtranslate.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testtranslate.R;
import com.example.testtranslate.db.HistoryResponse;
import com.example.testtranslate.interfaces.IFragment;
import com.example.testtranslate.ui.fragments.MainFragment;

import java.util.ArrayList;

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.HistoryItemViewHolder> {

    private ArrayList<HistoryResponse> mHistoryList;
    private Context mContext;

    public HistoryRecyclerAdapter(ArrayList<HistoryResponse> historyList, Context context) {
        this.mHistoryList = historyList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public HistoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        return new HistoryRecyclerAdapter.HistoryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryItemViewHolder holder, int position) {
        holder.textView.setText(mHistoryList.get(position).getResponse());
        holder.itemView.setOnClickListener(v ->
                openDetailFragment(new MainFragment(mHistoryList.get(position).getResponse()), MainFragment.class.getSimpleName()));
    }

    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }

    private void openDetailFragment(Fragment fragment, String tag) {
        ((IFragment) mContext).setFragment(fragment, tag);
    }

    public class HistoryItemViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        public HistoryItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_text_view);
        }
    }
}
