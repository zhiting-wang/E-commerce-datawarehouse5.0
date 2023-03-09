package com.wangzt.gmall.flume.utils;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class JSONUtil {
    /*
     * 通过异常判断是否是json字符串
     * 是：返回true  不是：返回false
     * */
    public static boolean isJSONValidatedata(String log){
        try {
            JSONObject.parseObject(log);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }
}
