package com.winchance.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
    public final static String DEFALUT_FOLDER_NAME = "AndroidImage";

    private File mFolderDir = null;

    public FileUtil(Context context) {
        this(context, DEFALUT_FOLDER_NAME);
    }

    public FileUtil(Context context, String folderName) {
        mFolderDir = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                new File(Environment.getExternalStorageDirectory().getPath(), folderName) :
                new File(context.getCacheDir().getPath(), folderName);
        if(!mFolderDir.exists()) {
            mFolderDir.mkdir();
        }
    }

    public void savaBitmap(String fileName, Bitmap bitmap)
            throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(mFolderDir, getRealName(fileName)));
            bitmap.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    public Bitmap getBitmap(String fileName){
        return BitmapFactory.decodeFile(new File(mFolderDir, getRealName(fileName)).getAbsolutePath());
    }

    public boolean exists(String fileName){
        return new File(mFolderDir, getRealName(fileName)).exists();
    }

    public long getLength(String fileName) {
        return new File(mFolderDir, getRealName(fileName)).length();
    }

    public void delete(String fileName) {
        new File(mFolderDir, getRealName(fileName)).delete();
    }

    public void deleteAll() {
        for (String fileName : mFolderDir.list()) {
            new File(mFolderDir, fileName).delete();
        }
    }

    private String getRealName(String fileName) {
        return StringUtil.getMD5(fileName);
    }
}
