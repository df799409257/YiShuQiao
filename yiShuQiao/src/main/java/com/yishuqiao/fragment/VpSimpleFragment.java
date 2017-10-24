package com.yishuqiao.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.CommentWebViewActivity;
import com.yishuqiao.activity.discover.WriterPersonalCenterActivity;
import com.yishuqiao.adapter.FragmentFistAdapter;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.CarouselPicDTO;
import com.yishuqiao.dto.NewsDTO;
import com.yishuqiao.dto.NewsDTO.NewInfor;
import com.yishuqiao.dto.NewsDTO.NewInfor.ItemImageUrlList;
import com.yishuqiao.dto.NewsSortDTO;
import com.yishuqiao.dto.PublicDTO;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.okhttp.OkHttpUtils;
import com.yishuqiao.okhttp.callback.Callback;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.ListViewDataUtils;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.CustomProgressDialog;
import com.yishuqiao.view.refreshlayout.BGAMeiTuanRefreshViewHolder;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout.BGARefreshLayoutDelegate;
import com.yishuqiao.view.smarttablayout.utils.v4.FragmentPagerItem;
import com.yishuqiao.view.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.yishuqiao.view.smarttablayout.utils.v4.FragmentPagerItems;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.tsz.afinal.http.AjaxParams;

import org.xutils.http.RequestParams;
import org.xutils.x;

import okhttp3.Call;
import okhttp3.Response;

public class VpSimpleFragment extends ForResultNestedCompatFragment implements BGARefreshLayoutDelegate {

    private int clickPosition = 0;// 记录listview点击的位置
    private int listPosition = 0;// 记录listview可见的位置
    private int listTop = 0;// 记录listview点击的item的偏移量

    private static final int DISMISS = 1001;
    private static final int SHOW = 1002;
    private CustomProgressDialog progressDialog = null;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW:
                    if (progressDialog != null) {
                        progressDialog.setTouchAble((Boolean) msg.obj);
                        progressDialog.show();
                    }
                    break;
                case DISMISS:
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private BGARefreshLayout myRefreshLayout;
    private RelativeLayout content;
    private View failedView = null;

    private int PagerNum = 1;// 分页加载

    private ListView lv_listview;
    private FragmentFistAdapter adapter;
    private List<NewInfor> newsList = new ArrayList<NewInfor>();

    private String NewsType = null;

    private View rootView;

