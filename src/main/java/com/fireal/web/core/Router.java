package com.fireal.web.core;

import java.lang.ref.WeakReference;
import java.util.Stack;

import com.fireal.web.objectPool.ObjectPool;

public class Router {

    private String url;
    private static final RouterCache redirectCache = new RouterCache();
    private RouterType routerType;

    private Router() {

    }

    public String getUrl() {
        return url;
    }

    public RouterType getRouterType() {
        return routerType;
    }

    /**
     * Redirects the request to a new URL.
     *
     * @param url The URL to which the request should be redirected.
     * @return The router responsible for performing the redirection.
     */
    public static Router redirect(String url) {
        Router router = redirectCache.release();
        router.url = url;
        router.routerType = RouterType.REDIRECT;
        return router;
    }

    /**
     * Forwards the request to a new URL.
     *
     * @param url The URL to which the request should be forwarded.
     * @return The router responsible for handling the forwarding process.
     */
    public static Router forward(String url) {
        Router router = redirectCache.release();
        router.url = url;
        router.routerType = RouterType.FORWARD;
        return router;
    }

    public static void recycle(Router router) {
        redirectCache.recycle(router);
    }

    public static enum RouterType {
        REDIRECT,
        FORWARD
    }

    private static class RouterCache implements ObjectPool<Router> {

        private final Stack<WeakReference<Router>> objStack = new Stack<>();

        @Override
        public Router release() {
            if (!objStack.isEmpty()) {
                for (int i = 0; i < objStack.size(); i++) {
                    var obj = objStack.pop().get();
                    if (obj != null)
                        return obj;
                }
            }

            return new Router();
        }

        @Override
        public void recycle(Router Router) {
            objStack.push(new WeakReference<>(Router));
        }

    }

}
