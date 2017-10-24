package com.yishuqiao.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.CarouselPicDTO;
import com.yishuqiao.dto.NewsSortDTO;
import com.yishuqiao.dto.NewsSortDTO.NewInforType;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.okhttp.OkHttpUtils;
import com.yishuqiao.okhttp.callback.Callback;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.view.MultiStateView;
import com.yishuqiao.view.MultiStateView.ViewState;
import com.yishuqiao.view.smarttablayout.SmartTabLayout;
import com.yishuqiao.view.smarttablayout.utils.v4.FragmentPagerItem;
import com.yishuqiao.view.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.yishuqiao.view.smarttablayout.utils.v4.FragmentPagerItems;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import net.tsz.afinal.http.AjaxParams;

import org.xutils.http.RequestParams;
import org.xutils.x;

import okhttp3.Call;
import okhttp3.Response;

public class FragmentThree extends ForResultNestedCompatFragment {

    private boolean isLoading = true;
    private boolean loadFail = false;

    private List<NewInforType> mDatas = new ArrayList<NewInforType>();

    private FragmentPagerItemAdapter fragmentPagerItemAdapter = null;

    private ViewGroup tab = null;
    private ViewPager viewPager = null;
    private SmartTabLayout viewPagerTab = null;
    private FragmentPagerItems pages = null;

    private MultiStateView mStateView;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.fragment_three, null);
        init(v);
        return v;
    }

    public void init(View v) {

        mStateView = (MultiStateView) v.findViewById(R.id.stateview);

        tab = (ViewGroup) v.findViewById(R.id.tab);
        tab.addView(LayoutInflater.from(getActivity()).inflate(R.layout.smarttablayout_indicator, tab, false));

        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        viewPagerTab = (SmartTabLayout) v.findViewById(R.id.viewpagertab);

        getDataFromService();

    }

    private void getDataFromService() {
        isLoading = true;
        loadFail = false;
        mStateView.setViewState(ViewState.LOADING);

            RequestParams params = new RequestParams(HttpUrl.VideoList);
            params.addBodyParameter("type", "1");
            x.http().post(params, new org.xutils.common.Callback.CommonCallback<String>() {
                @Override
                public void onCancelled(CancelledException arg0) {


                }


                @Override
                public void onError(Throwable arg0, boolean arg1) {
                    isLoading = false;
                    loadFail = true;

                    mStateView.setViewState(ViewState.ERROR);
                    mStateView.getView(ViewState.ERROR).findViewById(R.id.iv_error) //
                            .setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    getDataFromService();
                                }
                            });
                }


                @Override
                public void onFinished() {

                }


                @Override
                public void onSuccess(String arg0) {

                    isLoading = false;
                    loadFail = false;

                    mDatas.clear();



                    mStateView.setViewState(ViewState.CONTENT);

                    NewsSortDTO dataDTO = new NewsSortDTO();
                    List<NewInforType> dataList = new ArrayList<NewInforType>();
                    if (Conn.IsNetWork) {

                        dataDTO.setTxt_code("0");
                        dataDTO.setTxt_message("请求成功");
                        NewInforType datafor = dataDTO.new NewInforType();
                        for (int i = 0; i < 6; i++) {
                            datafor.setTxt_id("0");
                            datafor.setTxt_name("测试标题");
                            dataList.add(datafor);
                        }
                        dataDTO.setNewInforType(dataList);
                    } else {
                        Gson gson = new Gson();
                        dataDTO = gson.fromJson(arg0, NewsSortDTO.class);
                    }
                    if (dataDTO.getTxt_code().equals("0")) {

                        mDatas.addAll(dataDTO.getNewInforType());

                        pages = new FragmentPagerItems(getActivity());

                        for (int i = 0; i < mDatas.size(); i++) {

                            Bundle bundle = new Bundle();

                            bundle.putString("type", mDatas.get(i).getTxt_id());

                            pages.add(FragmentPagerItem.of(mDatas.get(i).getTxt_name(), FragmentThreeChildren.class,
                                    bundle));
                        }

                        fragmentPagerItemAdapter = new FragmentPagerItemAdapter(getChildFragmentManager(), pages);

                        viewPager.setAdapter(fragmentPagerItemAdapter);
                        viewPagerTab.setViewPager(viewPager);

                    } else {

                    }

                }
            });
    }

    public void reloadData() {

        if (!isLoading && loadFail) {
            getDataFromService();
        }

    }

}
