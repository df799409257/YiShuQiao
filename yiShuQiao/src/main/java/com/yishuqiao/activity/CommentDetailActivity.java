package com.yishuqiao.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.http.AjaxParams;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.yishuqiao.R;
import com.yishuqiao.activity.discover.SendCommentActivity;
import com.yishuqiao.activity.discover.WriterPersonalCenterActivity;
import com.yishuqiao.adapter.CommentDetailsAdapter;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.CommentListDTO;
import com.yishuqiao.dto.CommentListDTO.ReviewInforList;
import com.yishuqiao.dto.PublicDTO;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.CustomProgressDialog;
import com.yishuqiao.view.MyGridView;
import com.yishuqiao.view.RoundImageView;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName CommentDetailActivity
 * @Description TODO(评论详情通用界面)
 * @Date 2017年7月25日 下午2:40:38
 */
public class CommentDetailActivity extends BaseActivity {

    private int praiseNum = 0;


    public static final String HEADERBEAN = "HEADERBEAN";// 头布局数据
    private ReviewInforList headerDTO = null;

    public static String HEIGHT = "HEIGHT";// 视频高度
    private int height = 440;

    public static String ITEM_ID = "ITEM_ID";// 列表条目的id
    public static String ITEM_TYPE = "ITEM_TYPE";// 列表条目的id
    public static String COMMENT_ID = "COMMENT_ID";// 评论id

    private String item_id = "";
    private String item_type = "";
    private String comment_id = "";

    private CustomProgressDialog progressDialog = null;

    @Bind(R.id.tv_title)
    TextView tv_title;// 标题

    @Bind(R.id.userIcon)
    RoundImageView userIcon;// 头像

    @Bind(R.id.userName)
    TextView userName;// 用户名

    @Bind(R.id.time)
    TextView time;// 时间

    @Bind(R.id.chat_praise)
    TextView chat_praise;// 点赞数量

    @Bind(R.id.liked_state)
    ImageView liked_state;// 点赞状态

    @Bind(R.id.cr_content)
    TextView cr_content;// 评论内容

    @Bind(R.id.listview)
    MyGridView listview;// 子评论列表

    @Bind(R.id.content)
    LinearLayout content;// 跟布局

    private CommentDetailsAdapter adapter;

