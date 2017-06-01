package be.flashapps.beeroclock.activities;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.canelmas.let.AskPermission;
import com.canelmas.let.DeniedPermission;
import com.canelmas.let.Let;
import com.canelmas.let.RuntimePermissionListener;
import com.canelmas.let.RuntimePermissionRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Type;
import java.util.List;

import be.flashapps.beeroclock.App;
import be.flashapps.beeroclock.Models.ApiError;
import be.flashapps.beeroclock.Models.Beer;
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

public class ScanActivity extends BaseActivity implements RuntimePermissionListener {


    @BindView(R.id.barcode_scanner)
    DecoratedBarcodeView barcodeView;
    @BindView(R.id.barcodePreview)
    ImageView barcodePreview;
    private BeepManager beepManager;
    private String lastBarCode;


    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null || result.getText().equals(lastBarCode)) {
                // Prevent duplicate scans
                return;
            }

            lastBarCode = result.getText();
            barcodeView.setStatusText(result.getText());
            beepManager.playBeepSoundAndVibrate();

            //Added preview of scanned barcode
            barcodePreview.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));


            RestClient.getApiService(App.getContext()).getBeerByUPC(ConstantManager.BREWERYDB_API_KEY,lastBarCode). enqueue(new Callback<be.flashapps.beeroclock.Models.Response>() {
                        @Override
                        public void onResponse(Call<be.flashapps.beeroclock.Models.Response> call, final Response<be.flashapps.beeroclock.Models.Response> response) {
                            if (response.code() == 200) {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        JsonArray jsonArray=response.body().getData();
                                        if(jsonArray!=null) {
                                            Type type = new TypeToken<List<Beer>>() {
                                            }.getType();
                                            List<Beer> beers = ConstantManager.getGson().fromJson(jsonArray.toString(), type);
                                            for (Beer beer : beers) {
                                                beer.setBarCode(lastBarCode);
                                                addBeer(beer);
                                            }

                                        }else{
                                            Snackbar.make(findViewById(android.R.id.content),"No beers found for this code",Snackbar.LENGTH_LONG).show();
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
                        public void onFailure(Call<be.flashapps.beeroclock.Models.Response> call, Throwable t) {
                            ErrorHelper.processError(t, ScanActivity.this);
                        }
                    });;
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };
    private DatabaseReference fireBaseInstance;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        realm=getRealm();
        setUpBarcode();

        setUpFireBase();


    }

    private void addBeer(Beer beer) {
       fireBaseInstance.child("beers").child(beer.getId()).setValue(beer);
        //adden to realm is in onchangelistener
    }

    private void setUpFireBase() {
        fireBaseInstance = getFireBaseDataBaseInstance();
        Query getAllrecipesQuery = fireBaseInstance.child("beers").orderByChild("id");
        getAllrecipesQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Beer beer = dataSnapshot.getValue(Beer.class);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealmOrUpdate(beer);
                    }
                });
                Snackbar.make(findViewById(android.R.id.content),beer.getName()+" beer added",Snackbar.LENGTH_LONG).show();
                Logger.d("beer added " + beer.getName());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final Beer beer = dataSnapshot.getValue(Beer.class);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealmOrUpdate(beer);

                    }
                });
                Logger.d("beer changed " + beer.getName());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final Beer beer = dataSnapshot.getValue(Beer.class);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.where(Beer.class).equalTo("id", beer.getId()).findFirst().deleteFromRealm();
                    }
                });
                Logger.d("beer changed " + beer.getName());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Logger.d("Failed to read value.", databaseError.toException().toString());
            }
        });
    }

    @AskPermission({
            Manifest.permission.CAMERA,
    })
    public void setUpBarcode() {
        barcodeView.decodeContinuous(callback);

        beepManager = new BeepManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Let.handle(this, requestCode, permissions, grantResults);
    }


    @Override
    public void onShowPermissionRationale(List<String> permissionList, RuntimePermissionRequest permissionRequest) {
        ConstantManager.getRuntimePermissionListener(this).onShowPermissionRationale(permissionList, permissionRequest);
    }

    @Override
    public void onPermissionDenied(List<DeniedPermission> deniedPermissionList) {
        ConstantManager.getRuntimePermissionListener(this).onPermissionDenied(deniedPermissionList);
    }

}
