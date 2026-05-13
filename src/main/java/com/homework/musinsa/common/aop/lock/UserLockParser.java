package com.homework.musinsa.common.aop.lock;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class UserLockParser {
    private static final ExpressionParser parser = new SpelExpressionParser();
    private static final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    public static String getUserIdFromArgs(JoinPoint joinPoint, String keyExpression) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(
                joinPoint.getTarget(),
                method,
                args,
                nameDiscoverer
        );

        Object value = parser.parseExpression(keyExpression).getValue(context);

        return value != null ? value.toString() : "";
    }
}
