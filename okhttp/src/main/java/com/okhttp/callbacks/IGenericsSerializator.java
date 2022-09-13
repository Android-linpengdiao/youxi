package com.okhttp.callbacks;


import java.lang.reflect.Type;

public interface IGenericsSerializator {
    <T> T transform(String response, Class<T> classOfT);
    <T> T transform(String response, Type type);
}
