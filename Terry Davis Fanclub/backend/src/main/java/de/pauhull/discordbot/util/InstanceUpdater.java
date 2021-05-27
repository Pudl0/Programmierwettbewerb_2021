package de.pauhull.discordbot.util;

import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;

public class InstanceUpdater<T> implements InstanceCreator<T> {

    private T object;

    public T createInstance(Type type) {
        return object;
    }

    public InstanceUpdater(T object) {
        this.object = object;
    }

}