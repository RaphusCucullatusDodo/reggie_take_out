package com.raphus.test;

import com.raphus.reggie.ReggieApplication;
import com.raphus.reggie.entity.User;
import com.raphus.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes=ReggieApplication.class)
public class SpringCacheRedisTest {
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private UserService userService;

//    /**
//     * org.junit.runners.model.InvalidTestClassError: Invalid test class 'com.raphus.test.SpringCacheRedisTest':
//     *   1. Method test() should be void
//     * @return 单元测试不能有返回值
//     */
//    @Test
//    @Cacheable(value = "testRedisCache",key="user" ,unless="#result == null")
//    public List<User> test(){
//        List<User> list = userService.list();
//        return list;
//    }


}
