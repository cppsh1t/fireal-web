package io.github.cppsh1t.fireal.web.security;

import io.github.cppsh1t.fireal.web.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

public class JwtAuthenticator implements Authenticator{

    private final Function<String, UserDetail> mapperFunc;
    private final Date time;

    public JwtAuthenticator(Function<String, UserDetail> mapper) {
        mapperFunc = mapper;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 24 * 7);
        time = calendar.getTime();
    }

    public JwtAuthenticator(Function<String, UserDetail> mapper, Date time) {
        mapperFunc = mapper;
        this.time = time;
    }


    @Override
    public boolean login(String username, String password, Boolean remember, int rememberTime, HttpServletResponse resp) {
        UserDetail user = mapperFunc.apply(username);
        if (user == null) return false;
        if (!user.getPassword().equals(password)) return false;

        String token = JwtUtil.createJwt(user, time);
        Cookie cookie = new Cookie("token", token);
        if (remember) {
            cookie.setMaxAge(rememberTime);
        }
        cookie.setPath("/");
        resp.addCookie(cookie);
        return true;
    }

    @Override
    public void logout(HttpServletRequest req, HttpServletResponse resp) {
        Cookie cookie = Arrays.stream(req.getCookies())
                .filter(c -> c.getName().equals("token")).findFirst().orElse(null);
        if (cookie == null) return;
        String token = cookie.getValue();
        if (token.isEmpty()) return;
        UserDetail userDetail = JwtUtil.resolveJwt(token);
        if (userDetail == null) return;
        cookie = new Cookie("token", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
    }

    public UserDetail resolve(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        Cookie tokenCookie = Arrays.stream(cookies).filter(c -> c.getName().equals("token")).findFirst().orElse(null);
        if (tokenCookie == null) return null;
        String token = tokenCookie.getValue();
        return JwtUtil.resolveJwt(token);
    }
}
