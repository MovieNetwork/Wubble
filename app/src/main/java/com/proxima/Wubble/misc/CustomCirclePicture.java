package com.proxima.Wubble.misc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * From an open source project by Mostafa Gazar
 * URL : https://github.com/MostafaGazar/CustomShapeImageView/blob/master/library/src/com/meg7/widget/BaseImageView.java
 */
public class CustomCirclePicture extends BaseImageView {
    public CustomCirclePicture(Context context) {
        super(context);
    }

    public CustomCirclePicture(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCirclePicture(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public static Bitmap getBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        canvas.drawOval(new RectF(0.0f, 0.0f, width, height), paint);
        return bitmap;
    }

    @Override
    public Bitmap getBitmap() {
        return getBitmap(getWidth(), getHeight());
    }
}
