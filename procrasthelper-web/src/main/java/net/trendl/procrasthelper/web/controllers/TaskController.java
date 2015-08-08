package net.trendl.procrasthelper.web.controllers;

import net.trendl.procrasthelpercore.domain.AppliedReward;
import net.trendl.procrasthelpercore.domain.Reward;
import net.trendl.procrasthelpercore.domain.Task;
import net.trendl.procrasthelpercore.service.RewardService;
import net.trendl.procrasthelpercore.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * Created by Tomas.Rendl on 22.7.2015.
 */
@RestController
@RequestMapping("")
public class TaskController extends BaseController {

    @Resource(name="taskService")
    TaskService taskService;

    @Resource(name="rewardService")
    RewardService rewardService;

    @RequestMapping(value= "/tasks", method = RequestMethod.GET)
    public ResponseEntity listTasks() {
        return new ResponseEntity(taskService.list(getCurrentUserName(), false), HttpStatus.OK);
    }

    @RequestMapping(value="/task", method = RequestMethod.POST)
    public void insertTask(@RequestBody Task task) throws Exception {
        task.setId(UUID.randomUUID().toString());
        task.setUserId(getCurrentUserName());
        taskService.save(task);
    }

    @RequestMapping(value="/task/{taskId}/complete", method = RequestMethod.POST)
    public ResponseEntity completeTask(@PathVariable String taskId) throws Exception {
        Task task = taskService.findOne(taskId);
        task.setCompleted(true);
        taskService.save(task);
        int newRewardCount = rewardService.applyReward(getCurrentUserName(), task.getDifficulty());
        return new ResponseEntity(newRewardCount, HttpStatus.OK);
    }

    @RequestMapping(value="/task/{taskId}/delete", method = RequestMethod.POST)
    public void deleteTask(@PathVariable String taskId) throws Exception {
        taskService.delete(taskId);
    }

    @RequestMapping(value= "/rewards", method = RequestMethod.GET)
    public ResponseEntity listRewards() {
        return new ResponseEntity(rewardService.list(getCurrentUserName()), HttpStatus.OK);
    }

    @RequestMapping(value="/reward", method = RequestMethod.POST)
    public void insertReward(@RequestBody Reward reward) throws Exception {
        reward.setId(UUID.randomUUID().toString());
        reward.setUserId(getCurrentUserName());
        rewardService.save(reward);
    }

    @RequestMapping(value="/reward/{rewardId}/delete", method = RequestMethod.POST)
    public void deleteReward(@PathVariable String rewardId) throws Exception {
        rewardService.deleteOne(rewardId);
    }

    @RequestMapping(value= "/pendingrewards", method = RequestMethod.GET)
    public ResponseEntity listPendingRewards() {
        return new ResponseEntity(rewardService.listPendingRewards(getCurrentUserName()), HttpStatus.OK);
    }

    @RequestMapping(value="/pendingreward/{rewardId}/take", method = RequestMethod.POST)
    public void takePendingReward(@PathVariable String rewardId) throws Exception {
        AppliedReward appliedReward = rewardService.findAppliedReward(rewardId);
        appliedReward.setPending(false);
        rewardService.save(appliedReward);
    }

    @RequestMapping(value="/pendingreward/{rewardId}/delete", method = RequestMethod.POST)
    public void deletePendingReward(@PathVariable String rewardId) throws Exception {
        rewardService.deletePendingReward(rewardId);
    }

    private String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return currentPrincipalName;
    }
}
