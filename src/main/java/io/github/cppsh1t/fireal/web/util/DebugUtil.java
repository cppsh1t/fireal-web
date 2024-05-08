package io.github.cppsh1t.fireal.web.util;

import java.time.LocalDateTime;

public class DebugUtil {

    public static void log(Object... objs) {
        LocalDateTime currentTime = LocalDateTime.now();
        long threadId = Thread.currentThread().getId();
        String head = "[" + currentTime + "]-[" + threadId + "]";
        StringBuilder stringBuilder = new StringBuilder();
        for (Object obj : objs) {
            stringBuilder.append("-{").append(obj.toString()).append("}");
        }
        System.out.println(head + stringBuilder);
    }

    public static void log(String... objs) {
        LocalDateTime currentTime = LocalDateTime.now();
        long threadId = Thread.currentThread().getId();
        String head = "[" + currentTime + "]-[" + threadId + "]";
        StringBuilder stringBuilder = new StringBuilder();
        for (String obj : objs) {
            stringBuilder.append("-{").append(obj).append("}");
        }
        System.out.println(head + stringBuilder);
    }

}
