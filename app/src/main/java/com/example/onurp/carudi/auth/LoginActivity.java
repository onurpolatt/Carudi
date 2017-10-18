package com.example.onurp.carudi.auth;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onurp.carudi.MainActivity;
import com.example.onurp.carudi.R;
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

public class LoginActivity extends AppCompatActivity{
    @BindView(R.id.loginEmail)EditText uEmail;
    @BindView(R.id.loginPassword)EditText uPassword;
    @BindView(R.id.loginButton)Button uLoginButton;
    @BindView(R.id.txtForgetPass)TextView tForgetPass;
    @BindView(R.id.txtSignupLink)TextView tLoginLink;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        tLoginLink.setText(Html.fromHtml(getString(R.string.signup_link)));
        setupWindowAnimations();
    }

    @OnClick(R.id.txtSignupLink)
    public void loginScreen(){
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this);
        Intent intent = new Intent(this,SignupActivity.class);
        startActivity(intent,options.toBundle());
    }

    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setExitTransition(fade);
    }

    @OnClick(R.id.loginButton)
    public void login(){
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = uEmail.getText().toString();
        String password = uPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("S", "Giriş başarılı: " + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess();
                        } else {
                            Toast.makeText(LoginActivity.this, "Giriş Başarısız.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
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

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(uEmail.getText().toString())) {
            uEmail.setError("Bu alanın doldurulması zorunludur.");
            result = false;
        } else {
            uEmail.setError(null);
        }

        if (TextUtils.isEmpty(uPassword.getText().toString())) {
            uPassword.setError("Bu alanın doldurulması zorunludur.");
            result = false;
        } else {
            uPassword.setError(null);
        }

        return result;
    }
}
