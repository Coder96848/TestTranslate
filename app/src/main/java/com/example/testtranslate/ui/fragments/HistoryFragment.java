package com.example.testtranslate.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testtranslate.App;
import com.example.testtranslate.R;
import com.example.testtranslate.db.HistoryResponse;
import com.example.testtranslate.interfaces.IFragment;
import com.example.testtranslate.util.HistoryRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HistoryFragment extends Fragment {

    private static final String TAG = HistoryFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private CompositeDisposable mDisposable = new CompositeDisposable();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Toolbar toolbar = view.findViewById(R.id.fragment_history_toolbar);
        toolbar.setTitle(getString(R.string.history));
        toolbar.setNavigationOnClickListener(v -> onBack());

        mRecyclerView = view.findViewById(R.id.fragment_history_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mDisposable.add(App.getHistoryDAO().getHistory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setAdapter,
                        throwable -> Log.e(TAG, "Unable to get products", throwable)));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
    }

    private void setAdapter(List<HistoryResponse> historyList){
        HistoryRecyclerAdapter adapter = new HistoryRecyclerAdapter( new ArrayList<>(historyList), this.getContext());
        mRecyclerView.setAdapter(adapter);
    }

    private void onBack(){
        if(getActivity() instanceof IFragment){
            ((IFragment) getActivity()).onFragmentBackStack();
        }
    }
}
