package com.example.xuan.cotripper.main;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.example.xuan.cotripper.R;
import com.example.xuan.cotripper.huanxin.DemoHelper;

/**
 * Created by Xuan on 2016/10/14.
 */
public class LoginHuanxin extends BaseActivity{

    private boolean progressShow;
    private ProgressDialog progressDialog;
    private String im_id;
    private String im_pw;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Intent intent = getIntent();
        im_id = intent.getStringExtra("im_id");
        im_pw = intent.getStringExtra("im_pw");
        //EMChat.getInstance().isLoggedIn() 可以检测是否已经登录过环信，如果登录过则环信SDK会自动登录，不需要再次调用登录操作
        if (EMChat.getInstance().isLoggedIn()) {
            progressDialog = getProgressDialog();
            progressDialog.setMessage(getResources().getString(R.string.is_contact_customer));
            progressDialog.show();
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        //加载本地数据库中的消息到内存中
                        EMChatManager.getInstance().loadAllConversations();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    toChatActivity();
                }
            }).start();
        } else {
            //创建一个用户并登录环信服务器
           loginHuanxinServer(im_id,im_pw);
        }
    }

    private ProgressDialog getProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(LoginHuanxin.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    progressShow = false;
                }
            });
        }
        return progressDialog;
    }

    private void toChatActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!LoginHuanxin.this.isFinishing())
                    progressDialog.dismiss();
                // 进入主页面
                startActivity(new Intent(LoginHuanxin.this, ChatActivity.class));
                finish();
            }
        });
    }

    public void loginHuanxinServer(final String uname, final String upwd) {
        progressShow = true;
        progressDialog = getProgressDialog();
        progressDialog.setMessage(getResources().getString(R.string.is_contact_customer));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        // login huanxin server
        EMChatManager.getInstance().login(uname, upwd, new EMCallBack() {
            @Override
            public void onSuccess() {
                if (!progressShow) {
                    return;
                }
                DemoHelper.getInstance().setCurrentUserName(uname);
                DemoHelper.getInstance().setCurrentPassword(upwd);
                try {
                    EMChatManager.getInstance().loadAllConversations();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                toChatActivity();
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                if (!progressShow) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(LoginHuanxin.this,
                                getResources().getString(R.string.is_contact_customer_failure_seconed) + message,
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }
}
