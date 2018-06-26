package com.swaas.mwc;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.Hashtable;

/**
 * Created by harika on 12-06-2018.
 */

public class TypeFaces {

    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

    public static Typeface getTypeFace(Context context, String assetPath) {
        synchronized (cache) {
            if (!cache.containsKey(assetPath)) {
                try {
                    Typeface typeFace = Typeface.createFromAsset(
                            context.getAssets(), assetPath);
                    cache.put(assetPath, typeFace);
                } catch (Exception e) {
                    Log.e("TypeFaces", "Typeface not loaded." + e);
                    return null;
                }
            }
            return cache.get(assetPath);
        }
    }
}
