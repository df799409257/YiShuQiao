package com.yishuqiao.fragment;


import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.CommentWebViewActivity;
import com.yishuqiao.activity.PreviewVideoActivity;
import com.yishuqiao.activity.PublishTextActivity;
import com.yishuqiao.activity.PublishVideoActivity;
import com.yishuqiao.activity.PublishVideoPopActivity;
import com.yishuqiao.activity.UpDateActivity;
import com.yishuqiao.activity.discover.DiscoverDetailsActivity;
import com.yishuqiao.activity.discover.WriterPersonalCenterActivity;
import com.yishuqiao.activity.video.VideoPlayActivity;
import com.yishuqiao.activity.yiyuan.WriterDetailsActivity;
import com.yishuqiao.adapter.FragmentFirstDifferentItemAdapter;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.CarouselPicDTO;
import com.yishuqiao.dto.CarouselPicDTO.FindDetail;
import com.yishuqiao.dto.FindDTO;
import com.yishuqiao.dto.FindDTO.FindItem;
import com.yishuqiao.dto.FindDTO.FindItemImageUrl;
import com.yishuqiao.dto.PublicDTO;
import com.yishuqiao.dto.UpDateDTO;
import com.yishuqiao.dto.UpDateDTO.UpdateInfo;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.okhttp.OkHttpUtils;
import com.yishuqiao.okhttp.callback.Callback;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.ListViewDataUtils;
import com.yishuqiao.utils.MoreWindow;
import com.yishuqiao.utils.MoreWindowResult;
import com.yishuqiao.utils.UriUtils;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.video.YWRecordVideoActivity;
import com.yishuqiao.view.AutoPlayingViewPager;
import com.yishuqiao.view.AutoPlayingViewPager.OnPageItemClickListener;
import com.yishuqiao.view.CustomProgressDialog;
import com.yishuqiao.view.refreshlayout.BGAMeiTuanRefreshViewHolder;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout.BGARefreshLayoutDelegate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.VideoColumns;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.tsz.afinal.http.AjaxParams;


import org.xutils.http.RequestParams;
import org.xutils.x;

import okhttp3.Call;
import okhttp3.Response;

