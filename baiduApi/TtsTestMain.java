package baiduApi;

/**
 * @Author Zilin Hsu
 * @Date 2019/10/26
 */

import baiduApi.core.ConnException;

import java.io.IOException;

    public class TtsTestMain {

        public static void main(String[] args) throws IOException, ConnException {
            TtsUtil.text2Sound("文字转换语音测试");
            TtsUtil.text2Sound("使用度丫丫",4);
            TtsUtil.text2Sound("使用度丫丫,语速10，音调10，音量3",4,10,10,3,3);
        }

    }
