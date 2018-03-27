package com.doc.wei.addresspicker.lastest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doc.wei.addresspicker.AddressInfoBean;
import com.doc.wei.addresspicker.R;

import java.util.List;



/**
 * Created by git on 2018/3/25.
 */

public class AddressInfoAdapter extends RecyclerView.Adapter<AddressInfoAdapter.AddressViewHolder> implements View.OnClickListener {
    private List<AddressInfoBean> mAddressBeanList;
    private int                   level;
    private Context               mContext;

    public AddressInfoAdapter(Context context, List<AddressInfoBean> addressBeanList, int  level) {
        mAddressBeanList = addressBeanList;
        this.level= level;
        mContext = context;
    }
    public AddressInfoAdapter setLevel(int level){
        this.level=level;
        return this;
    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.recycler_item, parent, false);
        AddressViewHolder holder=new AddressViewHolder(view);
        holder.ll_address.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(AddressViewHolder holder, int position) {
           holder.ll_address.setTag(position);
           AddressInfoBean bean=mAddressBeanList.get(position);

           holder.tv_address.setSelected(bean.isSelect());
           holder.iv_address.setVisibility(bean.isSelect()?View.VISIBLE:View.GONE);
           holder.tv_address.setText(mAddressBeanList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mAddressBeanList.size();
    }
   public void updateData(List<AddressInfoBean> list){
        mAddressBeanList.clear();
        mAddressBeanList.addAll(list);
        notifyDataSetChanged();
   }
    @Override
    public void onClick(View v) {
        int position= (int) v.getTag();
        if(mOnItemClickListener!=null){
            for(AddressInfoBean bean:mAddressBeanList){
                bean.setSelect(false);
            }
            mAddressBeanList.get(position).setSelect(true);
            mOnItemClickListener.onSelectItem(mAddressBeanList.get(position),level);
        }
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder{
        TextView  tv_address;
        ImageView iv_address;
        LinearLayout ll_address;
        public AddressViewHolder(View itemView) {
            super(itemView);
            tv_address =itemView.findViewById(R.id.tv_address);
            iv_address =itemView.findViewById(R.id.iv_address);
            ll_address =itemView.findViewById(R.id.ll_address);
        }
    }
    public OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener){
        mOnItemClickListener=listener;
    }
    public interface  OnItemClickListener{
        void onSelectItem(AddressInfoBean bean,int level);
    }
}
