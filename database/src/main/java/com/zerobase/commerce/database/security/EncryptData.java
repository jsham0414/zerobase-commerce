package com.zerobase.commerce.database.security;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class EncryptData {
    private final EncryptString encryptString;
    private final EntityManager entityManager;

    @Pointcut("execution(* com.zerobase.commerce.database.user.repository.*.save*(*))")
    private void isSave() {
    }

    @Pointcut("execution(* com.zerobase.commerce.database.user.repository.*.find*(*))")
    private void isFind() {
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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

        boolean detached = false;
        var fields = target.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Encrypt.class))
                continue;

            field.setAccessible(true);
            try {
                Object data = field.get(target);

                if (data instanceof String) {
                    if (!detached)
                        entityManager.detach(target);
                    detached = true;

                    field.set(target, encryptString.decryptString(String.valueOf(data)));
                }

            } catch (RuntimeException e) {
                log.error("{}", e.getCause().toString());
            } finally {
                field.setAccessible(false);
            }
        }

        return targetData;
    }

}
