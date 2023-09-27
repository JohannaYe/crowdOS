package cn.crowdos.demo.controller;

import cn.crowdos.demo.common.R;
import cn.crowdos.demo.entity.User;
import cn.crowdos.demo.service.UserService;
import cn.crowdos.demo.utils.ValidateCodeUtils;
import cn.crowdos.demo.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();

        if(StringUtils.isNotEmpty(phone)){
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
//            log.info("code={}",code);

            //调用阿里云提供的短信服务API完成发送短信
            //SMSUtils.sendMessage("wall-e","",phone,code);

            //需要将生成的验证码保存到Session
            session.setAttribute(phone,code);

            return R.success("手机验证码短信发送成功");
        }

        return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
//        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从Session中获取保存的验证码
        Object codeInSession = session.getAttribute(phone);

        //进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
        if(codeInSession != null && codeInSession.equals(code)){
            //如果能够比对成功，说明登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);
            if(user == null){
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
//                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }
    @Autowired
    private UserMapper usermapper;
    @GetMapping("/query")
    public List query(){
        List<User> list=usermapper.selectList(null);
        System.out.println(list);
        return list;
    }
    @PostMapping("/add")
    public String save(User user){
        int i=usermapper.insert(user);
        if(i>0){
            return "插入成功";
        }else{
            return "插入失败";
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            int rowsAffected = usermapper.deleteById(id); // 使用MyBatis的deleteById方法删除记录
            if (rowsAffected == 1) {
                return ResponseEntity.ok("User with ID " + id + " deleted successfully.");
            } else {
                return ResponseEntity.notFound().build(); // 如果未找到匹配的记录，返回404
            }
        } catch (Exception e) {
            // 处理异常情况，例如数据库访问问题
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user: " + e.getMessage());
        }
    }
}
