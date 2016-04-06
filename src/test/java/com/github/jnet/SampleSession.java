package com.github.jnet;

import com.github.jnet.utils.IOBuffer;


public class SampleSession extends Session {

    @Override
    public void open(IOBuffer readBuf, IOBuffer writeBuf) throws Exception {
        System.out.println("open");

    }


    @Override
    public void read(IOBuffer readBuf, IOBuffer writeBuf) throws Exception {
        System.out.println("reading");

    }

//    @Override
//    public void writeCompleted(IoBuffer readBuf, IoBuffer writeBuf) throws Exception {
//        System.out.println("writeCompleted");
//
//    }

    @Override
    public void write(IOBuffer readBuf, IOBuffer writeBuf) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

}
