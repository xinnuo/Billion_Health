package com.ruanmeng.db;

import android.os.Parcel;
import android.os.Parcelable;

import io.rong.imlib.model.UserInfo;

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-22 15:40
 */

public class UserData implements Parcelable {

    private UserInfo userInfo;
    private String userType;

    public UserData() { }

    public UserData(String userType, UserInfo userInfo) {
        this.userType = userType;
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.userInfo, 0);
        dest.writeString(this.userType);
    }

    protected UserData(Parcel in) {
        this.userInfo = in.readParcelable(UserInfo.class.getClassLoader());
        this.userType = in.readString();
    }

    public static final Parcelable.Creator<UserData> CREATOR = new Parcelable.Creator<UserData>() {
        public UserData createFromParcel(Parcel source) {
            return new UserData(source);
        }

        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };
}
