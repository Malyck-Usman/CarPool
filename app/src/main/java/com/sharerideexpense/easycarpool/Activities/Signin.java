package com.sharerideexpense.easycarpool.Activities;

import static com.sharerideexpense.easycarpool.Activities.ApplicationController.mAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Patterns;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.sharerideexpense.easycarpool.Ads.AppOpen;
import com.sharerideexpense.easycarpool.Ads.TemplateView;
import com.sharerideexpense.easycarpool.BuildConfig;
import com.sharerideexpense.easycarpool.DriverPanel.Activities.MyRides;
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.UserPanel.Activities.UserSearchActivity;
import com.sharerideexpense.easycarpool.UserPanel.Classes.CarPoolDb;
import com.sharerideexpense.easycarpool.UserPanel.Classes.TripRequestsData;
import com.sharerideexpense.easycarpool.UserPanel.Interface.TripRequestDao;
import com.sharerideexpense.easycarpool.classes.BookingRequestInfo;
import com.sharerideexpense.easycarpool.classes.UserInfo;
import com.sharerideexpense.easycarpool.classes.MySharedPreferences;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Signin extends AppCompatActivity implements View.OnClickListener, AppOpen {
    private static final int PLAY_SERVICES_ERROR_CODE = 2;
    private static final int GPS_REQUEST_CODE = 3;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 4;
    private static final int NOTIFICATION_REQUEST_CODE = 7;
    private static final int READ_STORAGE_REQUEST_CODE = 5;
    private static final int REQUEST_PERMISSION_SETTING = 6;
    CardView btn_signin;
    TextView tv_signup, tv_forget_password;
    String email, password;
    TextInputLayout edt_Email, edt_Password;
    //    FirebaseAuth mAuth;
//    GoogleSignInClient googleSignInClient;
    LinearLayout btn_gmail_signin;// btn_facebook_signin;
    DocumentReference mDocRef;
    String mFCMToken;
    FirebaseFirestore mDBRef;
    FirebaseDatabase mRTRef;
    MySharedPreferences preferences;
    ProgressDialog dialog;
    private static final int GOOGLE_SIGNIN_REQ = 2;
    private boolean showOneTapUI = true;
    CarPoolDb db;
    FirebaseUser user;
    FrameLayout fl_ad;
    View margin;
    ApplicationController mController;
    FirebaseUser currentUser;

    //    static {
//        System.loadLibrary("native-lib");
//    }
//    public native String apiKey();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Init();
        CheckVersion();


    }

    private void CheckVersion() {
        Log.d("TAG","Remote version code is "+preferences.getRemoteVersionCode());
        if (preferences.getRemoteVersionCode() != 0) {
            PackageManager manager = getPackageManager();
            PackageInfo info = null;
            try {
                info = manager.getPackageInfo(getPackageName(), 0);
                if (info != null) {
                    long appVersionCode = info.getLongVersionCode();
                    if (appVersionCode < preferences.getRemoteVersionCode()) {
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this)
                                .setCancelable(false)
                                .setTitle("App Update Required")
                                .setMessage("A newer version of app is available on Google Play Store. You need to update your app to continue")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)); // Add package name of your application
                                        startActivity(intent);
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                });
                        builder.show();
                    }
                    else {
                        CheckCurrentUser();
                        AskForPermissions();
                    }

                }

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            CheckCurrentUser();
            AskForPermissions();
        }
    }

    private void Init() {
//        String baseApi = new String(Base64.decode(apiKey(),Base64.DEFAULT));
        mController = ((ApplicationController) getApplicationContext());
        TemplateView templateView = findViewById(R.id.ad_container);
        mController.initNativeAd(templateView);
        mController.appOpenManager.setAppOpen(this);
        fl_ad = findViewById(R.id.fl_ad);
        margin = findViewById(R.id.ad_margin);


        setToolbar();
        db = Room.databaseBuilder(this, CarPoolDb.class, getString(R.string.CARPOOL_DB)).build();
//        ApplicationController.googleSignInClient = SignInRequest();
        mDBRef = FirebaseFirestore.getInstance();
        mRTRef = FirebaseDatabase.getInstance();
        preferences = new MySharedPreferences(this);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Signing in");
        dialog.setMessage("Please wait ...");
        btn_signin = findViewById(R.id.btn_signin);
        btn_signin.setOnClickListener(this);
        edt_Email = findViewById(R.id.edt_email_signin);
        edt_Password = findViewById(R.id.edt_password_signin);
        tv_signup = findViewById(R.id.tv_signup);
        tv_signup.setOnClickListener(this);
        btn_gmail_signin = findViewById(R.id.btn_gmail_signup);
//        btn_facebook_signin = findViewById(R.id.btn_facebook_signup);
//        btn_facebook_signin.setOnClickListener(this);
        btn_gmail_signin.setOnClickListener(this);
        tv_forget_password = findViewById(R.id.tv_forget_password);
        tv_forget_password.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signin:

                if (!ValidateEmail() | !ValidatePassword()) {
                    return;
                } else {
                    SignInUser();
                }
                break;
            case R.id.iv_toolbar:
                onBackPressed();
                break;
            case R.id.tv_signup:
                startActivity(new Intent(Signin.this, Signup.class));
                break;
            case R.id.btn_gmail_signup:
                if (CheckLocationPermission()) {
                    GmailSignin();
                } else {
                    AskForPermissions();
                }
                break;
//            case R.id.btn_facebook_signup:
//                Toast.makeText(this, "This feature is currently un-available", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.tv_forget_password:
                startActivity(new Intent(Signin.this, ForgetPassword.class));
                break;
        }
    }

    private void GmailSignin() {
        Intent googleSignIn = ApplicationController.googleSignInClient.getSignInIntent();
        startActivityForResult(googleSignIn, GOOGLE_SIGNIN_REQ);
    }

    private void SignInUser() {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            Toast.makeText(Signin.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            user = mAuth.getCurrentUser();
                            if (user != null) {
                                edt_Password.setError(null);
                                edt_Email.setError(null);
                                InitFCMToken(user);
                                CheckUserDataInDb(user);

                            }
                        } else {
                            edt_Email.setError("Wrong E-mail");
                            edt_Password.setError("Wrong password");
                            // If sign in fails, display a message to the user.
                            Log.d("TAG", "createUserWithEmail:failure", task.getException());
//                            Log.d("TAG", "createUserWithEmail:failure", task.getException().getMessage();
                            Toast.makeText(Signin.this, "Wrong Email or password.", Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
    }

    private boolean ValidateEmail() {
        email = edt_Email.getEditText().getText().toString().trim();
        if (email.isEmpty()) {
            edt_Email.setError("E-mail is required ");
            //edt_Email.isFocused();
        } else if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            edt_Email.setError("Invalid e-mail address");
        } else {
            edt_Email.setError(null);
            return true;
        }

        return false;
    }

    private boolean ValidatePassword() {
        password = edt_Password.getEditText().getText().toString().trim();
        if (password.isEmpty()) {
            edt_Password.setError("password is required ");
            //edt_password.isFocused();
        } else if (password.length() < 6) {
            edt_Password.setError("Wrong password ");

        } else {
            edt_Password.setError(null);
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void CheckCurrentUser() {
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            InitFCMToken(currentUser);
//            StartActivityFromMode();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GOOGLE_SIGNIN_REQ:
                try {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = task.getResult().getIdToken();
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d("TAG", "Name : " + account.getDisplayName());
                    if (idToken != null) {
                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                        mAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("TAG", "signInWithCredential:success " + task.getResult().getUser().getDisplayName());
                                            user = mAuth.getCurrentUser();
                                            InitFCMToken(user);
                                            if (user != null) {
                                                CheckUserDataInDb(user);
                                            }
                                        } else {
                                            Log.d("TAG", "signInWithCredential:failure", task.getException());
                                        }
                                    }
                                });
                    }
                } catch (Exception e) {
                    Log.d("TAG", "Error " + e.getMessage());
                }
                break;
        }
    }

    private void CheckUserDataInDb(FirebaseUser user) {
        String u_id = user.getUid();
        dialog.show();
        Task<DocumentSnapshot> userSnap = mDBRef.collection(getString(R.string.USERS)).document(u_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override

            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        CheckUserInfo(document);
                    } else {
                        mDocRef = mDBRef.collection(getString(R.string.USERS)).document(u_id);
                        mDocRef.set(new UserInfo(user.getDisplayName(), user.getEmail(), String.valueOf(user.getPhotoUrl()), mFCMToken, false))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        upDateSharedPreferences(user);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(Signin.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                                        Log.d("TAG", "Signin:Cannot insert data " + e.getMessage());
                                    }
                                });
                    }
                } else {
                    dialog.dismiss();
                    Log.d("TAG", "Failed with: ", task.getException());
                }
            }
        });
    }

