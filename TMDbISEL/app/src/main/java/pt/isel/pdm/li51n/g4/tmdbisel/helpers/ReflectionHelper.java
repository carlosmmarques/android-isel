package pt.isel.pdm.li51n.g4.tmdbisel.helpers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionHelper {
    public static Method getMethod(Class<?> clazz, String name) throws NoSuchMethodException {
        try {
            return clazz.getDeclaredMethod(name);
        } catch (NoSuchMethodException ex) {
            if (clazz == Object.class) {
                throw ex;
            }
            return getMethod(clazz.getSuperclass(), name);
        }
    }

    public static Iterable<Field> getFieldsUpTo(@NonNull Class<?> startClass, @Nullable Class<?> exclusiveParent) {

        List<Field> currentClassFields = new ArrayList<>();
        currentClassFields.addAll(Arrays.asList(startClass.getDeclaredFields()));
        Class<?> parentClass = startClass.getSuperclass();

        if (parentClass != null && (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
            List<Field> parentClassFields = (List<Field>) getFieldsUpTo(parentClass, exclusiveParent);
            currentClassFields.addAll(parentClassFields);
        }
        return currentClassFields;
    }
}

