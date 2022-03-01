package com.anand.upiqr.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anand.upiqr.R;
import com.anand.upiqr.databinding.ActivityMainBinding;
import com.anand.upiqr.databinding.FragmentHomeBinding;
import com.google.android.material.chip.Chip;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        binding.amountChip.setOnCheckedChangeListener((group, i) -> {
            Chip chip = binding.amountChip.findViewById(i);
            if (chip != null)
                if (chip.isChecked()) {
                    binding.amountText.setText(chip.getText());
                } else {
                    binding.amountText.setText("");
                }
        });

        binding.qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return view;
    }
}