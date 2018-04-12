package com.example.azatk.sdccommunity;

public class Finder {
 
    private int id=0;
    private static int idInc;
 
    public Finder() {
        this.id = idInc++;
    }
 
    public int getId() {
        return id;
    }
 

 
    }