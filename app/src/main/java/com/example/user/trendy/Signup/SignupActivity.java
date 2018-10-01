package com.example.user.trendy.Signup;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.trendy.R;

import java.util.regex.Pattern;

public class SignupActivity extends Activity implements TextWatcher{

    EditText first_name,last_name,mobile,email_id;
    TextInputEditText create_password;
    TextInputLayout firstNameInputLayout,lastNameInputLayout,emailInputLayout,mobileInputLayout,passwordInputLayout;
    Button submit_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final String PHONE_REGEX = "^[0-9][0-9]{9}$";

        first_name= findViewById(R.id.first_name);
        last_name=findViewById(R.id.last_name);
        mobile=findViewById(R.id.number);
        email_id=findViewById(R.id.email_id);
        create_password=findViewById(R.id.create_password);

        firstNameInputLayout=findViewById(R.id.first_name_input_layout);
        lastNameInputLayout = findViewById(R.id.last_name_input_layout);;
        mobileInputLayout = findViewById(R.id.mobile_input_layout);
        emailInputLayout = findViewById(R.id.email_input_layout);
        passwordInputLayout = findViewById(R.id.password_input_layout);

        submit_btn=(Button)findViewById(R.id.signup);

        first_name.addTextChangedListener(this);
        last_name.addTextChangedListener(this);
        email_id.addTextChangedListener(this);
        mobile.addTextChangedListener(this);
        create_password.addTextChangedListener(this);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(first_name.getText().toString().isEmpty())
                {
                    firstNameInputLayout.setError("Please Enter First Name");
                }

                if(last_name.getText().toString().isEmpty())
                {
                    lastNameInputLayout.setError("Please Enter Last Name");
                }

                if(email_id.getText().toString().isEmpty())
                {
                    emailInputLayout.setError("Please Enter Email");
                }

                if(create_password.getText().toString().isEmpty())
                {
                    passwordInputLayout.setError("Please Enter Password");
                }

                if(mobile.getText().toString().length()!=0) {
                    if (isValidMobile(mobile.getText().toString())) {
                        //GOT THE CORRECT EMAIL;
                        Toast.makeText(SignupActivity.this, "GOT Phone Number", Toast.LENGTH_SHORT).show();
                    } else {
                        mobileInputLayout.setError("Please Enter mobile no ");
                    }
                }

                if(isValidMail(email_id.getText().toString()))
                {
                    //GOT THE CORRECT EMAIL;
                    Toast.makeText(SignupActivity.this,"GOT ",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    emailInputLayout.setError("Please Enter Email");
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        if (charSequence.hashCode() == first_name.getText().hashCode()) {
            Log.e("hashcode", String.valueOf(first_name.getText().hashCode()));
            if (first_name.getText().toString().isEmpty()) {
                firstNameInputLayout.setError("First name is empty");
            } else {
                firstNameInputLayout.setError(null);
            }
        } else if (charSequence.hashCode() == last_name.getText().hashCode()) {
            if (last_name.getText().toString().isEmpty()) {
                lastNameInputLayout.setError("Last name is empty");
            } else {
                lastNameInputLayout.setError(null);
            }

        } else if (charSequence.hashCode() == email_id.getText().hashCode()) {
            if (email_id.getText().toString().isEmpty()) {
                emailInputLayout.setError("Enter email id");
            } else {
                emailInputLayout.setError(null);
            }
        }
        else if (charSequence.hashCode() == mobile.getText().hashCode()) {
            if (mobile.getText().toString().isEmpty()) {
                mobileInputLayout.setError("Enter mobile no");
            } else {
                mobileInputLayout.setError(null);
            }
        }
        else {
            if (create_password.getText().toString().isEmpty()) {
                passwordInputLayout.setError("password is empty");
            } else {
                passwordInputLayout.setError(null);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidMobile(String phone) {
        boolean check=false;
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            if(phone.length() < 1) {
                // if(phone.length() != 10) {
                check = false;
                mobileInputLayout.setError("Not Valid Number");
            } else {
                check = true;
            }
        } else {
            check=false;
        }
        return check;
    }
}
