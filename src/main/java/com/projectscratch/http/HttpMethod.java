package com.projectscratch.http;

public enum HttpMethod {
    GET, HEAD;

    public static final int MAX_LENGTH;
    static{
        int tempMaxValue = -1;
        for(HttpMethod method : values()){
            if(method.name().length() > tempMaxValue){
                tempMaxValue = method.name().length();
            }
        }
        MAX_LENGTH = tempMaxValue;
    }
}
