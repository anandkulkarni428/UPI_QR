package com.anand.upiqr.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.anand.upiqr.R;
import com.anand.upiqr.Utils.HttpHelpers;
import com.anand.upiqr.databinding.FragmentAboutMeBinding;


public class AboutMeFragment extends Fragment {
    FragmentAboutMeBinding binding;
    private String url;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAboutMeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        url = HttpHelpers.BASE_URL;
        binding.webView.setWebViewClient(new AboutMe());
        binding.webView.getSettings().setLoadsImagesAutomatically(true);
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        binding.webView.loadUrl(url);
        return view;
    }

    public class AboutMe extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(url);
            return true;
        }
    }
}