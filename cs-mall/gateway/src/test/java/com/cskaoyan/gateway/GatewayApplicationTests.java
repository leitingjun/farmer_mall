package com.cskaoyan.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

@SpringBootTest
class GatewayApplicationTests {

    public void method(MyInterface m){
        m.print("hello");
    }

    @Test
    void contextLoads() {
        LinkedList<String> strings = new LinkedList<>();
        strings.add("a");
        strings.add("b");
        method(str -> System.out.println(strings));

    }
    @Test
     void test1(){
        int[] ints={1,4,2,6};
        Arrays.sort(ints);
        System.out.println(ints.toString());
    }

}
