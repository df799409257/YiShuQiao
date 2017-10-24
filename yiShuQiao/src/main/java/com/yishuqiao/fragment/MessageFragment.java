package com.yishuqiao.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.CommentWebViewActivity;
import com.yishuqiao.activity.WebViewActivity;
import com.yishuqiao.activity.YiyuanWebViewActivity;
import com.yishuqiao.activity.discover.DiscoverDetailsActivity;
import com.yishuqiao.activity.video.VideoPlayActivity;
import com.yishuqiao.activity.yiyuan.WriterDetailsActivity;
import com.yishuqiao.adapter.MessageFragmentAdapter;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.FindDTO;
import com.yishuqiao.dto.SystemMessageDTO;
import com.yishuqiao.dto.FindDTO.FindItem;
import com.yishuqiao.dto.FindDTO.FindItemImageUrl;
import com.yishuqiao.dto.SystemMessageDTO.NewsInfor;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.CustomProgressDialog;
import com.yishuqiao.view.refreshlayout.BGAMeiTuanRefreshViewHolder;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout.BGARefreshLayoutDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import net.tsz.afinal.http.AjaxParams;

/**
 * 作者：admin 创建时间：2017-7-5 下午1:40:26 项目名称：YiShuQiao 文件名称：MessageFragment.java 类说明：系统消息界面
 */
public class MessageFragment extends Fragment implements BGARefreshLayoutDelegate {

    private ListView listview = null;
    private View failedView = null;
    private BGARefreshLayout swipeRefreshLayout;
    private RelativeLayout content = null;
    private CustomProgressDialog progressDialog = null;
    private View rootView;

    private List<SystemMessageDTO.NewsInfor> list = new ArrayList<SystemMessageDTO.NewsInfor>();
    private MessageFragmentAdapter adapter;

