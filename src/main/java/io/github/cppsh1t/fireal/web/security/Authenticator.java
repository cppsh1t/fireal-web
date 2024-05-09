package io.github.cppsh1t.fireal.web.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface Authenticator {

    boolean login(String username, String password, Boolean remember, int rememberTime, HttpServletResponse resp);

    void logout(HttpServletRequest req, HttpServletResponse resp);

    UserDetail resolve(HttpServletRequest request);
}
