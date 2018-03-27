package com.doc.wei.addresspicker;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.doc.wei.addresspicker.lastest.AddressProcessor;

public class MainActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private AddressProcessor mAddressProcessor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       initData();
    }

    private void initData() {
        final TextView tv_text =findViewById(R.id.tv_select);
        tv_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                AddressPickerView view=new AddressPickerView(MainActivity.this);
                builder.setCustomTitle(view);
                builder.setCancelable(true);
                final AlertDialog dialog =builder.show();
                view.setSelectAddressListener(new AddressPickerView.ISelectAddressListener() {
                    @Override
                    public void cancel() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onSelectAddress(String address) {
                          dialog.dismiss();
                          tv_text.setText(address);
                    }
                });
                Window                     window=dialog.getWindow();
                window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                WindowManager.LayoutParams layoutParams=window.getAttributes();
                layoutParams.gravity= Gravity.BOTTOM;
                layoutParams.width=WindowManager.LayoutParams.MATCH_PARENT;
                window.setAttributes(layoutParams);
            }
        });
    }


}
