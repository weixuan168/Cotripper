package com.example.xuan.cotripper.main;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;

import com.easemob.easeui.ui.EaseBaseActivity;

/**
 * Created by Xuan on 2016/10/11.
 */
public class BaseActivity extends EaseBaseActivity {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 通过xml查找相应的ID，通用方法
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T $(@IdRes int id) {
        return (T) findViewById(id);
    }
}
