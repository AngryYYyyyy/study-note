package com.boge.adapter;

/**
 * 适配器 (类适配器方式)
 * (相当于usb和ps/2的转接器)
 * @author 波波烤鸭
 *
 */
public class Adapter extends Adaptee implements Target {

    @Override
    public void handleReq() {
        super.request();
    }
}

