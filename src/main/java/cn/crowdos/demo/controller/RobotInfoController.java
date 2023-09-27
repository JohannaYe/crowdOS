package cn.crowdos.demo.controller;

import cn.crowdos.demo.entity.RobotInfo;
import cn.crowdos.demo.mapper.RobotInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RobotInfoController {
    @Autowired
    private RobotInfoMapper RobotInfomapper;
    @GetMapping("/robot")
    public List query(){
        List<RobotInfo> list=RobotInfomapper.selectList(null);
        System.out.println(list);
        return list;
    }
    @PostMapping("/robot")
    public String save(@RequestBody RobotInfo robotInfo){
        System.out.println(robotInfo);
        int i=RobotInfomapper.insert(robotInfo);
        if(i>0){
            return "插入成功";
        }else{
            return "插入失败";
        }
    }
    @DeleteMapping("/robot/{robotId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long robotId) {
        try {
            int rowsAffected = RobotInfomapper.deleteById(robotId); // 使用MyBatis的deleteById方法删除记录
            if (rowsAffected == 1) {
                return ResponseEntity.ok("User with ID " + robotId + " deleted successfully.");
            } else {
                return ResponseEntity.notFound().build(); // 如果未找到匹配的记录，返回404
            }
        } catch (Exception e) {
            // 处理异常情况，例如数据库访问问题
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user: " + e.getMessage());
        }
    }
}
