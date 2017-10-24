package com.yishuqiao.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.discover.DiscoverDetailsActivity;
import com.yishuqiao.activity.video.VideoPlayActivity;
import com.yishuqiao.activity.yiyuan.WriterDetailsActivity;
import com.yishuqiao.adapter.SearchAdapter;
import com.yishuqiao.dto.FindDTO;
import com.yishuqiao.dto.FindDTO.FindItem;
import com.yishuqiao.dto.FindDTO.FindItemImageUrl;
import com.yishuqiao.dto.SearchDTO;
import com.yishuqiao.dto.SearchDTO.SearchList;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.okhttp.OkHttpUtils;
import com.yishuqiao.okhttp.callback.Callback;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.refreshlayout.BGAMeiTuanRefreshViewHolder;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout.BGARefreshLayoutDelegate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import net.tsz.afinal.http.AjaxParams;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName SearchActivity
 * @Description TODO(搜索界面)
 * @Date 2017年8月3日 下午2:45:54
 */
@SuppressLint("InflateParams")
public class SearchActivity extends BaseActivity
        implements OnClickListener, OnItemClickListener, BGARefreshLayoutDelegate {

    private int PagerNum = 1;// 分页加载

    private ImageView empty_pic;// 空视图
    private LinearLayout ll_content;// 结果列表

    public static final String TYPE = "TYPE";
    private ListView listview;
    private int type = -1;
    private PopupWindow mPopupWindow = null;
    private View mPopWindowView = null;
    private TextView category = null;
    private EditText searchKeyword = null;
    private String keyWord = null;

    private BGARefreshLayout swipeRefreshLayout;
    private RelativeLayout content = null;
    private View failedView = null;

    private List<SearchDTO.SearchList> list = new ArrayList<SearchDTO.SearchList>();
    private SearchAdapter adapter;

    private List<FindItem> findItemList = new ArrayList<FindDTO.FindItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        // 当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        initView();

    }

    @SuppressLint("InflateParams")
    private void initView() {

        content = (RelativeLayout) findViewById(R.id.content);
        swipeRefreshLayout = (BGARefreshLayout) findViewById(R.id.swipe_container);
        BGAMeiTuanRefreshViewHolder meiTuanRefreshViewHolder = new BGAMeiTuanRefreshViewHolder(this, true);
        meiTuanRefreshViewHolder.setPullDownImageResource(R.drawable.bga_refresh_loading01);
        meiTuanRefreshViewHolder.setChangeToReleaseRefreshAnimResId(R.anim.pull_refresh);
        meiTuanRefreshViewHolder.setRefreshingAnimResId(R.anim.pull_refreshing);
        swipeRefreshLayout.setRefreshViewHolder(meiTuanRefreshViewHolder);
        swipeRefreshLayout.setPullDownRefreshEnable(false);
        swipeRefreshLayout.setDelegate(this);

        listview = (ListView) findViewById(R.id.listview);

        empty_pic = (ImageView) findViewById(R.id.empty_pic);

        ll_content = (LinearLayout) findViewById(R.id.ll_content);

        View emptyView = LayoutInflater.from(this).inflate(R.layout.search_list_empty, null);
        LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(-1, -1);
        emptyParams.gravity = Gravity.CENTER;
        ((ViewGroup) listview.getParent()).addView(emptyView, emptyParams);
        listview.setEmptyView(emptyView);

        adapter = new SearchAdapter(this, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(this);

        category = (TextView) findViewById(R.id.category);
        category.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    return;
                } else {
                    showPopWindow(v);
                }

            }
        });

        searchKeyword = (EditText) findViewById(R.id.search_keyword);
        type = getIntent().getIntExtra(TYPE, -1);
        switch (type) { // 1 发现 2 资讯3 视频 4 艺苑
            case 1:
                category.setText("发现");
                break;
            case 2:
                category.setText("资讯");
                break;
            case 3:
                category.setText("视频");
                break;
            case 4:
                category.setText("艺苑");
                break;
            default:
                break;
        }

        // 监听键盘的搜索按钮
        searchKeyword.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    // 先影藏输入法
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    if (TextUtils.isEmpty(searchKeyword.getText().toString())) {
                        Utils.showToast(SearchActivity.this, "请输入关键字");
                    } else {
                        keyWord = searchKeyword.getText().toString();
                        PagerNum = 1;
                        list.clear();
                        adapter.notifyDataSetChanged();
                        getDataFormService();
                    }

                    return true;
                }

                return false;
            }
        });

    }

    // 点击搜索
    public void toSearch(View view) {

        // 先影藏输入法
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        if (TextUtils.isEmpty(searchKeyword.getText().toString())) {
            Utils.showToast(this, "请输入关键字");
        } else {
            keyWord = searchKeyword.getText().toString();
            PagerNum = 1;
            list.clear();
            adapter.notifyDataSetChanged();
            getDataFormService();
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.pop_discover:
                dissPopWindow();
                type = 1;
                category.setText("发现");
                break;
            case R.id.pop_news:
                dissPopWindow();
                type = 2;
                category.setText("资讯");
                break;
            case R.id.pop_video:
                dissPopWindow();
                type = 3;
                category.setText("视频");
                break;
            case R.id.pop_yiyuan:
                dissPopWindow();
                type = 4;
                category.setText("艺苑");
                break;
            default:
                break;
        }

        // 先影藏输入法
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        keyWord = searchKeyword.getText().toString();
        if (keyWord != null && !TextUtils.isEmpty(keyWord)) {
            PagerNum = 1;
            list.clear();
            adapter.notifyDataSetChanged();
            getDataFormService();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = null;
        switch (type) {
            case 1:
                // 发现

                getSendDate(position);

                break;
            case 2:
                // 资讯

                intent = new Intent(this, CommentWebViewActivity.class);
                intent.putExtra(CommentWebViewActivity.ITEM_ID, list.get(position).getTxt_id());
                intent.putExtra(CommentWebViewActivity.ITEM_TYPE, "2");
                intent.putExtra(CommentWebViewActivity.ACTION_TYPE, "1");
                intent.putExtra("txt_title", "资讯详情");

                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "1");
                params.put("txt_id", list.get(position).getTxt_id());
                intent.putExtra(CommentWebViewActivity.URL_PARAMS, (Serializable) params);

                startActivity(intent);

                break;
            case 3:
                // 视频

                intent = new Intent(this, VideoPlayActivity.class);
                intent.putExtra(VideoPlayActivity.VIDEO_PLAY_ID, list.get(position).getTxt_id());
                intent.putExtra(VideoPlayActivity.VIDEO_PLAY_TITLE, list.get(position).getTxt_name());
                startActivity(intent);

                break;
            case 4:
                // 艺苑

                intent = new Intent(this, WriterDetailsActivity.class);
                intent.putExtra(WriterDetailsActivity.ITEMID, list.get(position).getTxt_id());
                startActivity(intent);

                break;
            default:
                break;
        }

    }

    private void getDataFormService() {

        if (Conn.IsOkhttp) {

            showLoadingView();

            Map<String, String> params = new HashMap<String, String>();
            params.put("type", type + "");
            params.put("txt_text", keyWord);
            params.put("pagerindex", Conn.PagerSize);
            params.put("pagernum", "" + PagerNum++);
            OkHttpUtils.post().url(HttpUrl.Search).params(params).build().execute(new Callback<String>() {

                @Override
                public String parseNetworkResponse(Response response) throws Exception {
                    String temtResponse = response.body().string();
                    Log.e("数据", temtResponse);
                    return temtResponse;
                }

                @Override
                public void onError(Call call, Exception e) {
                    Log.e("Error = ", e.getMessage());
                    dismissLoadingView();
                    swipeRefreshLayout.endLoadingMore();
                    addFailedView();
                }

                @Override
                public void onResponse(String response) {

                    swipeRefreshLayout.setPullDownRefreshEnable(true);

                    empty_pic.setVisibility(View.GONE);
                    ll_content.setVisibility(View.VISIBLE);

                    swipeRefreshLayout.endLoadingMore();
                    dismissLoadingView();
                    removeFailedView();

                    SearchDTO dataDTO = new SearchDTO();
                    List<SearchDTO.SearchList> dataList = new ArrayList<SearchDTO.SearchList>();
                    if (Conn.IsNetWork) {
                        dataDTO.setTxt_code("0");
                        dataDTO.setTxt_message("请求成功");
                        SearchList datafor = dataDTO.new SearchList();
                        datafor.setTxt_id("0");
                        datafor.setTxt_name("测试信息");
                        datafor.setTxt_time("2017-7-4");
                        datafor.setTxt_url("");
                        dataList.add(datafor);
                        dataDTO.setSearchList(dataList);
                    } else {
                        Gson gson = new Gson();
                        dataDTO = gson.fromJson(response, SearchDTO.class);
                    }
                    if (dataDTO.getTxt_code().equals("0")) {
                        list.addAll(dataDTO.getSearchList());
                        adapter.notifyDataSetChanged();

                        if (list.size() < Integer.parseInt(Conn.PagerSize)) {
                            swipeRefreshLayout.closeLoadingMore();
                        } else {
                            swipeRefreshLayout.openLoadingMore();
                        }

                    } else {
                        // Utils.showToast(SearchActivity.this, "没有搜索到内容");
                    }
                }

            });

        } else {

            AjaxParams ajaxParams = new AjaxParams();
            ajaxParams.put("type", type + "");
            ajaxParams.put("txt_text", keyWord);
            ajaxParams.put("pagerindex", Conn.PagerSize);
            ajaxParams.put("pagernum", "" + PagerNum++);
            HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

                @Override
                public void succ(String t) {

                    swipeRefreshLayout.setPullDownRefreshEnable(true);

                    empty_pic.setVisibility(View.GONE);
                    ll_content.setVisibility(View.VISIBLE);

                    swipeRefreshLayout.endLoadingMore();
                    dismissLoadingView();
                    removeFailedView();

                    SearchDTO dataDTO = new SearchDTO();
                    List<SearchDTO.SearchList> dataList = new ArrayList<SearchDTO.SearchList>();
                    if (Conn.IsNetWork) {
                        dataDTO.setTxt_code("0");
                        dataDTO.setTxt_message("请求成功");
                        SearchList datafor = dataDTO.new SearchList();
                        datafor.setTxt_id("0");
                        datafor.setTxt_name("测试信息");
                        datafor.setTxt_time("2017-7-4");
                        datafor.setTxt_url("");
                        dataList.add(datafor);
                        dataDTO.setSearchList(dataList);
                    } else {
                        Gson gson = new Gson();
                        dataDTO = gson.fromJson(t, SearchDTO.class);
                    }
                    if (dataDTO.getTxt_code().equals("0")) {
                        list.addAll(dataDTO.getSearchList());
                        adapter.notifyDataSetChanged();

                        if (list.size() < Integer.parseInt(Conn.PagerSize)) {
                            swipeRefreshLayout.closeLoadingMore();
                        } else {
                            swipeRefreshLayout.openLoadingMore();
                        }

                    } else {
                        // Utils.showToast(SearchActivity.this, "没有搜索到内容");
                    }
                }

                @Override
                public void start() {
                    // TODO Auto-generated method stub
                    showLoadingView();
                }

                @Override
                public void error() {
                    // TODO Auto-generated method stub
                    dismissLoadingView();
                    swipeRefreshLayout.endLoadingMore();
                    addFailedView();

                }

            }, this);
            httpNetWork.postHttp(ajaxParams, HttpUrl.Search);

        }

    }

    private void showPopWindow(View view) {
        mPopupWindow = new PopupWindow(this);
        mPopWindowView = getLayoutInflater().inflate(R.layout.popwindow_search_layout, null);
        mPopWindowView.findViewById(R.id.pop_discover).setOnClickListener(this);
        mPopWindowView.findViewById(R.id.pop_news).setOnClickListener(this);
        mPopWindowView.findViewById(R.id.pop_video).setOnClickListener(this);
        mPopWindowView.findViewById(R.id.pop_yiyuan).setOnClickListener(this);
        mPopWindowView.setBackgroundResource(R.drawable.send_comment_edittext_bg);
        mPopupWindow.setHeight((int) (48 * 4 * getResources().getDisplayMetrics().density));
        mPopupWindow.setWidth(view.getMeasuredWidth());
        mPopupWindow.setContentView(mPopWindowView);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAsDropDown(view, 0, (int) (5 * getResources().getDisplayMetrics().density));
    }

    private void dissPopWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    public void getSendDate(final int position) {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "1");
        ajaxParams.put("txt_findid", list.get(position).getTxt_id());
        Log.e("txt_findid", "");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                FindDTO newsDTO = new FindDTO();
                List<FindItem> newinforList = new ArrayList<FindDTO.FindItem>();
                if (Conn.IsNetWork) {

                    newsDTO.setTxt_code("0");
                    newsDTO.setTxt_message("请求成功");

                    for (int i = 0; i < 3; i++) {
                        FindItem newinfor = newsDTO.new FindItem();
                        newinfor.setTxt_name("测试昵称");
                        newinfor.setTxt_id(i + "");
                        newinfor.setTxt_url("");
                        newinfor.setTxt_content(
                                "测试作品，测试作品测试作品，测试作品测试作品，测试作品测试作品，测试作品测试作品，测试作品，测试作品测试作品，测试作品测试作品，测试作品，测试作品测试作品，测试作品测试作品，测试作品");
                        newinfor.setTxt_id("0");
                        newinfor.setTxt_praise("0");
                        newinfor.setTxt_talk("0");
                        newinfor.setTxt_time("04-11 10:00");
                        newinfor.setTxt_other("");
                        List<FindItemImageUrl> findItemImageUrlList = new ArrayList<FindDTO.FindItemImageUrl>();
                        for (int j = 0; j < 8; j++) {
                            FindItemImageUrl findItemImageUrl = newsDTO.new FindItemImageUrl();
                            findItemImageUrl.setTxt_other("测试信息");
                            findItemImageUrl
                                    .setTxt_url("http://www.gmw.cn/images/2005-12/18/xin_561202181152562033333.jpg");
                            findItemImageUrlList.add(findItemImageUrl);
                        }
                        newinfor.setTxt_imglist(findItemImageUrlList);
                        newinforList.add(newinfor);
                    }
                    newsDTO.setFindItemList(newinforList);

                } else {
                    Gson gson = new Gson();
                    newsDTO = gson.fromJson(t, FindDTO.class);
                }
                if (newsDTO.getTxt_code().equals("0")) {

                    findItemList.clear();

                    findItemList.addAll(newsDTO.getFindItemList());

                    Intent intent = new Intent(SearchActivity.this, DiscoverDetailsActivity.class);
                    intent.putExtra(DiscoverDetailsActivity.DISCOVER_DETAILS_ID, findItemList.get(0).getTxt_id());
                    intent.putExtra(DiscoverDetailsActivity.DISCOVER_DETAILS_TITLE, findItemList.get(0).getTxt_name());

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(DiscoverDetailsActivity.HEAD_BEAN, findItemList.get(0));
                    intent.putExtras(bundle);
                    startActivity(intent);

                } else {
                    Utils.showToast(SearchActivity.this, newsDTO.getTxt_message());
                }
            }

            @Override
            public void start() {

            }

            @Override
            public void error() {

            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.FindList);
    }

    public void onFinish(View view) {
        finish();
    }

    // 下拉刷新
    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

        PagerNum = 1;
        list.clear();
        getDataFormService();

        new Handler().postDelayed(new Runnable() {

            public void run() {
                swipeRefreshLayout.endRefreshing();
            }
        }, 1000);

    }

    // 上拉加载
    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        getDataFormService();
        swipeRefreshLayout.closeLoadingMore();
        return true;
    }

    protected void addFailedView() {
        if (failedView == null) {
            failedView = getLayoutInflater().inflate(R.layout.net_error_view, null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.BELOW, R.id.header);
            content.addView(failedView, params);
            failedView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    PagerNum = 1;
                    getDataFormService();
                }
            });
        }

    }

    protected void removeFailedView() {
        if (failedView != null) {
            content.removeView(failedView);
            failedView = null;
        }
    }

}
