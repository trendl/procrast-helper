package net.trendl.procrasthelpercore.service;

import net.homecredit.innovations.mongorepository.CommonMongoRepository;
import net.trendl.procrasthelpercore.domain.User;
import org.joda.time.DateTime;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tomas.Rendl on 7.8.2015.
 */
public class UserService {

    @Resource(name="userRepository")
    CommonMongoRepository userRepository;

    public User saveUser(User user) throws Exception {
        try {
            // first try to find user with the same name
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("googleId", user.getGoogleId());
            Collection usersWithSameName = userRepository.find(params);
            if (usersWithSameName != null && usersWithSameName.size() > 0) {
                User u = User.convert((Map<String, Object>)usersWithSameName.iterator().next());
                u.setPassword(user.getPassword());
                u.setPasswordExpiration(user.getPasswordExpiration());
                userRepository.save(u.getId(), u);
                return u;
            } else {
                userRepository.save(user.getId(), user);
                return user;
            }
        } finally {
            userRepository.close();
        }
    }

    public String getValidPassword(String name) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("name", name);
            Collection foundUsers = userRepository.find(params);
            if (foundUsers != null && foundUsers.iterator().hasNext()) {
                Map<String, Object> firstUser = (Map<String, Object>)foundUsers.iterator().next();
                if (firstUser.containsKey("passwordExpiration")) {
                    Long millis = (Long)((Map<String, Object>)firstUser.get("passwordExpiration")).get("millis");
                    if (millis > DateTime.now().getMillis()) {
                        return (String)firstUser.get("password");
                    }
                }
            }

            return null;
        } finally {
            userRepository.close();
        }
    }

}
