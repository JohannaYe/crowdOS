package cn.crowdos.demo.kernel;

import com.alibaba.fastjson.JSONObject;
import cn.crowdos.demo.entity.Action;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class realHumanInstructionParsing implements HumanInstruction{
    private final String humanInstruction;
    private List<Action> taskSteps;

    public realHumanInstructionParsing(String humanInstruction){
        this.humanInstruction = humanInstruction;
    }

    public static JSONObject postResponse(String url,JSONObject jsonParam){

        HttpClient client = HttpClients.createDefault();
//     要调用的接口方法
        HttpPost post = new HttpPost(url);
        JSONObject jsonObject = null;
        try {
            StringEntity s = new StringEntity(jsonParam.toString(),"UTF-8");
            s.setContentType("application/json");
            post.setEntity(s);
            post.setHeader("Content-Type","application/json");

            HttpResponse res = client.execute(post);
            if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                /*返回json格式*/
                jsonObject = JSONObject.parseObject(EntityUtils.toString(res.getEntity()));
            }
        } catch (IOException e) {
            System.out.println("接口调用出错！");
            e.printStackTrace();
            throw new RuntimeException(e);

        }
        return jsonObject;
    }

    private JSONObject chatWithAI(String instruction){
        JSONObject requestPram = new JSONObject();
        requestPram.put("prompt",instruction);
        JSONObject postReturn = postResponse("http://43.156.40.89/chat",requestPram);
//        String chatResult = "{\"任务1\"：[\"导航\",\"用户所在位置\"],\"任务2\"：[\"拿取\",\"饮料\"],\"任务3\"：[\"导航\",\"笃行宿舍楼\"],\"任务4\"：[\"放下\",\"快递\"]}";
        return postReturn;
    }

    @Override
    public List<Action> humanInstructionParsing() {
        JSONObject taskStepJSON = chatWithAI(humanInstruction);
//        String chatResult = "{\"任务1\"：[\"导航\",\"用户所在位置\"],\"任务2\"：[\"拿取\",\"饮料\"],\"任务3\"：[\"导航\",\"笃行宿舍楼\"],\"任务4\"：[\"放下\",\"快递\"]}";
//        JSONObject taskStepJSON = JSON.parseObject(chatResult);
        System.out.println(taskStepJSON);
        List<Action> result = new ArrayList<>();
        for(String key : taskStepJSON.keySet()){
            System.out.println(key);
            if (key.contains("任务")){
                Object value = taskStepJSON.get(key);
                List<String> taskStep = (List)value;
//                System.out.println(taskStep);
                if (taskStep.size() == 2) {
                    Action step = new Action(taskStep.get(0), taskStep.get(1));
                    result.add(step);
                }
            }
        }
        return result;
    }

    @Override
    public List<Action> getTaskSteps() {
        return taskSteps;
    }

    @Override
    public String getHumanInstruction() {
        return humanInstruction;
    }
}
