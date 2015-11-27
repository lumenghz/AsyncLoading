package com.winchance.util.convertor;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.Reader;
import java.lang.reflect.Type;

@SuppressWarnings("unuse")
public class GsonConvertor {
    private static final ThreadLocal<Gson> gsonThreadLocal = new ThreadLocal<Gson>() {
        @Override
        protected Gson initialValue() {
            return new Gson();
        }
    };

    public static String toJson(Object object) {
        return gsonThreadLocal.get().toJson(object);
    }

    public static String toJson(Object object, Type type) {
        return gsonThreadLocal.get().toJson(object, type);
    }

    public static <T> T fromJson(String jsonString, Class<T> classOfT)
            throws JsonSyntaxException {
        return gsonThreadLocal.get().<T>fromJson(jsonString, classOfT);
    }

    public static <T> T fromJson(String jsonString, Type type)
            throws JsonSyntaxException {
        return gsonThreadLocal.get().<T>fromJson(jsonString, type);
    }

    public static <T> T fromJson(Reader reader, Class<T> classOfT)
            throws JsonIOException, JsonSyntaxException  {
        return gsonThreadLocal.get().<T>fromJson(reader, classOfT);
    }

    public static <T> T fromJson(Reader reader, Type type)
            throws JsonIOException, JsonSyntaxException  {
        return gsonThreadLocal.get().<T>fromJson(reader, type);
    }
}
