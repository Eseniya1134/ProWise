package com.example.ourpro.bottomnav.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ourpro.R;
import com.example.ourpro.databinding.FragmentItemFullFormExpertBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ItemFullFormFragment extends Fragment {

    private FragmentItemFullFormExpertBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentItemFullFormExpertBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        binding.profileButton.setOnClickListener(v -> {
            navigateToProfile();

        });

        return view;
    }

    private void navigateToProfile() {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.menu_fr, new ProfileFragment());
        ft.commit();
    }




    /* @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Получаем ссылку на нужный путь в Firebase Realtime Database
        FirebaseDatabase.getInstance().getReference("yourNode/pdfUrls")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String pdfUrls = snapshot.getValue(String.class);
                        if (pdfUrls != null && !pdfUrls.isEmpty()) {
                            String[] urls = pdfUrls.split(",");

                            for (String url : urls) {
                                openPdfUrl(url.trim());
                            }
                        } else {
                            Toast.makeText(requireContext(), "Нет PDF-ссылок", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openPdfUrl(String pdfUrl) {
        WebView webView = new WebView(requireContext());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + pdfUrl);

        binding.pdfContainer.addView(webView);
    }
*/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
