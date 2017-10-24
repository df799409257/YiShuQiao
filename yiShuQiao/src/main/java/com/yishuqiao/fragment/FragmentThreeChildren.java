package com.yishuqiao.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.video.VideoPlayActivity;
import com.yishuqiao.adapter.FragmentThreeAdapter;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.CarouselPicDTO;
import com.yishuqiao.dto.Imglist;
import com.yishuqiao.dto.VideoDTO;
import com.yishuqiao.dto.VideoDTO.ViewInfor;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.okhttp.OkHttpUtils;
import com.yishuqiao.okhttp.callback.Callback;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.ListViewDataUtils;
import com.yishuqiao.view.CustomProgressDialog;
import com.yishuqiao.view.refreshlayout.BGAMeiTuanRefreshViewHolder;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout.BGARefreshLayoutDelegate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import net.tsz.afinal.http.AjaxParams;

import org.xutils.http.RequestParams;
import org.xutils.x;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName FragmentThreeChildren
 * @Description TODO(FragmentThree的子Fragment)
 * @Date 2017年8月9日 下午2:13:53
 */
public class FragmentThreeChildren extends ForResultNestedCompatFragment implements BGARefreshLayoutDelegate {

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
    private BGAMeiTuanRefreshViewHolder meiTuanRefreshViewHolder = null;
    private RelativeLayout content;
    private View failedView = null;

    private int PagerNum = 1;// 分页加载

    private String VideoType = null;

    private FragmentThreeAdapter adapter;
    private int width;
    public List<ViewInfor> list = new ArrayList<ViewInfor>();
    public ListView lv_listview;

