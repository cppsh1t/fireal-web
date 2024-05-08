package test.pathTest;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.cppsh1t.fireal.web.path.AntPathMatcher;

public class MainTest {

    @Test
    public void normalUse() {
        AntPathMatcher matcher = new AntPathMatcher();

        // 设置路径匹配为不区分大小写
        matcher.setCaseSensitive(false);

        // 检查路径是否匹配给定的模式
        boolean isMatched = matcher.match("/api/**", "/api/v1/users");
        System.out.println("Path '/api/v1/users' matches pattern '/api/**': " + isMatched); // 输出：true

        // 使用路径变量
        isMatched = matcher.match("/api/{version}/users", "/api/v1/users");
        Map<String, String> uriTemplateVariables = new HashMap<>();
        uriTemplateVariables = matcher.extractUriTemplateVariables("/api/{version}/users", "/api/v1/users");
        System.out.println("Path '/api/v1/users' matches pattern '/api/{version}/users': " + isMatched); // 输出：true
        System.out.println("Extracted variable 'version': " + uriTemplateVariables.get("version")); // 输出：v1

        // 只匹配路径开始部分
        isMatched = matcher.matchStart("/api/**", "/api/docs/index.html");
        System.out.println("Path '/api/docs/index.html' starts with pattern '/api/**': " + isMatched); // 输出：true

    }

}
