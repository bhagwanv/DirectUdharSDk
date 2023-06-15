package com.sk.directudhar.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sk.directudhar.R;
import com.sk.directudhar.databinding.ActvityDirectUdharBinding;

public class DirectUdharActivity extends AppCompatActivity {
    private ActvityDirectUdharBinding mBinding;
    private DirectUdharActivity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.actvity_direct_udhar);
        activity = this;
        initialization();
    }

    private void initialization() {
    }
}
