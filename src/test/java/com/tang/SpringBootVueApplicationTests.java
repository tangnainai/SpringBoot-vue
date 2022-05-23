package com.tang;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.entity.User;
import com.tang.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class SpringBootVueApplicationTests {
    @Autowired
    private UserService userService;
    @Test
    void contextLoads() {

    }

    @Test
    void select(){
        IPage<User> page = new Page<>(1, 5);
        IPage<User> list = userService.page(page);
        List<User> records = list.getRecords();
        records.forEach(System.out::println);
    }

    @Test
    public void delete(){
        userService.removeById(15);
    }

}
