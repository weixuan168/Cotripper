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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class SignUp_2 extends Activity implements View.OnClickListener {
    private TextView changePhoto;
    private TextView input_name;
    private Button next;
    private TextView signIn;
    private RadioButton choose_male;
    private RadioButton choose_female;
    private String name;
    private String gender = null;
    private RadioGroup genders;
    private String email;
    Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_2);
//        获取用户邮箱地址
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
//        获取控件
        changePhoto = (TextView) findViewById(R.id.change_photo);
        input_name = (TextView) findViewById(R.id.name);
        next = (Button) findViewById(R.id.next2);
        signIn = (TextView) findViewById(R.id.signin2);
        genders = (RadioGroup) findViewById(R.id.genders);
        choose_male = (RadioButton) findViewById(R.id.radio_male);
        choose_female = (RadioButton) findViewById(R.id.radio_female);
//        为控件设置点击监控
        changePhoto.setOnClickListener(this);
        next.setOnClickListener(this);
        signIn.setOnClickListener(this);
        genders.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == choose_male.getId()) {
                    gender = choose_male.getText().toString().substring(0,1);
                }
                else if (checkedId == choose_female.getId()) {
                    gender = choose_female.getText().toString().substring(0,1);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_photo:
                up_photo();
            case R.id.next2:
                validate();
            case R.id.signin2:
                // TODO: 2016/8/16 已有帐号？直接登录
        }
    }

    private void up_photo() {

    }

    private void validate() {
        name = input_name.getText().toString().trim();
//        验证昵称是否为空
        if (name.isEmpty()) {
            new AlertDialog.Builder(this).setTitle("Alert").
                    setMessage("Please fill in your name.").create().show();
            input_name.setSelected(true);
        }
//        限制昵称长度
        else if (name.length() > 20) {
            new AlertDialog.Builder(this).setTitle("Alert").
                    setMessage("Name should be at most 20 characters.").create().show();
            input_name.setSelected(true);
//        验证性别是否为空
        } else if (gender==null) {
            new AlertDialog.Builder(this).setTitle("Alert").
                    setMessage("Please select your gender.").create().show();
        } else {
            // TODO: 2016/8/28 若已上传头像，则上传至服务器
            JSONObject json = new JSONObject();
            try {
                json.put("gender", gender);
                json.put("nickname", name);
                json.put("email", email);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String post_msg = json.toString();
            System.out.println(post_msg);

            RequestBody body = RequestBody.create(OkHttpUtils.JSON, post_msg);
            Request request = new Request.Builder()
                    .url("http://123.56.76.203/User/update_user_info/")
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
                                    startSignUp3();
                                } else {
                                    new AlertDialog.Builder(context).setTitle("Alert").
                                            setMessage("Update fail. Please retry").create().show();
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

    private void startSignUp3() {
        Intent intent = new Intent(this, SignUp_3.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up_2, menu);
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
