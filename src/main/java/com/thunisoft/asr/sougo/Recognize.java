package com.thunisoft.asr.sougo;

import com.google.protobuf.ByteString;
import com.sogou.speech.asr.v1.*;
import io.grpc.ManagedChannel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelProvider;
import io.grpc.stub.StreamObserver;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;

/**
 * @author liuwenchang
 * @date 2019-05-30 10:54
 */
public class Recognize {
    private asrGrpc.asrStub client;
    private StreamObserver<StreamingRecognizeRequest> mRequestObserver;
    private StreamObserver<StreamingRecognizeResponse> mResponseObserver;
    private SougoConfig sougoConfig;
    private volatile boolean isCompleted = false;
    private static Logger logger = LoggerFactory.getLogger(Recognize.class);
    public Recognize(StreamObserver<StreamingRecognizeResponse> mResponseObserver,SougoConfig sougoConfig) {
        this.mResponseObserver = mResponseObserver;
        this.sougoConfig = sougoConfig;
    }
    public void  start(){
        isCompleted = false;
        createGrpcClient();
        buildGrpcConnection();
    }
    public void stop() {
        if (isCompleted) {
            return;
        }
        if (mRequestObserver == null) {
            return;
        }
        mRequestObserver.onCompleted();
        isCompleted = true;
        destroy();
    }
    public void sendData(byte[] data, int offset,int size) {
        if (isCompleted) {
            return;
        }
        if (mRequestObserver == null) {
            return;
        }
        // Call the streaming recognition API
        ByteString tempData = null;
        tempData = ByteString.copyFrom(data,offset,size);
        if (data != null && data.length > 0) {
            mRequestObserver.onNext(StreamingRecognizeRequest.newBuilder()
                    .setAudioContent(tempData)
                    .build());
        }
    }

    private void finishRecognizing() {
        if (!isCompleted && mRequestObserver != null) {
            mRequestObserver.onCompleted();
        }
        mRequestObserver = null;
        if (client == null) {
            return;
        }
        final ManagedChannel channel = (ManagedChannel) client.getChannel();
        if (channel != null && !channel.isShutdown()) {
            channel.shutdownNow();
        }
    }
    private void destroy() {
        finishRecognizing();
        client = null;
    }


    private void createGrpcClient() {
      HashMap<String, String> headerParams = new HashMap<String, String>();
        headerParams.put("Authorization", "Bearer " + sougoConfig.getToken());
        headerParams.put("appid", sougoConfig.getAppId());
        headerParams.put("uuid", sougoConfig.getAppId());

        ManagedChannel channel=
              new NettyChannelProvider()
                .builderForAddress(sougoConfig.ip,
                        sougoConfig.port)
                .overrideAuthority(sougoConfig.ip + ":" + sougoConfig.port)
                .negotiationType(NegotiationType.TLS)
                .intercept(new HeaderClientInterceptor(headerParams)).build();

        client = asrGrpc.newStub(channel);
    }

    private void buildGrpcConnection() {
        mRequestObserver = client.streamingRecognize(mResponseObserver);
        mRequestObserver.onNext(StreamingRecognizeRequest.newBuilder()
                .setStreamingConfig(StreamingRecognitionConfig.newBuilder()
                        .setConfig(RecognitionConfig.newBuilder()
                                .setLanguageCode(sougoConfig.getLanguageCode())
                                .setEncoding(sougoConfig.getAudioEncoding())
                                .setSampleRateHertz(16000)
                                .setEnableWordTimeOffsets(false)
                                .setMaxAlternatives(1)
                                .setProfanityFilter(true)
                                .setDisableAutomaticPunctuation(false)
                                .setModel(sougoConfig.getModel())
                                .setEnableWordTimeOffsets(true)
                                .build())
                        .setInterimResults(true)
                        .setSingleUtterance(true)
                        .build())
                .build());
    }

}
