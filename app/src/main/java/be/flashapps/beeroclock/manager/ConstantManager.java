package be.flashapps.beeroclock.manager;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.canelmas.let.DeniedPermission;
import com.canelmas.let.RuntimePermissionListener;
import com.canelmas.let.RuntimePermissionRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import be.flashapps.beeroclock.App;
import be.flashapps.beeroclock.R;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dietervaesen on 28/03/17.
 */

public class ConstantManager {

    public static String BREWERYDB_API_KEY="a9becf806f9838cf8c12c9a38327537d";

    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        /*builder.registerTypeAdapter(Day.class, new DayAdapter());*/
        /*builder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(RealmObject.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });

        builder.registerTypeAdapter(new TypeToken<RealmList<RealmString>>() {
        }.getType(), new TagRealmListConverter());*/

        Gson gson = builder.create();
        return gson;
    }

    public static GsonConverterFactory getFactory() {
        GsonConverterFactory factory = GsonConverterFactory.create(getGson());
        return factory;
    }

    public static RuntimePermissionListener getRuntimePermissionListener(final Activity mActivity) {
        return new RuntimePermissionListener() {
            @Override
            public void onShowPermissionRationale(List<String> permissionList, RuntimePermissionRequest permissionRequest) {
                permissionRequest.retry();
            }


            @Override
            public void onPermissionDenied(List<DeniedPermission> deniedPermissionList) {
                String denied = "";
                for (DeniedPermission deniedPermission : deniedPermissionList) {
                    denied += deniedPermission.getPermission() + "\n";
                    if (deniedPermission.isNeverAskAgainChecked()) {
                        //show dialog to go to settings & manually allow the permission
                        MaterialDialog permissionDialog = new MaterialDialog.Builder(mActivity)
                                .title(R.string.permission_dialog_title)
                                .theme(Theme.LIGHT)
                                .positiveColor(ContextCompat.getColor(App.getContext(), R.color.colorPrimary))
                                .negativeColor(ContextCompat.getColor(App.getContext(), R.color.colorPrimary))
                                .content(R.string.permission_dialog_body)
                                .positiveText(R.string.permission_go_to_settings)
                                .negativeText(android.R.string.cancel)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        DialogManager.startInstalledAppDetailsActivity(mActivity);
                                        dialog.dismiss();
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .build();
                        if (!permissionDialog.isShowing()) {
                            permissionDialog.show();
                        }
                    }
                }
            }
        };
    }


    /*public static class TagRealmListConverter implements JsonSerializer<RealmList<RealmString>>, JsonDeserializer<RealmList<RealmString>> {

        @Override
        public JsonElement serialize(RealmList<RealmString> src, Type typeOfSrc,
                                     JsonSerializationContext context) {
            JsonArray ja = new JsonArray();
            for (RealmString realmString : src) {
                ja.add(context.serialize(realmString));
            }
            return ja;
        }

        @Override
        public RealmList<RealmString> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            RealmList<RealmString> tags = new RealmList<>();
            if (json != null) {
                if (json.isJsonArray()) {
                    JsonArray ja = json.getAsJsonArray();
                    for (JsonElement je : ja) {
                        tags.add(new RealmString((String) context.deserialize(je, String.class)));
                    }
                } else {
                    tags.add(new RealmString((String) context.deserialize(json, String.class)));
                }

            }
            return tags;
        }
    }*/

}
