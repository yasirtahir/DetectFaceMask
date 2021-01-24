package com.yasir.detectfacemask.views;

import android.graphics.Rect;

public class MarkingBoxModel {

    private Rect rect;
    private String label;
    private boolean isMask;
    private boolean isSound;

    public MarkingBoxModel(Rect rect, String label, boolean isMask, boolean isSound) {
        this.rect = rect;
        this.label = label;
        this.isMask = isMask;
        this.isSound = isSound;
    }

    public Rect getRect() {
        return rect;
    }

    public String getLabel() {
        return label;
    }

    public boolean isMask() {
        return isMask;
    }

    public boolean isSound() {
        return isSound;
    }
}