public class FragmentFirst extends ForResultNestedCompatFragment
        implements BGARefreshLayoutDelegate {

    private boolean isLoading = true;
    private boolean loadFail = false;

    private static final int VIDEO_CAPTURE = 7;
    private static final int VIDEO_LOCAL = 101;

    private int clickPosition = 1;// 记录listview点击的位置
    private int listPosition = 1;// 记录listview可见的位置
    private int listTop = 1;// 记录listview点击的item的偏移量

    private static final int DISMISS = 1001;
    private static final int SHOW = 1002;
    private CustomProgressDialog progressDialog = null;

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.CAMERA" };

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
    private View failedView = null;

    private int PagerNum = 1;// 分页加载

    private MoreWindow mMoreWindow;

    private RelativeLayout layoutView = null;
    private TextView fab = null;

    private ListView lv_listviewOne;
    private List<FindItem> findItemList = new ArrayList<FindItem>();
    private FragmentFirstDifferentItemAdapter fragmentTwoAdapterOne;

    private String[] imageUrl = {"http://p1.pstatp.com/large/2ebb0003b179820c6dc1",
            "http://p3.pstatp.com/large/2ec0000207110a4fb764", "http://p3.pstatp.com/large/2ebc00021401da6a78a4",
            "http://p1.pstatp.com/large/2ec000020717087527bf", "http://p3.pstatp.com/large/2eba000470a47dfc8605",
            "http://p3.pstatp.com/large/2ebc000214088a1d1f6e", "http://p1.pstatp.com/large/2ebb0003b1849745aa8d"};

    private String[] imageTitle = new String[]{"当代艺术", "符合人类理解到的思维判断连贯性、规律性、因果以及逻辑等关系的形式", "艺术可以是宏观概念也可以是个体现象", "展览",
            "形象把握与理性把握的统一", "符合宇宙运动规律的过程在人类能够识别的范围内表现出来的形态与运动关系", "艺术形象贯穿于艺术活动的全过程"};

    private AutoPlayingViewPager slideShowView;// 顶部广告栏控件

    // 轮播图图片资源
    private List<FindDetail> carouselPicDTO = new ArrayList<FindDetail>();

    private boolean isFirst = true;// 避免轮播图启动播放重复调用

    private LinearLayout mHeaderView;

    private View rootView;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    if (Utils.checkAnony(getActivity())) {
                        if (findItemList.get(msg.arg1).getTxt_isfollow().equals("0")) {
                            chagePhone(msg.arg1, false);
                        } else if (findItemList.get(msg.arg1).getTxt_isfollow().equals("1")) {
                            chagePhone(msg.arg1, true);
                        }
                    }
                    break;
                case 1:
                    Intent intent = new Intent(getActivity(), WriterPersonalCenterActivity.class);
                    intent.putExtra(WriterPersonalCenterActivity.USERID, findItemList.get(msg.arg1).getTxt_userid());
                    intent.putExtra(WriterPersonalCenterActivity.TITLE, findItemList.get(msg.arg1).getTxt_name());
                    startActivity(intent);
                    break;
            }

        }

        ;
    };

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(getActivity());
        }

        if (null != rootView) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent) {
                parent.removeView(rootView);
            }
        } else {
            rootView = inflater.inflate(R.layout.fragment_first, null);
            init(rootView);// 控件初始化
        }

        return rootView;
    }

    @SuppressLint("InflateParams")
    public void init(View v) {

        mHeaderView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_hotvpsimple_head,
                null);

        slideShowView = (AutoPlayingViewPager) mHeaderView.findViewById(R.id.auto_play_viewpager);

        layoutView = (RelativeLayout) v.findViewById(R.id.layoutView);
        fab = new TextView(getActivity());
        fab.setBackgroundResource(R.drawable.send_topic_selector);
        RelativeLayout.LayoutParams fabParams = new RelativeLayout.LayoutParams(
                Utils.dipTopx(60, getResources().getDisplayMetrics().density),
                Utils.dipTopx(60, getResources().getDisplayMetrics().density));
        fabParams.rightMargin = 20;
        fabParams.bottomMargin = 20;
        fabParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        fabParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutView.addView(fab, fabParams);
        fab.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (Utils.checkAnony(getActivity())) {
                    showMoreWindow(v);
                }

            }
        });

        fragmentTwoAdapterOne = new FragmentFirstDifferentItemAdapter(getActivity(), findItemList, handler);
        lv_listviewOne = (ListView) v.findViewById(R.id.lv_listview);

        lv_listviewOne.addHeaderView(mHeaderView);

