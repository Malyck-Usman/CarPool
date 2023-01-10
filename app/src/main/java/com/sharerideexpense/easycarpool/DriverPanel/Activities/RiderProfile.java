package com.sharerideexpense.easycarpool.DriverPanel.Activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sharerideexpense.easycarpool.Activities.ApplicationController;
import com.sharerideexpense.easycarpool.Activities.Signin;
import com.sharerideexpense.easycarpool.Ads.InterstitialAdSingleton;
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.classes.MySharedPreferences;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sharerideexpense.easycarpool.databinding.ActivityRiderProfileBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class RiderProfile extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private static final int READ_STORAGE_REQUEST_CODE = 1;
    private static final int REQUEST_PERMISSION_SETTING = 2;
    ActivityRiderProfileBinding binding;
    String name, phone_no, vehicle_name, vehicle_no;
    FirebaseFirestore mDBRef;
    StorageReference mStorageRef;
    Uri imageUri;
    private boolean imageOpened = false;
    MySharedPreferences prefs;
    String ImageUri;
    ProgressDialog mPDialog;
    String vehicle_type = "";

    ActivityResultLauncher<String> mImagePick = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                    if (uri != null) {
                        Glide.with(RiderProfile.this)
                                .load(uri)
                                .into(binding.ivRiderProfile);
                        imageUri = uri;
                        imageOpened = true;
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRiderProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Init();
    }

    private void Init() {
        binding.toolbar.tvToolbar.setText("My Profile");
        binding.toolbar.ivToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.ivEditProfilePic.setOnClickListener(this);
        binding.tvDeleteProfile.setOnClickListener(this);
        binding.btnSaveRiderProfile.setOnClickListener(this);
        prefs = new MySharedPreferences(this);
        mDBRef = FirebaseFirestore.getInstance();
        binding.edtNameRiderProfile.getEditText().setText(prefs.getUserName());
        if (!prefs.getUserImageUri().equals("")) {
            binding.ivRiderProfile.setImageURI(Uri.parse(prefs.getUserImageUri()));
        }
        binding.rgVehicleType.setOnCheckedChangeListener(this);
//        typeAdaptor.setDropDownViewResource(R.layout.item_spiner);
        if (!(prefs.getUserPhoneNo().equals(""))) {
            binding.edtPhoneNoRiderProfile.getEditText().setText(prefs.getUserPhoneNo());
        }
        if (!prefs.getVehicleName().equals("")) {
            binding.edtVehicleNameRiderProfile.getEditText().setText(prefs.getVehicleName());
        }
        if (!prefs.getVehicleNo().equals("")) {
            binding.edtVehicleNumber.getEditText().setText(prefs.getVehicleNo());
        }
        if (!prefs.getVehicleType().equals("")) {
            String vehicle = prefs.getVehicleType();
            if (prefs.getVehicleType().equals(getString(R.string.BIKE))) {
                binding.rgVehicleType.check(R.id.rb_bike);
            } else if (prefs.getVehicleType().equals(getString(R.string.CAR))) {
                binding.rgVehicleType.check(R.id.rb_car);

            } else if (prefs.getVehicleType().equals(getString(R.string.SUV))) {

                binding.rgVehicleType.check(R.id.rb_suv);
            }
        }
//        if (prefs.getProfileUpdated()) {
//            Objects.requireNonNull(binding.edtVehicleNumber.getEditText()).setEnabled(false);
//            Objects.requireNonNull(binding.rgVehicleType).setEnabled(false);
//            Objects.requireNonNull(binding.rbBike).setEnabled(false);
//            Objects.requireNonNull(binding.rbCar).setEnabled(false);
//            Objects.requireNonNull(binding.rbSuv).setEnabled(false);
//            Objects.requireNonNull(binding.edtVehicleNameRiderProfile.getEditText()).setEnabled(false);
//        }
        StorageReference mStorage = FirebaseStorage.getInstance().getReference("Images");
        mStorageRef = mStorage.child(prefs.getUserId() + ".png");
        mPDialog = new ProgressDialog(this);
        mPDialog.setTitle("Saving data");
        mPDialog.setMessage("Please wait . . .");
        mPDialog.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_edit_profile_pic:
                if (CheckStoragePermission()) {
                    openPicture();
                } else {
                    ActivityCompat.requestPermissions(RiderProfile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_REQUEST_CODE);
                }
                break;
            case R.id.btn_save_rider_profile:
                saveData();
                break;
            case R.id.tv_delete_profile:
                startDelete();
                break;
        }

    }

    private void startDelete() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Are you sure you want to delete your profile ?");
        dialog.setMessage("This will delete all your Rides,Bookings and data.\nThis action cannot be undone");
        dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DeleteProfile();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).show();
    }
    private void DeleteProfile() {
        String userId = prefs.getUserId();
        if (userId.equals("")) {
            Toast.makeText(this, "You have to login again to delete profile", Toast.LENGTH_SHORT).show();
        }
        mDBRef.collection(getString(R.string.TRIPS)).whereEqualTo(getString(R.string.DRIVER_ID), userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                        qds.getReference().delete();
                    }
                }).addOnFailureListener(e -> {
                    Log.d("TAG", "Cannot delete Trips " + e.getMessage());
                });
        mDBRef.collection(getString(R.string.BOOKING_REQUEST)).whereEqualTo(getString(R.string.DRIVER_ID), userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                        qds.getReference().delete();
                    }
                }).addOnFailureListener(e -> {
                    Log.d("TAG", "Cannot delete Driver's Booking Request " + e.getMessage());
                });
        mDBRef.collection(getString(R.string.BOOKING_REQUEST)).whereEqualTo(getString(R.string.PASSENGER_ID), userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                        qds.getReference().delete();
                    }
                }).addOnFailureListener(e -> {
                    Log.d("TAG", "Cannot delete Passenger's Booking Request " + e.getMessage());
                });
        mDBRef.collection(getString(R.string.USERS)).document(userId).delete()
                .addOnSuccessListener(unused -> Log.d("TAG", "User's Profile Deleted Successfully "))
                .addOnFailureListener(e -> Log.d("TAG", "Failed to delete user profile " + e.getMessage()));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("TAG", "User Authentication Deleted ");
                    prefs.clearData();
                    startActivity(new Intent(RiderProfile.this, Signin.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("TAG", "Failed to delete Authentication user " + e.getMessage());
                }
            });
        }
    }

    private void openPicture() {
        mImagePick.launch("image/*");
    }

    private void saveData() {
    ShowInterstitialAd();
        if (ValidateName() && ValidatePhoneNo() && ValidateVehicleName() && ValidateVehicleType() && ValidateVehicleNo()) {
            mPDialog.show();
            String name = binding.edtNameRiderProfile.getEditText().getText().toString();
            String phone_no = binding.edtPhoneNoRiderProfile.getEditText().getText().toString();
            String vehicle_name = binding.edtVehicleNameRiderProfile.getEditText().getText().toString();
            String vehicle_no = binding.edtVehicleNumber.getEditText().getText().toString();
            if (imageOpened) {
                binding.ivRiderProfile.setDrawingCacheEnabled(true);
                binding.ivRiderProfile.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) binding.ivRiderProfile.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = mStorageRef.putBytes(data);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return mStorageRef.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null)
                                prefs.setUserOnlineImageUri(task.getResult().toString());
                            ImageUri = task.getResult().toString();
                            String path = getExternalFilesDir("profile").getPath();
                            File file = new File(path + File.separator + "dp.png");
                            try {
                                FileOutputStream fos = null;//save image to device private storage
                                fos = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                fos.close();
                                prefs.setUserImageUri(file.getAbsolutePath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (!prefs.getUserId().equals("")) {
                                saveUserData(name, phone_no, vehicle_type, vehicle_name, vehicle_no, ImageUri, true);
                            } else {
                                Toast.makeText(RiderProfile.this, "You need to login to continue this operation", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Log.d("TAG", "Failed to update data " + task.getException().toString());
                            // Handle failures
                            // ...
                        }
                    }
                });

            } else {
                if (!prefs.getUserId().equals("")) {

                    saveUserData(name, phone_no, vehicle_type, vehicle_name, vehicle_no, null, true);
                } else {
                    Toast.makeText(this, "You need to login to continue this operation", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveUserData(String name, String phone_no, String vehicle_type, String vehicle_name, String vehicle_no, String imageUri, boolean profile_updated) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("phone_no", phone_no);
        map.put("vehicle_type", vehicle_type);
        map.put("vehicle_name", vehicle_name);
        map.put("vehicle_no", vehicle_no);
        map.put("profile_updated", profile_updated);
        if (imageUri != null) {
            map.put("photo_uri", imageUri);
        }
        mDBRef.collection(getString(R.string.USERS)).document(prefs.getUserId()).update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        prefs.setUserName(name);
                        prefs.setUserPhoneNo(phone_no);
                        prefs.setVehicleNo(vehicle_no);
                        prefs.setVehicleType(vehicle_type);
                        prefs.setVehicleName(vehicle_name);
                        prefs.setProfileUpdated(true);
                        mPDialog.dismiss();
                        startActivity(new Intent(RiderProfile.this, MyRides.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RiderProfile.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private boolean ValidateName() {
        name = binding.edtNameRiderProfile.getEditText().getText().toString().trim();
        if (name.isEmpty()) {
            binding.edtNameRiderProfile.setError("Rider name is required ");
            //edt_Email.isFocused();
        } else {
            binding.edtNameRiderProfile.setError(null);
            return true;
        }

        return false;
    }

    private boolean ValidateVehicleName() {
        vehicle_name = binding.edtVehicleNameRiderProfile.getEditText().getText().toString().trim();
        if (vehicle_name.isEmpty()) {
            binding.edtVehicleNameRiderProfile.setError("Vehicle name is required ");
            //edt_Email.isFocused();
        } else {
            binding.edtVehicleNameRiderProfile.setError(null);
            return true;
        }

        return false;
    }

    private boolean ValidateVehicleNo() {
        vehicle_no = binding.edtVehicleNumber.getEditText().getText().toString().trim();
        if (vehicle_no.isEmpty()) {
            binding.edtVehicleNumber.setError("vehicle no is required ");
            //edt_Email.isFocused();
        } else {
            binding.edtVehicleNumber.setError(null);
            return true;
        }

        return false;
    }

    private boolean ValidatePhoneNo() {
        phone_no = binding.edtPhoneNoRiderProfile.getEditText().getText().toString().trim();
        if (phone_no.isEmpty()) {
            binding.edtPhoneNoRiderProfile.setError("Mobile number is required ");
            //edt_Phone.isFocused();
        } else if (phone_no.length() != 11) {
            binding.edtPhoneNoRiderProfile.setError("Mobile number not valid ");
        } else {
            binding.edtPhoneNoRiderProfile.setError(null);
            return true;
        }

        return false;
    }

    private boolean ValidateVehicleType() {
        return !vehicle_type.equals("");

    }

    private boolean CheckStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("Storage Permission")
                        .setMessage("Storage permission is required for this app to work")
                        .setPositiveButton("Ok", (new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(RiderProfile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_REQUEST_CODE);
                            }
                        }))
                        .setCancelable(false)
                        .show();
                Log.d("TAG", "permissions not granted2");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_STORAGE_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            Log.d("TAG", "permission granted");
            openPicture();
        } else {
//            Log.d("TAG", "permission denied");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                if (showRationale) {
                    requestStoragePermission();
                } else {
                    requestStoragePermissionFromSettings();

                }
            }
        }
    }

    private void requestStoragePermissionFromSettings() {
        {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Storage Permission")
                    .setMessage("Storage permission is required for this feature to work.")
                    .setPositiveButton("Ok", (new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        }
                    })).setNeutralButton("Cancel", (new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }))
                    .setCancelable(true)
                    .show();

        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_bike:
                vehicle_type = "Bike";
                break;
            case R.id.rb_car:
                vehicle_type = "Car";
                break;
            case R.id.rb_suv:
                vehicle_type = "SUV";
                break;

        }
    }
    private void ShowInterstitialAd() {
            ApplicationController.adPos = 0;
            InterstitialAdSingleton.getInstance(this).showInterstitial(this);

    }
}