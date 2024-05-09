package io.github.cppsh1t.fireal.web.security;

import io.github.cppsh1t.fireal.web.path.AntPathMatcher;
import io.github.cppsh1t.fireal.web.path.PathMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FilterBlock {

    private final List<String> patterns = new ArrayList<>();
    private final List<String> passRoles = new ArrayList<>();
    private final PathMatcher pathMatcher = new AntPathMatcher();

    public void addUrlPatterns(String... patterns) {
        this.patterns.addAll(Arrays.asList(patterns));
    }

    public void addPassRoles(String... roles) {
        passRoles.addAll(Arrays.asList(roles));
    }

    public boolean checkAuth(String urlString, UserDetail user) {
        for(String pattern : patterns) {
            if (pathMatcher.match(pattern, urlString)) {
                if (user == null) return false;
                return checkRole(user);
            }
        }
        return true;
    }

    protected boolean checkRole(UserDetail user) {
        return passRoles.stream().anyMatch(role -> role.equals(user.getRole()));
    }

}