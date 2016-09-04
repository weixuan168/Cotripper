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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUp_1 extends Activity implements View.OnClickListener{
    private EditText input_email;
    private EditText input_password;
    private Button next;
    private TextView sign_in;
    private String email;
    private String password;
    Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_1);
        input_email = (EditText) findViewById(R.id.email);
        input_password = (EditText) findViewById(R.id.password);
        next = (Button) findViewById(R.id.next);
        sign_in = (TextView) findViewById(R.id.signin);
        next.setOnClickListener(this);
        sign_in.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next:
                validate();
                break;
            case R.id.signin:
                // TODO: 2016/8/13 已有帐号？直接登录
                startSignUp2();
                break;
            default:
        }
    }

    /**
     * 验证邮箱和密码 Validate the email and password
     */
    private void validate() {
        email = input_email.getText().toString().trim();
        password = input_password.getText().toString().trim();
        Pattern p1 = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher m1 = p1.matcher(email);

//        验证邮箱是否为空
        if (email.isEmpty()) {
            System.out.println("email is null");
            new AlertDialog.Builder(this).setTitle("Alert").
                    setMessage("Please input a valid email address.").create().show();
            input_email.setSelected(true);
        }
//        验证邮箱格式
        else if (!m1.matches()) {
            new AlertDialog.Builder(this).setTitle("Alert").
                    setMessage("Please fill in the correct email address.").create().show();
            input_email.setSelected(true);
        }
//        验证密码格式
        else if (password.length() < 6 || password.length() > 20) {
            new AlertDialog.Builder(this).setTitle("Alert").
                    setMessage("Password should be 6~20 characters.").create().show();
            input_password.setSelected(true);
        }
        // TODO: 2016/8/15 密码为字母+数字，屏蔽空格
//        验证邮箱是否已被注册
        else {
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
                    .url("http://123.56.76.203/User/dealing_register/")
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
                                if ((boolean) jsonObject.get("successful")) {
                                    startSignUp2();
                                } else if ((boolean) jsonObject.get("exist")) {
                                    new AlertDialog.Builder(context).setTitle("Alert").
                                            setMessage("That email address is already in use.").create().show();
                                    input_email.setSelected(true);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });
        }
    }

    private void startSignUp2() {
        Intent intent = new Intent(this, SignUp_2.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up_1, menu);
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
