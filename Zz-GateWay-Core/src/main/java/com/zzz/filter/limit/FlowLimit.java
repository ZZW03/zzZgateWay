package com.zzz.filter.limit;

import com.zzz.model.GatewayContext;

import java.util.concurrent.ExecutionException;

public interface FlowLimit {

    void doLimit(GatewayContext ctx) throws ExecutionException;
}
