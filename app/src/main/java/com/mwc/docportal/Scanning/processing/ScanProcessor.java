package com.mwc.docportal.Scanning.processing;

import android.content.Context;

import com.mwc.docportal.Scanning.model.Page;
import com.thegrizzlylabs.geniusscan.sdk.core.GeniusScanLibrary;
import com.thegrizzlylabs.geniusscan.sdk.core.ImageType;
import com.thegrizzlylabs.geniusscan.sdk.core.LicenseException;
import com.thegrizzlylabs.geniusscan.sdk.core.ProcessingException;
import com.thegrizzlylabs.geniusscan.sdk.core.ProcessingParameters;
import com.thegrizzlylabs.geniusscan.sdk.core.Quadrangle;
import com.thegrizzlylabs.geniusscan.sdk.core.ScanContainer;

import java.io.IOException;

/**
 * Created by pnollet on 04/10/2016.
 */

public class ScanProcessor {
    private static final String TAG = ScanProcessor.class.getSimpleName();

    /**
     * Generate the enhanced image for the given page
     *
     * @param context
     * @param scanContainer
     */
    public void processPage(Context context, ScanContainer scanContainer) throws LicenseException, IOException, ProcessingException {
        String enhancedImagePath = scanContainer.getEnhancedImage().getAbsolutePath(context);
        String originalImagePath = scanContainer.getOriginalImage().getAbsolutePath(context);

        ImageType imageType = scanContainer.getImageType();
        Quadrangle quadrangle = scanContainer.getQuadrangle();
        boolean distortionCorrectionEnabled = ((Page)scanContainer).isDistortionCorrectionEnabled();

        ProcessingParameters parameters = new ProcessingParameters(quadrangle, imageType, distortionCorrectionEnabled);
        parameters = GeniusScanLibrary.detectWarpEnhance(originalImagePath, enhancedImagePath, parameters);

        scanContainer.setQuadrangle(parameters.getQuadrangle());
        scanContainer.setImageType(parameters.getImageType());
    }
}
