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
import com.yishuqiao.adapter.FragmentFistAdapter;
import com.yishuqiao.dto.CarouselPicDTO;
import com.yishuqiao.dto.CarouselPicDTO.FindDetail;
import com.yishuqiao.dto.NewsDTO;
import com.yishuqiao.dto.NewsDTO.NewInfor;
import com.yishuqiao.dto.NewsDTO.NewInfor.ItemImageUrlList;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.AutoPlayingViewPager;
import com.yishuqiao.view.AutoPlayingViewPager.OnPageItemClickListener;
import com.yishuqiao.view.MultiStateView;
import com.yishuqiao.view.MultiStateView.ViewState;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import net.tsz.afinal.http.AjaxParams;

/**
 * 作者：admin 创建时间：2017-7-11 下午4:36:06 项目名称：YiShuQiao 文件名称：HotVpSimpleFragment.java 类说明：资讯最热界面
 */
@SuppressLint("InflateParams")
public class HotVpSimpleFragment extends Fragment {

    private boolean isFirst = true;// 避免轮播图启动播放重复调用

    private LinearLayout mHeaderView;

    private ListView lv_listview;
    private FragmentFistAdapter adapter;
    private List<NewInfor> newsList = new ArrayList<NewInfor>();
    private MultiStateView mStateView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private String[] imageUrl = {"http://p1.pstatp.com/large/2ebb0003b179820c6dc1",
            "http://p3.pstatp.com/large/2ec0000207110a4fb764", "http://p3.pstatp.com/large/2ebc00021401da6a78a4",
            "http://p1.pstatp.com/large/2ec000020717087527bf", "http://p3.pstatp.com/large/2eba000470a47dfc8605",
            "http://p3.pstatp.com/large/2ebc000214088a1d1f6e", "http://p1.pstatp.com/large/2ebb0003b1849745aa8d"};

    private String[] imageTitle = new String[]{"当代艺术", "符合人类理解到的思维判断连贯性、规律性、因果以及逻辑等关系的形式", "艺术可以是宏观概念也可以是个体现象", "展览",
            "形象把握与理性把握的统一", "符合宇宙运动规律的过程在人类能够识别的范围内表现出来的形态与运动关系", "艺术形象贯穿于艺术活动的全过程"};

    private AutoPlayingViewPager slideShowView;// 顶部广告栏控件

    // 轮播图图片资源
    private List<CarouselPicDTO.FindDetail> carouselPicDTO = new ArrayList<CarouselPicDTO.FindDetail>();

    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            if (msg.what == 0) {

                Intent intent = new Intent(getActivity(), CommentWebViewActivity.class);
                intent.putExtra(CommentWebViewActivity.ITEM_ID, newsList.get(msg.arg1).getTxt_id());
                intent.putExtra(CommentWebViewActivity.ITEM_TYPE, "2");
                intent.putExtra("txt_title", "资讯详情");
                intent.putExtra(CommentWebViewActivity.ACTION_TYPE, "1");

                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "1");
                params.put("txt_id", newsList.get(msg.arg1).getTxt_id());
                intent.putExtra(CommentWebViewActivity.URL_PARAMS, (Serializable) params);

                startActivityForResult(intent, 0);

            }

        }

        ;
    };

    @SuppressWarnings("static-access")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.from(getActivity()).inflate(R.layout.fragment_hotvpsimple, null);

        initView(view);
        return view;
    }

    private void initView(View view) {

        mHeaderView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_hotvpsimple_head,
                null);

        slideShowView = (AutoPlayingViewPager) mHeaderView.findViewById(R.id.auto_play_viewpager);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                new Handler().postDelayed(new Runnable() {

                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        mStateView = (MultiStateView) view.findViewById(R.id.main_stateview);
        lv_listview = (ListView) view.findViewById(R.id.listview);
        lv_listview.addHeaderView(mHeaderView);
        adapter = new FragmentFistAdapter(getActivity(), newsList, handler);
        lv_listview.setAdapter(adapter);

        lv_listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("txt_url", "http://www.baidu.com");
                startActivity(intent);
            }
        });

        getData();
        initBanner();

    }

    private void initBanner() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "13");
        ajaxParams.put("token", "7ef56049939b7296240e3e596757dc03");
        ajaxParams.put("userid", "319039");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                carouselPicDTO.clear();

                CarouselPicDTO dto = new CarouselPicDTO();

                if (!Conn.IsNetWork) {

                    List<FindDetail> itemList = new ArrayList<CarouselPicDTO.FindDetail>();

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
                    dto = gson.fromJson(t, CarouselPicDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {

                    carouselPicDTO.addAll(dto.getFindDetail());

                    // 轮播图数据加载完后更新UI
                    slideShowView.initialize(carouselPicDTO).build();
                    slideShowView.setOnPageItemClickListener(onPageItemClickListener);// 点击事件
                    // slideShowView.setSwapDuration(5000);// 轮播时间
                    if (isFirst) {
                        slideShowView.startPlaying();// 开始轮播
                    }

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
        httpNetWork.postHttp(ajaxParams, "http://aap.android.libaite.vip/user/userinfo.php");

    }

    public void getData() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "13");
        ajaxParams.put("token", "7ef56049939b7296240e3e596757dc03");
        ajaxParams.put("userid", "319039");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                // TODO Auto-generated method stub
                mStateView.setViewState(ViewState.CONTENT);
                NewsDTO newsDTO = new NewsDTO();
                List<NewInfor> newinforList = new ArrayList<NewsDTO.NewInfor>();
                if (!Conn.IsNetWork) {

                    newsList.clear();

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
                        List<ItemImageUrlList> imglist = new ArrayList<NewsDTO.NewInfor.ItemImageUrlList>();
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
                    newsDTO = gson.fromJson(t, NewsDTO.class);
                }
                if (newsDTO.getTxt_code().equals("0")) {
                    newsList.clear();
                    newsList.addAll(newsDTO.getNewInforList());
                    adapter.notifyDataSetChanged();
                } else {
                    Utils.showToast(getActivity(), newsDTO.getTxt_message());
                }
            }

            @Override
            public void start() {
                // TODO Auto-generated method stub
                mStateView.setViewState(ViewState.LOADING);
            }

            @Override
            public void error() {
                // TODO Auto-generated method stub
                mStateView.setViewState(ViewState.ERROR);
                mStateView.getView(ViewState.ERROR).findViewById(R.id.iv_error) //
                        .setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                getData();
                            }
                        });
            }
        }, getActivity());
        httpNetWork.postHttp(ajaxParams, "http://aap.android.libaite.vip/user/userinfo.php");
    }

    private OnPageItemClickListener onPageItemClickListener = new OnPageItemClickListener() {

        @Override
        public void onPageItemClick(int position) {
            // 0为发现；1为资讯；2为视频，3为艺苑
            if ((carouselPicDTO.get(position).getTxt_type()).equals("0")) {

            } else if ((carouselPicDTO.get(position).getTxt_type()).equals("1")) {

            } else if ((carouselPicDTO.get(position).getTxt_type()).equals("2")) {

            } else if ((carouselPicDTO.get(position).getTxt_type()).equals("3")) {

            }

        }

    };

    @Override
    public void onResume() {
        // 没有数据时不执行startPlaying,避免执行几次导致轮播混乱
        if (carouselPicDTO != null && !carouselPicDTO.isEmpty()) {
            slideShowView.startPlaying();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        slideShowView.stopPlaying();
        isFirst = false;
        super.onPause();
    }

}
