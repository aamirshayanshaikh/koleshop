package com.koleshop.appkoleshop.ui.common.activities;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.koleshop.appkoleshop.R;
import com.koleshop.appkoleshop.constant.Constants;
import com.koleshop.appkoleshop.model.realm.BuyerAddress;
import com.koleshop.appkoleshop.services.RegistrationIntentService;
import com.koleshop.appkoleshop.ui.seller.activities.HomeActivity;
import com.koleshop.appkoleshop.util.CommonUtils;
import com.koleshop.appkoleshop.util.KoleshopUtils;
import com.koleshop.appkoleshop.util.PreferenceUtils;
import com.koleshop.appkoleshop.ui.seller.activities.SelectSellerCategoryActivity;
import com.koleshop.appkoleshop.util.RealmUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.ButterKnife;

public class InitialActivity extends AppCompatActivity {

    // Constants
    static private boolean TEST_MODE = false;
    static private Class TEST_CLASS = SelectSellerCategoryActivity.class;

    static Account mAccount;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "InitialActivity";
    String sessionType;

    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
    static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1002;
    String mEmail; // Received from newChooseAccountIntent(); passed to getToken()
    Context mContext;

    @BindView(R.id.btn_sell_initial)
    ImageButton buttonSell;
    @BindView(R.id.btn_buy_initial)
    ImageButton buttonBuy;
    @BindView(R.id.pb_initial_activity)
    ProgressBar progressBar;
    @BindString(R.string.google_api_scope)
    String GOOGLE_API_SCOPE;
    @BindView(R.id.imageViewShopLogo)
    ImageView imageViewLogo;
    @BindView(R.id.textViewShopDescription)
    TextView textViewShopDescription;
    @BindDimen(R.dimen.initial_screen_logo_height)
    int logoHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_initial);
        ButterKnife.bind(this);
        if (checkPlayServices()) {
            thugLife();
            realmMigrate();
            configureHomePage();
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(PreferenceUtils.isSessionTypeSeller(mContext)) {
            KoleshopUtils.showTheUpdateNotificationsIfRequired(mContext);
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * first method that run in InitialActivity
     */
    private void thugLife() {

        Log.d(TAG, "!! living thug life !!");

        //1. DELETE THE NETWORK REQUEST STATUSES
        new AsyncTask<Void, Void, Void>(
        ) {
            @Override
            protected Void doInBackground(Void... params) {
                PreferenceUtils.deleteNetworkRequestStatusPreferences(mContext);
                return null;
            }
        }.execute();

        //2. UPDATE THE TOKEN ON SERVER IF REQD
        boolean deviceIdSyncedToServer = PreferenceUtils.getPreferencesFlag(mContext, Constants.FLAG_DEVICE_ID_SYNCED_TO_SERVER);
        Log.d(TAG, "device id synced to server = " + deviceIdSyncedToServer);
        if (!deviceIdSyncedToServer) {
            Intent tokenRefreshIntent = new Intent(mContext, RegistrationIntentService.class);
            tokenRefreshIntent.setAction(RegistrationIntentService.REGISTRATION_INTENT_SERVICE_ACTION_UPDATE_TOKEN_ON_SERVER);
            Log.d(TAG, "refreshing token");
            startService(tokenRefreshIntent);
        }
    }

    /**
     * second method that run in InitialActivity
     * Load the user if logged in | buyer/seller home activity is loaded according to session
     * If user is not logged in, then the home page to choose buyer/seller is shown
     */
    private void configureHomePage() {

        Log.d(TAG, "checking user login status");
        if (PreferenceUtils.isSessionTypeSeller(mContext)) {
            //seller session
            //if logged in
            Log.d(TAG, "seller session logged in");
            if (CommonUtils.getUserId(mContext) != null && CommonUtils.getUserId(mContext) > 0) {
                //if settings setup is finished then open home
                Log.d(TAG, "user id is not null");
                boolean settingsSetupFinished = PreferenceUtils.getPreferencesFlag(this, Constants.FLAG_SELLER_SETTINGS_SETUP_FINISHED);
                if (settingsSetupFinished) {
                    //go to home activity
                    Log.d(TAG, "settings setup finished...opening home activity");
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    //go to get started activity
                    Log.d(TAG, "go to get started activity");
                    Intent intent = new Intent(this, GetStartedActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            } else {
                Log.d(TAG, "user id is null");
                showTheOptions();
            }
        } else if (PreferenceUtils.isSessionTypeBuyer(mContext)) {
            //buyer session
            Log.d(TAG, "buyer session logged in");
            boolean userLoggedIn = CommonUtils.getUserId(mContext) != null && CommonUtils.getUserId(mContext) > 0;
            BuyerAddress buyerAddress = RealmUtils.getDefaultUserAddress();
            boolean deliveryLocationSelected = buyerAddress != null;
            Log.d(TAG, "deliveryLocationSelected = " + deliveryLocationSelected);
            //if(userLoggedIn || deliveryLocationSelected) {
            if (deliveryLocationSelected) {
                Log.d(TAG, "starting home activity for buyer");
                Intent intent = new Intent(this, com.koleshop.appkoleshop.ui.buyer.activities.HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                //user need to select the delivery location
                Log.d(TAG, "user need to select delivery location");
                showTheOptions();
            }

        } else {
            //let the user choose the session type
            Log.d(TAG, "no session selected, showing the options");
            showTheOptions();
        }

    }

    private void realmMigrate() {
        //this is a workaround of realm migration
        int currentRealmVersion = PreferenceUtils.getCurrentRealmVersion(mContext);
        Log.d(TAG, "realm version fix , current realm version" + currentRealmVersion);
        if(currentRealmVersion<Constants.REALM_VERSION) {
            RealmUtils.resetRealm(mContext);
        }
    }

    private void showTheOptions() {
        //animation will be shown
        //buy/sell buttons will be shown
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                moveLogoFromCenterToOffset();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showAllUiAnimations();
                    }
                }, 1000);
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.notification_glance);
                mp.start();
            }
        }, 1000);

        /*Intent initialActivityIntent = new Intent(mContext, InitialActivity.class);
        initialActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(initialActivityIntent);*/
    }

    public void goBuy(View v) {
        sessionType = Constants.SESSION_TYPE_BUYER;
        next();
    }

    public void goSell(View v) {
        sessionType = Constants.SESSION_TYPE_SELLER;
        next();
    }

    private void next() {
        PreferenceUtils.setPreferences(this, Constants.KEY_USER_SESSION_TYPE, sessionType);
        goToNextScreen();
    }

    private void setProgressing(boolean progressing) {
        progressBar.setVisibility(progressing ? View.VISIBLE : View.GONE);
        buttonBuy.setEnabled(!progressing);
        buttonSell.setEnabled(!progressing);
    }

    public void goToNextScreen() {
        String token = PreferenceUtils.getPreferences(mContext, Constants.KEY_GOOGLE_API_TOKEN);
        Log.d(TAG, "go to next screen");
        if (token != null && !token.isEmpty()) {
            Log.d(TAG, "token is not null");
            if (sessionType == Constants.SESSION_TYPE_SELLER) {
                Log.d(TAG, "session type - seller");
                goToVerifyPhoneNumberScreen();
            } else {
                Log.d(TAG, "session type - buyer | will choose delivery location");
                chooseDeliveryLocation();
            }
        } else {
            if (checkPlayServices()) {
                pickUserAccount();
                setProgressing(true);
            }
        }
    }

    private void chooseDeliveryLocation() {
        Intent mapsActivityIntent = new Intent(mContext, MapsActivity.class);
        mapsActivityIntent.putExtra("twoButtonMode", false);
        mapsActivityIntent.putExtra("title", getString(R.string.title_set_delivery_location));
        mapsActivityIntent.putExtra("markerTitle", "Delivery location");
        mapsActivityIntent.putExtra("actionButtonTitle", getString(R.string.title_start_shopping));
        startActivity(mapsActivityIntent);
    }

    public void goToVerifyPhoneNumberScreen() {
        Intent intent = new Intent(this, VerifyPhoneNumberActivity.class);
        if (sessionType == Constants.SESSION_TYPE_BUYER) {
            intent.putExtra(Constants.KEY_SKIP_ALLOWED, true);
        }
        startActivity(intent);
    }

    private void moveLogoFromCenterToOffset() {
        imageViewLogo.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.pulse_logo_animation));
        TranslateAnimation animationDescription = new TranslateAnimation(0, 0, 0, -200);
        animationDescription.setDuration(1000);
        animationDescription.setFillAfter(true);
        textViewShopDescription.startAnimation(animationDescription);
    }

    private void showAllUiAnimations() {
        textViewShopDescription.setVisibility(View.VISIBLE);
        buttonBuy.setVisibility(View.VISIBLE);
        buttonSell.setVisibility(View.VISIBLE);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);

        textViewShopDescription.startAnimation(alphaAnimation);
        buttonSell.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.rotate_clock_wise));
        buttonBuy.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.rotate_counter_clock_wise));

    }

    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                // With the account name acquired, go get the auth token
                getUsername();
            } else if (resultCode == RESULT_CANCELED) {
                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setProgressing(false);
                        Toast.makeText(mContext, R.string.pick_account, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR ||
                requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
                && resultCode == RESULT_OK) {
            // Receiving a result that follows a GoogleAuthException, try auth again
            getUsername();
        }
    }

    /**
     * Attempts to retrieve the username.
     * If the account is not yet known, invoke the picker. Once the account is known,
     * start an instance of the AsyncTask to get the auth token and do work with it.
     */
    private void getUsername() {
        if (mEmail == null) {
            pickUserAccount();
        } else {
            if (CommonUtils.isConnectedToInternet(this)) {
                new GetUsernameTask(InitialActivity.this, mEmail).execute();
            } else {
                Toast.makeText(this, R.string.not_online, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void internetConnectionCheck() {
        if (!CommonUtils.isConnectedToInternet(this)) {
            setProgressing(false);
            Toast.makeText(this, R.string.not_online, Toast.LENGTH_LONG).show();
        }
    }

    public class GetUsernameTask extends AsyncTask<Void, Void, Void> {
        Activity mActivity;
        String mScope;
        String mEmail;

        GetUsernameTask(Activity activity, String name) {
            this.mActivity = activity;
            this.mScope = GOOGLE_API_SCOPE;
            this.mEmail = name;
        }

        /**
         * Executes the asynchronous job. This runs when you call execute()
         * on the AsyncTask instance.
         */
        @Override
        protected Void doInBackground(Void... params) {
            try {
                String token = fetchToken();
                if (token != null) {
                    // **Insert the good stuff here.**
                    // Use the token to access the user's Google data.
                    //todo encrypt for saving
                    PreferenceUtils.setPreferences(mContext, Constants.KEY_GOOGLE_API_TOKEN, token);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setProgressing(false);
                        }
                    });

                    goToNextScreen();
                }
            } catch (IOException e) {
                // The fetchToken() method handles Google-specific exceptions,
                // so this indicates something went wrong at a higher level.
                // TIP: Check for network connectivity before starting the AsyncTask.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setProgressing(false);
                        internetConnectionCheck();
                    }
                });
            }
            return null;
        }

        /**
         * Gets an authentication token from Google and handles any
         * GoogleAuthException that may occur.
         */
        protected String fetchToken() throws IOException {
            try {
                return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
            } catch (UserRecoverableAuthException userRecoverableException) {
                // GooglePlayServices.apk is either old, disabled, or not present
                // so we need to show the user some UI in the activity to recover.
                ((InitialActivity) mActivity).handleException(userRecoverableException);
            } catch (GoogleAuthException fatalException) {
                // Some other type of unrecoverable exception has occurred.
                // Report and log the error as appropriate for your app.
                Toast.makeText(mContext, "Some problem occurred. Please restart the app.", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

    public void handleException(final Exception e) {
        // Because this call comes from the AsyncTask, we must ensure that the following
        // code instead executes on the UI thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    // The Google Play services APK is old, disabled, or not present.
                    // Show a dialog created by Google Play services that allows
                    // the user to update the APK
                    int statusCode = ((GooglePlayServicesAvailabilityException) e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            InitialActivity.this,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    Intent intent = ((UserRecoverableAuthException) e).getIntent();
                    startActivityForResult(intent,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
