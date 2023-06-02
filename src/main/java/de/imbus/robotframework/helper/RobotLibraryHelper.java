package de.imbus.robotframework.helper;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RobotLibraryHelper {

    static class ArgumentInfo {
        ArgumentInfo(String name, Class<?> cls) {
            this.name = name;
            this.cls = cls;
        }

        String name;
        Class<?> cls;

        public String getParameterType() {

            if (cls == Object.class)
                return "Any";
            else if (cls == String.class)
                return "str";
            else if (cls == boolean.class) {
                return "bool";
            }
            return cls.getSimpleName();
        }
    }

    static class KeywordInfo {
        KeywordInfo(String name, Method method, List<ArgumentInfo> arguments) {
            this.name = name;
            this.method = method;
            this.arguments = arguments;
        }

        String name;
        Method method;
        List<ArgumentInfo> arguments;

        static Map<Class<?>, Map<String, KeywordInfo>> keywordInfos = new HashMap<>();
    }

    public static List<String> getKeywordNames(Object obj) {
        return getKeywordInfo(obj.getClass()).keySet().stream().collect(Collectors.toList());
    }

    public static Object runKeyword(Object obj, String name, List<Object> args, Map<String, Object> kwargs)
            throws Throwable { // NOSONAR
        KeywordInfo info = getKeywordInfo(obj.getClass()).get(name);
        if (info.arguments.stream().anyMatch(v -> "*args".equals(v.name))
                && info.arguments.stream().anyMatch(v -> "**kwargs".equals(v.name))) {
            return info.method.invoke(obj, args, kwargs);
        }

        if (info.arguments.stream().anyMatch(v -> "**kwargs".equals(v.name))) {
            List<Object> newArgs = new ArrayList<>();
            newArgs.addAll(args);
            newArgs.add(kwargs);

            return info.method.invoke(obj, newArgs.toArray());
        }

        return info.method.invoke(obj, args.toArray());
    }

    public static List<String> getKeywordArguments(Object obj, String name) {
        return getKeywordInfo(obj.getClass()).get(name).arguments.stream().map(a -> a.name)
                .collect(Collectors.toList());
    }

    public static String getKeywordDocumentation(Object obj, String name) {
        return null;
    }

    public static List<String> getKeywordTags(Object obj, String name) {
        return null;
    }

    public static List<String> getKeywordTypes(Object obj, String name) {
        return getKeywordInfo(obj.getClass()).get(name).arguments.stream().map(ArgumentInfo::getParameterType)
                .collect(Collectors.toList());
    }

    public static String getName(Object obj) {
        return obj.getClass().getSimpleName();
    }

    static String getKeywordName(Method method) {
        return method.getName();
    }

    static Map<String, KeywordInfo> collectKeywords(Class<?> cls) {
        Map<String, KeywordInfo> result = new HashMap<>();

        Arrays.stream(cls.getDeclaredMethods()).filter(m -> Modifier.isPublic(m.getModifiers()))
                .forEach(m -> {
                    String name = getKeywordName(m);
                    List<ArgumentInfo> arguments = new ArrayList<>();

                    for (Parameter p : m.getParameters()) {
                        String n = p.getName();
                        Class<?> paramcls = p.getType();

                        if ("args".equals(n)) {
                            n = "*" + n;
                            paramcls = Object.class;
                        } else if ("kwargs".equals(n)) {
                            n = "**" + n;
                            paramcls = Object.class;
                        }

                        arguments.add(new ArgumentInfo(n, paramcls));
                    }

                    result.put(name, new KeywordInfo(name, m, arguments));
                });

        return result;
    }

    public static Map<String, KeywordInfo> getKeywordInfo(Class<?> cls) {
        return KeywordInfo.keywordInfos.computeIfAbsent(cls, RobotLibraryHelper::collectKeywords);
    }
}
