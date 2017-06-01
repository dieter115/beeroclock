package be.flashapps.beeroclock.helpers;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

import be.flashapps.beeroclock.App;


/**
 * Created by thomasbeerten on 02/03/16.
 */
public class Helper_Utils {


    public static void hideKeyboard(Activity activity) {

        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting() && netInfo.isAvailable();
    }

    //Validation
    public static String checkEditTextEmpty(EditText editText) {
        String output = null;
        output = editText.getText().toString();
        if (output.matches("")) {
            return null;
        } else {
            return String.valueOf(output).trim();
        }
    }

    public static boolean isStringNotNullOrEmpty(String string) {
        return (string != null && !string.isEmpty());
    }

    public static long getUnixNow() {
        return System.currentTimeMillis() / 1000L;
    }

    public static Point getScreenDimensions2(FrameLayout layout_for_dragging) {
        return null;
    }

    public static float marsX(float i) {
        if (i > 1.0f)
            return 1.0f;
        if (i < 0.0f)
            return 0.0f;

        return i;
    }

    public static float marsY(float i) {
        if (i > 1.0f)
            return 1.0f;
        if (i < 0.0f)
            return 0.0f;

        return i;
    }

   /* public class ColorLayer implements Transformation {
        private final int destinationColor;

        public ColorLayer(int destinationColor) {
            this.destinationColor = destinationColor;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            Canvas canvas = new Canvas(source);
            int dr = Color.red(destinationColor);
            int dg = Color.green(destinationColor);
            int db = Color.blue(destinationColor);
            canvas.drawColor(Color.argb(220, dr, dg, db));
            return source;
        }

        @Override
        public String key() {
            String hexColor = String.format("#%08x", destinationColor);
            return "color-layer(destinationColor=" + hexColor + ")";
        }
    }*/

    //Get elpased time between two points in code
    //Example:
    //setTimeAnchorpoint("myasynctask")
    //Logger.d("myasynctask "+ getElapsedTime("myasynctask")
    //Prints: myasynctask Elapsed time (in ms): 2121
    static Map<String, Long> elapsedTimeMap;

    public static void setTimeAnchorpoint(String uuidAnchorpoint) {

        if (elapsedTimeMap == null) {
            elapsedTimeMap = new HashMap<>();
        }

        Long currentTime = System.currentTimeMillis();

        elapsedTimeMap.put(uuidAnchorpoint, currentTime);
    }

    public static String getElapsedTime(String uuidAnchorpoint) {

        Long elapsedTime = null;
        try {
            Long startTime = elapsedTimeMap.get(uuidAnchorpoint);
            Long currentTime = System.currentTimeMillis();
            elapsedTime = currentTime - startTime;
        } catch (Exception e) {
            e.printStackTrace();
            return uuidAnchorpoint + " Elapsed time (in ms) failed with error";
        }

        //If you don't have logger, change to Log.d, or just use returned string
        Logger.d(uuidAnchorpoint + " Elapsed time (in ms): " + elapsedTime);

        return uuidAnchorpoint + " Elapsed time (in ms): " + elapsedTime;
    }

    //Getting screen values and dimensions
    public static int getXPercent(int screenWidth, int xTouch) {

        double coeficientOfZoiets = screenWidth / 100;
        double xPercent = xTouch / coeficientOfZoiets;
        return (int) xPercent;
    }

    public static int getXValue(int xPercent) {

        int xValue;

        //12 increments
        if (xPercent >= 70) {
            xValue = 0;
        } else if (xPercent >= 58) {
            xValue = 1;
        } else if (xPercent >= 46) {
            xValue = 2;
        } else if (xPercent >= 34) {
            xValue = 3;
        } else {
            xValue = 4;
        }

        return xValue;
    }

    public static int getYPercent(int screenHeight, int yTouch) {

        double coeficientOfZoiets = screenHeight / 100;
        double xPercent = yTouch / coeficientOfZoiets;
        return (int) xPercent;
    }

    public static int getYValue(int yPercent) {

        int yValue;

        //12 increments
        if (yPercent >= 80) {
            yValue = 0;
        } else if (yPercent >= 70) {
            yValue = 1;
        } else if (yPercent >= 60) {
            yValue = 2;
        } else if (yPercent >= 50) {
            yValue = 3;
        } else if (yPercent >= 40) {
            yValue = 4;
        } else if (yPercent >= 30) {
            yValue = 5;
        } else {
            yValue = 6;
        }

        return yValue;
    }

    public static Point getScreenDimensions(Activity parentActivity) {
        Display display = parentActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Rect locateView(View view) {
        Rect loc = new Rect();
        int[] location = new int[2];
        if (view == null) {
            return loc;
        }
        view.getLocationOnScreen(location);

        loc.left = location[0];
        loc.top = location[1];
        loc.right = loc.left + view.getWidth();
        loc.bottom = loc.top + view.getHeight();
        return loc;
    }

    public static boolean isRectTouched(Rect rect, int x, int y) {

        boolean inX = false;
        boolean inY = false;

        if (x >= rect.left && x <= rect.right) {
            inX = true;
        }

        if (y <= rect.bottom && y >= rect.top) {
            inY = true;
        }

        return (inX && inY);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    public static void setBackgroundNotDeprecated(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }

}