package com.zerobase.commerce.database.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class EncryptData {
    private final EncryptString encryptString;

    @Pointcut("execution(* com.zerobase.commerce.database.repository.*.save*(*))")
    private void isSave() {
    }

    @Pointcut("execution(* com.zerobase.commerce.database.repository.*.find*(*))")
    private void isFind() {
    }

    @Around("isSave()")
    Object encrypt(ProceedingJoinPoint joinPoint) throws Throwable {
        var target = joinPoint.getArgs()[0];
        var fields = target.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (!field.isAnnotationPresent(Encrypt.class))
                continue;

            field.setAccessible(true);
            try {
                Object data = field.get(target);

                if (data instanceof String) {
                    field.set(target, encryptString.encryptString(String.valueOf(data)));
                }
            } catch (IllegalAccessException e) {
                log.error("{}", e.getCause().toString());
            } finally {
                field.setAccessible(false);
            }
        }

        return joinPoint.proceed(joinPoint.getArgs());
    }

    @Around("isFind()")
    Object decrypt(ProceedingJoinPoint joinPoint) throws Throwable {
        var targetData = joinPoint.proceed(joinPoint.getArgs());
        if (targetData == null)
            return null;

        Object target;
        if (Optional.class.isAssignableFrom(targetData.getClass())) {
            Optional<?> optional = (Optional<?>) targetData;
            if (optional.isEmpty()) {
                return targetData;
            }

            target = optional.get();
        } else {
            target = targetData;
        }

        var fields = target.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Encrypt.class))
                continue;

            field.setAccessible(true);
            try {
                Object data = field.get(target);

                if (data instanceof String) {
                    field.set(target, encryptString.decryptString(String.valueOf(data)));
                }
            } catch (IllegalArgumentException e) {
                log.error("{}", e.getCause().toString());
            } finally {
                field.setAccessible(false);
            }
        }

        return targetData;
    }

}
