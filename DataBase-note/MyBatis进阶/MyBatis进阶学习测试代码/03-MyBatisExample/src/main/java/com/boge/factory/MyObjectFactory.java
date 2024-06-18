package com.boge.factory;

import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

public class MyObjectFactory extends DefaultObjectFactory {

    @Override
    public <T> T create(Class<T> type) {
        System.out.println("type666 = " + type);
        return super.create(type);
    }
}
