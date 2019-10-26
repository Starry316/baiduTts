package baiduApi.core;

import baiduApi.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author Zilin Hsu
 * @Date 2019/10/26
 * AccessToken缓存类，单列实现
 */
public class TokenHolder {
    private static class Token{
        /**
         * 保存访问接口获取的token
         */
        private String token;

        /**
         * 当前的时间戳，毫秒
         */
        private long expiresAt;
        Token(String token, long expiresAt){
            this.token = token;
            this.expiresAt = expiresAt;
        }

        public String getToken() {
            return token;
        }

        public long getExpiresAt() {
            return expiresAt;
        }
    }

    /**
     * 懒加载单列模式
     */
    private static volatile Token token;
    public static synchronized String getToken() throws IOException, ConnException {
        if (token == null || token.expiresAt < System.currentTimeMillis()) {
            refresh();
            return token.token;
        }
        return token.token;
    }

    private static final String APPKEY = "U9kGBhwGGbNNBG2Dap3zw5U7";
    private static final String SECRETKEY = "RmpSavzjWLO3Ahx7XZwkuTmnM3y0EvnV";

    public static final String ASR_SCOPE = "audio_voice_assistant_get";

    public static final String TTS_SCOPE = "audio_tts_post";

    /**
     * URL , Token的url，http可以改为https
     */
    private static final String URL = "http://openapi.baidu.com/oauth/2.0/token";

    /**
     * asr的权限 scope 是  "audio_voice_assistant_get"
     * tts 的权限 scope 是 "audio_tts_post"
     */
    private static final String SCOPE = "audio_tts_post";




    public TokenHolder() {
    }


    /**
     * 获取token
     *
     * @return
     * @throws IOException   http请求错误
     * @throws ConnException http接口返回不是 200, access_token未获取
     */
    public static void refresh() throws IOException, ConnException {
        String getTokenURL = URL + "?grant_type=client_credentials"
                + "&client_id=" + ConnUtil.urlEncode(APPKEY) + "&client_secret=" + ConnUtil.urlEncode(SECRETKEY);

        // 打印的url出来放到浏览器内可以复现
        System.out.println("token URL:" + getTokenURL);

        java.net.URL urlconn = new URL(getTokenURL);
        HttpURLConnection conn = (HttpURLConnection) urlconn.openConnection();
        conn.setConnectTimeout(5000);
        String result = ConnUtil.getResponseString(conn);
        System.out.println("Token result json:" + result);
        parseJson(result);
    }

    /**
     * @param result token接口获得的result
     * @throws ConnException
     */
    private static void parseJson(String result) throws ConnException {
        JSONObject json = new JSONObject(result);
        if (!json.has("access_token")) {
            // 返回没有access_token字段
            throw new ConnException("access_token not obtained, " + result);
        }
        if (!json.has("scope")) {
            // 返回没有scope字段
            throw new ConnException("scope not obtained, " + result);
        }
        if (!json.getString("scope").contains(SCOPE)) {
            throw new ConnException("scope not exist, " + SCOPE + "," + result);
        }
        token = new Token(
                json.getString("access_token"),
                System.currentTimeMillis() + json.getLong("expires_in") * 1000);
    }




}
