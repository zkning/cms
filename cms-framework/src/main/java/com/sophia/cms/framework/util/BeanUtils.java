package com.sophia.cms.framework.util;

import org.apache.commons.beanutils.*;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by ningzuokun on 2017/12/21.
 */
public class BeanUtils {
    private static Logger logger = LoggerFactory.getLogger(BeanUtils.class);
    public static ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
    private static BeanUtilsBean beanUtilsBean;
    public BeanUtils() {
    }

    public static boolean isEmpty(Object o) {
        if(o == null) {
            return true;
        } else {
            if(o instanceof String) {
                if(((String)o).trim().length() == 0) {
                    return true;
                }
            } else if(o instanceof Collection) {
                if(((Collection)o).isEmpty()) {
                    return true;
                }
            } else if(o.getClass().isArray()) {
                if(((Object[])((Object[])o)).length == 0) {
                    return true;
                }
            } else if(o instanceof Map) {
                if(((Map)o).isEmpty()) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    public static boolean isNotEmpty(Long o) {
        return !isEmpty(o);
    }

    public static boolean isNumber(Object o) {
        if(o == null) {
            return false;
        } else if(o instanceof Number) {
            return true;
        } else if(o instanceof String) {
            try {
                Double.parseDouble((String)o);
                return true;
            } catch (NumberFormatException var2) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static Object populateEntity(Map map, Object entity) throws IllegalAccessException, InvocationTargetException {
        beanUtilsBean.populate(entity, map);
        return entity;
    }

    public static boolean validClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException var2) {
            return false;
        }
    }

    public static boolean isInherit(Class cls, Class parentClass) {
        return parentClass.isAssignableFrom(cls);
    }

    public static Object cloneBean(Object bean) {
        try {
            return beanUtilsBean.cloneBean(bean);
        } catch (Exception var2) {
            handleReflectionException(var2);
            return null;
        }
    }

    public static void copyNotNullProperties(Object dest, Object orig) {
        if(dest == null) {
            logger.error("No destination bean specified");
        } else if(orig == null) {
            logger.error("No origin bean specified");
        } else {
            try {
                int i;
                String name;
                Object e;
                if(orig instanceof DynaBean) {
                    DynaProperty[] ex = ((DynaBean)orig).getDynaClass().getDynaProperties();

                    for(i = 0; i < ex.length; ++i) {
                        name = ex[i].getName();
                        if(beanUtilsBean.getPropertyUtils().isReadable(orig, name) && beanUtilsBean.getPropertyUtils().isWriteable(dest, name)) {
                            e = ((DynaBean)orig).get(name);
                            beanUtilsBean.copyProperty(dest, name, e);
                        }
                    }
                } else if(orig instanceof Map) {
                    Iterator var8 = ((Map)orig).entrySet().iterator();

                    while(var8.hasNext()) {
                        Map.Entry var10 = (Map.Entry)var8.next();
                        name = (String)var10.getKey();
                        if(beanUtilsBean.getPropertyUtils().isWriteable(dest, name)) {
                            beanUtilsBean.copyProperty(dest, name, var10.getValue());
                        }
                    }
                } else {
                    PropertyDescriptor[] var9 = beanUtilsBean.getPropertyUtils().getPropertyDescriptors(orig);

                    for(i = 0; i < var9.length; ++i) {
                        name = var9[i].getName();
                        if(!"class".equals(name) && beanUtilsBean.getPropertyUtils().isReadable(orig, name) && beanUtilsBean.getPropertyUtils().isWriteable(dest, name)) {
                            try {
                                e = beanUtilsBean.getPropertyUtils().getSimpleProperty(orig, name);
                                if(e != null && !"null".equals(e.toString())) {
                                    beanUtilsBean.copyProperty(dest, name, e);
                                }
                            } catch (NoSuchMethodException var6) {
                                var6.printStackTrace();
                            }
                        }
                    }
                }
            } catch (Exception var7) {
                handleReflectionException(var7);
            }

        }
    }

    public static void copyProperties(Object dest, Object orig) {
        try {
            beanUtilsBean.copyProperties(dest, orig);
        } catch (Exception var3) {
            handleReflectionException(var3);
        }

    }

    public static void copyProperty(Object bean, String name, Object value) {
        try {
            beanUtilsBean.copyProperty(bean, name, value);
        } catch (Exception var4) {
            handleReflectionException(var4);
        }

    }

    public static Map describe(Object bean) {
        try {
            return beanUtilsBean.describe(bean);
        } catch (Exception var2) {
            handleReflectionException(var2);
            return null;
        }
    }

    public static String[] getArrayProperty(Object bean, String name) {
        try {
            return beanUtilsBean.getArrayProperty(bean, name);
        } catch (Exception var3) {
            handleReflectionException(var3);
            return null;
        }
    }

    public static ConvertUtilsBean getConvertUtils() {
        return beanUtilsBean.getConvertUtils();
    }

    public static String getIndexedProperty(Object bean, String name, int index) {
        try {
            return beanUtilsBean.getIndexedProperty(bean, name, index);
        } catch (Exception var4) {
            handleReflectionException(var4);
            return null;
        }
    }

    public static String getIndexedProperty(Object bean, String name) {
        try {
            return beanUtilsBean.getIndexedProperty(bean, name);
        } catch (Exception var3) {
            handleReflectionException(var3);
            return null;
        }
    }

    public static String getMappedProperty(Object bean, String name, String key) {
        try {
            return beanUtilsBean.getMappedProperty(bean, name, key);
        } catch (Exception var4) {
            handleReflectionException(var4);
            return null;
        }
    }

    public static String getMappedProperty(Object bean, String name) {
        try {
            return beanUtilsBean.getMappedProperty(bean, name);
        } catch (Exception var3) {
            handleReflectionException(var3);
            return null;
        }
    }

    public static String getNestedProperty(Object bean, String name) {
        try {
            return beanUtilsBean.getNestedProperty(bean, name);
        } catch (Exception var3) {
            handleReflectionException(var3);
            return null;
        }
    }

    public static String getProperty(Object bean, String name) {
        try {
            return beanUtilsBean.getProperty(bean, name);
        } catch (Exception var3) {
            handleReflectionException(var3);
            return null;
        }
    }

    public static PropertyUtilsBean getPropertyUtils() {
        try {
            return beanUtilsBean.getPropertyUtils();
        } catch (Exception var1) {
            handleReflectionException(var1);
            return null;
        }
    }

    public static String getSimpleProperty(Object bean, String name) {
        try {
            return beanUtilsBean.getSimpleProperty(bean, name);
        } catch (Exception var3) {
            handleReflectionException(var3);
            return null;
        }
    }

    public static void populate(Object bean, Map properties) {
        try {
            beanUtilsBean.populate(bean, properties);
        } catch (Exception var3) {
            handleReflectionException(var3);
        }

    }

    public static void setProperty(Object bean, String name, Object value) {
        try {
            beanUtilsBean.setProperty(bean, name, value);
        } catch (Exception var4) {
            handleReflectionException(var4);
        }

    }

    private static void handleReflectionException(Exception e) {
        logger.error("BeanUtils error！", e);
    }

    public static Object convertByActType(String typeName, String value) {
        Object o = null;
        if(typeName.equals("int")) {
            o = Integer.valueOf(Integer.parseInt(value));
        } else if(typeName.equals("short")) {
            o = Short.valueOf(Short.parseShort(value));
        } else if(typeName.equals("long")) {
            o = Long.valueOf(Long.parseLong(value));
        } else if(typeName.equals("float")) {
            o = Float.valueOf(Float.parseFloat(value));
        } else if(typeName.equals("double")) {
            o = Double.valueOf(Double.parseDouble(value));
        } else if(typeName.equals("boolean")) {
            o = Boolean.valueOf(Boolean.parseBoolean(value));
        } else if(typeName.equals("java.lang.String")) {
            o = value;
        } else {
            o = value;
        }

        return o;
    }

    public static boolean fieldHasEmptyValue(Object bean) {
        if(bean == null) {
            return true;
        } else {
            Class cls = bean.getClass();
            Method[] methods = cls.getDeclaredMethods();
            Field[] fields = cls.getDeclaredFields();
            Field[] superclass = fields;
            int sp_methods = fields.length;

            for(int sp_fields = 0; sp_fields < sp_methods; ++sp_fields) {
                Field field = superclass[sp_fields];

                try {
                    String e = parGetName(field.getName());
                    if(checkGetMet(methods, e)) {
                        Method fieldGetMet = cls.getMethod(e, new Class[0]);
                        Object field1 = fieldGetMet.invoke(bean, new Object[0]);
                        if(field1 == null) {
                            return true;
                        }

                        if("".equals(field1)) {
                            return true;
                        }
                    }
                } catch (Exception var15) {
                    ;
                }
            }

            Class var16 = cls.getSuperclass();
            if(null != var16) {
                Method[] var17 = var16.getDeclaredMethods();
                Field[] var18 = var16.getDeclaredFields();
                Field[] var19 = var18;
                int var20 = var18.length;

                for(int var21 = 0; var21 < var20; ++var21) {
                    Field var22 = var19[var21];

                    try {
                        String e1 = parGetName(var22.getName());
                        if(checkGetMet(var17, e1)) {
                            Method fieldGetMet1 = var16.getMethod(e1, new Class[0]);
                            Object fieldVal = fieldGetMet1.invoke(bean, new Object[0]);
                            if(fieldVal == null) {
                                return true;
                            }

                            if("".equals(fieldVal)) {
                                return true;
                            }
                        }
                    } catch (Exception var14) {
                        ;
                    }
                }
            }

            return false;
        }
    }

    public static String parGetName(String fieldName) {
        if(null != fieldName && !"".equals(fieldName)) {
            byte startIndex = 0;
            if(fieldName.charAt(0) == 95) {
                startIndex = 1;
            }

            return "get" + fieldName.substring(startIndex, startIndex + 1).toUpperCase() + fieldName.substring(startIndex + 1);
        } else {
            return null;
        }
    }

    public static boolean checkGetMet(Method[] methods, String fieldGetMet) {
        Method[] var2 = methods;
        int var3 = methods.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Method met = var2[var4];
            if(fieldGetMet.equals(met.getName())) {
                return true;
            }
        }

        return false;
    }

    public static <K, V> Map<K, V> transBean2Map(Object obj) {
        if(obj == null) {
            return null;
        } else {
            HashMap map = new HashMap();

            try {
                BeanInfo e = Introspector.getBeanInfo(obj.getClass());
                PropertyDescriptor[] propertyDescriptors = e.getPropertyDescriptors();
                PropertyDescriptor[] var4 = propertyDescriptors;
                int var5 = propertyDescriptors.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    PropertyDescriptor property = var4[var6];
                    String key = property.getName();
                    if(!key.equals("class")) {
                        Method getter = property.getReadMethod();
                        Object value = getter.invoke(obj, new Object[0]);
                        map.put(key, value);
                    }
                }
            } catch (Exception var11) {
                System.out.println("transBean2Map Error " + var11);
            }

            return map;
        }
    }

    public static void transMap2Bean(Map<String, Object> map, Object obj) {
        if(map != null && obj != null) {
            try {
                populate(obj, map);
            } catch (Exception var3) {
                System.out.println("transMap2Bean2 Error " + var3);
            }

        }
    }

    static {
        beanUtilsBean = new BeanUtilsBean(convertUtilsBean, new PropertyUtilsBean());
        convertUtilsBean.register(new DateConverter(), Date.class);
        convertUtilsBean.register(new LongConverter((Object)null), Long.class);
    }
}
