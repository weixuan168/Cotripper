package com.example.xuan.cotripper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.xuan.cotripper.okhttputils.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogIn extends Activity implements View.OnClickListener{
    private EditText input_email;
    private EditText input_password;
    private Button log_in;
    private TextView login_facebook;
    private String email;
    private String password;
    Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        input_email = (EditText) findViewById(R.id.login_email);
        input_password = (EditText) findViewById(R.id.login_password);
        log_in = (Button) findViewById(R.id.login);
        login_facebook = (TextView) findViewById(R.id.facebook2);
        log_in.setOnClickListener(this);
        login_facebook.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                validate();
                break;
            case R.id.facebook2:
                // TODO: 2016/9/9 用facebook登录
                break;
            default:
        }
    }

    private void validate() {
        email = input_email.getText().toString().trim();
        password = input_password.getText().toString().trim();

            JSONObject json = new JSONObject();
            try {
                json.put("passwd", password);
                json.put("email", email);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String post_msg = json.toString();
            System.out.println(post_msg);

            RequestBody body = RequestBody.create(OkHttpUtils.JSON, post_msg);
            Request request = new Request.Builder()
                    .url(OkHttpUtils.baseUrl+"dealing_login/")
                            .post(body)
                            .build();
            Response response = null;
            Call call = OkHttpUtils.getInstance().getOkHttpClient().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(context).setTitle("Alert").
                                    setMessage("Can not connect to the server.").create().show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String receive_msg = response.body().string();
                    System.out.println(receive_msg);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(receive_msg);
                                if ((boolean) jsonObject.get("info_completed")) {
                                    // TODO: 2016/9/9 接入环信聊天页面 
                                } else if (!(boolean)jsonObject.get("login")) {
                                    new AlertDialog.Builder(context).setTitle("Alert").
                                            setMessage("Email or password is uncorrect.").create().show();
                                }else if (!(boolean) jsonObject.get("info_completed")) {
                                    Intent intent = new Intent(context, SignUp_2.class);
                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
