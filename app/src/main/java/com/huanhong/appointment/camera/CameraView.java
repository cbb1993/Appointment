package com.huanhong.appointment.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huanhong.appointment.R;

import java.io.File;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by 坎坎.
 * Date: 2020/7/31
 * Time: 21:56
 * describe:
 */
public class CameraView extends RelativeLayout {
    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private CameraSurfaceView mCameraView;
    private CameraProxy mCameraProxy;
    private Context context;

    private void init(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.view_camera, null);
        addView(view);
        mCameraView = view.findViewById(R.id.camera_view);
        mCameraProxy = mCameraView.getCameraProxy();
        view.findViewById(R.id.btn_camera).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                setVisibility(GONE);
                if(dismissCallback!=null){
                    dismissCallback.dismiss();
                }
            }
        });
    }

    public void takePhoto() {
        mCameraProxy.takePicture(mPictureCallback);
    }
    private final Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            mCameraProxy.startPreview(mCameraView.getHolder()); // 拍照结束后继续预览
            new ImageSaveTask2().execute(data); // 保存图片
        }
    };

    private class ImageSaveTask2 extends AsyncTask<byte[], Void, Void> {

        private static final String TAG = "camera";

        @Override
        protected Void doInBackground(byte[]... bytes) {
            long time = System.currentTimeMillis();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes[0], 0, bytes[0].length);
            Log.d(TAG, "BitmapFactory.decodeByteArray time: " + (System.currentTimeMillis() - time));
            int rotation = mCameraProxy.getLatestRotation();
            time = System.currentTimeMillis();
            Bitmap rotateBitmap = ImageUtils.rotateBitmap(bitmap, rotation, mCameraProxy.isFrontCamera(), true);
            Log.d(TAG, "rotateBitmap time: " + (System.currentTimeMillis() - time));
            time = System.currentTimeMillis();
            ImageUtils.saveBitmap(rotateBitmap);
            Log.d(TAG, "saveBitmap time: " + (System.currentTimeMillis() - time));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            startCompare();
        }

        private void startCompare() {
            if(takePhotoStateCallback!=null){
                takePhotoStateCallback.start();
            }
        }
    }


    private TakePhotoStateCallback takePhotoStateCallback;

    public void setTakePhotoStateCallback(TakePhotoStateCallback callback) {
        this.takePhotoStateCallback = callback;
    }
    private DismissCallback dismissCallback;

    public void setDismissCallback(DismissCallback callback) {
        this.dismissCallback = callback;
    }

    public interface TakePhotoStateCallback{
        void start();
    }
    public interface DismissCallback{
        void dismiss();
    }

}
