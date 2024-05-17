package io.github.cppsh1t.fireal.web.security;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LocalAuthenticator implements Authenticator{

    private final Map<String, UserDetail> userMap = new HashMap<>();
    private UserDetail currentUser;

    public void addUser(UserDetail userDetail) {
        userMap.put(userDetail.getName(), userDetail);
    }

    @Override
    public boolean login(String username, String password, Boolean remember, int rememberTime,
            HttpServletResponse resp) {
        UserDetail userDetail = userMap.get(username);
        if (userDetail == null) return false;
        if (!userDetail.getPassword().equals(password)) return false;
        currentUser = userDetail;
        return true;
    }

    @Override
    public void logout(HttpServletRequest req, HttpServletResponse resp) {
        currentUser = null;
    }

    @Override
    public UserDetail resolve(HttpServletRequest request) {
        return currentUser;
    }

}
