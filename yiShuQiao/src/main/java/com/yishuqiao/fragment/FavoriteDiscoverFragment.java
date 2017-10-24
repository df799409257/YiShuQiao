package com.yishuqiao.fragment;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.discover.DiscoverDetailsActivity;
import com.yishuqiao.adapter.FavoriteAdapter;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.CollectDTO;
import com.yishuqiao.dto.FavoriteDTO;
import com.yishuqiao.dto.FavoriteDTO.CollectInfoList;
import com.yishuqiao.dto.FindDTO;
import com.yishuqiao.dto.FindDTO.FindItem;
import com.yishuqiao.dto.FindDTO.FindItemImageUrl;
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
 * @author Administrator
 * @version 1.0.0
 * @ClassName FavoriteDiscoverFragment
 * @Description TODO(我的收藏 发现界面)
 * @Date 2017年7月29日 下午6:22:32
 */
public class FavoriteDiscoverFragment extends Fragment implements BGARefreshLayoutDelegate {

    private ListView listview = null;
    private View failedView = null;
    private BGARefreshLayout swipeRefreshLayout;
    private RelativeLayout content = null;
    private CustomProgressDialog progressDialog = null;
    private View rootView;

    private FavoriteAdapter adapter;
    private List<FavoriteDTO.CollectInfoList> list = new ArrayList<FavoriteDTO.CollectInfoList>();

    private List<FindItem> findItemList = new ArrayList<FindDTO.FindItem>();
    int pagerindex = 1;
    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    cancleFavorite(msg.arg1);
                    break;

                default:
                    break;
            }
        }

    };

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
            rootView = inflater.inflate(R.layout.fragment_information, null);
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

    @Override
    public void onPause() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
        super.onPause();
    }

    @SuppressLint("InflateParams")
    private void initView(View view) {

        listview = (ListView) view.findViewById(R.id.listview);

        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.empty_view, null);
        LinearLayout.LayoutParams emptyparams = new LinearLayout.LayoutParams(-1, -1);
        emptyparams.gravity = Gravity.CENTER;
        ((ViewGroup) listview.getParent()).addView(emptyView, emptyparams);
        listview.setEmptyView(emptyView);

        adapter = new FavoriteAdapter(getActivity(), list, handler);
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

    private void getDataFromService() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "0");
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_cate", "2");
        ajaxParams.put("pagerindex", "50");
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

                FavoriteDTO dto = new FavoriteDTO();
                List<FavoriteDTO.CollectInfoList> dataList = new ArrayList<FavoriteDTO.CollectInfoList>();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("请求成功");
                    CollectInfoList data = dto.new CollectInfoList();
                    data.setTxt_img(
                            "http://img.zcool.cn/community/01cee257f9eb87a84a0e282b086b70.jpg@900w_1l_2o_100sh.jpg");
                    data.setTxt_title("测试作品，测试作品");
                    data.setTxt_creattime("2017-7-4");
                    data.setTxt_id("0");
                    dataList.add(data);
                    dto.setCollectInfoList(dataList);
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, FavoriteDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {
                    list.addAll(dto.getCollectInfoList());
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
        httpNetWork.postHttp(ajaxParams, HttpUrl.Action);

    }

    // 取消收藏
    private void cancleFavorite(int position) {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "0");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("txt_workid", list.get(position).getTxt_id());
        ajaxParams.put("txt_cate", "1");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                progressDialog.dismiss();
                CollectDTO dataDTO = new CollectDTO();
                if (Conn.IsNetWork) {

                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("请求成功");

                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, CollectDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {
                    getDataFromService();
                    Utils.showToast(getActivity(), dataDTO.getCollect());

                } else {
                    Utils.showToast(getActivity(), dataDTO.getTxt_message());
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
            }

        }, getActivity());
        httpNetWork.postHttp(ajaxParams, HttpUrl.Action);

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
                // TODO Auto-generated method stub
                progressDialog.show();
            }

            @Override
            public void error() {
                // TODO Auto-generated method stub
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

}
