package com.flymaccin.meadowlarkdemonic;

import android.app.Activity;
import android.app.PictureInPictureParams;
import android.os.Build;
import android.util.Rational;
import android.view.View;

/** Android presentation adapter for the single canonical DemonicTv session. */
public final class TvSurfaceController {
    private final Activity activity;
    private final DemonicTv tv;

    public TvSurfaceController(Activity activity, DemonicTv tv) {
        if (activity == null || tv == null) throw new IllegalArgumentException();
        this.activity = activity;
        this.tv = tv;
    }

    public void inApp() {
        tv.setSurface(DemonicTv.Surface.IN_APP);
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    public void fullscreen() {
        tv.setSurface(DemonicTv.Surface.FULLSCREEN);
        activity.getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    public boolean pictureInPicture(int videoWidth, int videoHeight) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false;
        int w=Math.max(1,videoWidth), h=Math.max(1,videoHeight);
        PictureInPictureParams params = new PictureInPictureParams.Builder()
            .setAspectRatio(new Rational(w,h))
            .build();
        boolean entered = activity.enterPictureInPictureMode(params);
        if (entered) tv.setSurface(DemonicTv.Surface.PICTURE_IN_PICTURE);
        return entered;
    }

    public void onPictureInPictureModeChanged(boolean inPip) {
        tv.setSurface(inPip ? DemonicTv.Surface.PICTURE_IN_PICTURE : DemonicTv.Surface.IN_APP);
    }
}
