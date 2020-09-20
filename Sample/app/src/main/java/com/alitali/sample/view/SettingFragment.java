package com.alitali.sample.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.alitali.sample.R;
import com.alitali.sample.databinding.FragmentSettingBinding;

import org.jetbrains.annotations.NotNull;

public class SettingFragment extends Fragment {
    FragmentSettingBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        return binding.getRoot();

    }
}
