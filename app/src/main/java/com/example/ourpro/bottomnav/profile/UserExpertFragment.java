package com.example.ourpro.bottomnav.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ourpro.databinding.FragmentUserExpertBinding;
import com.example.ourpro.expert.Expert;
import com.example.ourpro.expert.ExpertAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserExpertFragment extends Fragment {

    private FragmentUserExpertBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserExpertBinding.inflate(inflater, container, false);
        loadExperts();
        return binding.getRoot();
    }

    private void loadExperts() {
        ArrayList<Expert> experts = new ArrayList<>();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference rootRef = db.getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Получаем строку чатов текущего пользователя
                String expertsStr = snapshot.child("Users").child(uid).child("idURLExpert").getValue(String.class);
                if (expertsStr == null || expertsStr.isEmpty()) return;

                String[] expertIds = expertsStr.split(",");
                for (String expertId : expertIds) {
                    DataSnapshot chatSnapshot = snapshot.child("ExpertQuestionnaire").child(uid).child(expertId);
                    if (!chatSnapshot.exists()) continue;

                    String expert = chatSnapshot.child("expert").getValue(String.class);
                    String status = chatSnapshot.child("status").getValue(String.class);
                    if (expert == null || status == null) continue;

                    Expert expert1 = new Expert(expert, status);
                    expert1.setExpertId(expertId);
                    experts.add(expert1);

                }

                if (experts.isEmpty()) {
                    binding.emptyExpertsTv.setVisibility(View.VISIBLE);
                    binding.consultationWndw.setVisibility(View.GONE);
                } else {
                    binding.emptyExpertsTv.setVisibility(View.GONE);
                    binding.consultationWndw.setVisibility(View.VISIBLE);
                    binding.consultationWndw.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.consultationWndw.setAdapter(new ExpertAdapter(experts));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Ошибка загрузки экспертов", Toast.LENGTH_SHORT).show();
                Log.e("ExpertsFragment", "Database error: " + error.getMessage());
            }
        });



    }
}




