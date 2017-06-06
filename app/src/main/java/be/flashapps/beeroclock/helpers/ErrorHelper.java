package be.flashapps.beeroclock.helpers;


import android.app.Activity;
import android.support.design.widget.Snackbar;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

import be.flashapps.beeroclock.App;
import be.flashapps.beeroclock.Models.ApiError;
import be.flashapps.beeroclock.R;
import be.flashapps.beeroclock.manager.RestClient;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by dietervaesen on 24/10/16.
 */

public class ErrorHelper {

   /* public static String parseError(ResultForElement responseBody) {

        String errorMessage="";
        int code=Integer.parseInt(responseBody.getStatus());


            if(code==500){
                errorMessage= App.getContext().getResources().getString(R.string.internal_server_error);
            }
            else if(code==404){
                errorMessage= App.getContext().getResources().getString(R.string.server_error);
            }
            else if(code==401){
                errorMessage= App.getContext().getResources().getString(R.string.login_error);
            }

        return errorMessage;
    }*/

    public static void processError(Throwable t, Activity activity) {
        if (t instanceof IOException) {
            Snackbar.make(activity.findViewById(android.R.id.content), activity.getResources().getString(R.string.network_error), Snackbar.LENGTH_LONG).show();
        }
        if (t instanceof SocketTimeoutException || t instanceof UnknownHostException) {
            Snackbar.make(activity.findViewById(android.R.id.content), activity.getResources().getString(R.string.server_error), Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content), t.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    public static ApiError parseError(Response response) {
        ApiError error = new ApiError();
        /*if(error.getMessage()==null){
            if(error.getException()!=null)
            error.setMessage(error.getException());//opvangen lege snackbar message
            else{*/
        int code = response.code();
        if (code == 500) {
            error.setMessage(App.getContext().getResources().getString(R.string.internal_server_error));
        } else if (code == 404) {
            error.setMessage(App.getContext().getResources().getString(R.string.network_error));
        } else if (code == 401) {
            error.setMessage(App.getContext().getResources().getString(R.string.login_error));
        } else if (code == 422) {
            try {
                Converter<ResponseBody, HashMap> hashmapConverter =
                        RestClient.retrofit()
                                .responseBodyConverter(HashMap.class, new Annotation[0]);

                String message = "";
                ResponseBody hashMapresponseBody = response.errorBody();
                HashMap<String, Object> hashMap = hashmapConverter.convert(hashMapresponseBody);
                for (String entry : hashMap.keySet()) {
                    String value = ((List<String>) hashMap.get(entry)).get(0).toString();
                    if (value.contains("verplicht")) {
                        message = value + " " + entry;
                    } else//dit is wanneer boodschap zelf genoeg is
                        message = value;
                }
                error.setMessage(message);
            } catch (Exception e) {
                Logger.d("foutje");
                e.printStackTrace();
            }
        } else {
            Converter<ResponseBody, ApiError> converter =
                    RestClient.retrofit()
                            .responseBodyConverter(ApiError.class, new Annotation[0]);

            ResponseBody responseBody = response.errorBody();
            try {
                error = converter.convert(responseBody);
            } catch (Exception e) {
                error = new ApiError();
            }
        }

        return error;
    }
}
