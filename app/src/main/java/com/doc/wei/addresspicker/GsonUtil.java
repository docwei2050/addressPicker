package com.doc.wei.addresspicker;

import com.google.gson.Gson;

/**
 * Created by git on 2018/3/25.
 */

public class GsonUtil {
    private static class GsonHolder{
        static final Gson gson =new Gson();
    }
    public static Gson getInstance(){
        return GsonHolder.gson;
    }
}