//        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.empty_view, null);
//        LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(-1, -1);
//        emptyParams.gravity = Gravity.CENTER;
//        ((ViewGroup) lv_listviewOne.getParent()).addView(emptyView, emptyParams);
//        lv_listviewOne.setEmptyView(emptyView);

        lv_listviewOne.setAdapter(fragmentTwoAdapterOne);
        lv_listviewOne.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (arg2 != 0) {

                    clickPosition = arg2 - 1;
                    getListPosition();

                    Intent intent = new Intent(getActivity(), DiscoverDetailsActivity.class);
                    intent.putExtra(DiscoverDetailsActivity.DISCOVER_DETAILS_ID,
                            findItemList.get(arg2 - 1).getTxt_id());
                    intent.putExtra(DiscoverDetailsActivity.DISCOVER_DETAILS_TITLE,
                            findItemList.get(arg2 - 1).getTxt_name());
                    intent.putExtra(DiscoverDetailsActivity.DISCOVER_DETAILS_IMA,
                            findItemList.get(arg2 - 1).getTxt_imglist().get(0).getTxt_url());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(DiscoverDetailsActivity.HEAD_BEAN, findItemList.get(arg2 - 1));
                    intent.putExtras(bundle);

                    startActivityForResult(intent, 0);

                }
            }

        });

        myRefreshLayout = (BGARefreshLayout) v.findViewById(R.id.myRefreshLayout);
        BGAMeiTuanRefreshViewHolder meiTuanRefreshViewHolder = new BGAMeiTuanRefreshViewHolder(getActivity(), true);
        meiTuanRefreshViewHolder.setPullDownImageResource(R.drawable.bga_refresh_loading01);
        meiTuanRefreshViewHolder.setChangeToReleaseRefreshAnimResId(R.anim.pull_refresh);
        meiTuanRefreshViewHolder.setRefreshingAnimResId(R.anim.pull_refreshing);
        myRefreshLayout.setRefreshViewHolder(meiTuanRefreshViewHolder);
        myRefreshLayout.setPullDownRefreshEnable(true);
        myRefreshLayout.setDelegate(this);

        getData();

        // 轮播图
        initBanner();

    }

    // 记住listview的位置 保存当前第一个可见的item的索引和偏移量
    private void getListPosition() {

        listPosition = lv_listviewOne.getFirstVisiblePosition();
        View v = lv_listviewOne.getChildAt(0);
        listTop = (v == null) ? 0 : v.getTop();

        ListViewDataUtils.saveTalk(findItemList.get(clickPosition).getTxt_talk());
        ListViewDataUtils.saveShow(Integer.parseInt(findItemList.get(clickPosition).getTxt_show()) + 11 + "");
        ListViewDataUtils.saveIsfollow(findItemList.get(clickPosition).getTxt_isfollow());
        ListViewDataUtils.savePraise(findItemList.get(clickPosition).getTxt_praise());
        ListViewDataUtils.saveIsfav(findItemList.get(clickPosition).getTxt_isfav());

    }

    // 检查升级
    private void checkUpData() {

        RequestParams params = new RequestParams(HttpUrl.UpDate);
        params.addBodyParameter("type", "0");
        params.addBodyParameter("ver", MyApplication.getAppVersionName(getActivity()));
        x.http().post(params, new org.xutils.common.Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException arg0) {


            }


            @Override
            public void onError(Throwable arg0, boolean arg1) {

            }


            @Override
            public void onFinished() {

            }


            @Override
            public void onSuccess(String arg0) {
                UpDateDTO dto = new UpDateDTO();

                if (Conn.IsNetWork) {

                    dto.setTxt_code("0");
                    dto.setTxt_message("请求成功");
                    UpdateInfo updateInfo = new UpdateInfo();
                    updateInfo.setTxt_description("1、优化产品细节；\\n2、修复已知bug；\\n3、提升用户体验。");
                    updateInfo.setTxt_downurl("http://manage.yishuqiao.cn:8083/down");
                    updateInfo.setTxt_version("1.1");
                    dto.setChkUpdateInfo(updateInfo);

                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(arg0, UpDateDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {

                    UpdateInfo updateInfo = dto.getChkUpdateInfo();
                    if (updateInfo.getTxt_isupdate().equals("1")) {
                        Intent intent = new Intent(getActivity(), UpDateActivity.class);
                        intent.putExtra(UpDateActivity.DOWNLOADURL, updateInfo.getTxt_downurl());
                        intent.putExtra(UpDateActivity.CONTENT, updateInfo.getTxt_description());
                        startActivity(intent);
                    }

                } else {
                    // Utils.showToast(getActivity(), dto.getTxt_message());
                }

            }
        });

    }

    private void initBanner() {
        RequestParams params = new RequestParams(HttpUrl.FindList);
        params.addBodyParameter("type", "2");
        x.http().post(params, new org.xutils.common.Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException arg0) {


            }


            @Override
            public void onError(Throwable arg0, boolean arg1) {

            }


            @Override
            public void onFinished() {

            }


            @Override
            public void onSuccess(String arg0) {
                CarouselPicDTO dto = new CarouselPicDTO();

                if (Conn.IsNetWork) {

                    List<FindDetail> itemList = new ArrayList<FindDetail>();

                    dto.setTxt_code("0");
                    dto.setTxt_message("请求成功");
                    for (int i = 0; i < imageUrl.length; i++) {
                        FindDetail item = dto.new FindDetail();
                        item.setTxt_h5url("http://www.baidu.com");
                        item.setTxt_type("0");
                        item.setTxt_id(i + "");
                        item.setTxt_title(imageTitle[i]);
                        item.setTxt_pic(imageUrl[i]);
                        itemList.add(item);
                    }
                    dto.setFindDetail(itemList);

                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(arg0, CarouselPicDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {

                    carouselPicDTO.addAll(dto.getFindDetail());
                    // 轮播图数据加载完后更新UI
                    slideShowView.initialize(carouselPicDTO).build();
                    slideShowView.setOnPageItemClickListener(onPageItemClickListener);// 点击事件
                    // slideShowView.setSwapDuration(5000);// 轮播时间
                    if (isFirst) {
                        slideShowView.startPlaying();// 开始轮播
                        isFirst = false;
                    }
                    // Utils.showToast(getActivity(), "开始轮播");
                } else {
                    // Utils.showToast(getActivity(), dto.getTxt_message());
                }

            }
        });

    }


    private void showMoreWindow(View view) {

        if (null == mMoreWindow) {
            mMoreWindow = new MoreWindow(getActivity(), new MoreWindowResult() {

                @Override
                public void WindowOnClick(int flag) {
                    switch (flag) {
                        case 0:
                            startActivityForResult(new Intent(getActivity(), PublishTextActivity.class), 6);
                            mMoreWindow.dismiss();
                            break;
                        case 1:
                            startActivityForResult(new Intent(getActivity(), PublishVideoPopActivity.class), 8);
                            mMoreWindow.dismiss();
                            break;

                        default:
                            break;
                    }
                }
            });
            mMoreWindow.init();
        }

        mMoreWindow.showMoreWindow(view);
    }

    public void getData() {

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screen_width = dm.widthPixels;

        loadFail = false;
//        if (PagerNum == 1) {
            showLoadingView();
//        }

        RequestParams params = new RequestParams(HttpUrl.FindList);
        params.addBodyParameter("type", "0");
        params.addBodyParameter("pagerindex", Conn.PagerSize);
        params.addBodyParameter("pagernum", "" + PagerNum++);
        params.addBodyParameter("screen_width", screen_width + "");
        params.addBodyParameter("txt_userid", MyApplication.MySharedPreferences.readUserid());
        params.addBodyParameter("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        x.http().post(params, new org.xutils.common.Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException arg0) {


            }


            @Override
            public void onError(Throwable arg0, boolean arg1) {
                isLoading = false;
                loadFail = true;
                dismissLoadingView();
                myRefreshLayout.endLoadingMore();
                addFailedView();
            }


            @Override
            public void onFinished() {

            }


            @Override
            public void onSuccess(String arg0) {

                isLoading = false;
                loadFail = false;
                MyApplication.MySharedPreferences.saveFragment1(arg0);

                if (PagerNum == 2) {
                    findItemList.clear();
                    // fragmentTwoAdapterOne.notifyDataSetChanged();
                }

                myRefreshLayout.endLoadingMore();
                dismissLoadingView();
                removeFailedView();

                FindDTO newsDTO = new FindDTO();
                List<FindItem> newinforList = new ArrayList<FindItem>();
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
                        List<FindItemImageUrl> findItemImageUrlList = new ArrayList<FindItemImageUrl>();
                        for (int j = 0; j < 8; j++) {
                            FindItemImageUrl findItemImageUrl = newsDTO.new FindItemImageUrl();
                            findItemImageUrl.setTxt_other("测试信息");
                            findItemImageUrl.setTxt_url(
                                    "http://www.gmw.cn/images/2005-12/18/xin_561202181152562033333.jpg");
                            findItemImageUrlList.add(findItemImageUrl);
                        }
                        newinfor.setTxt_imglist(findItemImageUrlList);
                        newinforList.add(newinfor);
                    }
                    newsDTO.setFindItemList(newinforList);

                } else {
                    Gson gson = new Gson();
                    newsDTO = gson.fromJson(arg0, FindDTO.class);
                }
                if (newsDTO.getTxt_code().equals("0")) {

                    findItemList.addAll(newsDTO.getFindItemList());
                    fragmentTwoAdapterOne.notifyDataSetChanged();

                    if (findItemList.size() < Integer.parseInt(Conn.PagerSize)) {
                        myRefreshLayout.closeLoadingMore();
                    } else {
                        myRefreshLayout.openLoadingMore();
                    }

                } else {
                    if (!MyApplication.MySharedPreferences.readFragment1().equals("")) {
                        Gson gson = new Gson();
                        newsDTO = gson.fromJson(MyApplication.MySharedPreferences.readFragment1(), FindDTO.class);
                        if (newsDTO.getTxt_code().equals("0")) {
                            findItemList.addAll(newsDTO.getFindItemList());
                            fragmentTwoAdapterOne.notifyDataSetChanged();
                            if (findItemList.size() < Integer.parseInt(Conn.PagerSize)) {
                                myRefreshLayout.closeLoadingMore();
                            } else {
                                myRefreshLayout.openLoadingMore();
                            }

                        }
                    }
                }
            }
        });

    }


    public void reloadData() {

        if (!isLoading && loadFail) {
            PagerNum = 1;
            getData();
            if (carouselPicDTO.isEmpty() && isFirst) {
                initBanner();
            }
        }

    }

    @Override
    public void onResume() {
        // 没有数据时不执行startPlaying,避免执行几次导致轮播混乱
        // if (carouselPicDTO != null && !carouselPicDTO.isEmpty()) {
        // slideShowView.startPlaying();
        // }

        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(getActivity());
        }

        // 检查更新升级
        checkUpData();

        super.onResume();
    }

    @Override
    public void onPause() {
        // if (carouselPicDTO != null && !carouselPicDTO.isEmpty()) {
        // slideShowView.stopPlaying();
        // }
        // isFirst = false;
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
        super.onPause();
    }

    /**
     * 关注与被关注
     *
     * @param isFollow
     */
    private void chagePhone(final int position, final boolean isFollow) {

        if (Conn.IsOkhttp) {

            Map<String, String> params = new HashMap<String, String>();
            params.put("type", "1");
            params.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
            params.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
            params.put("pa_user_id", findItemList.get(position).getTxt_userid());
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
                                    findItemList.get(position).setTxt_isfollow("0");
                                } else {
                                    findItemList.get(position).setTxt_isfollow("1");
                                }
                                fragmentTwoAdapterOne.notifyDataSetChanged();
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
            ajaxParams.put("pa_user_id", findItemList.get(position).getTxt_userid());
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
                            findItemList.get(position).setTxt_isfollow("0");
                        } else {
                            findItemList.get(position).setTxt_isfollow("1");
                        }
                        fragmentTwoAdapterOne.notifyDataSetChanged();
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

    /**
     * 轮播图点击事件
     */
    private OnPageItemClickListener onPageItemClickListener = new OnPageItemClickListener() {

        @Override
        public void onPageItemClick(int position) {

            // 0为发现；1为资讯；2为视频，3为艺苑
            if ((carouselPicDTO.get(position).getTxt_type()).equals("0")) {
                getSendDate(position);
            } else if ((carouselPicDTO.get(position).getTxt_type()).equals("1")) {

                Intent intent = new Intent(getActivity(), CommentWebViewActivity.class);
                intent.putExtra(CommentWebViewActivity.ITEM_ID, carouselPicDTO.get(position).getTxt_id());
                intent.putExtra(CommentWebViewActivity.ITEM_TYPE, "2");
                intent.putExtra(CommentWebViewActivity.ACTION_TYPE, "1");
                intent.putExtra("txt_title", "资讯详情");

                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "1");
                params.put("txt_id", carouselPicDTO.get(position).getTxt_id());
                intent.putExtra(CommentWebViewActivity.URL_PARAMS, (Serializable) params);

                startActivity(intent);

            } else if ((carouselPicDTO.get(position).getTxt_type()).equals("2")) {

                Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
                intent.putExtra(VideoPlayActivity.VIDEO_PLAY_ID, carouselPicDTO.get(position).getTxt_id());
                intent.putExtra(VideoPlayActivity.VIDEO_PLAY_TITLE, carouselPicDTO.get(position).getTxt_title());
                startActivity(intent);

            } else if ((carouselPicDTO.get(position).getTxt_type()).equals("3")) {

                Intent intent = new Intent(getActivity(), WriterDetailsActivity.class);
                intent.putExtra(WriterDetailsActivity.ITEMID, carouselPicDTO.get(position).getTxt_id());
                startActivity(intent);

            }

        }

    };

    public void getSendDate(final int position) {

        if (Conn.IsOkhttp) {

            Map<String, String> params = new HashMap<String, String>();
            params.put("type", "1");
            params.put("txt_findid", carouselPicDTO.get(position).getTxt_id());
            OkHttpUtils.post().url(HttpUrl.FindList).params(params).build().execute(new Callback<String>() {

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
                    FindDTO newsDTO = new FindDTO();
                    List<FindItem> newinforList = new ArrayList<FindItem>();
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
                            List<FindItemImageUrl> findItemImageUrlList = new ArrayList<FindItemImageUrl>();
                            for (int j = 0; j < 8; j++) {
                                FindItemImageUrl findItemImageUrl = newsDTO.new FindItemImageUrl();
                                findItemImageUrl.setTxt_other("测试信息");
                                findItemImageUrl.setTxt_url(
                                        "http://www.gmw.cn/images/2005-12/18/xin_561202181152562033333.jpg");
                                findItemImageUrlList.add(findItemImageUrl);
                            }
                            newinfor.setTxt_imglist(findItemImageUrlList);
                            newinforList.add(newinfor);
                        }
                        newsDTO.setFindItemList(newinforList);

                    } else {
                        Gson gson = new Gson();
                        newsDTO = gson.fromJson(response, FindDTO.class);
                    }
                    if (newsDTO.getTxt_code().equals("0")) {

                        findItemList.clear();

                        findItemList.addAll(newsDTO.getFindItemList());

                        Intent intent = new Intent(getActivity(), DiscoverDetailsActivity.class);
                        intent.putExtra(DiscoverDetailsActivity.DISCOVER_DETAILS_ID, findItemList.get(0).getTxt_id());
                        intent.putExtra(DiscoverDetailsActivity.DISCOVER_DETAILS_TITLE,
                                findItemList.get(0).getTxt_name());

                        Bundle bundle = new Bundle();
                        bundle.putSerializable(DiscoverDetailsActivity.HEAD_BEAN, findItemList.get(0));
                        intent.putExtras(bundle);
                        startActivity(intent);

                    } else {
                        Utils.showToast(getActivity(), newsDTO.getTxt_message());
                    }

                }

            });

        } else {

            AjaxParams ajaxParams = new AjaxParams();
            ajaxParams.put("type", "1");
            ajaxParams.put("txt_findid", carouselPicDTO.get(position).getTxt_id());
            HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

                @Override
                public void succ(String t) {

                    FindDTO newsDTO = new FindDTO();
                    List<FindItem> newinforList = new ArrayList<FindItem>();
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
                            List<FindItemImageUrl> findItemImageUrlList = new ArrayList<FindItemImageUrl>();
                            for (int j = 0; j < 8; j++) {
                                FindItemImageUrl findItemImageUrl = newsDTO.new FindItemImageUrl();
                                findItemImageUrl.setTxt_other("测试信息");
                                findItemImageUrl.setTxt_url(
                                        "http://www.gmw.cn/images/2005-12/18/xin_561202181152562033333.jpg");
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
                        intent.putExtra(DiscoverDetailsActivity.DISCOVER_DETAILS_TITLE,
                                findItemList.get(0).getTxt_name());

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

                }

                @Override
                public void error() {

                }

            }, getActivity());
            httpNetWork.postHttp(ajaxParams, HttpUrl.FindList);

        }

    }

    @Override
    public void onActivityResultNestedCompat(int requestCode, int resultCode, Intent data) {

        if (0 == requestCode && resultCode == Activity.RESULT_OK) {
            // 刷新数据
            findItemList.get(clickPosition).setTxt_isfollow(ListViewDataUtils.readIsfollow());
            findItemList.get(clickPosition).setTxt_show(ListViewDataUtils.readShow());
            findItemList.get(clickPosition).setTxt_talk(ListViewDataUtils.readTalk());
            findItemList.get(clickPosition).setTxt_praise(ListViewDataUtils.readPraise());
            findItemList.get(clickPosition).setTxt_isfav(ListViewDataUtils.readIsfav());
            fragmentTwoAdapterOne.notifyDataSetChanged();

            // 根据上次保存的index和偏移量恢复上次的位置
            lv_listviewOne.setSelectionFromTop(listPosition, listTop);

        } else if (resultCode == Activity.RESULT_OK && requestCode == VIDEO_CAPTURE) {
            Uri uri = data.getData();
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndex(VideoColumns.DATA));
                cursor.close();

                startActivityForResult(new Intent(getActivity(), PublishVideoActivity.class)
                        .putExtra(PreviewVideoActivity.FILEPATH, filePath), 6);

            }
        } else if (requestCode == VIDEO_LOCAL && resultCode == Activity.RESULT_OK) {

            if (data != null) {

                try {
                    String filePath;
                    Uri uri = data.getData();
                    uri = geturi(getActivity(), data);
                    File file = null;
                    if (uri.toString().indexOf("file") == 0) {
                        file = new File(new URI(uri.toString()));
                        filePath = file.getPath();
                    } else {
                        filePath = UriUtils.getPath(getActivity(), uri);
                        // filePath = Utils.getRealFilePath(getActivity(),uri);
                        // filePath = getPath(uri);
                        Log.e("uri", uri.toString());
                        file = new File(filePath);
                    }

                    if (!Utils.isVideo(file)) {
                        Utils.showToast(getActivity(), "请选择可以播放的视频文件");
                        return;
                    }

                    // if (!MediaFile.isVideoFileType(filePath)) {
                    // Utils.showToast(getActivity(), "请选择可以播放的视频文件");
                    // return;
                    // }

                    if (!file.exists()) {
                        Utils.showToast(getActivity(), "视频文件不存在");
                        return;
                    }
                    if (file.length() > 100 * 1024 * 1024) {
                        // "文件大于100M";
                        Utils.showToast(getActivity(), "视频文件过大,请选择小视频");
                        return;
                    }
                    if (file.length() > 15 * 1024 * 1024) {
                        // "文件大于10M";
                        showTip(filePath);
                        return;
                    }
                    startActivityForResult(new Intent(getActivity(), PublishVideoActivity.class)
                            .putExtra(PublishVideoActivity.FILEPATH, filePath)
                            .putExtra(PublishVideoActivity.ISLOCAL, "1"), 6);
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }

            } else {
                Utils.showToast(getActivity(), "获取视频文件失败");
            }

        } else if (8 == requestCode && resultCode == Activity.RESULT_OK) {
            if (resultCode == -1 && (data.getStringExtra("choose")).equals("Camera")) {
                // 拍摄
                // Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                // intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                // intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);// 限制录制时间10秒
                // intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1024 * 1024 * 20L);
                // startActivityForResult(intent, VIDEO_CAPTURE);
                verifyStoragePermissions(getActivity());

                Intent intent = new Intent(getActivity(), YWRecordVideoActivity.class);
                startActivity(intent);
            } else if (resultCode == -1 && (data.getStringExtra("choose")).equals("Album")) {
                // 本地
                // startActivityForResult(new Intent(getActivity(), VideoListActivity.class), 6);
                // Utils.showToast(getActivity(), "请选择小于10M的视频");

                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, VIDEO_LOCAL);

            }
        } else if (6 == requestCode && resultCode == Activity.RESULT_OK) {
            // 刷新数据
            PagerNum = 1;
            getData();
            lv_listviewOne.setSelection(0);
        }

    }
    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.CAMERA");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 上传提示
    private void showTip(final String filePath) {

        Utils.showDialog(getActivity(), "提示", "视频文件较大，上传耗时较长，确定上传吗？", "确定", "取消", new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.txtDialogLift:
                        startActivityForResult(new Intent(getActivity(), PublishVideoActivity.class)
                                .putExtra(PublishVideoActivity.FILEPATH, filePath)
                                .putExtra(PublishVideoActivity.ISLOCAL, "1"), 6);
                        Utils.dismissDialog();
                        break;
                    case R.id.txtDialogRight:
                        Utils.dismissDialog();
                        break;

                    default:
                        break;
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
            layoutView.addView(failedView, params);
            failedView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    PagerNum = 1;
                    getData();
                    if (carouselPicDTO.isEmpty() && isFirst) {
                        initBanner();
                    }
                }
            });
        }

    }

    protected void removeFailedView() {
        if (failedView != null) {
            layoutView.removeView(failedView);
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

    public static Uri geturi(Context context, Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'")
                        .append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.ImageColumns._ID}, buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                        Log.i("urishi", uri.toString());
                    }
                }
            }
        }
        return uri;
    }

    @SuppressWarnings("deprecation")
    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


}