//    public GoogleSignInClient SignInRequest() {
//        GoogleSignInOptions gso = new
//                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(this.getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        return GoogleSignIn.getClient(this, gso);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void InitFCMToken(FirebaseUser fbUser) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    mFCMToken = task.getResult();
                    if (!preferences.getUserFcm().equals(mFCMToken) || preferences.getUserFcm().equals("")) {
                        mDBRef.collection(getString(R.string.USERS)).document(fbUser.getUid()).update(getString(R.string.FCM_TOKEN), mFCMToken)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("TAG", "FCM Updated successfully");
                                        preferences.setUserFcm(mFCMToken);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("TAG", "Failed to update fcm " + e.getMessage());
                                    }
                                });
                    }
                    Log.d("TAG", " FCM Token " + task.getResult());


                } else {
                    Log.d("TAG", "Error getting FCM Token " + task.getException());

                }
            }
        });
    }

    private void upDateSharedPreferences(FirebaseUser user) {
        if (preferences.getUserId().equals("")) {
            preferences.setUserId(user.getUid());
        }
        if (preferences.getUserEmail().equals(""))
            preferences.setUserEmail(user.getEmail());
        if (preferences.getUserImageUri().equals(""))
            preferences.setUserImageUri(Objects.requireNonNull(user.getPhotoUrl()).toString());
        if (preferences.getUserOnlineImageUri().equals(""))
            preferences.setUserOnlineImageUri(Objects.requireNonNull(user.getPhotoUrl()).toString());
        if (preferences.getUserName().equals(""))
            preferences.setUserName(user.getDisplayName());
        DownloadImageFromPath(user.getPhotoUrl().toString(), preferences);

    }

    private void CheckUserInfo(DocumentSnapshot document) {
        UserInfo userInfo = document.toObject(UserInfo.class);
        preferences.setUserId(document.getId());
        assert userInfo != null;
        SaveDataFromFireBase(userInfo, preferences, document.getId());
//                                    SaveDataForNewUser(preferences, user);

    }

    private void SaveDataFromFireBase(UserInfo userInfo, MySharedPreferences preferences, String id) {
        preferences.setUserName(userInfo.getName());
        preferences.setUserEmail(userInfo.getEmail());
        preferences.setUserPhoneNo(userInfo.getPhone_no());
        preferences.setVehicleName(userInfo.getVehicle_name());
        preferences.setVehicleType(userInfo.getVehicle_type());
        preferences.setVehicleNo(userInfo.getVehicle_no());
        preferences.setProfileUpdated(userInfo.isProfile_updated());
        preferences.setUserOnlineImageUri(userInfo.getPhoto_uri());
        DownloadImageFromPath(userInfo.getPhoto_uri(), preferences);
        if (!userInfo.getFcm_token().equals(mFCMToken)) {
            mDBRef.collection(getString(R.string.USERS)).document(id).update(getString(R.string.FCM_TOKEN), mFCMToken)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("TAG", "FCM Token updated in db");
                            preferences.setUserFcm(mFCMToken);
                        }
                    });
        }
        updateTripList(id);
    }

    private void updateTripList(String id) {
        mDBRef.collection(getString(R.string.BOOKING_REQUEST)).whereEqualTo(getString(R.string.PASSENGER_ID), id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<TripRequestsData> tripRequestsDataList = new ArrayList<>();
                        for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                            BookingRequestInfo bookingRequestInfo = qds.toObject(BookingRequestInfo.class);
                            if (!bookingRequestInfo.isActive()) {
                                TripRequestsData tripRequestsData = new TripRequestsData();
                                tripRequestsData.TripId = bookingRequestInfo.getTrip_id();
                                tripRequestsDataList.add(tripRequestsData);
                            }
                        }
                        insertTripRequestData(tripRequestsDataList);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void insertTripRequestData(ArrayList<TripRequestsData> tripRequestsDataList) {
        TripRequestDao dao = db.tripRequestDao();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < tripRequestsDataList.size(); i++) {
                    dao.insertAll(tripRequestsDataList.get(i));
                }
            }
        }).start();
    }

    public void DownloadImageFromPath(String path, MySharedPreferences preferences) {
        Executor executors = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executors.execute(new Runnable() {
            @Override
            public void run() {//Do work in background
                InputStream in = null;
                Bitmap bmp = null;
//        ImageView iv = (ImageView)findViewById(R.id.img1);
                int responseCode = -1;
                try {

                    URL url = new URL(path);//"http://192.xx.xx.xx/mypath/img1.jpg
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoInput(true);
                    con.connect();
                    responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try {
                            //download
                            in = con.getInputStream();
                            bmp = BitmapFactory.decodeStream(in);
                            in.close();
                            String path = getExternalFilesDir("profile").getPath();
                            File file = new File(path + File.separator + "dp.png");
                            FileOutputStream fos = new FileOutputStream(file);
                            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.close();
                            preferences.setUserImageUri(file.getAbsolutePath());

                        } catch (Exception e) {
                            Log.d("TAG", "error saving file " + e.getMessage());
                        }
//                iv.setImageBitmap(bmp);
                    }

                } catch (Exception ex) {
                    Log.e("Exception", ex.toString());
                }
                handler.post(new Runnable() {

                    @Override
                    public void run() {//after execution runs on UI thread
                        dialog.dismiss();
                        AskForPermissions();
//                        StartActivityFromMode();
                    }
                });
                Log.d("TAG", "pre execute");
            }
        });

    }

    private void AskForPermissions() {
        if (isServicesOk()) {
            Log.d("TAG", "services ok");
            if (isGPSEnabled()) {
                Log.d("TAG", "gps is enabeled");
                if (CheckLocationPermission()) {
                    Log.d("TAG", "Ready to map");
                    if (Build.VERSION.SDK_INT >= 33) {
                        if (CheckNotificationPermission()) {
                            StartActivityFromMode();
                        }
                    } else {
                        StartActivityFromMode();
                    }
                } else {
                    ActivityCompat.requestPermissions(Signin.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                }
            }
        }
    }


    private boolean CheckLocationPermission() {

        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = 33)
    private boolean CheckNotificationPermission() {
        return true;
//        return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
//                PackageManager.PERMISSION_GRANTED;
    }

    private boolean isServicesOk() {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int result = googleApi.isGooglePlayServicesAvailable(this);
        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApi.isUserResolvableError(result)) {
            Dialog dialog = googleApi.getErrorDialog(this, result, PLAY_SERVICES_ERROR_CODE, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface task) {
                    Toast.makeText(getApplicationContext(), "dialog is cancelled by User", Toast.LENGTH_LONG).show();
                }
            });
            dialog.show();
        } else {
            Toast.makeText(this, "Play services are required by this application", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerEnabeled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (providerEnabeled) {
            return true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS Permission")
                    .setMessage("Gps is required for this app to work,please enable GPS")
                    .setPositiveButton("yes", (new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, GPS_REQUEST_CODE);
                        }
                    }))
                    .setCancelable(false)
                    .show();
        }
        return false;
    }

    private void requestLocationPermission() {
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("Location permission is required for this app to work")
                        .setPositiveButton("Ok", (new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(Signin.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                            }
                        }))
                        .setCancelable(false)
                        .show();
                Log.d("TAG", "permissions not granted2");
            }
        }
    }
