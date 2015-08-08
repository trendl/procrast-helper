package net.trendl.procrasthelper.web.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.jayway.jsonpath.JsonPath;
import net.trendl.procrasthelpercore.domain.User;
import net.trendl.procrasthelpercore.service.UserService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Tomas.Rendl on 7.8.2015.
 */
@RestController
@RequestMapping("/auth/google")
public class GoogleLoginController {
    Logger LOG = LoggerFactory.getLogger(GoogleLoginController.class.getName());

    public static final JacksonFactory JSON_FACTORY = new JacksonFactory();
    public static final HttpTransport TRANSPORT = new NetHttpTransport();
    private static String CLIENT_ID = "863921666066-u5u5itr52t2tb1jprcanvg4i8v6g2naa.apps.googleusercontent.com";

    @Resource(name="userService")
    private UserService userService;


    @RequestMapping(value = "/tokensignin", method = RequestMethod.POST)
    public ResponseEntity tokenSignIn(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response ) throws Exception {
        LOG.info("gplus/tokensignin called.");

        String idToken = JsonPath.read(requestBody, "$.idToken");
        String accessToken = JsonPath.read(requestBody, "$.accessToken");
        String userId = JsonPath.read(requestBody, "$.userId");

        if (verifyToken(idToken)) {
            LOG.info("token verified");
            User user = new User();
            String userIdAndName = UUID.randomUUID().toString();
            user.setId(userIdAndName);
            user.setName(userIdAndName);
            user.setGoogleId(userId);
            String tmpPassword = UUID.randomUUID().toString().substring(0, 10);
            user.setPassword(tmpPassword);
            user.setPasswordExpiration(DateTime.now().plusMinutes(1));
            user = userService.saveUser(user);

            return new ResponseEntity(user, HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private boolean verifyToken(String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(TRANSPORT, JSON_FACTORY).setAudience(Arrays.asList(CLIENT_ID)).build();
        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            System.out.println("User ID: " + payload.getSubject());
            return true;
        } else {
            System.out.println("Invalid ID token.");
            return false;
        }
    }
}
