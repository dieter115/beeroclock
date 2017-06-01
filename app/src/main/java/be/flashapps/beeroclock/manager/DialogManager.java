package be.flashapps.beeroclock.manager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import be.flashapps.beeroclock.App;
import be.flashapps.beeroclock.R;


/**
 * Created by dietervaesen on 24/10/16.
 */

public class DialogManager {

    public static final int TAKE_PICTURE = 1;
    public static final int PICK_IMAGE = 2;

    public static MaterialDialog getLoading(Activity activity, String content) {
        if (content == null) {
            content = activity.getResources().getString(R.string.loading_dialog_title);
        }
        MaterialDialog loading = new MaterialDialog.Builder(activity)
                .content(content)
                .theme(Theme.LIGHT)
                .progress(true, 0)
                .cancelable(false)
                .titleColor(ContextCompat.getColor(App.getContext(), R.color.colorPrimary))
                .contentColor(ContextCompat.getColor(App.getContext(), R.color.colorPrimary))
                .widgetColor(ContextCompat.getColor(App.getContext(), R.color.colorPrimary))
                .build();
        return loading;
    }

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }



    public static void takePhoto(Activity mActivity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mActivity.startActivityForResult(intent, TAKE_PICTURE);
    }

    public static void pickPhoto(Activity mActivity) {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);// ALLEEN
        getIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        mActivity.startActivityForResult(chooserIntent, PICK_IMAGE);
    }




}
