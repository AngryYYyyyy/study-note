package com.boge.domain;

public class Person {

    public Integer getId() {
        System.out.println("读取id");
        return 6;
    }

    public void setId(Integer id) {
        System.out.println("写入id:"+id);
    }

    public String getUserName() {

        return "张三";
    }

    public String show(){
        return "Show ....";
    }
}
