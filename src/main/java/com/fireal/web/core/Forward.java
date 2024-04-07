package com.fireal.web.core;

import java.lang.ref.WeakReference;
import java.util.Stack;

import com.fireal.web.objectPool.ObjectPool;

public class Forward {

    private String url;
    private static ForwardCache redirectCache = new ForwardCache();

    private Forward() {

    }

    public String getUrl() {
        return url;
    }

    public static Forward forward(String url) {
        Forward Forward = redirectCache.release();
        Forward.url = url;
        return Forward;
    }

    public static void recycle(Forward Forward) {
        redirectCache.recycle(Forward);
    }

    
    private static class ForwardCache implements ObjectPool<Forward>{

        private Stack<WeakReference<Forward>> objStack = new Stack<>();


        @Override
        public Forward release() {
            if (!objStack.isEmpty()) {
                for(int i = 0; i < objStack.size(); i++) {
                    var obj = objStack.pop().get();
                    if (obj != null) return obj;
                }
            }

            return new Forward();
        }

        @Override
        public void recycle(Forward Forward) {
            objStack.push(new WeakReference<Forward>(Forward));
        }
        

        
    }

}
