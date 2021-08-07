package com.gpmall.comment.bootstrap;

import com.mall.comment.dal.entitys.Comment;
import com.mall.comment.dal.entitys.CommentPicture;
import com.mall.comment.dal.entitys.CommentReply;
import com.mall.comment.dal.persistence.CommentMapper;
import com.mall.comment.dal.persistence.CommentPictureMapper;
import com.mall.comment.dal.persistence.CommentReplyMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommentProviderApplicationTest.class)
public class CommentProviderApplicationTest {
//    @Test
//    public void contextLoads() {
//    }
//
//    @Autowired
//    CommentMapper commentMapper;
//    @Autowired
//    CommentPictureMapper commentPictureMapper;
//    @Autowired
//    CommentReplyMapper commentReplyMapper;
//    @Test
//    public void  test1(){
//        Comment comment = commentMapper.selectByPrimaryKey(1L);
//        CommentPicture commentPicture = commentPictureMapper.selectByPrimaryKey(1L);
//        CommentReply commentReply = commentReplyMapper.selectByPrimaryKey(1L);
//        System.out.println("coupon = " + comment);
//        System.out.println("couponCode = " + commentPicture);
//        System.out.println("couponCode = " + commentReply);
//    }
    @Test
    public void test(){
        String str="aaaabbbccd";
        System.out.println(compressStr(str));

    }
    public String compress(String str){
        char[] strings=new char[1024];
        char[] chars = str.toCharArray();
        for (int i = 1; i < chars.length; i++) {
            if(chars[i]==chars[i-1]){
                if(i<3){
                    strings[i-1]=chars[i-1];
                    strings[i]=2;
                }else {
                    if(chars[i]==strings[i-2]){
                        strings[i-1]++;
                    }
                    strings[i-1]=chars[i-1];
                    strings[i]=2;
                }
            }
        }
        return String.valueOf(strings);
    }
    public String compressStr(String srcStr){
        if(srcStr == null){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int length = srcStr.length();
        char c1 = srcStr.charAt(0);
        int sum = 1;
        for (int i = 1; i < length;i++){
            char c2 = srcStr.charAt(i);
            if (c1 == c2){
                sum++;
                continue;
            }
            if (sum > 1){
                sb.append(c1).append(sum);
            }else {
                sb.append(c1);
            }
            c1 = c2;
            sum = 1;
        }
        // 处理最后一个字符
        if(sum>1){
            sb.append(c1).append(sum);
        }else{
            sb.append(c1);
        }
        return sb.toString();
    }
    @Test
    public void test2(){
        AtomicInteger integer = new AtomicInteger();
        integer.incrementAndGet();
    }
}