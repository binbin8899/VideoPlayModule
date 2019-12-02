package com.rlcc.videolibrary.video;

import android.os.Parcelable;
import android.view.View;

import java.util.ArrayList;

/**
 * author  yangc
 * date 2018/4/17
 * E-Mail:liu_bin_0129@163.com
 * Deprecated:
 */
public class ExoDataBean extends View.BaseSavedState {

    private  boolean isLand;
    private  int setSystemUiVisibility;
    private  int switchIndex;
    private ArrayList<String> nameSwitch;



    public ExoDataBean(Parcelable superState) {
        super(superState);
    }

    public ArrayList<String> getNameSwitch() {
        return nameSwitch;
    }

    public void setNameSwitch(ArrayList<String> nameSwitch) {
        this.nameSwitch = nameSwitch;
    }

    public boolean isLand() {
        return isLand;
    }

    public void setLand(boolean land) {
        isLand = land;
    }

    public int getSetSystemUiVisibility() {
        return setSystemUiVisibility;
    }

    public void setSetSystemUiVisibility(int setSystemUiVisibility) {
        this.setSystemUiVisibility = setSystemUiVisibility;
    }

    public int getSwitchIndex() {
        return switchIndex;
    }

    public void setSwitchIndex(int switchIndex) {
        this.switchIndex = switchIndex;
    }

}
