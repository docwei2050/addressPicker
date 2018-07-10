package com.doc.wei.addresspicker;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doc.wei.addresspicker.lastest.AddressInfoAdapter;
import com.doc.wei.addresspicker.lastest.AddressProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by git on 2018/3/26.
 */

public class AddressPickerView extends LinearLayout implements View.OnClickListener {

    private RecyclerView mRecycler;

    private AddressProcessor mAddressProcessor;
    private AddressInfoAdapter mAdapter;
    private Context mContext;
    private static final int MSG_ALREADY=100;
    public  static final int PROVINCE=0;
    public   static final int CITY=1;
    public  static final int COUNTRY=2;
    public static final int COUNT=3;//总共的层级
    private  List<AddressInfoBean> mProvinces=new ArrayList<>();
    private  List<AddressInfoBean> mCities=new ArrayList<>();
    private  List<AddressInfoBean> mCountries=new ArrayList<>();
    private TextView mTv_province;
    private TextView mTv_city;
    private TextView mTv_country;
    private View mIndicator;
    private LinearLayout mLl_tab;
    private TextView mTv_cancel;
    private int mHeight;
    private int mWidht;


    public AddressPickerView(Context context) {
          this(context,null);

    }

    public AddressPickerView(Context context, @Nullable AttributeSet attrs)
    {
       this(context, attrs,0);
    }

    public AddressPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext=context;
        setOrientation(VERTICAL);
        View         view        =View.inflate(context, R.layout.custom_address_picker, this);
        mRecycler = view.findViewById(R.id.recycler);
        mTv_province = view.findViewById(R.id.tv_province);
        mTv_city = view.findViewById(R.id.tv_city);
        mTv_country = view.findViewById(R.id.tv_country);
        mIndicator = view.findViewById(R.id.indicator);
        mLl_tab = view.findViewById(R.id.ll_tab);
        mTv_cancel = view.findViewById(R.id.tv_cancel);
        //初始化地址解析器;
        mAddressProcessor=new AddressProcessor("address", 0, context);
        mAddressProcessor.start();

        initData(context);
        DisplayMetrics metrics =context.getResources().getDisplayMetrics();
        mWidht = metrics.widthPixels;
        mHeight = metrics.heightPixels;

