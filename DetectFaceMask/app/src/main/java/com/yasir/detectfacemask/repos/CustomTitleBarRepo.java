package com.yasir.detectfacemask.repos;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yasir.detectfacemask.R;


public class CustomTitleBarRepo extends RelativeLayout {

    private TextView txtTitle;
    private ImageButton btnLeft, btnRight;

    public CustomTitleBarRepo(Context context) {
        super(context);
        initLayout(context);
    }

    public CustomTitleBarRepo(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public CustomTitleBarRepo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context);
    }

    private void bindViews() {
        txtTitle = this.findViewById(R.id.txtHeading);
        btnRight = this.findViewById(R.id.btnRight);
        btnLeft = this.findViewById(R.id.btnLeft);
    }

    private void initLayout(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.layout_title_bar, this);
        }
        bindViews();
    }

    public void hideRightButton() {
        btnRight.setVisibility(View.GONE);
    }

    public void hideLeftButton() {
        btnLeft.setVisibility(View.GONE);
    }

    public void showRightButton(int drawable, OnClickListener listener) {
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setImageResource(drawable);
        btnRight.setOnClickListener(listener);
    }

    public void showLeftButton(int drawable, OnClickListener listener) {
        btnLeft.setVisibility(View.VISIBLE);
        btnLeft.setImageResource(drawable);
        btnLeft.setOnClickListener(listener);
    }

    public void setHeading(String heading) {
        txtTitle.setVisibility(View.VISIBLE);
        txtTitle.setText(heading);
    }

    public void hideHeading() {
        txtTitle.setVisibility(View.GONE);
        txtTitle.setText("");
    }

    public void hideButtons() {
        hideHeading();
        hideRightButton();
        hideLeftButton();
    }
}
