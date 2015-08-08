package net.trendl.procrasthelpercore.service;

import net.homecredit.innovations.mongorepository.CommonMongoRepository;
import net.trendl.procrasthelpercore.domain.Task;
import org.bson.Document;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tomas.Rendl on 22.7.2015.
 */
public class TaskService {
    @Resource(name="taskRepository")
    CommonMongoRepository taskRepository;

    public Collection<Task> list(String userId, boolean showCompletedTasks) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("userId", userId);
        if (!showCompletedTasks) {
            parameters.put("completed", false);
        }

        try {
            Collection taskDocuments = taskRepository.find(parameters);
            Collection<Task> taskResult = new ArrayList<Task>();
            for (Object d : taskDocuments) {
                taskResult.add(convert((Document) d));
            }
            return taskResult;
        } finally {
            taskRepository.close();
        }
    }

    public Task findOne(String id) {
        try {
            Document taskObject = (Document) taskRepository.findOne(id);
            Task t = convert(taskObject);
            return t;
        } finally {
            taskRepository.close();
        }
    }

    private Task convert(Document taskObject) {
        Task t = new Task();
        t.setId(taskObject.getString("id"));
        t.setCompleted(taskObject.getBoolean("completed"));
        // TODO: fix this conversion, it's awful
        t.setDifficulty(Double.valueOf(String.valueOf(taskObject.get("difficulty"))));
        t.setName(taskObject.getString("name"));
        return t;
    }

    public void save(Task task) throws Exception {
        try {
            taskRepository.save(task.getId(), task);
        } finally {
            taskRepository.close();
        }
    }

    public void delete(String taskId) {
        try {
            taskRepository.deleteOne(taskId);
        } finally {
            taskRepository.close();
        }
    }
}
