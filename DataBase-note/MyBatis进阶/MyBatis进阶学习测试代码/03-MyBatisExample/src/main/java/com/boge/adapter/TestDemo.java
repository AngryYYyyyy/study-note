package com.boge.adapter;

public class TestDemo {
    public static void main(String[] args) {
        Adaptee a = new Adaptee();
        Target t = new Adapter2(a);
        t.handleReq();
    }


}
