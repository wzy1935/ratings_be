package com.sc.ratings.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class RespData extends HashMap<String, Object> {

    private RespData(String code, Object data) {
        this.put("code", code);
        this.put("data", data);
    }

    public static RespData success(Object data) {
        return new RespData("SUCCESS", data);
    }

    public static RespData success() {
        return new RespData("SUCCESS", new HashMap<String, Object>());
    }

    public static RespData resp(String code, Object data) {
        return new RespData(code, data);
    }

    public static RespData resp(String code) {
        return new RespData(code, new HashMap<String, Object>());
    }
}
