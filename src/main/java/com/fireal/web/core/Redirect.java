package com.fireal.web.core;

import java.lang.ref.WeakReference;
import java.util.Stack;

import com.fireal.web.objectPool.ObjectPool;

public class Redirect {

    private String url;
    private static RedirectCache redirectCache = new RedirectCache();

    private Redirect() {

    }


    public String getUrl() {
        return url;
    }

    public static Redirect redirect(String url) {
        Redirect redirect = redirectCache.release();
        redirect.url = url;
        return redirect;
    }

    public static void recycle(Redirect redirect) {
        redirectCache.recycle(redirect);
    }

    
    private static class RedirectCache implements ObjectPool<Redirect>{

        private Stack<WeakReference<Redirect>> objStack = new Stack<>();


        @Override
        public Redirect release() {
            if (!objStack.isEmpty()) {
                for(int i = 0; i < objStack.size(); i++) {
                    var obj = objStack.pop().get();
                    if (obj != null) return obj;
                }
            }

            return new Redirect();
        }

        @Override
        public void recycle(Redirect redirect) {
            objStack.push(new WeakReference<Redirect>(redirect));
        }
        

        
    }

}
