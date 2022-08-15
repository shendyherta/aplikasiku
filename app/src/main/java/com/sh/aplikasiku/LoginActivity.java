package com.sh.aplikasiku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;



import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private EditText editemail, editpasssword;
    private Button btnlogin, btnregister;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    //private SignInButton btnGoogle;
    //private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        editemail = findViewById(R.id.email);
        editpasssword = findViewById(R.id.password);
        btnlogin = findViewById(R.id.btn_login);
        btnregister = findViewById(R.id.btn_register);
        //btnGoogle = findViewById(R.id.btn_google);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("silakan tunggu");
        progressDialog.setCancelable(false);


        btnregister.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        });
        btnlogin.setOnClickListener(v -> {
            if (editemail.getText().length() > 0 && editpasssword.getText().length() > 0) {
                login(editemail.getText().toString(), editpasssword.getText().toString());
            } else {
                Toast.makeText(getApplicationContext(), "Silakan isi semua data", Toast.LENGTH_SHORT).show();
            }

        });
        //btnGoogle.setOnClickListener(v -> {
            //GooglesignIn();
        //});

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("843982745350-rh7vgipl32ia38me8gs9n84cmfak6ti2.apps.googleusercontent.com")
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

//    private void GooglesignIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, 1001);
//    }

    private void login(String email, String password) {
        // coding login
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (task.getResult().getUser() != null) {
                        reload();
                    } else {
                        Toast.makeText(getApplicationContext(), "Login gagal", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Login gagal", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void reload() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == 1001){
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try{
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                Log.d("GOOGLE SIGN IN", "firebaseAuthWithGoogle:" + account.getId());
//                firebaseAuthWithGoogle(account.getIdToken());
//            } catch (ApiException e) {
//                Log.w("GOOGLE SIGN IN", "Google sign in failed", e);
//            }
//        }
//    }

//    private void firebaseAuthWithGoogle(String idToken){
//        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("GOOGLE SIGN IN", "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            reload();
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w("GOOGLE SIGN IN", "signInWithCredential:failure", task.getException());
//                            reload();
//                        }
//                    }
//                });
//    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }
}