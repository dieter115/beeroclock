package be.flashapps.beeroclock.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import be.flashapps.beeroclock.App;
import be.flashapps.beeroclock.Models.ApiError;
import be.flashapps.beeroclock.Models.Beer;
import be.flashapps.beeroclock.Models.ResponseApi;
import be.flashapps.beeroclock.R;
import be.flashapps.beeroclock.helpers.ErrorHelper;
import be.flashapps.beeroclock.manager.ConstantManager;
import be.flashapps.beeroclock.manager.RestClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener {


    private Timer timer = new Timer();
    private final long DELAY = 1000; // milliseconds
    boolean isTyping = false;
    String query = "";
    Realm realm;
    private List<Beer> beers;
    private String mBarCode;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        realm = getRealm();

        mBarCode = getIntent().getStringExtra(ConstantManager.BARCODE_TO_CONNECT);


        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return true;//if listener handles it and otherwise default berhaviour
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        query = newText;
        if (!isTyping) {
            Logger.d("started typing");
            // Send notification for start typing event
            isTyping = true;
        }
        timer.cancel();
        timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        isTyping = false;
                        Logger.d("stopped typing");
                        //send notification for stopped typing event
                        getBeers(query);
                    }
                },
                DELAY
        );

        return true;//if listener handles it and otherwise default berhaviour
    }

    public void getBeers(String query) {
        RestClient.getApiService(App.getContext()).getBeersByName(ConstantManager.BREWERYDB_API_KEY,query).enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, final Response<ResponseApi> response) {
                if (response.code() == 200) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            JsonArray jsonArray = response.body().getData();
                            if (jsonArray != null) {
                                Type type = new TypeToken<List<Beer>>() {
                                }.getType();
                                beers = ConstantManager.getGson().fromJson(jsonArray.toString(), type);
                                Logger.d(ConstantManager.getGson().toJson(beers));

                            } else {
                                Snackbar.make(findViewById(android.R.id.content), "No beers found for this code", Snackbar.LENGTH_LONG).show();
                            }
                        }
                        //adden to realm is in onchangelistener
                    });
                } else {
                    ApiError error = ErrorHelper.parseError(response);
                    Snackbar.make(findViewById(android.R.id.content), error.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseApi> call, Throwable t) {
                ErrorHelper.processError(t, SearchActivity.this);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            Resources r = getResources();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, r.getDisplayMetrics());
            searchView.setMaxWidth((int) px);
            //openblijven staan bij opstarten
            searchView.setIconifiedByDefault(false);
            searchView.setBackgroundColor(ContextCompat.getColor(App.getContext(), R.color.colorPrimary));

            EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchEditText.setTextColor(ContextCompat.getColor(App.getContext(), android.R.color.white));
            searchEditText.setHintTextColor(ContextCompat.getColor(App.getContext(), R.color.colorPrimary));
            searchView.setQuery(query, false);
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(this);
        }

        return true;
    }
}