    private List<CommentListDTO.ReviewInforList> commentList = new ArrayList<CommentListDTO.ReviewInforList>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_commentdetails);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
        ButterKnife.bind(this);

        initView();

    }

    @Override
    protected void onResume() {

        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(this);
        }

        super.onResume();
    }

    private void initView() {

        tv_title.setText("评论详情");

        if (getIntent().hasExtra(ITEM_ID)) {
            item_id = getIntent().getStringExtra(ITEM_ID);
        }
        if (getIntent().hasExtra(ITEM_TYPE)) {
            item_type = getIntent().getStringExtra(ITEM_TYPE);
        }
        if (getIntent().hasExtra(COMMENT_ID)) {
            comment_id = getIntent().getStringExtra(COMMENT_ID);
        }
        if (getIntent().hasExtra(HEADERBEAN)) {
            headerDTO = (ReviewInforList) getIntent().getSerializableExtra(HEADERBEAN);
        }
        Log.e("视频高度 1 = ", height + "");
        // 获取屏幕宽高
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int windowHeight = dm.heightPixels;
        Log.e("屏幕高度  = ", windowHeight + "");
        if (getIntent().hasExtra(HEIGHT)) {
            height = Integer.parseInt(getIntent().getStringExtra(HEIGHT));
            Log.e("视频高度 2 = ", height + "");
        } else {
            height = windowHeight / 3;
        }

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, windowHeight - height - 60);
        content.setLayoutParams(layoutParams);

        View emptyView = LayoutInflater.from(this).inflate(R.layout.discoverdetails_emetyview, null);
        LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(-1, -1);
        emptyParams.gravity = Gravity.CENTER;
        ((ViewGroup) listview.getParent()).addView(emptyView, emptyParams);
        listview.setEmptyView(emptyView);

        adapter = new CommentDetailsAdapter(this, commentList);
        listview.setAdapter(adapter);

        if (headerDTO != null) {

            if (!TextUtils.isEmpty(headerDTO.getTxt_url())) {
                Picasso.with(this).load(headerDTO.getTxt_url()).error(R.drawable.default_user_icon).fit()
                        .into(userIcon);
            }

            userIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CommentDetailActivity.this, WriterPersonalCenterActivity.class);
                    intent.putExtra(WriterPersonalCenterActivity.USERID, headerDTO.getTxt_userid());
                    intent.putExtra(WriterPersonalCenterActivity.TITLE, headerDTO.getTxt_username());
                    startActivity(intent);
                }
            });

            if (headerDTO.getTxt_ispraise().equals("0")) {
                liked_state.setBackgroundResource(R.drawable.like_n);
            } else {
                liked_state.setBackgroundResource(R.drawable.liked);
            }

            userName.setText(headerDTO.getTxt_username());
            time.setText(headerDTO.getTxt_time());
            chat_praise.setText((headerDTO.getTxt_praise()).equals("赞") ? "0" : headerDTO.getTxt_praise());
            cr_content.setText(headerDTO.getTxt_content());

            if (!TextUtils.isEmpty(chat_praise.getText())) {
                praiseNum = Integer.parseInt(chat_praise.getText().toString());
            }

        }

        getCommentList();

    }

    // 获取数据
    private void getCommentList() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "0");
        ajaxParams.put("re_type", item_type);
        ajaxParams.put("detail_id", item_id);
        ajaxParams.put("re_id", comment_id);
        if (MyApplication.MySharedPreferences.readIsLogin()) {
            ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
            ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        }

        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                commentList.clear();

                CommentListDTO dataDTO = new CommentListDTO();
                List<CommentListDTO.ReviewInforList> comment = new ArrayList<CommentListDTO.ReviewInforList>();
                if (Conn.IsNetWork) {

                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("请求成功");

                    for (int i = 0; i < 3; i++) {
                        ReviewInforList datafor = dataDTO.new ReviewInforList();
                        datafor.setTxt_other("");
                        datafor.setTxt_id(i + "");
                        datafor.setTxt_url("");
                        datafor.setTxt_content("测试评论信息");
                        datafor.setTxt_ispraise("0");
                        datafor.setTxt_username("测试用户");
                        datafor.setTxt_praise("20");
                        datafor.setTxt_recount("3");
                        datafor.setTxt_time("7-13");
                        datafor.setTxt_userid("");
                        comment.add(datafor);
                    }
                    dataDTO.setReviewInforList(comment);

                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, CommentListDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {
                    commentList.addAll(dataDTO.getReviewInforList());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void start() {

            }

            @Override
            public void error() {

            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.CommentList);

    }

    // 父评论点赞
    @OnClick(R.id.commitLike)
    public void commitLike(View view) {

        if (!MyApplication.MySharedPreferences.readIsLogin()) {
            Utils.showToast(CommentDetailActivity.this, "请先登录!");
            return;
        }

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "2");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("pr_re_id", comment_id);
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                PublicDTO dataDTO = new PublicDTO();
                if (Conn.IsNetWork) {

                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("请求成功");

                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, PublicDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {
                    Utils.showToast(CommentDetailActivity.this, dataDTO.getTip());
                    // 刷新数据
                    if ((headerDTO.getTxt_ispraise()).equals("1")) {
                        liked_state.setBackgroundResource(R.drawable.praise_selector);

                        if (praiseNum > 0) {
                            chat_praise.setText(praiseNum - 1 + "");
                            praiseNum--;
                        }

                    } else {
                        liked_state.setBackgroundResource(R.drawable.liked);
                        chat_praise.setText(praiseNum + 1 + "");
                        praiseNum++;

                    }
                    CommentDetailActivity.this.setResult(RESULT_OK);
                } else {
                    Utils.showToast(CommentDetailActivity.this, dataDTO.getTxt_message());
                }
            }

            @Override
            public void start() {
                // TODO Auto-generated method stub
                progressDialog.setTouchAble(false);
                progressDialog.show();
            }

            @Override
            public void error() {
                Utils.showToast(CommentDetailActivity.this, "网络连接失败");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.CommentList);

    }

    // 进入作家详情
    public void toWriterPersonalCenter(int position) {
        Intent intent = new Intent(CommentDetailActivity.this, WriterPersonalCenterActivity.class);
        intent.putExtra(WriterPersonalCenterActivity.USERID, commentList.get(position).getTxt_userid());
        intent.putExtra(WriterPersonalCenterActivity.TITLE, commentList.get(position).getTxt_username());
        startActivity(intent);
    }

    // 评论列表点赞
    public void commitCommentPraise(int position) {

        if (!MyApplication.MySharedPreferences.readIsLogin()) {
            Utils.showToast(CommentDetailActivity.this, "请先登录!");
            return;
        }

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "2");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("pr_re_id", commentList.get(position).getTxt_id());
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                PublicDTO dataDTO = new PublicDTO();
                if (Conn.IsNetWork) {

                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("请求成功");

                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, PublicDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {
                    Utils.showToast(CommentDetailActivity.this, dataDTO.getTip());
                    getCommentList();// 刷新数据
                    CommentDetailActivity.this.setResult(RESULT_OK);
                } else {
                    Utils.showToast(CommentDetailActivity.this, dataDTO.getTxt_message());
                }
            }

            @Override
            public void start() {
                // TODO Auto-generated method stub
                progressDialog.setTouchAble(false);
                progressDialog.show();
            }

            @Override
            public void error() {
                Utils.showToast(CommentDetailActivity.this, "网络连接失败");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.CommentList);

    }

    // 发表评论
    @OnClick(R.id.sendComment)
    public void sendComment(View view) {

        Intent it = new Intent(this, SendCommentActivity.class);
        it.putExtra(SendCommentActivity.URL, HttpUrl.CommentList);
        it.putExtra(SendCommentActivity.CONTENT, "text_content");
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "1");
        params.put("re_type", item_type);
        params.put("detail_id", item_id);
        params.put("re_id", comment_id);
        it.putExtra(SendCommentActivity.PARAMS, (Serializable) params);
        startActivityForResult(it, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (0 == requestCode && resultCode == RESULT_OK) {
            getCommentList();// 刷新数据
            this.setResult(RESULT_OK);
        }
    }

    @Override
    protected void onPause() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
        super.onPause();

    }

}
