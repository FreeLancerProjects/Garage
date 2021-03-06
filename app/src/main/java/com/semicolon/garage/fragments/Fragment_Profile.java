package com.semicolon.garage.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.semicolon.garage.R;
import com.semicolon.garage.activities.HomeActivity;
import com.semicolon.garage.adapters.CityDialogAdapter;
import com.semicolon.garage.adapters.NationalityDialogAdapter;
import com.semicolon.garage.models.CityModel;
import com.semicolon.garage.models.Country_Nationality;
import com.semicolon.garage.models.UserModel;
import com.semicolon.garage.preferences.Preferences;
import com.semicolon.garage.remote.Api;
import com.semicolon.garage.share.Common;
import com.semicolon.garage.singletone.UserSingleTone;
import com.semicolon.garage.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.semicolon.garage.tags.Tags.update_email;
import static com.semicolon.garage.tags.Tags.update_phone;

public class Fragment_Profile extends Fragment{
    private static final String TAG = "user_data";
    private RoundedImageView image,image_doc;
    private KenBurnsView image_bg;
    private ImageView image_update_photo;
    private TextView tv_name,tv_phone,tv_email, tv_city,tv_nationality;
    private LinearLayout ll_update_name,ll_update_image_doc;
    private CardView cardView_update_phone,cardView_update_email,cardView_update_password,cardView_update_city,cardView_update_nationality;
    private final  int img1_req_profile=12,img2_req_doc=13;
    private ProgressDialog updateImagedialog;
    private UserModel userModel;
    private String lang;
    private AlertDialog updatedialog;
    private ProgressDialog progressDialog;
    private UserSingleTone userSingleTone;
    private Preferences preferences;
    private AlertDialog cityDialog;
    private AlertDialog country_NationalityDialog;
    private List<Country_Nationality> country_nationalityList;
    private List<CityModel> cityModelList;
    private HomeActivity homeActivity;
    private final String read_per = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final int read_req = 102,read_req_2=103;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        initView(view);
        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            userModel = (UserModel) bundle.getSerializable(TAG);
            UpdateUi(userModel);
        }
        return view;
    }

    public static Fragment_Profile getInstance(UserModel userModel)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,userModel);
        Fragment_Profile fragment = new Fragment_Profile();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initView(View view)
    {
        homeActivity = (HomeActivity) getActivity();
        userSingleTone = UserSingleTone.getInstance();
        preferences = Preferences.getInstance();
        Paper.init(getActivity());
        lang = Paper.book().read("language");
        country_nationalityList = new ArrayList<>();
        cityModelList = new ArrayList<>();
        image = view.findViewById(R.id.image);
        image_doc = view.findViewById(R.id.image_doc);
        image_bg = view.findViewById(R.id.image_bg);
        image_update_photo = view.findViewById(R.id.image_update_photo);
        tv_name = view.findViewById(R.id.tv_name);
        tv_phone = view.findViewById(R.id.tv_phone);
        tv_email = view.findViewById(R.id.tv_email);
        tv_city = view.findViewById(R.id.tv_city);
        tv_nationality = view.findViewById(R.id.tv_nationality);
        ll_update_name = view.findViewById(R.id.ll_update_name);
        ll_update_image_doc = view.findViewById(R.id.ll_update_image_doc);
        cardView_update_phone = view.findViewById(R.id.cardView_update_phone);
        cardView_update_email = view.findViewById(R.id.cardView_update_email);
        cardView_update_password = view.findViewById(R.id.cardView_update_password);
        cardView_update_city = view.findViewById(R.id.cardView_update_city);
        cardView_update_nationality = view.findViewById(R.id.cardView_update_nationality);


        image_update_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission(read_req);

            }
        });

        ll_update_image_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission(read_req_2);

            }
        });

        ll_update_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog_UpdateProfile(Tags.update_full_name);
            }
        });
        cardView_update_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog_UpdateProfile(Tags.update_email);
            }
        });

        cardView_update_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateAlertDialog_UpdateProfile(Tags.update_phone);
            }
        });

        cardView_update_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog_UpdateProfile(Tags.update_password);
            }
        });

        cardView_update_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView_update_city.setEnabled(false);
                if (cityModelList.size()>0)
                {
                    createCityDialog(cityModelList);
                }else
                {
                    getCities();
                }
            }
        });

        cardView_update_nationality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView_update_nationality.setEnabled(false);

                if (country_nationalityList.size()>0)
                {
                    createNationalityDialog(country_nationalityList);
                }else
                {
                    getNationality();
                }
            }
        });
    }

    private void CheckPermission(int req)
    {
        if (ContextCompat.checkSelfPermission(getActivity(),read_per)!= PackageManager.PERMISSION_GRANTED)
        {
            String [] perm = {read_per};
            ActivityCompat.requestPermissions(getActivity(),perm,req);
        }else
            {
                if (req==read_req)
                {
                    SelectImage(img1_req_profile);

                }else if (req==read_req_2)
                {
                    SelectImage(img2_req_doc);

                }
            }
    }
    private void UpdateUi(UserModel userModel) {
        Log.e("name",userModel.getUser_full_name());
        Log.e("phone",userModel.getUser_phone());
        Log.e("email",userModel.getUser_email());
        Log.e("country",userModel.getAr_name()+" _ "+userModel.getEn_name());
        Log.e("nationality",userModel.getAr_nationality()+" _ "+userModel.getEn_nationality());

        Picasso.with(getActivity()).load(Uri.parse(Tags.IMAGE_URL+userModel.getUser_photo())).into(image);
        Picasso.with(getActivity()).load(Uri.parse(Tags.IMAGE_URL+userModel.getUser_photo())).into(image_bg);
        Picasso.with(getActivity()).load(Uri.parse(Tags.IMAGE_URL+userModel.getDocument_type_definition())).into(image_doc);

        tv_name.setText(userModel.getUser_full_name());
        tv_email.setText(userModel.getUser_email());
        tv_phone.setText(userModel.getUser_phone());

        if (lang.equals("ar"))
        {
            tv_city.setText(userModel.getAr_city_title());
            tv_nationality.setText(userModel.getAr_nationality());
        }else if (lang.equals("en"))
        {
            tv_city.setText(userModel.getEn_city_title());
            tv_nationality.setText(userModel.getEn_nationality());

        }


    }



    private void createCityDialog(List<CityModel> cityModelList)
    {
        cityDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .create();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.city_dialog,null);
        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.choose_cities));
        RecyclerView recView  = view.findViewById(R.id.recView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        RecyclerView.Adapter adapter = new CityDialogAdapter(getActivity(),cityModelList,lang,this);
        recView.setLayoutManager(manager);
        recView.setAdapter(adapter);

        cityDialog.getWindow().getAttributes().windowAnimations = R.style.dialog;
        cityDialog.setView(view);
        cityDialog.show();
        cardView_update_city.setEnabled(true);



    }

    private void createNationalityDialog(List<Country_Nationality> country_nationalityList)
    {
        country_NationalityDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .create();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.city_dialog,null);
        TextView tv_title  = view.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.ch_nationality));
        RecyclerView recView  = view.findViewById(R.id.recView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        RecyclerView.Adapter adapter = new NationalityDialogAdapter(getActivity(),country_nationalityList,lang,this);
        recView.setLayoutManager(manager);
        recView.setAdapter(adapter);

        country_NationalityDialog.getWindow().getAttributes().windowAnimations = R.style.dialog;
        country_NationalityDialog.setView(view);
        country_NationalityDialog.show();
        cardView_update_nationality.setEnabled(true);




    }
    private void CreateAlertDialog_UpdateProfile(final String type)
    {
        updatedialog = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_update_profile,null);
        final TextView tv_title = view.findViewById(R.id.tv_title);
        final EditText edt_update = view.findViewById(R.id.edt_update);
        final EditText edt_newPassword = view.findViewById(R.id.edt_newPassword);
        Button btn_update = view.findViewById(R.id.btn_update);
        Button btn_close = view.findViewById(R.id.btn_close);

        if (type.equals(Tags.update_full_name))
        {
            tv_title.setText(R.string.upd_name);
            edt_update.setInputType(InputType.TYPE_CLASS_TEXT);
            edt_newPassword.setVisibility(View.GONE);
            edt_update.setHint(R.string.name);
            if (userModel!=null)
            {
                edt_update.setText(userModel.getUser_full_name());


            }
        }else if (type.equals(update_phone))
        {
            tv_title.setText(R.string.upd_phone);
            edt_update.setInputType(InputType.TYPE_CLASS_PHONE);
            edt_newPassword.setVisibility(View.GONE);
            edt_update.setHint(R.string.phone_number);
            if (userModel!=null)
            {
                edt_update.setText(userModel.getUser_phone());


            }
        }else if (type.equals(update_email))
        {
            tv_title.setText(R.string.upd_email);
            edt_update.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            edt_newPassword.setVisibility(View.GONE);
            edt_update.setHint(R.string.email);
            if (userModel!=null)
            {
                edt_update.setText(userModel.getUser_email());


            }
        }else if (type.equals(Tags.update_password))
        {

            tv_title.setText(R.string.upd_password);
            edt_update.setTransformationMethod(PasswordTransformationMethod.getInstance());
            edt_newPassword.setVisibility(View.VISIBLE);
            edt_update.setHint(R.string.old_pass);

        }

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatedialog.dismiss();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals(Tags.update_full_name))
                {
                    String m_name = edt_update.getText().toString();
                    if (!TextUtils.isEmpty(m_name))
                    {
                        edt_update.setError(null);

                        Common.CloseKeyBoard(getActivity(),edt_update);
                        progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.uptng_name));
                        progressDialog.show();
                        update_name(m_name);

                    }else
                    {
                        edt_update.setError(getString(R.string.name_req));
                    }

                }else if (type.equals(update_phone))
                {

                    String m_phone = edt_update.getText().toString();

                    if (TextUtils.isEmpty(m_phone))
                    {
                        edt_update.setError(getString(R.string.phone_req));


                    }else if (!Patterns.PHONE.matcher(m_phone).matches()||m_phone.length()<6||m_phone.length()>13)
                    {
                        edt_update.setError(getString(R.string.inv_phone));

                    }
                    else
                    {
                        Common.CloseKeyBoard(getActivity(),edt_update);
                        edt_update.setError(null);
                        progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.uptng_phone));
                        progressDialog.show();
                        update_phone(m_phone);

                    }
                }else if (type.equals(update_email))
                {

                    String m_email = edt_update.getText().toString();

                    if (TextUtils.isEmpty(m_email))
                    {
                        edt_update.setError(getString(R.string.email_req));

                    }else if (!Patterns.EMAIL_ADDRESS.matcher(m_email).matches())
                    {
                        edt_update.setError(getString(R.string.inv_email));

                    }else
                    {
                        Common.CloseKeyBoard(getActivity(),edt_update);

                        edt_update.setError(null);
                        progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.uptng_email));
                        progressDialog.show();
                        update_email(m_email);

                    }

                }else if (type.equals(Tags.update_password))
                {

                    Log.e("upd","password");
                    String m_oldPassword = edt_update.getText().toString();
                    String m_newPassword = edt_newPassword.getText().toString();

                    if (!TextUtils.isEmpty(m_oldPassword)&&!TextUtils.isEmpty(m_newPassword))
                    {
                        Common.CloseKeyBoard(getActivity(),edt_update);
                        edt_update.setError(null);
                        edt_newPassword.setError(null);

                        progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.updtng_pass));
                        progressDialog.show();
                        update_Password(m_oldPassword,m_newPassword);

                    }else
                    {
                        if (TextUtils.isEmpty(m_oldPassword))
                        {
                            edt_update.setError(getString(R.string.pass_req));

                        }else
                        {
                            edt_update.setError(null);

                        }

                        if (TextUtils.isEmpty(m_newPassword))
                        {
                            edt_newPassword.setError(getString(R.string.newpass_req));

                        }
                        else
                        {
                            edt_newPassword.setError(null);

                        }

                    }





                }
            }
        });
        updatedialog.getWindow().getAttributes().windowAnimations=R.style.dialog;
        updatedialog.setView(view);
        updatedialog.show();




    }

    private void getCities()
    {
        Api.getService()
                .getCity()
                .enqueue(new Callback<List<CityModel>>() {
                    @Override
                    public void onResponse(Call<List<CityModel>> call, Response<List<CityModel>> response) {
                        if (response.isSuccessful())
                        {
                            cityModelList.clear();
                            cityModelList.addAll(response.body());

                            createCityDialog(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CityModel>> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void getNationality()
    {
        Api.getService()
                .getCountries_Nationality()
                .enqueue(new Callback<List<Country_Nationality>>() {
                    @Override
                    public void onResponse(Call<List<Country_Nationality>> call, Response<List<Country_Nationality>> response) {
                        if (response.isSuccessful())
                        {
                            country_nationalityList.clear();
                            country_nationalityList.addAll(response.body());
                            createNationalityDialog(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Country_Nationality>> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void SelectImage(int img_req)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        getActivity().startActivityForResult(intent.createChooser(intent,"Select Image"),img_req);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==img1_req_profile&&resultCode== Activity.RESULT_OK&&data!=null)
        {
            Uri uri = data.getData();
            UpdateImage(uri,"user_photo");

        }else if (requestCode==img2_req_doc&&resultCode== Activity.RESULT_OK&&data!=null)
        {
            Uri uri = data.getData();
            UpdateImage(uri,"document_type_definition");


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==read_req)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]!=PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getActivity(),R.string.per_den, Toast.LENGTH_SHORT).show();
                }else
                    {
                        SelectImage(img1_req_profile);


                    }
            }
        }else if (requestCode == read_req_2)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]!=PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getActivity(),R.string.per_den, Toast.LENGTH_SHORT).show();
                }else
                    {
                        SelectImage(img2_req_doc);

                    }
            }
        }
    }
    private void UpdateImage(Uri uri,String part_name) {

        updateImagedialog = Common.createProgressDialog(getActivity(),getString(R.string.upd_img));
        updateImagedialog.show();

        RequestBody name_part = Common.getRequestBodyText(userModel.getUser_full_name());
        RequestBody email_part = Common.getRequestBodyText(userModel.getUser_email());
        RequestBody phone_part = Common.getRequestBodyText(userModel.getUser_phone());
        RequestBody nationality_part = Common.getRequestBodyText(userModel.getId_country());
        RequestBody city_part = Common.getRequestBodyText(userModel.getCountry_personal_proof());
        MultipartBody.Part image_part =Common.getMultiPart(getActivity(),uri,part_name);

        Api.getService()
                .updateImage(userModel.getUser_id(),phone_part,nationality_part,email_part,name_part,city_part,image_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        if (response.isSuccessful())
                        {
                            updateImagedialog.dismiss();
                            if (response.body().getSuccess_update()==1)
                            {
                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.upd_succ, Toast.LENGTH_SHORT).show();
                            }
                            else if (response.body().getSuccess_update()==2)
                            {
                                Toast.makeText(getActivity(), R.string.em_ph_exist, Toast.LENGTH_SHORT).show();

                            }                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                        updateImagedialog.dismiss();
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();

                    }
                });



    }

    private void update_name(String newName)
    {
        Log.e("name",newName);

        RequestBody name_part = Common.getRequestBodyText(newName);
        RequestBody email_part = Common.getRequestBodyText(userModel.getUser_email());
        RequestBody phone_part = Common.getRequestBodyText(userModel.getUser_phone());
        RequestBody nationality_part = Common.getRequestBodyText(userModel.getId_country());
        RequestBody city_part = Common.getRequestBodyText(userModel.getCountry_personal_proof());


        Api.getService().updateProfileData(userModel.getUser_id(),phone_part,nationality_part,email_part,name_part,city_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();
                            if (response.body().getSuccess_update()==1)
                            {
                                updatedialog.dismiss();
                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.upd_succ, Toast.LENGTH_SHORT).show();
                            }
                            else if (response.body().getSuccess_update()==2)
                            {
                                Toast.makeText(getActivity(), R.string.em_ph_exist, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("Error",t.getMessage());
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void update_phone(String newPhone)
    {
        Log.e("phone",newPhone);
        RequestBody name_part = Common.getRequestBodyText(userModel.getUser_full_name());
        RequestBody email_part = Common.getRequestBodyText(userModel.getUser_email());
        RequestBody phone_part = Common.getRequestBodyText(newPhone);
        RequestBody nationality_part = Common.getRequestBodyText(userModel.getId_country());
        RequestBody city_part = Common.getRequestBodyText(userModel.getCountry_personal_proof());

        Api.getService().updateProfileData(userModel.getUser_id(),phone_part,nationality_part,email_part,name_part,city_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();
                            if (response.body().getSuccess_update()==1)
                            {
                                updatedialog.dismiss();
                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.upd_succ, Toast.LENGTH_SHORT).show();
                            }
                            else if (response.body().getSuccess_update()==2)
                            {
                                Toast.makeText(getActivity(), R.string.em_ph_exist, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("Error",t.getMessage());
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void update_email(String newEmail)
    {
        Log.e("email",newEmail);

        RequestBody name_part = Common.getRequestBodyText(userModel.getUser_full_name());
        RequestBody email_part = Common.getRequestBodyText(newEmail);
        RequestBody phone_part = Common.getRequestBodyText(userModel.getUser_phone());
        RequestBody nationality_part = Common.getRequestBodyText(userModel.getId_country());
        RequestBody city_part = Common.getRequestBodyText(userModel.getCountry_personal_proof());

        Api.getService().updateProfileData(userModel.getUser_id(),phone_part,nationality_part,email_part,name_part,city_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();
                            if (response.body().getSuccess_update()==1)
                            {
                                updatedialog.dismiss();

                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.upd_succ, Toast.LENGTH_SHORT).show();
                            }
                            else if (response.body().getSuccess_update()==2)
                            {
                                Toast.makeText(getActivity(), R.string.em_ph_exist, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("Error",t.getMessage());
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void update_Password(String oldPass,String newPass)
    {
        Api.getService().updatePassword(userModel.getUser_id(),oldPass,newPass)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();

                            Log.e("dddd",response.body().getSuccess_update_pass()+"");
                            if (response.body().getSuccess_update_pass()==0)
                            {

                                Toast.makeText(getActivity(), R.string.wrong_oldpass, Toast.LENGTH_SHORT).show();
                            }else if (response.body().getSuccess_update_pass()==1)
                            {
                                updatedialog.dismiss();

                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.upd_succ, Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("Error",t.getMessage());
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void  UpdateUserData(UserModel userModel)
    {
        this.userModel = userModel;
        this.userSingleTone.setUserModel(userModel);
        this.preferences.create_update_userData(getActivity(),userModel);
        UpdateUi(userModel);
        homeActivity.UpdateUI(userModel);

    }

    private void update_city(String newCity)
    {
        Log.e("city",newCity);
        progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.updng_city));
        progressDialog.show();

        RequestBody name_part = Common.getRequestBodyText(userModel.getUser_full_name());
        RequestBody email_part = Common.getRequestBodyText(userModel.getUser_email());
        RequestBody phone_part = Common.getRequestBodyText(userModel.getUser_phone());
        RequestBody nationality_part = Common.getRequestBodyText(userModel.getId_country());
        RequestBody city_part = Common.getRequestBodyText(newCity);


        Api.getService().updateProfileData(userModel.getUser_id(),phone_part,nationality_part,email_part,name_part,city_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();
                            if (response.body().getSuccess_update()==1)
                            {
                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.upd_succ, Toast.LENGTH_SHORT).show();
                            }
                            else if (response.body().getSuccess_update()==2)
                            {
                                Toast.makeText(getActivity(), R.string.em_ph_exist, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("Error",t.getMessage());
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void update_Nationality(String countryId)
    {

        progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.updng_nationality));
        progressDialog.show();
        RequestBody name_part = Common.getRequestBodyText(userModel.getUser_full_name());
        RequestBody email_part = Common.getRequestBodyText(userModel.getUser_email());
        RequestBody phone_part = Common.getRequestBodyText(userModel.getUser_phone());
        RequestBody nationality_part = Common.getRequestBodyText(countryId);
        RequestBody city_part = Common.getRequestBodyText(userModel.getCountry_personal_proof());


        Api.getService().updateProfileData(userModel.getUser_id(),phone_part,nationality_part,email_part,name_part,city_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();
                            if (response.body().getSuccess_update()==1)
                            {
                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.upd_succ, Toast.LENGTH_SHORT).show();
                            }
                            else if (response.body().getSuccess_update()==2)
                            {
                                Toast.makeText(getActivity(), R.string.em_ph_exist, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("Error",t.getMessage());
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void setCityItem(CityModel cityItem)
    {
        cityDialog.dismiss();
        update_city(cityItem.getId_city());
    }

    public void setNationalityItem(Country_Nationality nationalityItem)
    {
        country_NationalityDialog.dismiss();
        update_Nationality(nationalityItem.getId_country());
    }
}
