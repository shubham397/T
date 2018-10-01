package com.example.user.trendy.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.trendy.BuildConfig;
import com.example.user.trendy.MainActivity;
import com.example.user.trendy.Signup.SignupActivity;
import com.example.user.trendy.Util.SharedPreference;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import com.example.user.trendy.R;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.HttpCachePolicy;
import com.shopify.buy3.Storefront;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class LoginActiviy extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 007;
    CallbackManager callbackManager;
    LoginButton login_button;
    String firstname = "",lastname="", email = "";
    private GraphClient graphClient;
    Button facebook;
    TextView signin,signup;
    EditText name_text, email_text;
    ProgressBar progressBar;
    private ProgressDialog progressDoalog;



    @Override
    public void onClick(View v) {
//
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
//        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
//        SharedPreference.saveData("login", "true", getApplicationContext());

        graphClient = GraphClient.builder(this)
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();

        String login = SharedPreference.getData("login", getApplicationContext());
//
//            if (login.equals("true")) {
//            Intent i = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(i);
//        }

        GoogleApiClient mGoogleApiClient;
        ProgressDialog mProgressDialog;
        SignInButton btnSignIn;
        Button btnSignOut, btnRevokeAccess;

        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);

        btnSignIn.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
        btnRevokeAccess.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customizing G+ button
        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setScopes(gso.getScopeArray());

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        login_button = findViewById(R.id.login_button);
        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.signup);
        facebook = findViewById(R.id.facebookView);
        //  name_text = findViewById(R.id.name_text);
        email_text = findViewById(R.id.email_text);
        login_button.setReadPermissions("email", "public_profile");
        callbackManager = CallbackManager.Factory.create();

        login_button.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>()

                {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        System.out.println("onSuccess");

                        String accessToken = loginResult.getAccessToken()
                                .getToken();
                        Log.e("facebook_accessToken", "" + accessToken);

                        //  SharedPreference.saveData("data", "success", MainActivity.this);


                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.e("LoginActivity", response.toString());
                                        try {

                                            // Bundle bFacebookData = getFacebookData(object);
                                            // email = response.getJSONObject().getString("email");
                                            firstname = object.getString("first_name");
                                            lastname=object.getString("last_name");
                                            email = object.getString("email");
                                            Log.e("name", "" + firstname + email);
//                                            gender = object.getString("gender");
//                                            birthday = object.getString("birthday");

//                                            Intent i = new Intent(getApplicationContext(), ProfiiView.class);
//                                            i.putExtra("name", name);
//                                            i.putExtra("email", email);
//                                            startActivity(i);
                                            SharedPreference.saveData("email", email.trim(), getApplicationContext());
                                            SharedPreference.saveData("firstname", firstname.trim(), getApplicationContext());
                                            SharedPreference.saveData("lastname", lastname.trim(), getApplicationContext());
                                            checkCustomer(email);


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
//                                        name_edit.setText(name);
//                                        email_id.setText(email);
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "first_name,last_name,name,email");
                        request.setParameters(parameters);
                        request.executeAsync();

                        Log.e("name", "" + firstname + email);


                    }

                    @Override
                    public void onCancel() {
                        System.out.println("onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        System.out.println("onError");
                        Log.v("LoginActivity", exception.getCause().toString());
                    }
                });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActiviy.this, SignupActivity.class);
                startActivity(i);
            }
        });



        facebook.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                login_button.performClick();
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                progressDoalog = new ProgressDialog(LoginActiviy.this);
//                progressDoalog.setMessage("loading....");
//                progressDoalog.setTitle("Processing");
//                progressDoalog.setCancelable(true);
//                //   progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                progressDoalog.show();
                //  name=name_text.getText().toString().trim();
                email = email_text.getText().toString().trim();
                if (email.trim().length() != 0) {
                    checkCustomer(email.trim());
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String personName = acct.getDisplayName();
            String personPhotoUrl = acct.getPhotoUrl().toString();
             email = acct.getEmail();

            checkCustomer(email);
        }
    }

    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    public void create() {
//
//        StringTokenizer st = new StringTokenizer(name, " "); //pass comma as delimeter
//        String firstname = st.nextToken();
//        String lastname = st.nextToken();
//        Log.e("firstname", firstname);
//        Log.e("lastname", lastname);

        String password1 = email.trim();

        String password = Base64.encodeToString(password1.getBytes(), Base64.DEFAULT).trim();
        Log.e("coverted1", password.trim());

        Storefront.CustomerCreateInput input = new Storefront.CustomerCreateInput(email.trim(), password.trim())
                .setFirstName(firstname)
                  .setLastName(lastname)
                .setAcceptsMarketing(true);
        //  .setPhone(Input.value("1-123-456-7890"));

        Storefront.MutationQuery mutationQuery = Storefront.mutation(mutation -> mutation
                .customerCreate(input, query -> query
                        .customer(customer -> customer
                                .id()
                                .email()
                                .firstName()

                        )
                        .userErrors(userError -> userError
                                .field()
                                .message()
                        )
                )
        );


        graphClient.mutateGraph(mutationQuery).enqueue(new GraphCall.Callback<Storefront.Mutation>() {


            @Override
            public void onResponse(@NonNull com.shopify.buy3.GraphResponse<Storefront.Mutation> response) {
//                Log.e("response", response.toString());

                if (response.data().getCustomerCreate() != null) {

                    String id = response.data().getCustomerCreate().getCustomer().getId().toString();
                    String email = response.data().getCustomerCreate().getCustomer().getEmail();
                    Log.d("em", "Create Customer Info:" + email + ":" + id);

                    if (id != null) {
//                        if (progressDoalog != null) {
//                            progressDoalog.dismiss();
//                        }
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        SharedPreference.saveData("login", "true", getApplicationContext());
                        startActivity(i);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
//                if (progressDoalog != null) {
//                    progressDoalog.dismiss();
//                }
                Log.d("fa", "Create customer Account API FAIL:" + error.getMessage());

            }


        });


    }

    public void checkCustomer(String email) {
//        if (progressDoalog != null) {
//            progressDoalog = new ProgressDialog(LoginActiviy.this);
//            progressDoalog.setMessage("loading....");
//            progressDoalog.setTitle("Processing");
//            progressDoalog.setCancelable(true);
//            progressDoalog.show();
//        }

        String password1 = email;

        String password = Base64.encodeToString(password1.getBytes(), Base64.DEFAULT).trim();
        Log.e("coverted", password.trim());

        Storefront.CustomerAccessTokenCreateInput input1 = new Storefront.CustomerAccessTokenCreateInput(email.trim(), password.trim());
        Storefront.MutationQuery mutationQuery1 = Storefront.mutation(mutation -> mutation
                .customerAccessTokenCreate(input1, query -> query
                        .customerAccessToken(customerAccessToken -> customerAccessToken
                                .accessToken()
                                .expiresAt()
                        )

                        .userErrors(userError -> userError
                                .field()
                                .message()
                        )
                )
        );

        graphClient.mutateGraph(mutationQuery1).enqueue(new GraphCall.Callback<Storefront.Mutation>() {


            @Override
            public void onResponse(@NonNull com.shopify.buy3.GraphResponse<Storefront.Mutation> response) {
//                Log.e("response", response.toString());

                if (response.data() != null) {


                    if (response.data().getCustomerAccessTokenCreate().getCustomerAccessToken() != null) {
                        //                        if (progressDoalog != null) {
                        //                            progressDoalog.dismiss();
                        //                        }
                        String token = "" + response.data().getCustomerAccessTokenCreate().getCustomerAccessToken().getAccessToken().toString();
                        String expire = response.data().getCustomerAccessTokenCreate().getCustomerAccessToken().getExpiresAt().toString();
                        SharedPreference.saveData("accesstoken", token.trim(), getApplicationContext());

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        SharedPreference.saveData("login", "true", getApplicationContext());
                        startActivity(i);


                        Log.e("token", "" + token);
                        Log.e("expire", "" + expire);
                        //                    String id = response.data().getCustomerCreate().getCustomer().getId().toString();
                        //                    String email = response.data().getCustomerCreate().getCustomer().getEmail();
                        //  Log.d("em", "Create Customer Info:" + email + ":" + id);
                    } else {
                        Log.e("token", "" + "empty");
                        create();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull GraphError error) {
//                if (progressDoalog != null) {
//                    progressDoalog.dismiss();
//                }
                Log.d("fa", "Create customer Account API FAIL:" + error.getMessage());

            }


        });

    }

    public void getId() {
//        Storefront.QueryRootQuery query = Storefront.query(root -> root
//                .customer(accessToken, customer -> customer
//                        .
//                        .orders(arg -> arg.first(10), connection -> connection
//                                .edges(edge -> edge
//                                        .node(node -> node
//                                                .orderNumber()
//                                                .totalPrice()
//                                        )
//                                )
//                        )
//                )
//        );
    }
}