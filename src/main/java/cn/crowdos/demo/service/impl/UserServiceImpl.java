package cn.crowdos.demo.service.impl;


import cn.crowdos.demo.entity.User;
import cn.crowdos.demo.service.UserService;
import org.springframework.stereotype.Service;
import cn.crowdos.demo.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService{
}
