package test.MethodHandle;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

public class MainTest {

    @Test
    public void testInMethodHanle() throws NoSuchMethodException, IllegalAccessException {
        var start = System.currentTimeMillis();
        MethodType methodType = MethodType.methodType(void.class, String.class);
        Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.findVirtual(Foo.class, "hello", methodType);
        Foo foo = new Foo();
        methodHandle = methodHandle.bindTo(foo);
        try {
            for (int i = 0; i < 1000; i++) {
                methodHandle.invoke("korn");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    @Test
    public void testMethodHanleWithReflect() throws NoSuchMethodException, IllegalAccessException {
        Method method = Foo.class.getMethod("hello", String.class);
        Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.unreflect(method);
        Foo foo = new Foo();
        methodHandle = methodHandle.bindTo(foo);
        try {
            for (int i = 0; i < 1000; i++) {
                methodHandle.invoke("korn");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInReflect() {
        var start = System.currentTimeMillis();
        try {
            Method method = Foo.class.getMethod("hello", String.class);
            for (int i = 0; i < 1000; i++) {
                method.invoke(new Foo(), "korn");
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis() - start);
    }

}
