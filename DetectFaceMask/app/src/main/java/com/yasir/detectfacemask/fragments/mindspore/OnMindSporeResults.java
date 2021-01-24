package com.yasir.detectfacemask.fragments.mindspore;


import com.yasir.detectfacemask.views.MarkingBoxModel;

import java.util.ArrayList;

public interface OnMindSporeResults {
    void onResult(ArrayList<MarkingBoxModel> arrayList);
}
