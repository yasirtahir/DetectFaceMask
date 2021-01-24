package com.yasir.detectfacemask.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.yasir.detectfacemask.R;
import com.yasir.detectfacemask.fragments.FaceMaskDetectFragment;
import com.yasir.detectfacemask.repos.AlertsRepo;
import com.yasir.detectfacemask.repos.CustomTitleBarRepo;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mainHeader)
    CustomTitleBarRepo mainHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Dependency Injection
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            initFragment();
        }
    }

    private void initFragment() {
        addFragment(FaceMaskDetectFragment.newInstance(), FaceMaskDetectFragment.class.getSimpleName());
    }

    public void addFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrameLayout, fragment, tag);
        fragmentTransaction.addToBackStack(getSupportFragmentManager().getBackStackEntryCount() == 0 ? "FirstFragment" : null).commit();
    }

    public void setHeading(String title) {
        mainHeader.hideButtons();
        mainHeader.setHeading(title);

        if (!title.equalsIgnoreCase("Face Mask Detection")) {
            mainHeader.showLeftButton(R.drawable.ic_back, v -> onBackPressed());
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else {
            AlertsRepo.createQuitDialog(this, (dialog, which) -> finish(),
                    getResources().getString(R.string.quit_msg), getResources().getString(R.string.quit_title)).show();
        }
    }
}