//
//    @RequiresApi(api = 33)
//    private void requestNotificationPermission() {
//        {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
//                    PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(Signin.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_REQUEST_CODE);
//
//            }
//        }
//    }

    private void requestLocationPermissionFromSettings() {
        {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Location Permission")
                    .setMessage("Location permission is required for this app to work.You must grant location permission to continue using app")
                    .setPositiveButton("Ok", (new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        }
                    })).setNegativeButton("Close App", (new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }))
                    .setCancelable(false)
                    .show();
            Log.d("TAG", "permissions not granted2");

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                StartActivityFromMode();
            }
//        }
            else {
//            Log.d("TAG", "permission denied");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        requestLocationPermission();
                    } else {
                        requestLocationPermissionFromSettings();
                    }
                }
            }
        }
    }

    private void StartActivityFromMode() {
        String mode = preferences.getUserMode();
        if (mode.equals(getString(R.string.RIDER_MODE))) {
            startActivity(new Intent(Signin.this, MyRides.class));
            finish();
        } else if (mode.equals(getString(R.string.PASSENGER_MODE))) {
            startActivity(new Intent(Signin.this, UserSearchActivity.class));
            finish();
        } else if (!preferences.getUserId().equals("")) {
            startActivity(new Intent(Signin.this, SelectMode.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckVersion();
//        Log.d("TAG", "OnResumeCalled");
//        if (!CheckLocationPermission()) {
//            AskForPermissions();
//        }
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        ImageView btn_toolbar = toolbar.findViewById(R.id.iv_toolbar);
        btn_toolbar.setVisibility(View.INVISIBLE);
        TextView tv_title = toolbar.findViewById(R.id.tv_toolbar);
        tv_title.setText("Sign In");
        btn_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);
    }

    @Override
    public void restoreAds() {
        fl_ad.setVisibility(View.VISIBLE);
        margin.setVisibility(View.VISIBLE);
    }

    @Override
    public void closeAds() {
        fl_ad.setVisibility(View.INVISIBLE);
        margin.setVisibility(View.INVISIBLE);
    }


}