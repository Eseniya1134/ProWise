package com.example.ourpro;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PdfViewerFragment extends Fragment {
    private WebView webView;
    private ProgressBar progressBar;

    public static PdfViewerFragment newInstance(String pdfUrl) {
        PdfViewerFragment fragment = new PdfViewerFragment();
        Bundle args = new Bundle();
        args.putString("pdfUrl", pdfUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_viewer, container, false);
        webView = view.findViewById(R.id.pdfWebView);
        progressBar = view.findViewById(R.id.progressBar);

        String pdfUrl = getArguments() != null ? getArguments().getString("pdfUrl") : null;
        if (pdfUrl != null && !pdfUrl.isEmpty()) {
            loadPdf(pdfUrl);
        } else {
            Toast.makeText(requireContext(), "Ошибка: PDF URL пустой", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadPdf(String pdfUrl) {
        progressBar.setVisibility(View.VISIBLE);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Ошибка загрузки PDF", Toast.LENGTH_SHORT).show();
            }
        });

        // Прямая загрузка PDF без Google Viewer
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + pdfUrl);
    }

}
