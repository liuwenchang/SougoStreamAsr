package com.thunisoft.asr.sougo;

import com.sogou.speech.asr.v1.RecognitionConfig;

/**
 * @author liuwenchang
 * @date 2019-05-30 14:34
 */
public class SougoConfig {
    public String appkey;
    public String appId;
    public String token;
    public String languageCode;
    public RecognitionConfig.AudioEncoding audioEncoding = RecognitionConfig.AudioEncoding.LINEAR16;
    public String model;
    public String ip;
    public int port;
    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public RecognitionConfig.AudioEncoding getAudioEncoding() {
        return audioEncoding;
    }

    public void setAudioEncoding(RecognitionConfig.AudioEncoding audioEncoding) {
        this.audioEncoding = audioEncoding;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
