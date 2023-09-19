package com.shengjie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shengjie.entity.User;
import com.shengjie.mapper.UserMapper;
import com.shengjie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService{
}
