package com.example.ourpro.bottomnav.finance;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.transition.AutoTransition;
import android.transition.TransitionManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ourpro.R;
import com.example.ourpro.databinding.FragmentFinanceBinding;

import java.util.ArrayList;
import java.util.List;

public class FinanceFragment extends Fragment {

    private FragmentFinanceBinding binding;
    private boolean isExpanded = false;
    private HistoryAdapter adapter;
    private List<HistoryItem> fullList;
    private List<HistoryItem> shortList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFinanceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fullList = getHistoryItems();
        shortList = fullList.subList(0, Math.min(2, fullList.size()));

        adapter = new HistoryAdapter(shortList);
        binding.historyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.historyRecyclerView.setAdapter(adapter);

        binding.toggleTextView.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            TransitionManager.beginDelayedTransition(binding.rootLayout, new AutoTransition());

            if (isExpanded) {
                adapter.updateList(fullList);
                binding.toggleTextView.setText("Скрыть");
                Log.d("FinanceFragment", "Расширяем список. Полный размер: " + fullList.size());
            } else {
                adapter.updateList(shortList);
                binding.toggleTextView.setText("Посмотреть полностью");
                Log.d("FinanceFragment", "Скрываем список. Короткий размер: " + shortList.size());
            }
        });

    }



    private List<HistoryItem> getHistoryItems() {
        List<HistoryItem> list = new ArrayList<>();
        list.add(new HistoryItem("Консультация 29.03", "+1450,00 руб."));
        list.add(new HistoryItem("Консультация 23.03", "+2450,00 руб."));
        list.add(new HistoryItem("Вывод средств 19.03", "-450,00 руб."));
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
