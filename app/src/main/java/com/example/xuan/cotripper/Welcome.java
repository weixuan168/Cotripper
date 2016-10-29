package com.example.xuan.cotripper;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Welcome extends Activity implements View.OnClickListener {

    private Button signUp;
    private Button logIn;
    private TextView logIn_Facebook;
    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        signUp = (Button) findViewById(R.id.sign_up);
        logIn = (Button) findViewById(R.id.log_in);
        logIn_Facebook = (TextView) findViewById(R.id.facebook);
        signUp.setOnClickListener(this);
        logIn.setOnClickListener(this);
        logIn_Facebook.setOnClickListener(this);
        actionBar = getActionBar();
        actionBar.hide();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up:
                startSignUp();
                break;
            case R.id.log_in:
                startLogIn();
                break;
            case R.id.facebook:
                // TODO: 2016/8/12  facebook授权登录
                break;
            default:
        }

    }

    private void startLogIn() {
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);
    }

    /**
     * 跳转到注册页面 Jump to the sign up page.
     */
    private void startSignUp() {
        Intent intent = new Intent(this, SignUp_1.class);
        startActivity(intent);
    }
}
