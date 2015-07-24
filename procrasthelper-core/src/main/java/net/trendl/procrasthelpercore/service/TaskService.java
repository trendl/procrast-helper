package net.trendl.procrasthelpercore.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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

    public Collection<Task> list(boolean showCompletedTasks) {
        Map<String, Object> parameters = new HashMap<String, Object>();
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
        t.setDifficulty(taskObject.getInteger("difficulty"));
        t.setName(taskObject.getString("name"));
        return t;
    }

    public void save(Task task) throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(task);
        try {
            taskRepository.save(task.getId(), json);
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
