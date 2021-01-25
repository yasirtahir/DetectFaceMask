package com.yasir.detectfacemask.fragments.mindspore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;


import com.huawei.hms.mlsdk.custom.MLModelDataType;
import com.huawei.hms.mlsdk.custom.MLModelOutputs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MindSporeHelper {

    private static final int BITMAP_SIZE = 224;
    private static final float[] IMAGE_MEAN = new float[] {0.485f * 255f, 0.456f * 255f, 0.406f * 255f};
    private static final float[] IMAGE_STD = new float[] {0.229f * 255f, 0.224f * 255f, 0.225f * 255f};
    private final List<String> labelList;
    protected String modelName;
    protected String modelFullName;
    protected String modelLabelFile;
    protected int batchNum = 0;
    private static final int MAX_LENGTH = 10;

    public MindSporeHelper(Context activity) {
        modelName = "mindspore";
        modelFullName = "mindspore" + ".ms";
        modelLabelFile = "labels.txt";
        labelList = readLabels(activity, modelLabelFile);
    }

    public static MindSporeHelper create(Context activity) {
        return new MindSporeHelper(activity);
    }

    protected String getModelName() {
        return modelName;
    }

    protected String getModelFullName() {
        return modelFullName;
    }

    protected int getInputType() {
        return MLModelDataType.FLOAT32;
    }

    protected int getOutputType() {
        return MLModelDataType.FLOAT32;
    }

    protected Object getInput(Bitmap inputBitmap) {
        final float[][][][] input = new float[1][BITMAP_SIZE][BITMAP_SIZE][3];
        for (int h = 0; h < BITMAP_SIZE; h++) {
            for (int w = 0; w < BITMAP_SIZE; w++) {
                int pixel = inputBitmap.getPixel(w, h);
                input[batchNum][h][w][0] = ((Color.red(pixel) - IMAGE_MEAN[0])) / IMAGE_STD[0];
                input[batchNum][h][w][1] = ((Color.green(pixel) - IMAGE_MEAN[1])) / IMAGE_STD[1];
                input[batchNum][h][w][2] = ((Color.blue(pixel) - IMAGE_MEAN[2])) / IMAGE_STD[2];
            }
        }
        return input;
    }

    protected int[] getInputShape() {
        return new int[] {1, BITMAP_SIZE, BITMAP_SIZE, 3};
    }

    protected ArrayList<int[]> getOutputShapeList() {
        ArrayList<int[]> outputShapeList = new ArrayList<>();
        int[] outputShape = new int[] {1, labelList.size()};
        outputShapeList.add(outputShape);
        return outputShapeList;
    }

    protected HashMap<String, Float> resultPostProcess(MLModelOutputs output) {
        float[][] result = output.getOutput(0);
        float[] probabilities = result[0];

        Map<String, Float> localResult = new HashMap<>();
        ValueComparator compare = new ValueComparator(localResult);
        for (int i = 0; i < probabilities.length; i++) {
            localResult.put(labelList.get(i), probabilities[i]);
        }
        TreeMap<String, Float> treeSet = new TreeMap<>(compare);
        treeSet.putAll(localResult);

        int total = 0;
        HashMap<String, Float> finalResult = new HashMap<>();
        for (Map.Entry<String, Float> entry : treeSet.entrySet()) {
            if (total == MAX_LENGTH || entry.getValue() <= 0) {
                break;
            }

            finalResult.put(entry.getKey(), entry.getValue());

            total++;
        }

        return finalResult;
    }

    public static ArrayList<String> readLabels(Context context, String assetFileName) {
        ArrayList<String> result = new ArrayList<>();
        InputStream is = null;
        try {
            is = context.getAssets().open(assetFileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String readString;
            while ((readString = br.readLine()) != null) {
                result.add(readString);
            }
            br.close();
        } catch (IOException error) {
            Log.e(MindSporeHelper.class.getSimpleName(), "Asset file doesn't exist: " + error.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException error) {
                    Log.e(MindSporeHelper.class.getSimpleName(), "close failed: " + error.getMessage());
                }
            }
        }
        return result;
    }

    @SuppressWarnings({"ComparatorMethodParameterNotUsed", "ConstantConditions"})
    public static class ValueComparator implements Comparator<String> {
        Map<String, Float> base;

        ValueComparator(Map<String, Float> base) {
            this.base = base;
        }

        @Override
        public int compare(String o1, String o2) {
            if (base.get(o1) >= base.get(o2)) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
