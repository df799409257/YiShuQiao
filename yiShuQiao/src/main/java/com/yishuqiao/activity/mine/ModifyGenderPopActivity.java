package com.yishuqiao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yishuqiao.R;
import com.yishuqiao.activity.BaseActivity;

/**
 * 作者：admin
 * <p>
 * 创建时间：2017-7-5 下午5:41:44
 * <p>
 * 项目名称：YiShuQiao
 * <p>
 * 文件名称：ModifyGenderPopActivity.java
 * <p>
 * 类说明：修改性别
 */
public class ModifyGenderPopActivity extends BaseActivity implements
        OnClickListener {

    private RelativeLayout chooseMaleBtn = null;
    private RelativeLayout chooseFemaleBtn = null;
    private RelativeLayout noChooseBtn = null;
    private LinearLayout cancelBtn = null;
    private TextView maleTv = null;
    private TextView femaleTv = null;
    private TextView noChooseTv = null;
    private String gender = null;// 男 女 未选择

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_gender_popwindow);

        // 设置宽度全屏
        getWindow().setLayout(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
        gender = getIntent().getStringExtra("gender");
        if (gender == null) {
            gender = "未选择";
        }

        chooseMaleBtn = (RelativeLayout) findViewById(R.id.chooseMaleBtn);
        chooseFemaleBtn = (RelativeLayout) findViewById(R.id.chooseFemaleBtn);
        noChooseBtn = (RelativeLayout) findViewById(R.id.noChooseBtn);
        cancelBtn = (LinearLayout) findViewById(R.id.cancelBtn);

        maleTv = (TextView) findViewById(R.id.maleTv);
        femaleTv = (TextView) findViewById(R.id.femaleTv);
        noChooseTv = (TextView) findViewById(R.id.noChooseTv);

        if ("男".equals(gender)) {
            maleTv.setVisibility(View.VISIBLE);
            femaleTv.setVisibility(View.INVISIBLE);
            noChooseTv.setVisibility(View.INVISIBLE);
        } else if ("女".equalsIgnoreCase(gender)) {
            maleTv.setVisibility(View.INVISIBLE);
            femaleTv.setVisibility(View.VISIBLE);
            noChooseTv.setVisibility(View.INVISIBLE);
        } else {
            maleTv.setVisibility(View.INVISIBLE);
            femaleTv.setVisibility(View.INVISIBLE);
            noChooseTv.setVisibility(View.VISIBLE);
        }

        chooseMaleBtn.setOnClickListener(this);
        chooseFemaleBtn.setOnClickListener(this);
        noChooseBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chooseMaleBtn:
                maleTv.setVisibility(View.VISIBLE);
                femaleTv.setVisibility(View.INVISIBLE);
                noChooseTv.setVisibility(View.INVISIBLE);
                setResult("男");
                break;
            case R.id.chooseFemaleBtn:
                maleTv.setVisibility(View.INVISIBLE);
                femaleTv.setVisibility(View.VISIBLE);
                noChooseTv.setVisibility(View.INVISIBLE);
                setResult("女");
                break;
            case R.id.noChooseBtn:
                maleTv.setVisibility(View.INVISIBLE);
                femaleTv.setVisibility(View.INVISIBLE);
                noChooseTv.setVisibility(View.VISIBLE);
                break;
            case R.id.cancelBtn:
                finish();
                break;
            default:
                break;
        }
    }

    private void setResult(final String gender) {
        Intent it = new Intent();
        it.putExtra("gender", gender);
        setResult(RESULT_OK, it);
        finish();
    }

}
