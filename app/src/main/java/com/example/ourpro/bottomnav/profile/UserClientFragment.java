package com.example.ourpro.bottomnav.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ourpro.databinding.FragmentUserClientBinding;
import com.example.ourpro.requests.ClientRequest;
import com.example.ourpro.requests.RequestsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserClientFragment extends Fragment {
    private FragmentUserClientBinding binding;
    private RequestsAdapter adapter;
    private ValueEventListener requestsListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserClientBinding.inflate(inflater, container, false);
        setupRecyclerView();
        loadUserRequests();
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new RequestsAdapter(new ArrayList<>(), this::showRequestDetailsDialog);
        binding.requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.requestsRecyclerView.setAdapter(adapter);
    }

    private void loadUserRequests() {
        ArrayList<ClientRequest> requests = new ArrayList<>();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference rootRef = db.getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot userRequests = snapshot.child("ClientRequests").child(uid);
                if (!userRequests.exists()) {
                    binding.emptyState.setVisibility(View.VISIBLE);
                    binding.emptyState.setText("У вас нет заявок");
                    return;
                }

                for (DataSnapshot requestSnapshot : userRequests.getChildren()) {
                    ClientRequest request = requestSnapshot.getValue(ClientRequest.class);
                    if (request != null) {
                        request.setId(requestSnapshot.getKey());
                        requests.add(request);
                    }
                }

                if (requests.isEmpty()) {
                    binding.emptyState.setVisibility(View.VISIBLE);
                } else {
                    binding.emptyState.setVisibility(View.GONE);
                    binding.requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.requestsRecyclerView.setAdapter(new RequestsAdapter(requests, UserClientFragment.this::showRequestDetailsDialog));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Ошибка загрузки заявок", Toast.LENGTH_SHORT).show();
                Log.e("UserClientFragment", "Database error: " + error.getMessage());
            }
        });
    }

    private void showRequestDetailsDialog(ClientRequest request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Заявка от " + formatDate(request.getTimestamp()));

        String message = "Сфера: " + request.getDomain() + "\n" +
                "Описание: " + request.getShortDescription() + "\n" +
                "Дедлайн: " + request.getDeadline() + "\n" +
                "Подробности: " + request.getFullDescription();

        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Важно удалять слушатель при уничтожении фрагмента
        if (requestsListener != null) {
            FirebaseDatabase.getInstance()
                    .getReference("ClientRequests")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .removeEventListener(requestsListener);
        }
        binding = null;
    }
}