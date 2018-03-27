package com.doc.wei.addresspicker.lastest;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.doc.wei.addresspicker.AddressBean;
import com.doc.wei.addresspicker.AddressInfoBean;
import com.doc.wei.addresspicker.GsonUtil;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.doc.wei.addresspicker.AddressPickerView.CITY;
import static com.doc.wei.addresspicker.AddressPickerView.COUNTRY;
import static com.doc.wei.addresspicker.AddressPickerView.PROVINCE;


/**
 * Created by git on 2018/3/25.
 */

public class AddressProcessor extends HandlerThread implements Handler.Callback {
    //主线程Handler
    private Handler mUiHandler;
    //工作线程中Handler
    public  Handler mWorkHandler;
    private static final String fileName="china_city_data.json";
    private static final int MSG_AREA= 100;
    private static final int MSG_CITY = 101;
    private static final int MSG_COUNTRY = 102;
    public static final int MSG_START=104;
    public static final int MSG_FINISH=105;
    private String listStr;
    private Context mContext;
    private List<AddressInfoBean> mInfoBeans;

    public AddressProcessor(String name) {
        this(name, 0,null);
    }

    public AddressProcessor(String name, int priority, Context context) {
        super(name, priority);
        mContext=context;
    }
    public AddressProcessor setUiHandler(Handler uiHandler){
        this.mUiHandler=uiHandler;
        return this;
    }
    @Override
    protected void onLooperPrepared() {
        if(mWorkHandler==null) {
            mWorkHandler = new Handler(getLooper(), this);
        }
    }

    public void startProcess(String id,int level){

        Message msg =null;
        switch (level) {
            case PROVINCE:
                msg= Message.obtain(mWorkHandler, MSG_AREA, PROVINCE,0,null);
                 break;
            case CITY:
                System.out.println(MSG_CITY);
                msg= Message.obtain(mWorkHandler, MSG_CITY, CITY,0, id);
                break;
            case COUNTRY:
                msg= Message.obtain(mWorkHandler, MSG_COUNTRY,COUNTRY,0, id);
                break;
        }
        mWorkHandler.sendMessage(msg);
    }
    List<AddressInfoBean> proviceList=new ArrayList<>();
    List<AddressInfoBean> cityList=new ArrayList<>();
    List<AddressInfoBean> countryList=new ArrayList<>();

    @Override
    public boolean handleMessage(Message msg) {
        startParse();
        String ids= (String) msg.obj;

        switch (msg.what) {
            case MSG_AREA:

                proviceList.clear();
                List<AddressBean> list1=GsonUtil.getInstance().fromJson(listStr,new TypeToken<List<AddressBean>>(){}.getType());
                for(AddressBean bean:list1){
                    AddressInfoBean infoBean=new AddressInfoBean(bean.getName(),bean.getId(),false);
                    proviceList.add(infoBean);
                }
                mUiHandler.sendMessage(mUiHandler.obtainMessage(MSG_FINISH,msg.arg1,0,proviceList));
                break;
            case MSG_CITY:

                cityList.clear();
                List<AddressBean> list2=GsonUtil.getInstance().fromJson(listStr,new TypeToken<List<AddressBean>>(){}.getType());
                for(AddressBean bean:list2){
                    if(bean.getId().equals(ids)){
                        List<AddressBean.CityBean> sublist=bean.getCityList();
                        for(AddressBean.CityBean cityBean:sublist){
                            AddressInfoBean infoBean=new AddressInfoBean(cityBean.getName(),cityBean.getId(),false);
                            cityList.add(infoBean);
                        }
                        break;
                    }
                }
                mUiHandler.sendMessage(mUiHandler.obtainMessage(MSG_FINISH,msg.arg1,0,cityList));
                break;

            case MSG_COUNTRY:

                countryList.clear();
                String[] idStrs=ids.split("&");
                String id=idStrs[0];
                String subId=idStrs[1];
                List<AddressBean> list3=GsonUtil.getInstance().fromJson(listStr,new TypeToken<List<AddressBean>>(){}.getType());
                for(AddressBean bean:list3){
                    if(bean.getId().equals(id)){
                        List<AddressBean.CityBean> sublist=bean.getCityList();
                        for(AddressBean.CityBean cityBean:sublist){
                           if(cityBean.getId().equals(subId)){
                               List<AddressBean.CityBean.CountryBean> countryBeans=cityBean.getCityList();
                               for(AddressBean.CityBean.CountryBean countryBean:countryBeans){
                                   AddressInfoBean infoBean=new AddressInfoBean(countryBean.getName(),countryBean.getId(),false);
                                   countryList.add(infoBean);
                               }
                           }
                        }
                        break;
                    }

                   }
                mUiHandler.sendMessage(mUiHandler.obtainMessage(MSG_FINISH,msg.arg1,0,countryList));
                break;

        }

        return true;
    }

    private void startParse() {
       // mUiHandler.sendMessage(mUiHandler.obtainMessage(MSG_START));
        if(listStr==null){
            listStr=getJson(mContext,fileName);
        }

    }

    //
    public  String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager   assetManager = context.getAssets();
            BufferedReader bf           = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String         line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}
