package io.github.gjyaiya.realmbrowser.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

public class MagicUtils {

    @Nullable
    public static String createMethodName(Field field) {
        String methodName;
        if (field.getType().equals(boolean.class)) {
            if (field.getName().contains("is")) {
                methodName = field.getName();
            } else {
                methodName = "is" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
            }
        } else {
            methodName = "get" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
        }

        return methodName;
    }

    @NonNull
    public static String invokeMethod(Object realmObject, String methodName) {
        String result = "null";
        try {
            Method method = realmObject.getClass().getMethod(methodName);
            Object resultObj = method.invoke(realmObject);
            if(resultObj != null) {
                result = resultObj.toString();
            }
        } catch (Exception e) {
            //L.e(e.toString());
        }
        return result;

    }

    public static  boolean isParameterizedField(Field field) {
        return field.getGenericType() instanceof ParameterizedType;
    }

    @Nullable
    public static  String createParameterizedName(Field field) {
        ParameterizedType pType = (ParameterizedType) field.getGenericType();
        String rawType = pType.getRawType().toString();
        int rawTypeIndex = rawType.lastIndexOf(".");
        if(rawTypeIndex > 0) {
            rawType = rawType.substring(rawTypeIndex + 1);
        }

        String argument = pType.getActualTypeArguments()[0].toString();
        int argumentIndex = argument.lastIndexOf(".");
        if(argumentIndex > 0) {
            argument = argument.substring(argumentIndex + 1);
        }

        return rawType + "<" + argument + ">";
    }
}
