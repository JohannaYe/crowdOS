package cn.crowdos.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.crowdos.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User>{
}
