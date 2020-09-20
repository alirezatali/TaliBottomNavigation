package com.alitali.sample.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.alitali.sample.R;
import com.alitali.sample.databinding.FragmentDetailsBinding;

import org.jetbrains.annotations.NotNull;

public class DetailsFragment extends Fragment {
    FragmentDetailsBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            String titleSt = DetailsFragmentArgs.fromBundle(args).getTitle();
            binding.setTitle(titleSt);
        }
    }
}
