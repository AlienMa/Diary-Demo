package com.example.android.mooddiary.common.net;

import com.android.volley.VolleyError;

/**
 * 用于网络请求的回调
 *
 * Created by developerHaoz on 2017/5/3.
 */

public interface VolleyResponseCallback {
    void onSuccess(String response);
    void onError(VolleyError error);
}
