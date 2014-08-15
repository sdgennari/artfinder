package com.hooapps.pca.cvilleart.artfinder.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import com.squareup.picasso.Transformation;

public class BlurTransform implements Transformation {

    private RenderScript rs;

    public BlurTransform(Context context) {
        rs = RenderScript.create(context);
    }

    @Override
    public Bitmap transform(Bitmap source) {
        if (Build.CPU_ABI.contains("armeabi-v7a")) {
            // Create another bitmap that will hold the results of the filter.
            Bitmap blurredBitmap = Bitmap.createBitmap(source);

            // Allocate memory for Renderscript to work with
            Allocation inAlloc = Allocation.createFromBitmap(rs, source, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
            Allocation outAlloc = Allocation.createTyped(rs, inAlloc.getType());

            // Load up an instance of the specific script that we want to use.
            ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setInput(inAlloc);

            // Set the blur radius
            script.setRadius(25f);

            // Run the blur script
            script.forEach(outAlloc);

            // Copy the output to the blurred bitmap
            outAlloc.copyTo(blurredBitmap);
            if (blurredBitmap != source) {
                source.recycle();
            }

            return blurredBitmap;
        }
        return source;
    }

    @Override
    public String key() {
        return "Blur";
    }

}
