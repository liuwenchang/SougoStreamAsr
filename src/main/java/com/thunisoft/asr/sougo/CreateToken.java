package com.thunisoft.asr.sougo;

import com.google.protobuf.Duration;
import com.sogou.speech.auth.v1.CreateTokenRequest;
import com.sogou.speech.auth.v1.CreateTokenResponse;
import com.sogou.speech.auth.v1.authGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelProvider;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuwenchang
 * @date 2019-05-31 10:25
 */
public class CreateToken {
    private static Logger logger = LoggerFactory.getLogger(CreateToken.class);
    private static long TIME_EXP = 8 * 60 * 60;
    public static void CreateToken(SougoConfig sougoConfig){
        Duration duration = Duration.newBuilder().setSeconds(TIME_EXP).build();
        CreateTokenRequest request = CreateTokenRequest.newBuilder()
                .setExp(duration)
                .setAppid(sougoConfig.getAppId())
                .setAppkey(sougoConfig.getAppkey())
                .setUuid(sougoConfig.getAppId())
                .buildPartial();
        ManagedChannel channel = new NettyChannelProvider() .builderForAddress(sougoConfig.ip,
                sougoConfig.port)
                .overrideAuthority(sougoConfig.ip + ":" + sougoConfig.port)
                .negotiationType(NegotiationType.TLS)
                .build();
        authGrpc.authBlockingStub client = authGrpc.newBlockingStub(channel);
        CreateTokenResponse response =client.createToken(request);
         sougoConfig.setToken(response.getToken());
    }
}
