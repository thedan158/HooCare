package cf.khanhsb.icare_v2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import cf.khanhsb.icare_v2.Model.UserHelperClass;

public class SignupActivity extends Activity {
    private EditText mEmail, mPass, mName, mUsername;
    private TextView mHaveAccount;
    private Button signupButton;
    private RelativeLayout mProgressbarAuth1;
    //
    private FirebaseAuth mAuth;
    private FirebaseDatabase rootNode;
    private FirebaseFirestore firestore;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mEmail = findViewById(R.id.et_email_signup);
        mPass = findViewById(R.id.et_password_signup);
        mName = findViewById(R.id.et_fullname);
        mUsername = findViewById(R.id.et_username);
        mHaveAccount = findViewById(R.id.jumptosignin);
        signupButton = findViewById(R.id.btSignup);
        mProgressbarAuth1 =findViewById(R.id.progress_bar_signup);
        //
        mAuth = FirebaseAuth.getInstance();

        //Already have account
        mHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, SigninActivity.class));
            }
        });

        //Move to signin after sign up
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
                mProgressbarAuth1.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mProgressbarAuth1.setVisibility(View.INVISIBLE);
                    }
                }, 4000);
            }
        });
    }

    private void createUser() {

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");

        //Get all the values
        String name = mName.getText().toString();
        String username = mUsername.getText().toString();
        String email = mEmail.getText().toString();
        String pass = mPass.getText().toString();
        UserHelperClass helperClass = new UserHelperClass(name, username, email, pass);
        reference.child(username).setValue(helperClass);
        //..................


        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!pass.isEmpty()) {
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                CreateUserOnFirebase(email,username,pass);

                                Toast.makeText(SignupActivity.this, "Sign Up Successfully !!", Toast.LENGTH_SHORT).show();
                                //..........

                                startActivity(new Intent(SignupActivity.this, SigninActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignupActivity.this, "Registration Error !!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                mPass.setError("Your Password must not empty");
            }
        } else if (email.isEmpty()) {
            mEmail.setError("Your email must not empty");
        } else {
            mEmail.setError("Please enter correct email");
        }
    }

    private void CreateUserOnFirebase(String userEmail, String userName,String password) {
        //Set up firestore
        firestore = FirebaseFirestore.getInstance();

        // Save user data to firestore
        Map<String, Object> user = new HashMap<>();
        user.put("name", userName);
        user.put("email", userEmail);
        user.put("weight", "empty");
        user.put("height", "empty");
        user.put("step_goal", "empty");
        user.put("drink_goal", "empty");
        user.put("calories_burn_goal", "empty");
        user.put("sleep_goal", "empty");
        user.put("on_screen_goal", "empty");
        user.put("health_point", "empty");
        user.put("time_to_sleep","empty");
        user.put("time_to_wake","empty");
        firestore.collection("users").document(userEmail)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignupActivity.this, "Fail to save data to Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
