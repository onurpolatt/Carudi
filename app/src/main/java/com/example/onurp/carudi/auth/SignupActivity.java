package com.example.onurp.carudi.auth;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.transition.Fade;
import android.transition.Transition;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.onurp.carudi.MainActivity;
import com.example.onurp.carudi.R;
import com.example.onurp.carudi.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by onurp on 7.10.2017.
 */

public class SignupActivity extends AppCompatActivity {
    @BindView(R.id.signEmail)EditText email;
    @BindView(R.id.signName)EditText name;
    @BindView(R.id.signPassword)EditText password;
    @BindView(R.id.signButton)Button sButton;
    @BindView(R.id.txtLoginLink)TextView tLoginLink;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    private static final int REQUEST_SIGNUP = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        tLoginLink.setText(Html.fromHtml(getString(R.string.login_link)));
        setupWindowAnimations();
    }
    public void setupWindowAnimations(){
        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);
    }

    @OnClick(R.id.txtLoginLink)
    public void loginScreen(){
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SignupActivity.this);
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent,options.toBundle());
    }

    @OnClick(R.id.signButton)
    public void checkUser(){
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String uEmail = email.getText().toString();
        String uPassword = password.getText().toString();


        mAuth.createUserWithEmailAndPassword(uEmail, uPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("T", "createUser:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignupActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void onAuthSuccess(FirebaseUser user) {
        String uName = name.getText().toString();
        writeNewUser(user.getUid(), uName, user.getEmail());

        startActivity(new Intent(SignupActivity.this, MainActivity.class));
        finish();
    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }

    private boolean validateForm() {
        boolean result = true;
        if (isValidEmail(email.getText().toString())) {
            email.setError(null);
        } else {
            email.setError("Geçerli bir email adresi giriniz.");
            result = false;
        }

        if (!TextUtils.isEmpty(password.getText().toString())
                || ( password.getText().length() > 5
                &&   password.getText().length() < 11)) {
            password.setError(null);
        } else {
            password.setError(" 6 - 10 karakter uzunluğunda şifre giriniz.");
            result = false;
        }

        if (!TextUtils.isEmpty(name.getText().toString())) {
            name.setError(null);
        } else {
            name.setError("Ad Soyad kısmı boş bırakılamaz.");
            result = false;
        }

        return result;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Yükleniyor...");
        }

        mProgressDialog.show();
    }


    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
