package com.caca.imagemachine.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author caca rusmana on 19/03/22
 */
public final class FileUtil {

    private FileUtil() {
    }

    public static void writeToFile(Bitmap bitmap, File parent, String fileName) throws IOException {
        var file = new File(parent, fileName);
        if (!file.exists()) {
            var out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);
            out.flush();
            out.close();
        }
    }

    public static void deleteFile(File parent, String fileName) throws IOException {
        var file = new File(parent, fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) throws IOException {
        return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
    }

    public static String getFilenameFromUri(Context context, Uri uri) {
        var curs = context.getContentResolver().query(uri, null, null, null, null);
        int nameIndex = curs.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        curs.moveToFirst();
        return curs.getString(nameIndex);
    }
}
