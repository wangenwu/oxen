//package com.baren.test;
//
//import com.baren.yak.annoation.CacheData;
//import org.springframework.stereotype.Service;
//
///**
// * Created by user on 16/10/8.
// */
//@Service
//public class YksServiceImp implements YksService {
//
//
//    @CacheData(cacheName = "home",
//            key="#id + '#' + #name",
//            condition = "#id != 0", unless = "#result != 1")
//    public String getPlay(int id, String name) {
//
//        return "remote service111" + id + "---" + name;
//    }
//}
