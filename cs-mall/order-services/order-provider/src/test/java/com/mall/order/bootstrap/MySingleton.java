package com.mall.order.bootstrap;




/**
 * @Author ltj
 * @Date 2021/3/4 21:54
 **/

public class MySingleton {
    private static MySingleton mySingleton;
    private  MySingleton(){}
    public synchronized static MySingleton getInstance(){
        if(mySingleton==null){
            mySingleton=new MySingleton();
            System.out.println("初始化");
        }
        System.out.println("返回单例");
        return mySingleton;
    }
}
