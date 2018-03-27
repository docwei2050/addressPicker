package com.doc.wei.addresspicker;

/**
 * Created by git on 2018/3/25.
 */

public class AddressInfoBean {
    private  String name;
    private String id;
    private boolean isSelect;

    public AddressInfoBean(String name, String id, boolean isSelect) {
        this.name = name;
        this.id = id;
        this.isSelect = isSelect;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