    private View rootView;

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
            rootView = inflater.inflate(R.layout.fragment_three_children, null);
            initView(rootView);// 控件初始化
        }

        return rootView;
    }

    @SuppressWarnings("deprecation")
    private void initView(View view) {

        Bundle bundle = getArguments();
        VideoType = bundle.getString("type");

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();

        content = (RelativeLayout) view.findViewById(R.id.content);

        lv_listview = (ListView) view.findViewById(R.id.lv_listview);

        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.empty_view, null);
        LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(-1, -1);
        emptyParams.gravity = Gravity.CENTER;
        ((ViewGroup) lv_listview.getParent()).addView(emptyView, emptyParams);
        lv_listview.setEmptyView(emptyView);

        adapter = new FragmentThreeAdapter(list, getActivity(), width);
        lv_listview.setAdapter(adapter);
        lv_listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                clickPosition = arg2;
                getListPosition();

                Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
                intent.putExtra(VideoPlayActivity.VIDEO_PLAY_ID, list.get(arg2).getTxt_id());
                intent.putExtra(VideoPlayActivity.VIDEO_PLAY_TITLE, list.get(arg2).getTxt_content());

                startActivityForResult(intent, 2);

            }
        });

        myRefreshLayout = (BGARefreshLayout) view.findViewById(R.id.myRefreshLayout);
        meiTuanRefreshViewHolder = new BGAMeiTuanRefreshViewHolder(getActivity(), true);
        meiTuanRefreshViewHolder.setPullDownImageResource(R.drawable.bga_refresh_loading01);
        meiTuanRefreshViewHolder.setChangeToReleaseRefreshAnimResId(R.anim.pull_refresh);
        meiTuanRefreshViewHolder.setRefreshingAnimResId(R.anim.pull_refreshing);
        myRefreshLayout.setRefreshViewHolder(meiTuanRefreshViewHolder);
        myRefreshLayout.setPullDownRefreshEnable(true);
        myRefreshLayout.setDelegate(this);

        getData();
    }

    // 记住listview的位置 保存当前第一个可见的item的索引和偏移量
    private void getListPosition() {

        listPosition = lv_listview.getFirstVisiblePosition();
        View v = lv_listview.getChildAt(0);
        listTop = (v == null) ? 0 : v.getTop();

        ListViewDataUtils.saveTalk(list.get(clickPosition).getTxt_talk());
        ListViewDataUtils.saveShow(Integer.parseInt(list.get(clickPosition).getTxt_show()) + 11 + "");
        ListViewDataUtils.savePraise(list.get(clickPosition).getTxt_praise());

    }

    public void getData() {
//        if (PagerNum == 1) {
            showLoadingView();
//        }

        RequestParams params = new RequestParams(HttpUrl.VideoList);
        params.addBodyParameter("type", "0");
        params.addBodyParameter("txt_text", VideoType);
        params.addBodyParameter("pagerindex", Conn.PagerSize);
        params.addBodyParameter("pagernum", "" + PagerNum++);
            x.http().post(params, new org.xutils.common.Callback.CommonCallback<String>() {
                @Override
                public void onCancelled(CancelledException arg0) {


                }


                @Override
                public void onError(Throwable arg0, boolean arg1) {
                    dismissLoadingView();
                    myRefreshLayout.endLoadingMore();
                    addFailedView();
                }


                @Override
                public void onFinished() {

                }


                @Override
                public void onSuccess(String arg0) {
                    {

                        if (PagerNum == 2) {
                            list.clear();
                        }
                        MyApplication.MySharedPreferences.saveFragment3(arg0);
                        myRefreshLayout.endLoadingMore();
                        dismissLoadingView();
                        removeFailedView();

                        VideoDTO newsDTO = new VideoDTO();
                        List<ViewInfor> newinforList = new ArrayList<ViewInfor>();
                        if (Conn.IsNetWork) {

                            newsDTO.setTxt_code("0");
                            newsDTO.setTxt_message("请求成功");
                            for (int i = 0; i < 3; i++) {
                                ViewInfor newinfor = newsDTO.new ViewInfor();
                                newinfor.setTxt_content("测试昵称");
                                newinfor.setTxt_id("0");
                                newinfor.setTxt_praise("0");
                                newinfor.setTxt_talk("10");
                                newinfor.setTxt_show("303");
                                newinfor.setTxt_duration("23:02");

                                List<Imglist> imglist = new ArrayList<Imglist>();
                                for (int j = 0; j < 0; j++) {
                                    Imglist pic = new Imglist();
                                    pic.setTxt_url(
                                            "http://d.hiphotos.baidu.com/zhidao/pic/item/241f95cad1c8a7862239b60f6109c93d70cf50a4.jpg");
                                    imglist.add(pic);
                                }

                                newinfor.setTxt_imglist(imglist);

                                newinforList.add(newinfor);
                            }
                            newsDTO.setViewInforList(newinforList);
                        } else {
                            Gson gson = new Gson();
                            newsDTO = gson.fromJson(arg0, VideoDTO.class);
                        }
                        if (newsDTO.getTxt_code().equals("0")) {
                            list.addAll(newsDTO.getViewInforList());
                            adapter.notifyDataSetChanged();

                            if (list.size() < Integer.parseInt(Conn.PagerSize)) {
                                myRefreshLayout.closeLoadingMore();
                            } else {
                                myRefreshLayout.openLoadingMore();
                            }
                        }
                    }
                }
            });



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
     *
     * @param isTouchAble true:可点击 false:不可点击 默认是可点击
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

    @Override
    public void onActivityResultNestedCompat(int requestCode, int resultCode, Intent data) {

        if (2 == requestCode && resultCode == Activity.RESULT_OK) {
            // 刷新数据
            list.get(clickPosition).setTxt_show(ListViewDataUtils.readShow());
            list.get(clickPosition).setTxt_talk(ListViewDataUtils.readTalk());
            list.get(clickPosition).setTxt_praise(ListViewDataUtils.readPraise());
            adapter.notifyDataSetChanged();

            // 根据上次保存的index和偏移量恢复上次的位置
            lv_listview.setSelectionFromTop(listPosition, listTop);
        }

    }

}
