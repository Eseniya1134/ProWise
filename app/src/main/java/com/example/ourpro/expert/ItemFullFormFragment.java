package com.example.ourpro.expert;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ourpro.PdfViewerFragment;
import com.example.ourpro.R;
import com.example.ourpro.bottomnav.profile.ProfileFragment;
import com.example.ourpro.databinding.FragmentItemFullFormExpertBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ItemFullFormFragment extends Fragment {

    private FragmentItemFullFormExpertBinding binding;
    private String userId;

    private String expertId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentItemFullFormExpertBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        if (getArguments() != null) {
            expertId = getArguments().getString("expertId", "");
        }

        load();
        displayPdfList();

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


    private void load() {
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference()
                .child("ExpertQuestionnaire")
                .child(userId)
                .child(expertId);

        ref.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                // Загружаем и устанавливаем имя
                String education = snapshot.child("education").getValue(String.class);
                if (education != null) {
                    binding.educationSet.setText(education);
                }
                String experience = snapshot.child("experience").getValue(String.class);
                if (education != null) {
                    binding.aboutExperience.setText(experience);
                }
                String services = snapshot.child("services").getValue(String.class);
                if (education != null) {
                    binding.aboutServices.setText(services);
                }
                String expert = snapshot.child("expert").getValue(String.class);
                if (education != null) {
                    binding.expertTxt.setText(expert);
                }
            }
        });
    }

    private void displayPdfList() {
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference()
                .child("ExpertQuestionnaire")
                .child(userId)
                .child(expertId)
                .child("fileUrl");

        ref.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String fileUrls = snapshot.getValue(String.class);
                if (fileUrls != null) {
                    String[] urlArray = fileUrls.split(",");
                    binding.pdfContainer.removeAllViews(); // очищаем перед отображением

                    for (String url : urlArray) {
                        TextView pdfLink = new TextView(requireContext());
                        pdfLink.setText(url);
                        pdfLink.setTextColor(Color.BLUE);
                        pdfLink.setTextSize(16);
                        pdfLink.setPadding(0, 10, 0, 0);
                        pdfLink.setPaintFlags(pdfLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        pdfLink.setOnClickListener(v -> openPdfInWebView(url));
                        binding.pdfContainer.addView(pdfLink);
                    }
                }
            }
        }).addOnFailureListener(e -> {
            Log.e("TAG", "Ошибка загрузки списка файлов: " + e.getMessage());
            Toast.makeText(requireContext(), "Ошибка загрузки списка файлов", Toast.LENGTH_SHORT).show();
        });
    }

    // Исправленный метод для открытия PDF
    private void openPdfInWebView(String pdfUrl) {
        PdfViewerFragment pdfViewerFragment = PdfViewerFragment.newInstance(pdfUrl);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.menu_fr, pdfViewerFragment) // Замените на ваш контейнер
                .addToBackStack(null)
                .commit();
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
