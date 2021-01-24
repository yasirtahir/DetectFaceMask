package com.yasir.detectfacemask.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


import com.yasir.detectfacemask.R;
import com.yasir.detectfacemask.repos.MediaPlayerRepo;

import java.util.ArrayList;

public class CameraOverlayView extends View {

    private ArrayList<MarkingBoxModel> boundingMarkingBoxModels = new ArrayList<>();
    private Paint paint = new Paint();
    private Context mContext;

    public CameraOverlayView(Context context) {
        super(context);
        this.mContext = context;
    }

    public CameraOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public CameraOverlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeMiter(100f);

        for (MarkingBoxModel markingBoxModel : boundingMarkingBoxModels) {
            if (markingBoxModel.isMask()) {
                paint.setColor(Color.GREEN);
            } else {
                paint.setColor(Color.RED);
                if (markingBoxModel.isSound()) {
                    MediaPlayerRepo.playSound(mContext, R.raw.wearmask);
                }
            }
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(35);
            canvas.drawText(markingBoxModel.getLabel(), markingBoxModel.getRect().left, markingBoxModel.getRect().top - 9F, paint);
            canvas.drawRoundRect(new RectF(markingBoxModel.getRect()), 2F, 2F, paint);
        }
    }

    public void setBoundingMarkingBoxModels(ArrayList<MarkingBoxModel> boundingMarkingBoxModels) {
        this.boundingMarkingBoxModels = boundingMarkingBoxModels;
    }
}