    private int screen_width;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {

            if (msg.what == 0) {

                clickPosition = msg.arg1;
                getListPosition();

                NewInfor newInfor = newsList.get(msg.arg1);

                Intent intent = new Intent(getActivity(), CommentWebViewActivity.class);
                intent.putExtra(CommentWebViewActivity.ITEM_ID, newInfor.getTxt_id());
                intent.putExtra(CommentWebViewActivity.ITEM_TYPE, "2");
                intent.putExtra("txt_title", "资讯详情");
                intent.putExtra(CommentWebViewActivity.ACTION_TYPE, "1");

                Bundle bundle = new Bundle();
                bundle.putSerializable(CommentWebViewActivity.HEAD_BEAN, newInfor);
                intent.putExtras(bundle);

                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "1");
                params.put("txt_id", newInfor.getTxt_id());
                intent.putExtra(CommentWebViewActivity.URL_PARAMS, (Serializable) params);

                startActivityForResult(intent, 1);

            } else if (msg.what == 1) {

                if (Utils.checkAnony(getActivity())) {
                    if (newsList.get(msg.arg1).getTxt_isfollow().equals("0")) {
                        chagePhone(msg.arg1, false);
                    } else if (newsList.get(msg.arg1).getTxt_isfollow().equals("1")) {
                        chagePhone(msg.arg1, true);
                    }
                }

            } else if (msg.what == 2) {

                Intent intent = new Intent(getActivity(), WriterPersonalCenterActivity.class);
                intent.putExtra(WriterPersonalCenterActivity.USERID, newsList.get(msg.arg1).getTxt_userid());
                intent.putExtra(WriterPersonalCenterActivity.TITLE, newsList.get(msg.arg1).getTxt_name());
                startActivity(intent);

            }

        }

        ;
    };

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(getActivity());
        }

        if (null != rootView) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent) {
                parent.removeView(rootView);
            }
        } else {
            rootView = inflater.inflate(R.layout.view_first, null);
            init(rootView);// 控件初始化
        }
        return rootView;
    }

    public void init(View v) {

        Bundle bundle = getArguments();
        NewsType = bundle.getString("type");

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screen_width = dm.widthPixels;

        content = (RelativeLayout) v.findViewById(R.id.content);

        myRefreshLayout = (BGARefreshLayout) v.findViewById(R.id.myRefreshLayout);
        BGAMeiTuanRefreshViewHolder meiTuanRefreshViewHolder = new BGAMeiTuanRefreshViewHolder(getActivity(), true);
        meiTuanRefreshViewHolder.setPullDownImageResource(R.drawable.bga_refresh_loading01);
        meiTuanRefreshViewHolder.setChangeToReleaseRefreshAnimResId(R.anim.pull_refresh);
        meiTuanRefreshViewHolder.setRefreshingAnimResId(R.anim.pull_refreshing);
        myRefreshLayout.setRefreshViewHolder(meiTuanRefreshViewHolder);
        myRefreshLayout.setPullDownRefreshEnable(true);
        myRefreshLayout.setDelegate(this);

        lv_listview = (ListView) v.findViewById(R.id.lv_listview);

        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.empty_view, null);
        LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(-1, -1);
        emptyParams.gravity = Gravity.CENTER;
        ((ViewGroup) lv_listview.getParent()).addView(emptyView, emptyParams);
        lv_listview.setEmptyView(emptyView);

        adapter = new FragmentFistAdapter(getActivity(), newsList, handler);
        lv_listview.setAdapter(adapter);
        lv_listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub

                clickPosition = arg2;
                getListPosition();

                NewInfor newInfor = newsList.get(arg2);

                Intent intent = new Intent(getActivity(), CommentWebViewActivity.class);
                intent.putExtra(CommentWebViewActivity.ITEM_ID, newInfor.getTxt_id());
                intent.putExtra(CommentWebViewActivity.ITEM_TYPE, "2");
                intent.putExtra("txt_title", "资讯详情");
                intent.putExtra(CommentWebViewActivity.ACTION_TYPE, "1");

                Bundle bundle = new Bundle();
                bundle.putSerializable(CommentWebViewActivity.HEAD_BEAN, newInfor);
                intent.putExtras(bundle);

                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "1");
                params.put("txt_id", newInfor.getTxt_id());
                intent.putExtra(CommentWebViewActivity.URL_PARAMS, (Serializable) params);

                startActivityForResult(intent, 1);

            }
        });

        getData();

    }

    // 记住listview的位置 保存当前第一个可见的item的索引和偏移量
    private void getListPosition() {

        listPosition = lv_listview.getFirstVisiblePosition();
        View v = lv_listview.getChildAt(0);
        listTop = (v == null) ? 0 : v.getTop();

        ListViewDataUtils.saveTalk(newsList.get(clickPosition).getTxt_talk());
        ListViewDataUtils.saveShow(Integer.parseInt(newsList.get(clickPosition).getTxt_show()) + 11 + "");
        ListViewDataUtils.saveIsfollow(newsList.get(clickPosition).getTxt_isfollow());
        ListViewDataUtils.savePraise(newsList.get(clickPosition).getTxt_praise());
        ListViewDataUtils.saveIsfav(newsList.get(clickPosition).getTxt_isfav());
        ListViewDataUtils.saveIsSP(newsList.get(clickPosition).getTxt_isSP());

    }

    public void getData() {
//        if (PagerNum == 1) {
            showLoadingView();
//        }
        RequestParams params = new RequestParams(HttpUrl.NewsList);
        params.addBodyParameter("type", "0");
        params.addBodyParameter("txt_text", NewsType);
        params.addBodyParameter("txt_userid", MyApplication.MySharedPreferences.readUserid());
        params.addBodyParameter("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        params.addBodyParameter("pagerindex", Conn.PagerSize);
        params.addBodyParameter("pagernum", "" + PagerNum++);
        params.addBodyParameter("screen_width", screen_width + "");
        x.http().post(params, new org.xutils.common.Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException arg0) {


            }


            @Override
            public void onError(Throwable arg0, boolean arg1) {
                dismissLoadingView();
                removeFailedView();
                myRefreshLayout.endLoadingMore();
//                        addFailedView();

                Gson gson = new Gson();
                NewsDTO newsDTO = gson.fromJson(MyApplication.MySharedPreferences.readFragment2(), NewsDTO.class);
                newsList.addAll(newsDTO.getNewInforList());

                adapter.notifyDataSetChanged();

                if (newsList.size() < Integer.parseInt(Conn.PagerSize)) {
                    myRefreshLayout.closeLoadingMore();
                } else {
                    myRefreshLayout.openLoadingMore();
                }

            }


            @Override
            public void onFinished() {

            }


            @Override
            public void onSuccess(String arg0) {
                {

                    if (PagerNum == 2) {
                        newsList.clear();
                    }
                    MyApplication.MySharedPreferences.saveFragment2(arg0);

                    myRefreshLayout.endLoadingMore();
                    dismissLoadingView();
                    removeFailedView();

                    NewsDTO newsDTO = new NewsDTO();
                    List<NewInfor> newinforList = new ArrayList<NewInfor>();

                    if (Conn.IsNetWork) {

                        newsDTO.setTxt_code("0");
                        newsDTO.setTxt_message("请求成功");
                        for (int i = 0; i < 6; i++) {
                            NewInfor newinfor = newsDTO.new NewInfor();
                            newinfor.setTxt_name("测试昵称");
                            newinfor.setTxt_url("");
                            newinfor.setTxt_content("测试作品，测试作品");
                            newinfor.setTxt_show("167");
                            newinfor.setTxt_id("0");
                            newinfor.setTxt_other("");
                            newinfor.setTxt_praise("0");
                            newinfor.setTxt_talk("167");
                            newinfor.setTxt_time("04-11 10:00");

                            ItemImageUrlList imageviewList = newinfor.new ItemImageUrlList();
                            List<ItemImageUrlList> imglist = new ArrayList<ItemImageUrlList>();
                            for (int j = 0; j < 0; j++) {
                                imageviewList.setTxt_url("http://s8.sinaimg.cn/middle/001PMEuOzy7a7uLYghL9d");
                                imageviewList.setTxt_id("1");
                                imageviewList.setTxt_other("1");
                                imglist.add(imageviewList);
                            }

                            newinfor.setTxt_imglist(imglist);

                            newinforList.add(newinfor);
                        }
                        newsDTO.setNewInforList(newinforList);
                    } else {
                        Gson gson = new Gson();
                        newsDTO = gson.fromJson(arg0, NewsDTO.class);
                    }
                    if (newsDTO.getTxt_code().equals("0")) {

                        newsList.addAll(newsDTO.getNewInforList());

                        adapter.notifyDataSetChanged();

                        if (newsList.size() < Integer.parseInt(Conn.PagerSize)) {
                            myRefreshLayout.closeLoadingMore();
                        } else {
                            myRefreshLayout.openLoadingMore();
                        }


                    }

                }

            }
        });

    }


    /**
     * 关注与被关注
     */
    private void chagePhone(final int position, final boolean isFollow) {

        if (isFollow) {

            Map<String, String> params = new HashMap<String, String>();
            params.put("type", "1");
            params.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
            params.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
            params.put("pa_user_id", newsList.get(position).getTxt_userid());
            OkHttpUtils.post().url(HttpUrl.Urlmy + "user/follow.php").params(params).build()
                    .execute(new Callback<String>() {

                        @Override
                        public String parseNetworkResponse(Response response) throws Exception {

                            return response.body().string();
                        }

                        @Override
                        public void onError(Call call, Exception e) {
                            Log.e("Error = ", e.getMessage());

                        }

                        @Override
                        public void onResponse(String response) {

                            PublicDTO dto = new PublicDTO();
                            if (Conn.IsNetWork) {
                                dto.setTxt_code("0");
                                dto.setTxt_message("关注成功");
                            } else {
                                Gson gson = new Gson();
                                dto = gson.fromJson(response, PublicDTO.class);
                            }
                            if (dto.getTxt_code().equals("0")) {
                                if (isFollow) {
                                    newsList.get(position).setTxt_isfollow("0");
                                } else {
                                    newsList.get(position).setTxt_isfollow("1");
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Utils.showToast(getActivity(), dto.getTxt_message());
                            }

                        }

                    });

        } else {

            AjaxParams ajaxParams = new AjaxParams();
            ajaxParams.put("type", "1");
            ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
            ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
            ajaxParams.put("pa_user_id", newsList.get(position).getTxt_userid());
            HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

                @Override
                public void succ(String t) {
                    PublicDTO dto = new PublicDTO();
                    if (Conn.IsNetWork) {
                        dto.setTxt_code("0");
                        dto.setTxt_message("关注成功");
                    } else {
                        Gson gson = new Gson();
                        dto = gson.fromJson(t, PublicDTO.class);
                    }
                    if (dto.getTxt_code().equals("0")) {
                        if (isFollow) {
                            newsList.get(position).setTxt_isfollow("0");
                        } else {
                            newsList.get(position).setTxt_isfollow("1");
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Utils.showToast(getActivity(), dto.getTxt_message());
                    }
                }

                @Override
                public void start() {
                }

                @Override
                public void error() {
                }

            }, getActivity());
            httpNetWork.postHttp(ajaxParams, HttpUrl.Urlmy + "user/follow.php");

        }

    }

    public void newsRefrash() {

    }

    @Override
    public void onActivityResultNestedCompat(int requestCode, int resultCode, Intent data) {

        if (1 == requestCode && resultCode == Activity.RESULT_OK) {
            // 刷新数据
            newsList.get(clickPosition).setTxt_isfollow(ListViewDataUtils.readIsfollow());
            newsList.get(clickPosition).setTxt_isfav(ListViewDataUtils.readIsfav());
            newsList.get(clickPosition).setTxt_isSP(ListViewDataUtils.readIssp());
            newsList.get(clickPosition).setTxt_show(ListViewDataUtils.readShow());
            newsList.get(clickPosition).setTxt_talk(ListViewDataUtils.readTalk());
            newsList.get(clickPosition).setTxt_praise(ListViewDataUtils.readPraise());
            Log.e("zsy", "ListViewDataUtils.readIsfollow()=" + ListViewDataUtils.readIsfollow());
            adapter.notifyDataSetChanged();

            // 根据上次保存的index和偏移量恢复上次的位置
            lv_listview.setSelectionFromTop(listPosition, listTop);
        }

    }

    // 下拉刷新
    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

        PagerNum = 1;
        getData();

        new Handler().postDelayed(new Runnable() {

            public void run() {
                myRefreshLayout.endRefreshing();
            }
        }, 1000);

    }

    // 上拉加载
    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        getData();
        myRefreshLayout.closeLoadingMore();
        return true;
    }

    protected void addFailedView() {
        if (failedView == null) {
            failedView = getActivity().getLayoutInflater().inflate(R.layout.net_error_view, null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.BELOW, R.id.header);
            content.addView(failedView, params);
            failedView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    PagerNum = 1;
                    getData();
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

    /**
     * 显示加载视图
     */
    protected void showLoadingView(boolean isTouchAble) {
        Message m = mHandler.obtainMessage(SHOW, isTouchAble);
        mHandler.sendMessage(m);
    }

    protected void showLoadingView() {
        showLoadingView(true);
    }

    /**
     * 关闭加载视图
     */
    protected void dismissLoadingView() {
        mHandler.sendEmptyMessage(DISMISS);
    }

    protected boolean isShowingLoadingView() {
        if (progressDialog != null) {
            return progressDialog.isShowing();
        } else {
            return false;
        }
    }

    @Override
    public void onPause() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
        super.onPause();
    }

    @Override
    public void onResume() {

        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(getActivity());
        }
        super.onResume();
    }

}
