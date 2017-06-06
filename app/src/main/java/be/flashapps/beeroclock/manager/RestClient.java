package be.flashapps.beeroclock.manager;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import be.flashapps.beeroclock.Models.ResponseApi;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by dietervaesen on 7/09/15.
 */
public class RestClient {

    public static Context mContext;
    static SharedPreferences sharedPreferences;
    public final static String ENDPOINT = "http://api.brewerydb.com/v2/";
    static Retrofit retrofit;

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(ENDPOINT)
            .addConverterFactory(ConstantManager.getFactory());

    static OkHttpClient client;

    public interface ApiManagerService {

        @GET("search/upc")
        Call<ResponseApi> getBeerByUPC(@Query("key") String key, @Query("code") String upc);

        @GET("beers")
        Call<ResponseApi> getBeersByName(@Query("key") String key,@Query("name") String name);


    }

    public static ApiManagerService getApiService(Context context) {
        mContext = context;
        buildHttpClient(/*null*/);//alleen wanneer header moet mee sturen
        ApiManagerService apiService = retrofit.create(ApiManagerService.class);
        return apiService;
    }

    /*public static ApiManagerService getApiService(Context context, AccesToken accesToken) {
        mContext = context;
        buildHttpClient(accesToken);//alleen wanneer header moet mee sturen
        ApiManagerService apiService = retrofit.create(ApiManagerService.class);
        return apiService;
    }
*/
    public static Retrofit retrofit() {
        return retrofit;
    }


   /* public static void buildHttpClient(final AccesToken accesToken) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Dispatcher dispatcher = new Dispatcher();


        // this is the critical point that helped me a lot.
        // we using only one retrofit instance in our application
        // and it uses this dispatcher which can only do 1 request at the same time

        // the docs says : Set the maximum number of requests to execute concurrently.
        // Above this requests queue in memory, waiting for the running calls to complete.
        dispatcher.setMaxRequests(1);
        if (accesToken != null) {

            Interceptor interceptor = new Interceptor() {
                @Override
                public ResponseApi intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", accesToken.getTokenType() + " " + accesToken.getAccesToken())
                            .header("Accept", "application/json")
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            };

            httpClient.addInterceptor(interceptor);
            TokenAuthenticator authenticator = new TokenAuthenticator();
            httpClient.authenticator(authenticator);
            httpClient.dispatcher(dispatcher);

        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.interceptors().add(logging);

        OkHttpClient client = httpClient.build();

        retrofit = builder.client(client).build();
    }*/

    public static void buildHttpClient() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                    /*String text="appwise:thrilled-supermarine-spitfire";
                    byte[] data = text.getBytes("UTF-8");
                    String base64 = Base64.encodeToString(data, Base64.NO_WRAP);//geen onzichtbare new lines*/

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Accept", "application/json")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };


        httpClient.addInterceptor(interceptor);


       /* HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.interceptors().add(logging);*/

        OkHttpClient client = httpClient.build();

        retrofit = builder.client(client).build();
    }



}