    private List<FindItem> findItemList = new ArrayList<FindDTO.FindItem>();
    private int pagerindex = 1;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(getActivity());
        }

        if (null != rootView) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent) {
                parent.removeView(rootView);
            }
        } else {
            rootView = inflater.inflate(R.layout.fragment_message, null);
            initView(rootView);// 控件初始化
        }
        return rootView;
    }

    @SuppressLint("InflateParams")
    private void initView(View view) {

        listview = (ListView) view.findViewById(R.id.listview);

        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.empty_view, null);
        LinearLayout.LayoutParams emptyparams = new LinearLayout.LayoutParams(-1, -1);
        emptyparams.gravity = Gravity.CENTER;
        ((ViewGroup) listview.getParent()).addView(emptyView, emptyparams);
        listview.setEmptyView(emptyView);

        adapter = new MessageFragmentAdapter(getActivity(), list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ((list.get(position).getTxt_type()).equals("1")) {
                    // 发现

                    getSendDate(position);

                } else if ((list.get(position).getTxt_type()).equals("2")) {
                    // 资讯

                    Intent intent = new Intent(getActivity(), CommentWebViewActivity.class);
                    intent.putExtra(CommentWebViewActivity.ITEM_ID, list.get(position).getTxt_id());
                    intent.putExtra(CommentWebViewActivity.ITEM_TYPE, "2");
                    intent.putExtra(CommentWebViewActivity.ACTION_TYPE, "1");
                    intent.putExtra("txt_title", "资讯详情");

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("type", "1");
                    params.put("txt_id", list.get(position).getTxt_id());
                    intent.putExtra(CommentWebViewActivity.URL_PARAMS, (Serializable) params);

                    startActivity(intent);

                } else if ((list.get(position).getTxt_type()).equals("3")) {
                    // 视频

                    Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
                    intent.putExtra(VideoPlayActivity.VIDEO_PLAY_ID, list.get(position).getTxt_id());
                    intent.putExtra(VideoPlayActivity.VIDEO_PLAY_TITLE, list.get(position).getTxt_title());
                    startActivity(intent);

                } else if ((list.get(position).getTxt_type()).equals("4")) {
                    // 艺苑

                    Intent intent = new Intent(getActivity(), WriterDetailsActivity.class);
                    intent.putExtra(WriterDetailsActivity.ITEMID, list.get(position).getTxt_id());
                    startActivity(intent);

                }

            }
        });

        content = (RelativeLayout) view.findViewById(R.id.content);
        swipeRefreshLayout = (BGARefreshLayout) view.findViewById(R.id.swipe_container);
        BGAMeiTuanRefreshViewHolder meiTuanRefreshViewHolder = new BGAMeiTuanRefreshViewHolder(getActivity(), true);
        meiTuanRefreshViewHolder.setPullDownImageResource(R.drawable.bga_refresh_loading01);
        meiTuanRefreshViewHolder.setChangeToReleaseRefreshAnimResId(R.anim.pull_refresh);
        meiTuanRefreshViewHolder.setRefreshingAnimResId(R.anim.pull_refreshing);
        swipeRefreshLayout.setRefreshViewHolder(meiTuanRefreshViewHolder);
        swipeRefreshLayout.setPullDownRefreshEnable(true);
        swipeRefreshLayout.setDelegate(this);
        getDataFromService();

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(getActivity());
        }

        super.onResume();
    }

    private void getDataFromService() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "2");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("pagerindex", Conn.PagerSize);
        ajaxParams.put("pagernum", pagerindex + "");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                if (pagerindex == 1) {
                    list.clear();
                    adapter.notifyDataSetChanged();
                }

                swipeRefreshLayout.endLoadingMore();
                progressDialog.dismiss();
                removeFailedView();
                SystemMessageDTO messageDTO = new SystemMessageDTO();
                List<SystemMessageDTO.NewsInfor> messageInfor = new ArrayList<SystemMessageDTO.NewsInfor>();
                if (Conn.IsNetWork) {
                    NewsInfor newinfor = messageDTO.new NewsInfor();
                    newinfor.setTxt_content("测试作品，测试作品");
                    newinfor.setTxt_id("0");
                    newinfor.setTxt_detailpic("");
                    newinfor.setTxt_time("");
                    newinfor.setTxt_userpic("");
                    newinfor.setTxt_title("");
                    newinfor.setTxt_toptitle("");
                    newinfor.setTxt_type("");
                    newinfor.setTxt_userpic("");
                    messageInfor.add(newinfor);
                } else {
                    Gson gson = new Gson();
                    messageDTO = gson.fromJson(t, SystemMessageDTO.class);
                }
                if (messageDTO.getTxt_code().equals("0")) {
                    list.addAll(messageDTO.getMyInfoList());
                    adapter.notifyDataSetChanged();
                    if (list.size() < Integer.parseInt(Conn.PagerSize)) {
                        swipeRefreshLayout.closeLoadingMore();
                    } else {
                        swipeRefreshLayout.openLoadingMore();
                    }
                } else {
                    if (pagerindex != 1) {
                        pagerindex--;
                    }
                }
            }

            @Override
            public void start() {
                // TODO Auto-generated method stub
                progressDialog.show();
            }

            @Override
            public void error() {
                // TODO Auto-generated method stub
                progressDialog.dismiss();
                addFailedView();
            }

        }, getActivity());
        httpNetWork.postHttp(ajaxParams, HttpUrl.Message);

    }

    public void getSendDate(final int position) {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "1");
        ajaxParams.put("txt_findid", list.get(position).getTxt_id());
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                progressDialog.dismiss();
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
                        newinfor.setTxt_talk("10");
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

                    Intent intent = new Intent(getActivity(), DiscoverDetailsActivity.class);
                    intent.putExtra(DiscoverDetailsActivity.DISCOVER_DETAILS_ID, findItemList.get(0).getTxt_id());
                    intent.putExtra(DiscoverDetailsActivity.DISCOVER_DETAILS_TITLE, findItemList.get(0).getTxt_name());

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(DiscoverDetailsActivity.HEAD_BEAN, findItemList.get(0));
                    intent.putExtras(bundle);
                    startActivity(intent);

                } else {
                    Utils.showToast(getActivity(), newsDTO.getTxt_message());
                }
            }

            @Override
            public void start() {
                progressDialog.show();
            }

            @Override
            public void error() {
                progressDialog.dismiss();
            }

        }, getActivity());
        httpNetWork.postHttp(ajaxParams, HttpUrl.FindList);
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

        pagerindex = 1;
        getDataFromService();

        new Handler().postDelayed(new Runnable() {

            public void run() {
                swipeRefreshLayout.endRefreshing();
            }
        }, 1000);

    }

    // 上拉加载
    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        pagerindex++;
        getDataFromService();
        swipeRefreshLayout.closeLoadingMore();
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
                    pagerindex = 1;
                    getDataFromService();
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

    @Override
    public void onPause() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
        super.onPause();
    }

}
