package com.johnny.test.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.johnny.test.annotation.DataSource;
import com.johnny.test.component.DynamicDataSourceHolder;

public class DataSourceAspect {
    
    public void before(JoinPoint point) {
        Object target = point.getTarget();
        String method = point.getSignature().getName();
        
        Class<?> clazz = target.getClass();
        
        Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
        try {
            Method m = clazz.getMethod(method, parameterTypes);
            if (m != null && m.isAnnotationPresent(DataSource.class)) {
                DataSource data = m.getAnnotation(DataSource.class);
                DynamicDataSourceHolder.putDataSource(data.value());
            }
            
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
