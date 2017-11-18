package com.wkx.provider.config;

import org.I0Itec.zkclient.ZkClient;

import java.util.List;
import java.util.stream.Collectors;

public class ZkUtils {

    public static ZkClient zkClient=BeanFactory.getBean(ZkClient.class);

    public static Object getData(String path){
        return zkClient.readData(path);
    }

    public static List<Object> getChildData(String path){
        List<String> pathList=zkClient.getChildren(path);
        return pathList.stream().map(str->path+"/"+str).map(str->zkClient.readData(str)).collect(Collectors.toList());
    }

}
