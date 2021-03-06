﻿/**
 * WeiXin
 * @title TokenAccessor.java
 * @package com.chn.wx.invocation
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年12月17日-上午10:31:29
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.wx.invocation;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.chn.common.Assert;
import com.chn.common.StringTemplate;
import com.chn.wx.dto.Context;
import com.chn.wx.util.HttpUtils;
import com.chn.wx.vo.result.AccessTokenResult;

/**
 * @class TokenAccessor
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class TokenAccessor {

    private static Logger log = Logger.getLogger(TokenAccessor.class);
    
    private static StringTemplate accessTokenUrl = StringTemplate.compile(WeiXinURL.GET_TOKEN);
    
    private static long expireTime;
    private static String accessToken;
    
    public static synchronized String getAccessToken() {
        
        if(System.currentTimeMillis() < expireTime) return accessToken;
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appId", Context.info.getId());
        params.put("appSecret", Context.info.getSecret());
        String urlLocation = accessTokenUrl.replace(params);
        AccessTokenResult result = null;
        try {
            String respJson = HttpUtils.get(urlLocation);
            result = JSON.parseObject(respJson, AccessTokenResult.class);
        } catch (Exception e) {
            log.error("请求 AccessToken 失败，继续采用之前 Token！", e);
        }
        Assert.notNull(result.getAccessToken(), "获取Token失败[%s][%s]", result.getErrcode(), result.getErrmsg());
        accessToken = result.getAccessToken();
        //900 是 1000 毫秒的 0.9 倍，百分十的提前量更新 Token 
        expireTime = System.currentTimeMillis() + result.getExpiresIn() * 900;
        log.info("更新AccessToken：" + accessToken);
        return accessToken;
    }
    
}
