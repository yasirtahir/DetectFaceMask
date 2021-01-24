package com.yasir.detectfacemask.fragments.mindspore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.custom.MLCustomLocalModel;
import com.huawei.hms.mlsdk.custom.MLModelExecutor;
import com.huawei.hms.mlsdk.custom.MLModelExecutorSettings;
import com.huawei.hms.mlsdk.custom.MLModelInputOutputSettings;
import com.huawei.hms.mlsdk.custom.MLModelInputs;
import com.yasir.detectfacemask.views.MarkingBoxModel;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MindSporeProcessor {

    private final WeakReference<Context> weakContext;
    private MLModelExecutor modelExecutor;
    private MindSporeHelper mindSporeHelper;
    private final OnMindSporeResults mindSporeResultsListener;
    private String mModelName;
    private String mModelFullName; // .om, .mslite, .ms
    private boolean isSound;

    public MindSporeProcessor(Context context, OnMindSporeResults mindSporeResultsListener, boolean isSound) {
        this.mindSporeResultsListener = mindSporeResultsListener;
        this.isSound = isSound;
        weakContext = new WeakReference<>(context);

        initEnvironment();
    }

    private void initEnvironment() {
        mindSporeHelper = MindSporeHelper.create(weakContext.get());
        mModelName = mindSporeHelper.getModelName();
        mModelFullName = mindSporeHelper.getModelFullName();
    }

    public void processFaceImages(Bitmap bitmap, Rect rect, boolean isSound) {
        this.isSound = isSound;

        if (dumpBitmapInfo(bitmap)) {
            return;
        }

        MLCustomLocalModel localModel =
                new MLCustomLocalModel.Factory(mModelName).setAssetPathFile(mModelFullName).create();
        MLModelExecutorSettings settings = new MLModelExecutorSettings.Factory(localModel).create();

        try {
            modelExecutor = MLModelExecutor.getInstance(settings);
            executorImpl(bitmap, rect);
        } catch (MLException error) {
            error.printStackTrace();
        }
    }

    private boolean dumpBitmapInfo(Bitmap bitmap) {
        if (bitmap == null) {
            return true;
        }
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        Log.e(MindSporeProcessor.class.getSimpleName(), "bitmap width is " + width + " height " + height);
        return false;
    }

    private void executorImpl(Bitmap inputBitmap, Rect rect) {
        Object input = mindSporeHelper.getInput(inputBitmap);
        Log.e(MindSporeProcessor.class.getSimpleName(), "interpret pre process");

        MLModelInputs inputs = null;

        try {
            inputs = new MLModelInputs.Factory().add(input).create();
        } catch (MLException e) {
            Log.e(MindSporeProcessor.class.getSimpleName(), "add inputs failed! " + e.getMessage());
        }

        MLModelInputOutputSettings inOutSettings = null;
        try {
            MLModelInputOutputSettings.Factory settingsFactory = new MLModelInputOutputSettings.Factory();
            settingsFactory.setInputFormat(0, mindSporeHelper.getInputType(), mindSporeHelper.getInputShape());
            ArrayList<int[]> outputSettingsList = mindSporeHelper.getOutputShapeList();
            for (int i = 0; i < outputSettingsList.size(); i++) {
                settingsFactory.setOutputFormat(i, mindSporeHelper.getOutputType(), outputSettingsList.get(i));
            }
            inOutSettings = settingsFactory.create();
        } catch (MLException e) {
            Log.e(MindSporeProcessor.class.getSimpleName(), "set input output format failed! " + e.getMessage());
        }

        Log.e(MindSporeProcessor.class.getSimpleName(), "interpret start");
        execModel(inputs, inOutSettings, rect);
    }

    private void execModel(MLModelInputs inputs, MLModelInputOutputSettings outputSettings, Rect rect) {
        modelExecutor.exec(inputs, outputSettings).addOnSuccessListener(mlModelOutputs -> {
            Log.e(MindSporeProcessor.class.getSimpleName(), "interpret get result");
            HashMap<String, Float> labels = mindSporeHelper.resultPostProcess(mlModelOutputs);

            if(labels == null){
                labels = new HashMap<>();
            }

            ArrayList<MarkingBoxModel> markingBoxModelList = new ArrayList<>();

            String result = "";

            if(labels.get("WithMask") != null && labels.get("WithoutMask") != null){
                Float with = labels.get("WithMask");
                Float without = labels.get("WithoutMask");

                if (with != null && without != null) {

                    with = with * 100;
                    without = without * 100;

                    float maxValue = Math.max(with, without);

                    if (maxValue == with && with > 90) {
                        result = "Wearing Mask: " + String.format(new Locale("en"), "%.1f", with) + "%";
                    } else {
                        result = "Not wearing Mask: " + String.format(new Locale("en"), "%.1f", without) + "%";
                    }
                    if (!result.trim().isEmpty()) {
                        // Add this to our Overlay List as Box with Result and Percentage
                        markingBoxModelList.add(new MarkingBoxModel(rect, result, maxValue == with && with > 90, isSound));
                    }
                }
            }

            if (mindSporeResultsListener != null && markingBoxModelList.size() > 0) {
                mindSporeResultsListener.onResult(markingBoxModelList);
            }
            Log.e(MindSporeProcessor.class.getSimpleName(), "result: " + result);
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            Log.e(MindSporeProcessor.class.getSimpleName(), "interpret failed, because " + e.getMessage());
        }).addOnCompleteListener(task -> {
            try {
                modelExecutor.close();
            } catch (IOException error) {
                error.printStackTrace();
            }
        });
    }
}
