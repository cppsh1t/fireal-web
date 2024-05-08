package io.github.cppsh1t.fireal.web.core;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Stack;

import io.github.cppsh1t.fireal.web.objectPool.ObjectPool;

public class Request {

    private String url;
    private Map<String, String> paramMap;
    private static RequestCache requestCache = new RequestCache();

    private Request() {

    }

    public String getUrl() {
        return url;
    }

    public static Request make(String url, Map<String, String> paramMap) {
        Request request = requestCache.release();
        request.url = url;
        request.paramMap = paramMap;
        return request;
    }

    public static void recycle(Request request) {
        request.paramMap = null;
        requestCache.recycle(request);
    }

    private static class RequestCache implements ObjectPool<Request> {

        private Stack<WeakReference<Request>> objStack = new Stack<>();

        @Override
        public Request release() {
            if (!objStack.isEmpty()) {
                for (int i = 0; i < objStack.size(); i++) {
                    var obj = objStack.pop().get();
                    if (obj != null)
                        return obj;
                }
            }

            return new Request();
        }

        @Override
        public void recycle(Request request) {
            objStack.push(new WeakReference<Request>(request));
        }

    }

}
