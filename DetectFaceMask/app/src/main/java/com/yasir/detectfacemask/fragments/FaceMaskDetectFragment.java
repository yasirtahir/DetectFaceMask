package com.yasir.detectfacemask.fragments;

import android.animation.Animator;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.frame.Frame;
import com.yasir.detectfacemask.R;
import com.yasir.detectfacemask.fragments.mindspore.MindSporeProcessor;
import com.yasir.detectfacemask.repos.MediaPlayerRepo;
import com.yasir.detectfacemask.views.CameraOverlayView;

import java.io.ByteArrayOutputStream;

import br.vince.owlbottomsheet.OnClickInterceptor;
import br.vince.owlbottomsheet.OwlBottomSheet;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FaceMaskDetectFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.cameraView)
    CameraView cameraView;

    @BindView(R.id.overlayView)
    CameraOverlayView cameraOverlayView;

    @BindView(R.id.btnSwitchCamera)
    FloatingActionButton btnSwitchCamera;

    @BindView(R.id.btnToggleSound)
    FloatingActionButton btnToggleSound;

    @BindView(R.id.helpBottomSheet)
    OwlBottomSheet helpBottomSheet;

    private View rootView;
    private MLFaceAnalyzer mAnalyzer;
    private MindSporeProcessor mMindSporeProcessor;
    private boolean isSound = false;

    public static FaceMaskDetectFragment newInstance() {
        return new FaceMaskDetectFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMainActivity().setHeading("Face Mask Detection");

        initObjects();
    }

    private void setupHelpBottomSheet() {
        helpBottomSheet.setActivityView(getMainActivity());
        helpBottomSheet.setIcon(R.drawable.ic_help);
        helpBottomSheet.setBottomSheetColor(ContextCompat.getColor(getMainActivity(), R.color.colorAccent));
        helpBottomSheet.attachContentView(R.layout.layout_help_sheet);
        helpBottomSheet.setOnClickInterceptor(new OnClickInterceptor() {
            @Override
            public void onExpandBottomSheet() {
                LottieAnimationView lottieAnimationView = helpBottomSheet.getContentView()
                        .findViewById(R.id.maskDemo);
                lottieAnimationView.playAnimation();
            }

            @Override
            public void onCollapseBottomSheet() {

            }
        });
        helpBottomSheet.getContentView().findViewById(R.id.btnCancel)
                .setOnClickListener(v -> helpBottomSheet.collapse());
        LottieAnimationView lottieAnimationView = helpBottomSheet.getContentView()
                .findViewById(R.id.maskDemo);
        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                helpBottomSheet.collapse();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_face_detect, container, false);
        } else {
            container.removeView(rootView);
        }

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSwitchCamera:
                cameraView.toggleFacing();
                break;
            case R.id.btnToggleSound:
                isSound = !isSound;
                toggleSound();
                break;
        }
    }

    private void initObjects() {

        btnSwitchCamera.setOnClickListener(this);
        btnToggleSound.setOnClickListener(this);

        setupHelpBottomSheet();

        btnToggleSound.setBackgroundTintList(ColorStateList.valueOf(getMainActivity().getResources().getColor(R.color.colorGrey)));
        btnSwitchCamera.setBackgroundTintList(ColorStateList.valueOf(getMainActivity().getResources().getColor(R.color.colorAccent)));

        cameraView.setLifecycleOwner(this); // This refers to Camera Lifecycle based on different states

        if (mAnalyzer == null) {
            // Use custom parameter settings, and enable the speed preference mode and face tracking function to obtain a faster speed.
            MLFaceAnalyzerSetting setting = new MLFaceAnalyzerSetting.Factory()
                    .setPerformanceType(MLFaceAnalyzerSetting.TYPE_SPEED)
                    .setTracingAllowed(false)
                    .create();
            mAnalyzer = MLAnalyzerFactory.getInstance().getFaceAnalyzer(setting);
        }

        if (mMindSporeProcessor == null) {
            mMindSporeProcessor = new MindSporeProcessor(getMainActivity(), arrayList -> {
                cameraOverlayView.setBoundingMarkingBoxModels(arrayList);
                cameraOverlayView.invalidate();
            }, isSound);
        }

        cameraView.addFrameProcessor(this::processCameraFrame);
    }

    private void processCameraFrame(Frame frame) {
        Matrix matrix = new Matrix();
        matrix.setRotate(frame.getRotationToUser());
        matrix.preScale(1, -1);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuvImage = new YuvImage(
                frame.getData(),
                ImageFormat.NV21,
                frame.getSize().getWidth(),
                frame.getSize().getHeight(),
                null
        );
        yuvImage.compressToJpeg(new
                        Rect(0, 0, frame.getSize().getWidth(), frame.getSize().getHeight()),
                100, out);
        byte[] imageBytes = out.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap = Bitmap.createScaledBitmap(bitmap, cameraOverlayView.getWidth(), cameraOverlayView.getHeight(), true);

        // MindSpore Processor
        findFacesMindSpore(bitmap);
    }

    private void findFacesMindSpore(Bitmap bitmap) {

        MLFrame frame = MLFrame.fromBitmap(bitmap);
        SparseArray<MLFace> faces = mAnalyzer.analyseFrame(frame);

        for (int i = 0; i < faces.size(); i++) {
            MLFace thisFace = faces.get(i); // Getting the face object recognized by HMS ML Kit

            // Crop the image to face and pass it to MindSpore processor
            float left = thisFace.getCoordinatePoint().x;
            float top = thisFace.getCoordinatePoint().y;
            float right = left + thisFace.getWidth();
            float bottom = top + thisFace.getHeight();

            Bitmap bitmapCropped = Bitmap.createBitmap(bitmap, (int) left, (int) top,
                    ((int) right > bitmap.getWidth() ? bitmap.getWidth() - (int) left : (int) thisFace.getWidth()),
                    (((int) bottom) > bitmap.getHeight() ? bitmap.getHeight() - (int) top : (int) thisFace.getHeight()));

            // Pass the cropped image to MindSpore processor to check
            mMindSporeProcessor.processFaceImages(bitmapCropped, thisFace.getBorder(), isSound);
        }
    }

    private void toggleSound() {
        if (isSound) {
            btnToggleSound.setImageResource(R.drawable.ic_img_sound);
            btnToggleSound.setBackgroundTintList(ColorStateList.valueOf(getMainActivity().getResources().getColor(R.color.colorAccent)));
        } else {
            btnToggleSound.setImageResource(R.drawable.ic_img_sound_disable);
            btnToggleSound.setBackgroundTintList(ColorStateList.valueOf(getMainActivity().getResources().getColor(R.color.colorGrey)));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MediaPlayerRepo.stopSound();
    }
}
