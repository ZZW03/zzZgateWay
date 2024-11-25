package com.zzz.netty.process.disruptor;


public interface ParallelQueue<E> {


    void add(E event);

    void add(E... event);

    boolean tryAdd(E event);

    boolean tryAdd(E... event);

    void start();

    void shutDown();

    boolean isShutDown();

}
