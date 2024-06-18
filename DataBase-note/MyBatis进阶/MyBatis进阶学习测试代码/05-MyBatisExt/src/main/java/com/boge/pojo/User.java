package com.boge.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    private Integer id;

    private String userName;

    private String password;

    private String realName;

    private  Integer age;

    private  Integer dId;

    private Integer iId;

}
