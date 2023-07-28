package com.example.groceryappp.Activity.Fragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import static com.example.groceryappp.Activity.Activity.LoginActivity.mGoogleSignInClient;
import static com.example.groceryappp.Activity.Utills.ProgressDialogUtils.hideProgressDialog;
import static com.example.groceryappp.Activity.Utills.ProgressDialogUtils.showProgressDialog;
import static com.example.groceryappp.Activity.Utills.SharedPreferenceManager.USER_INFO_PREF_NAME;
import static com.example.groceryappp.Activity.Utills.SharedPreferenceManager.getInstance;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.groceryappp.Activity.Activity.AdminEditShopDetails;
import com.example.groceryappp.Activity.Activity.LoginActivity;
import com.example.groceryappp.Activity.Activity.UserOrderHeaderActivity;
import com.example.groceryappp.Activity.Adapter.MixVegPriceDetails;
import com.example.groceryappp.Activity.AllModel.UserInfo;
import com.example.groceryappp.Activity.Firebase.FirebaseClient;
import com.example.groceryappp.Activity.Utills.SharedPreferenceManager;
import com.example.groceryappp.R;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private double lat, lng;
    private Uri newUri;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BottomSheetDialog dialogBottom;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private AppCompatButton edprofilee, currentAddress, btnUpdate, updateProfile;
    private String City, Street, Pin, Landmark, FloorNo, fullAd;
    private MaterialButton login;
    private ProgressBar pgbar, pgbarBottom;

    private CircleImageView profile, circleImage;
    private TextInputEditText userName, userNumber;
    private TextView name, email, number, fullAdress, logout, orders, fulladdDial;
    private String  userType, uri , userId;
    private ImageView edAdres, back;
    View view;
    private UserInfo listInfo;
    private SharedPreferenceManager sharedPreferenceManager;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      view = inflater.inflate(R.layout.fragment_profile, container, false);

        ////
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        ////

        viewInitialize(view);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.findViewById(R.id.pgbarHome).setVisibility(View.GONE);
            }
        },1000);
        ////checking if user then show ordrs unless hide ordres
        sharedPreferenceManager= SharedPreferenceManager.getInstance(getContext());
        SharedPreferences preferences=getContext().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
       /* if (sharedPreferenceManager!=null&&preferences.contains(USER_INFO_PREF_NAME)){
            listInfo=sharedPreferenceManager.retrieveUserInfoSharedP(getContext(),USER_INFO_PREF_NAME);
        }*/
        swipeRefreshLayout = view.findViewById(R.id.swiperRefresh);
        if (sharedPreferenceManager != null && preferences.contains("USER_ID")) {
            userId=SharedPreferenceManager.getInstance(getContext()).getUserId();
        }
        swipeRefreshLayout.setColorSchemeColors(getContext().getColor(R.color.buttonBg));
        //swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getContext().getColor(R.color.bg2));
        view.findViewById(R.id.editshopDetials).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AdminEditShopDetails.class);
                startActivity(intent);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchingUserDetail();
                swipeRefreshLayout.setRefreshing(false);
                // Perform your refresh action here
              /*  new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Stop the refreshing animation
                    }
                }, 2000);*/
            }
        });

        if (userId == null) {
            view.findViewById(R.id.loginScreen).setVisibility(View.VISIBLE);
            view.findViewById(R.id.root22).setVisibility(View.GONE);
            view.findViewById(R.id.edprofile).setVisibility(View.GONE);
            view.findViewById(R.id.root3).setVisibility(View.GONE);
            view.findViewById(R.id.relativeRoot).setBackgroundColor(Color.WHITE);
        }
        // userType =getActivity().getIntent().getStringExtra("userType");
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog1 = new AlertDialog.Builder(getContext());
                dialog1.setIcon(R.drawable.baseline_logout);
                dialog1.setTitle("Logout").setMessage("Are you sure ?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("USER_ID");
                        editor.apply();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (mGoogleSignInClient != null) {
                            mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });
                        }
                        startActivity(intent);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        });
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserOrderHeaderActivity.class);
                intent.putExtra("orderType", "user");
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        edprofilee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditProfileBottomSheet(listInfo.getName(), listInfo.getNumber(), listInfo.getProfilePic());
                edprofilee.setEnabled(false);
            }
        });
        edAdres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog(fullAdress.getText().toString());
            }
        });
        return view;
    }

    private void viewInitialize(View view) {
        number = view.findViewById(R.id.user_number);
        profile = view.findViewById(R.id.user_profile);
        email = view.findViewById(R.id.user_email);
        name = view.findViewById(R.id.user_name);
        fullAdress = view.findViewById(R.id.fulladress);
        edAdres = view.findViewById(R.id.edAdress);
        orders = view.findViewById(R.id.orders);
        edprofilee = view.findViewById(R.id.edprofile);
        back = view.findViewById(R.id.back);
        logout = view.findViewById(R.id.logout);
        pgbar = view.findViewById(R.id.pgbar);
        login = view.findViewById(R.id.login);

    }

    private void fetchingUserDetail() {
        if (userId != null) {
            FirebaseClient.getInstance().collection("CurrentUser").document(userId).get().addOnSuccessListener(
                    new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            listInfo=documentSnapshot.toObject(UserInfo.class);
                            uri=listInfo.getProfilePic();
                            listInfo=documentSnapshot.toObject(UserInfo.class);
                            fullAdress.setText(listInfo.getFullAd());
                            if (listInfo.getProfilePic() != null) {
                                Glide.with(getContext()).load(listInfo.getProfilePic()).into(profile);
                                pgbar.setVisibility(View.GONE);
                            }
                            number.setText(listInfo.getNumber());
                            email.setText(listInfo.getEmail());
                            name.setText(listInfo.getName());
                        }
                    });
        } else {

            view.findViewById(R.id.loginScreen).setVisibility(View.VISIBLE);
            view.findViewById(R.id.root22).setVisibility(View.GONE);
            view.findViewById(R.id.edprofile).setVisibility(View.GONE);
            view.findViewById(R.id.root3).setVisibility(View.GONE);
            view.findViewById(R.id.relativeRoot).setBackgroundColor(Color.WHITE);

        }
    }

    private void showBottomSheetDialog(String fulladd) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheetadresschange, null);
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(view);
        dialog.show();
        dialog.setCancelable(false);
        currentAddress = view.findViewById(R.id.current_Address);
        btnUpdate = view.findViewById(R.id.btn_adress_update);
        fulladdDial = view.findViewById(R.id.fullAddress);
        fulladdDial.setText(fulladd);
        ImageView cancel = view.findViewById(R.id.cancel);
        // Set your desired image to the ImageView
        // imageView.setImageResource(R.drawable.your_image);
        btnUpdate.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();

            }
        });
        currentAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(getContext()).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {

                            showProgressDialog(getContext(), "Fetching Location....");
                            fetchLastLocation();

///first create user authentication in firebase,so that we can login next time

                        } else {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, 101);
                            Toast.makeText(getContext(), "Please allow permission", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog(getContext(), "Updating Address...");
                FirebaseClient.getInstance().collection("CurrentUser").document(userId).update("fullAd", fulladdDial.getText()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        fullAdress.setText(fulladdDial.getText());
                        hideProgressDialog();
                        dialog.cancel();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }
        });
    }


    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            lat = location.getLatitude();
                            lng = location.getLongitude();
                            // Use the latitude and longitude values
                            // for your desired purpose
                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                            List<Address> list;
                            try {
                                list = geocoder.getFromLocation(lat, lng, 1);
                                ///GETTING ADRESS
                                City = list.get(0).getLocality();
                                Pin = list.get(0).getPostalCode();
                                Street = list.get(0).getThoroughfare() + "," + list.get(0).getSubLocality();
                                Landmark = list.get(0).getLocality();
                                FloorNo = list.get(0).getFeatureName();
                                fullAd = FloorNo + "," + Street + "," + Landmark + "," + City + "," + Pin;
                                fulladdDial.setText(fullAd);
                                btnUpdate.setEnabled(true);
                                currentAddress.setVisibility(View.GONE);
                                int color = Color.parseColor("#19543E");
                                ColorStateList colorStateList = ColorStateList.valueOf(color);
                                btnUpdate.setBackgroundTintList(colorStateList);
                                btnUpdate.setVisibility(View.VISIBLE);
                                hideProgressDialog();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }
                });
    }

    private void showEditProfileBottomSheet(String nameS, String numberS, String uriS) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.userprofilebottomsheet, null);
        dialogBottom = new BottomSheetDialog(getContext());
        dialogBottom.setContentView(view);
        dialogBottom.show();
        dialogBottom.setCancelable(false);
        circleImage = view.findViewById(R.id.profile);
        userName = view.findViewById(R.id.ed_name);
        userNumber = view.findViewById(R.id.ed_number);
        updateProfile = view.findViewById(R.id.update_profile);
        pgbarBottom = view.findViewById(R.id.pgbar);
        ImageView cancel = view.findViewById(R.id.cancel);
        userName.setText(nameS);
        userNumber.setText(numberS);
        if (uriS != null) {
            Glide.with(getContext()).load(uriS).into(circleImage);
            pgbarBottom.setVisibility(View.GONE);
        } else {
            pgbarBottom.setVisibility(View.GONE);
        }


        // Set your desired image to the ImageView
        // imageView.setImageResource(R.drawable.your_image);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBottom.cancel();
                edprofilee.setEnabled(true);
            }
        });
        circleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(getContext()).withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            showOptionDialog();
                        } else {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, 101);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

            }
        });
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userName.getText() == null) {
                    userName.setError("Name Required");
                    return;

                }
                if (userNumber.getText() == null) {
                    userNumber.setError("Number Required");
                    return;
                }   if (userNumber.getText().length() != 10) {
                    userNumber.setError("Number Should 10 digit");
                    return;
                }
                if (circleImage.getDrawable() == null) {
                    Toast.makeText(getContext(), "Please select profile pic", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialogBottom.cancel();
                updateUser();
            }
        });

    }

    private void showOptionDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        String item[] = {"Upload from Gallery"};
        alert.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 8);

            }
        }).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 6) {
                pgbar.setVisibility(View.VISIBLE);
                String nameUser = data.getStringExtra("name");
                String numberUser = data.getStringExtra("number");
                String uriPic = data.getStringExtra("IMAGE");
                name.setText(nameUser);
                number.setText(numberUser);
                if (uriPic != null) {
                    Glide.with(getContext()).load(uriPic).into(profile);
                    pgbar.setVisibility(View.GONE);
                }
            }
            if (requestCode == 8) {
                pgbarBottom.setVisibility(View.VISIBLE);
                newUri = data.getData();
                Glide.with(getContext()).load(newUri).into(circleImage);
                pgbarBottom.setVisibility(View.GONE);
            }
            if (requestCode == 2) {
                String nameUser = data.getStringExtra("name");
                String numberUser = data.getStringExtra("number");
                name.setText(nameUser);
                number.setText(numberUser);
            }
            if (resultCode == 90) {
                fullAdress.setText(data.getStringExtra("FULL_ADDRS"));
            }
        }
        if (resultCode == RESULT_CANCELED) {
            return;
        }

    }

    private void updateUser() {
        showProgressDialog(getContext(), "Updating User Details...");
        if (newUri != null) {
            String filePathName = "/profile/" + userId;
            StorageReference reference = FirebaseStorage.getInstance().getReference(filePathName);
            reference.putFile(newUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                    while (!task.isSuccessful()) ;
                    Uri downloadUri = task.getResult();
                    if (task.isSuccessful()) {
                        FirebaseClient.getInstance().collection("CurrentUser").document(userId).update("name", userName.getText().toString(), "number", userNumber.getText().toString(), "profilePic", downloadUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                number.setText(userNumber.getText().toString());
                                name.setText(userName.getText().toString());
                                pgbarBottom.setVisibility(View.GONE);
                                if (newUri != null) {
                                    Glide.with(getContext()).load(newUri).into(profile);
                                    pgbarBottom.setVisibility(View.GONE);
                                }
                                Glide.with(getContext()).load(uri).into(profile);
                                hideProgressDialog();
                                edprofilee.setEnabled(true);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                hideProgressDialog();
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            FirebaseClient.getInstance().collection("CurrentUser").document(userId).update("name", userName.getText().toString(), "number", userNumber.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    number.setText(userNumber.getText().toString());
                    name.setText(userName.getText().toString());
                    pgbarBottom.setVisibility(View.GONE);
                    Glide.with(getContext()).load(uri).into(profile);
                    hideProgressDialog();
                    edprofilee.setEnabled(true);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressDialog();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        userType = getArguments().getString("USER_TYPE");
        if (userType != null) {
            if (userType.equals("Seller")) {
                view.findViewById(R.id.orderLayout).setVisibility(View.GONE);
                view.findViewById(R.id.editshopDetialsLayout).setVisibility(View.VISIBLE);
                if (listInfo!=null){
                    retriveFromLocalDataBase();
                }
                fetchingUserDetail();
            }
            if (userType.equals("User")) {
                if (listInfo!=null){
                    retriveFromLocalDataBase();
                }
                fetchingUserDetail();
            }



        } else {
                if (listInfo!=null){
                    retriveFromLocalDataBase();
                }
                fetchingUserDetail();

        }
    }

    private void retriveFromLocalDataBase() {
        number.setText(listInfo.getNumber());
        email.setText(listInfo.getEmail());
        name.setText(listInfo.getName());
        fullAdress.setText(listInfo.getFullAd());
        if (listInfo.getProfilePic() != null) {
            Glide.with(getContext()).load(Uri.parse(listInfo.getProfilePic())).into(profile);
            pgbar.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(getContext(), "pauseprofileHomeFreagment", Toast.LENGTH_SHORT).show();
        Log.e("frag", "destroyprofileHomeFreagment");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("frag", "pause");
    }
}