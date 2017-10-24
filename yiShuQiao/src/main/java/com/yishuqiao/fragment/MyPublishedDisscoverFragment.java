package com.yishuqiao.fragment;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.http.AjaxParams;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.discover.DiscoverDetailsActivity;
import com.yishuqiao.adapter.MyCommunicationFragmentAdapter;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.FindDTO;
import com.yishuqiao.dto.FindDTO.FindItem;
import com.yishuqiao.dto.FindDTO.FindItemImageUrl;
import com.yishuqiao.dto.MyDiscoverFragmentDTO;
import com.yishuqiao.dto.MyDiscoverFragmentDTO.SubmitFindList;
import com.yishuqiao.dto.MyDiscoverFragmentDTO.SubmitFindList.ImgList;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.ListViewDataUtils;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.CustomProgressDialog;
import com.yishuqiao.view.refreshlayout.BGAMeiTuanRefreshViewHolder;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout.BGARefreshLayoutDelegate;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName MyCommunicationFragment
 * @Description TODO(我发起的 发现界面)
 * @Date 2017年7月29日 下午8:20:52
 */
public class MyPublishedDisscoverFragment extends Fragment implements BGARefreshLayoutDelegate {

    private int clickPosition = 0;// 记录listview点击的位置
    private int listPosition = 0;// 记录listview可见的位置
    private int listTop = 0;// 记录listview点击的item的偏移量

    private List<FindItem> findItemList = new ArrayList<FindDTO.FindItem>();

    private ListView listview = null;
    private View failedView = null;
    private BGARefreshLayout swipeRefreshLayout;
    private RelativeLayout content = null;
    private CustomProgressDialog progressDialog = null;
    private View rootView;

    private MyCommunicationFragmentAdapter adapter;
    private List<MyDiscoverFragmentDTO.SubmitFindList> list = new ArrayList<MyDiscoverFragmentDTO.SubmitFindList>();
    int pagerindex = 1;

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
            rootView = inflater.inflate(R.layout.fragment_mycommunication, null);
            initView(rootView);// 控件初始化
        }
        return rootView;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(getActivity());
        }

        super.onResume();
    }

    @SuppressLint("InflateParams")
    private void initView(View view) {

        listview = (ListView) view.findViewById(R.id.listview);

        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.empty_view, null);
        LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(-1, -1);
        emptyParams.gravity = Gravity.CENTER;
        ((ViewGroup) listview.getParent()).addView(emptyView, emptyParams);
        listview.setEmptyView(emptyView);

        adapter = new MyCommunicationFragmentAdapter(getActivity(), list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                getSendDate(position);

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

    // 记住listview的位置 保存当前第一个可见的item的索引和偏移量
    private void getListPosition() {

        listPosition = listview.getFirstVisiblePosition();
        View v = listview.getChildAt(0);
        listTop = (v == null) ? 0 : v.getTop();

        ListViewDataUtils.saveCollect(list.get(clickPosition).getTxt_collect());
        ListViewDataUtils.saveShare(list.get(clickPosition).getTxt_share());
        ListViewDataUtils.saveTalk(list.get(clickPosition).getTxt_talk());
        ListViewDataUtils.saveShow(Integer.parseInt(list.get(clickPosition).getTxt_show()) + 1 + "");
        ListViewDataUtils.savePraise(list.get(clickPosition).getTxt_praise());

    }

    private void getDataFromService() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "0");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("pagerindex", "10");
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
                MyDiscoverFragmentDTO dto = new MyDiscoverFragmentDTO();
                List<MyDiscoverFragmentDTO.SubmitFindList> dataList = new ArrayList<MyDiscoverFragmentDTO.SubmitFindList>();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("请求成功");
                    SubmitFindList data = dto.new SubmitFindList();
                    data.setTxt_content("测试作品，测试作品");
                    data.setTxt_praise("100");
                    data.setTxt_share("100");
                    data.setTxt_time("2017-7-4");
                    data.setTxt_id("0");
                    data.setTxt_state("已通过");
                    data.setTxt_talk("100");
                    data.setTxt_type("1");
                    data.setTxt_show("100");
                    dataList.add(data);

                    ImgList imgList = data.new ImgList();
                    for (int i = 0; i < 1; i++) {
                        imgList.setTxt_url(
                                "http://img.zcool.cn/community/01cee257f9eb87a84a0e282b086b70.jpg@900w_1l_2o_100sh.jpg");
                    }

                    dto.setSubmitFindList(dataList);
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, MyDiscoverFragmentDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {
                    list.addAll(dto.getSubmitFindList());
                    adapter.notifyDataSetChanged();
                    if (list.size() < Integer.parseInt(Conn.PagerSize)) {
                        swipeRefreshLayout.closeLoadingMore();
                    } else {
                        swipeRefreshLayout.openLoadingMore();
                    }
                } else {
                    if (pagerindex == 1) {

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
        httpNetWork.postHttp(ajaxParams, HttpUrl.mySubmit);

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

                    clickPosition = position;
                    getListPosition();

                    findItemList.clear();

                    findItemList.addAll(newsDTO.getFindItemList());

                    Intent intent = new Intent(getActivity(), DiscoverDetailsActivity.class);
                    intent.putExtra(DiscoverDetailsActivity.DISCOVER_DETAILS_ID, findItemList.get(0).getTxt_id());
                    intent.putExtra(DiscoverDetailsActivity.DISCOVER_DETAILS_TITLE, findItemList.get(0).getTxt_name());

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(DiscoverDetailsActivity.HEAD_BEAN, findItemList.get(0));
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 0);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (0 == requestCode && resultCode == Activity.RESULT_OK) {
            // 刷新数据
            list.get(clickPosition).setTxt_share(ListViewDataUtils.readShare());
            list.get(clickPosition).setTxt_collect(ListViewDataUtils.readCollect());
            list.get(clickPosition).setTxt_show(ListViewDataUtils.readShow());
            list.get(clickPosition).setTxt_talk(ListViewDataUtils.readTalk());
            list.get(clickPosition).setTxt_praise(ListViewDataUtils.readPraise());
            adapter.notifyDataSetChanged();

            // 根据上次保存的index和偏移量恢复上次的位置
            listview.setSelectionFromTop(listPosition, listTop);
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
