package com.example.xuan.cotripper.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.xuan.cotripper.R;
import com.example.xuan.cotripper.okhttputils.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUp_2 extends BaseActivity implements View.OnClickListener {
    private ImageView changePhoto;
    private TextView input_name;
    private Button next;
    private TextView signIn;
    private RadioButton choose_male;
    private RadioButton choose_female;
    private String name;
    private String gender = null;
    private RadioGroup genders;
    private String email;
    Boolean flag =false;
    Context context=this;

    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;
    //请求截图
    private static final int REQUEST_CROP_PHOTO = 102;
    //调用照相机返回图片临时文件
    private File tempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_2);
        createCameraTempFile(savedInstanceState);
//        获取用户邮箱地址
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
//        获取控件
        changePhoto = (ImageView) findViewById(R.id.change_photo);
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
                break;
            case R.id.next2:
                validate();
                break;
            case R.id.signin2:
                // TODO: 2016/8/16 已有帐号？直接登录
                break;
            default:
        }
    }

    private void up_photo() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_popupwindow, null);
        TextView btnCarema = (TextView) view.findViewById(R.id.btn_camera);
        TextView btnPhoto = (TextView) view.findViewById(R.id.btn_photo);
        TextView btnCancel = (TextView) view.findViewById(R.id.btn_cancel);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        popupWindow.setOutsideTouchable(true);
        View parent = LayoutInflater.from(this).inflate(R.layout.activity_sign_up_2, null);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        //popupWindow在弹窗的时候背景半透明
        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.5f;
        getWindow().setAttributes(params);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params.alpha = 1.0f;
                getWindow().setAttributes(params);
            }
        });

        btnCarema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到调用系统相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                startActivityForResult(intent, REQUEST_CAPTURE);
                popupWindow.dismiss();
            }
        });
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到调用系统图库
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK);
                popupWindow.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

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
                    .url(OkHttpUtils.baseUrl+"update_user_info/")
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

    /**
     * 创建调用系统照相机待存储的临时文件
     *
     * @param savedInstanceState
     */
    private void createCameraTempFile(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey("tempFile")) {
            tempFile = (File) savedInstanceState.getSerializable("tempFile");
        } else {
            tempFile = new File(checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/image/"),
                    System.currentTimeMillis() + ".jpg");
        }
    }

    /**
     * 检查文件是否存在
     */
    private static String checkDirPath(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return "";
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("tempFile", tempFile);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_CAPTURE: //调用系统相机返回
                if (resultCode == RESULT_OK) {
                    gotoClipActivity(Uri.fromFile(tempFile));
                }
                break;
            case REQUEST_PICK:  //调用系统相册返回
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    gotoClipActivity(uri);
                }
                break;
            case REQUEST_CROP_PHOTO:  //剪切图片返回
                if (resultCode == RESULT_OK) {
                    final Uri uri = intent.getData();
                    if (uri == null) {
                        return;
                    }
                    final String cropImagePath = getRealFilePathFromUri(getApplicationContext(), uri);
                    Bitmap bitMap = BitmapFactory.decodeFile(cropImagePath);
                   changePhoto.setImageBitmap(bitMap);
                    flag=true;
//                    将bitmap上传至服务器
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), new File(cropImagePath));
                            MultipartBody.Builder builder = new MultipartBody.Builder();
                            builder.setType(MultipartBody.FORM);
                            builder.addFormDataPart("email", "111111111@qq.com");
                            builder.addFormDataPart("avater", cropImagePath, fileBody);
                            RequestBody requestBody = builder.build();
                            Request request = new Request.Builder().
                                    url(OkHttpUtils.baseUrl + "upload_avatar/").post(requestBody).build();
                            Response response = null;
                            Call call = OkHttpUtils.getInstance().getOkHttpClient().newCall(request);
                            call.enqueue(new Callback(){

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
                                                if ( (int)jsonObject.get("code")==1) {
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
                    }).start();


                }
                break;
        }
    }


    /**
     * 打开截图界面
     *
     * @param uri
     */
    public void gotoClipActivity(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this, ClipImageActivity.class);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }


    /**
     * Try to return the absolute file path from the given Uri  兼容了file:///开头的 和 content://开头的情况
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePathFromUri(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
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
