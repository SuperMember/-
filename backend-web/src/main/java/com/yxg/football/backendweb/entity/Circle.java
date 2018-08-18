package com.yxg.football.backendweb.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;
import java.util.Date;

@Document(indexName = "football", type = "circle", indexStoreType = "fs", shards = 5, replicas = 1, refreshInterval = "-1")
public class Circle implements Serializable {
    @Id
    private Integer ID;
    @Field
    private String title;
    @Field
    private String IMG;
    @Field
    private Date CREATED;
    @Field
    private String BACKGROUND;
    @Field
    private Integer STATUE;
    @Field
    private Integer COUNT;
    @Field
    private Integer TYPE;
    @Field
    private Integer USERID;
    @Field
    private String INTRODUCTION;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIMG() {
        return IMG;
    }

    public void setIMG(String IMG) {
        this.IMG = IMG;
    }

    public Date getCREATED() {
        return CREATED;
    }

    public void setCREATED(Date CREATED) {
        this.CREATED = CREATED;
    }

    public String getBACKGROUND() {
        return BACKGROUND;
    }

    public void setBACKGROUND(String BACKGROUND) {
        this.BACKGROUND = BACKGROUND;
    }

    public Integer getSTATUE() {
        return STATUE;
    }

    public void setSTATUE(Integer STATUE) {
        this.STATUE = STATUE;
    }

    public Integer getCOUNT() {
        return COUNT;
    }

    public void setCOUNT(Integer COUNT) {
        this.COUNT = COUNT;
    }

    public Integer getTYPE() {
        return TYPE;
    }

    public void setTYPE(Integer TYPE) {
        this.TYPE = TYPE;
    }

    public Integer getUSERID() {
        return USERID;
    }

    public void setUSERID(Integer USERID) {
        this.USERID = USERID;
    }

    public String getINTRODUCTION() {
        return INTRODUCTION;
    }

    public void setINTRODUCTION(String INTRODUCTION) {
        this.INTRODUCTION = INTRODUCTION;
    }
}