        mRecycler.setNestedScrollingEnabled(true);
    }

    private void initData(Context context) {
        List<AddressInfoBean> infoBeans=new ArrayList<>();
        mRecycler.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        mAdapter = new AddressInfoAdapter(context, infoBeans, PROVINCE);
        mRecycler.setAdapter(mAdapter);

        //默认第一个tab请选择
        mTv_province.setVisibility(View.VISIBLE);
        updateIndicator(PROVINCE,false);

        mHandler.sendEmptyMessage(MSG_ALREADY);
        initEvent();

    }
    private String proviceId;
    private String cityId;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mWidht,2*mHeight/3);
    }

    private void initEvent() {
    /*
    * 点击RecyclerView的item：------------------
    * 1.当前tab显示选中的文字
    * 2. 如果有下一个tab那么它将被选中,文本显示为"请选择"
    * 3. 如果有下下个tab，那么它将隐藏
    * 4.如果没有下一个tab，那么将只修改当前tab的文字
    *  每点击一次都会有指示器的更新,选中的index更新
    */

        mAdapter.setOnItemClickListener(new AddressInfoAdapter.OnItemClickListener() {
            @Override
            public void onSelectItem(AddressInfoBean bean, int level) {
                performTabFromItem(level,bean.getName());
                //选择的是省市
                if(level==PROVINCE){
                    mAdapter.setLevel(CITY);
                    proviceId=bean.getId();
                   mAddressProcessor.startProcess(proviceId,CITY);
                   updateIndicator(CITY,false);
                }else if(level==CITY){
                    cityId=bean.getId();
                    mAdapter.setLevel(COUNTRY);
                    mAddressProcessor.startProcess(proviceId+"&"+cityId,COUNTRY);
                    updateIndicator(COUNTRY,false);

                }else if(level==COUNTRY){
                    mAdapter.notifyDataSetChanged();
                    updateIndicator(COUNTRY,false);
                    if(mSelectAddressListener!=null){
                        mSelectAddressListener.onSelectAddress(String.format(Locale.getDefault(),"%1s%2s%3s",mTv_province.getText().toString(),mTv_city.getText().toString(),mTv_country.getText().toString()));
                    }
                }
            }
        });
    /*
    * 点击tab：------------------
    * selectIndex更新，如果之前是当前tab，点击还是该tab，那么selectIndex不会更新
    *
    */
        mTv_province.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateIndicator(PROVINCE,true);

            }
        });
        mTv_city.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateIndicator(CITY,true);
            }
        });
        mTv_country.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateIndicator(COUNTRY,true);

            }
        });
        mTv_cancel.setOnClickListener(this);
    }

    public void performTabFromItem(int currentPos,String text){
        if(currentPos==PROVINCE){
            mTv_province.setText(text);
            if(mTv_city.getVisibility()!=View.VISIBLE){
                mTv_city.setVisibility(View.VISIBLE);
            }
            mTv_city.setText("请选择");

            mTv_country.setVisibility(View.GONE);
        }else if(currentPos==CITY){
            mTv_city.setText(text);
            if(mTv_country.getVisibility()!=View.VISIBLE){
                mTv_country.setVisibility(View.VISIBLE);
            }
            mTv_country.setText("请选择");
        }else if(currentPos==COUNTRY){
            mTv_country.setText(text);
        }
    }



    private int selectIndex=-1;

    /**
     * 更新tab 指示器
     */
    private void updateIndicator(final int position, final boolean isFromClickTab) {
            selectIndex=position;
            // 处理选中Tab的颜色
           performSelectTab(selectIndex);
            mAdapter.setLevel(position);
            post(new Runnable() {
                @Override
                public void run() {
                    switch (selectIndex) {
                        case PROVINCE: //省份
                           if(isFromClickTab){
                             mAdapter.updateData(mProvinces);
                           }
                            buildIndicatorAnimatorTowards(mTv_province).start();
                            break;
                        case CITY: //城市
                            if(isFromClickTab){
                                mAdapter.updateData(mCities);
                            }
                            buildIndicatorAnimatorTowards(mTv_city).start();
                            break;
                        case COUNTRY: //乡镇
                            if(isFromClickTab){
                                mAdapter.updateData(mCountries);
                            }
                            buildIndicatorAnimatorTowards(mTv_country).start();
                            break;

                    }
                }
            });
    }

    private void performSelectTab(int selectIndex) {
        for(int i=0,z=mLl_tab.getChildCount();i<z;i++){
           mLl_tab.getChildAt(i).setSelected(i==selectIndex);
        }
    }

    /**
     * tab 来回切换的动画
     *
     * @param tab
     * @return
     */
    private AnimatorSet buildIndicatorAnimatorTowards(TextView tab) {
        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(mIndicator, "X", mIndicator.getX(), tab.getX());

        final ViewGroup.LayoutParams params        = mIndicator.getLayoutParams();
        tab.measure(0,0);
        ValueAnimator                widthAnimator = ValueAnimator.ofInt(params.width, tab.getMeasuredWidth());
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.width = (int) animation.getAnimatedValue();
                mIndicator.setLayoutParams(params);
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.playTogether(xAnimator, widthAnimator);

        return set;
    }

    Handler mHandler=new Handler(Looper.getMainLooper()){
       @Override
       public void handleMessage(Message msg) {
           switch (msg.what) {
              /* case AddressProcessor.MSG_START:
                   Toast.makeText(getContext(), "开始", Toast.LENGTH_SHORT).show();
                   break;*/

               case MSG_ALREADY:
                   if(mAddressProcessor.mWorkHandler==null){
                       mHandler.sendEmptyMessageDelayed(MSG_ALREADY,50);
                   }else{
                       mAddressProcessor.setUiHandler(mHandler) .startProcess("", PROVINCE);
                   }
                   break;
               case AddressProcessor.MSG_FINISH:
                   if(msg.arg1==PROVINCE) {
                       mProvinces=(List<AddressInfoBean>) msg.obj;
                       mAdapter.updateData(mProvinces);
                   }else if(msg.arg1==CITY){
                       mCities=(List<AddressInfoBean>) msg.obj;
                       mAdapter.updateData(mCities);
                   }else if(msg.arg1==COUNTRY){
                       mCountries=(List<AddressInfoBean>) msg.obj;
                       mAdapter.updateData(mCountries);
                   }
                   break;
           }
       }
   };

    public void clearHandlerMessage(){
        mHandler.removeCallbacksAndMessages(null);
        if(mAddressProcessor.mWorkHandler!=null){
            mAddressProcessor.mWorkHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onClick(View v) {
        if(mSelectAddressListener!=null){
            clearHandlerMessage();
            mSelectAddressListener.cancel();
        }
    }
    public ISelectAddressListener mSelectAddressListener;
    public void setSelectAddressListener(ISelectAddressListener listener){
        mSelectAddressListener=listener;
    }
    public interface ISelectAddressListener{
        void cancel();
        void onSelectAddress(String address);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //需要手动退出handlerThread
        if(mAddressProcessor!=null){
            mAddressProcessor.quit();
        }
    }
}
