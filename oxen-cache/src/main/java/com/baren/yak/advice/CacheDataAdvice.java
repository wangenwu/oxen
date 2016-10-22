package com.baren.yak.advice;

import com.baren.yak.ICacheClient;
import com.baren.yak.ICacheManager;
import com.baren.yak.annoation.CacheData;
import com.baren.yak.except.AnnotationAsignException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by user on 16/7/30.
 */
@Aspect
@Component
public class CacheDataAdvice {


    @Autowired
    private ICacheManager cacheManager;


    public CacheDataAdvice() {
    }


    @Pointcut("@annotation(com.baren.yak.annoation.CacheData)")
    public void cacheDataPointcut() {
        /* pointcut definition */
    }

    @Around("cacheDataPointcut()")
    public Object cacheDataHand(final ProceedingJoinPoint pjp) throws Throwable {
        /**
         * 需要注意,如果没有启用cglib进行动态代理,就会得不到注解,因此,需要在配置文件中打上@EnableAspectJAutoProxy(proxyTargetClass=true)标注.
         * 强制启用cglib动态代理.
         *
         * TODO 需要解决使用jdk proxy也能获取到注解.
         */
        final Signature sig = pjp.getSignature();
        if (!(sig instanceof MethodSignature)) {
            throw new AnnotationAsignException("This annotation is only valid on a method.");
        }

        final MethodSignature msig = (MethodSignature) sig;
        Method m = msig.getMethod();
        Annotation baseAnno = m.getAnnotation(CacheData.class);
        CacheData cacheDataAnno = (CacheData) baseAnno;

        StandardEvaluationContext sec = this.genContext(msig.getParameterNames(), pjp.getArgs());
        String cacheKey = this.genSpelValue(cacheDataAnno.key(), sec);
        boolean condition = this.condition(cacheDataAnno.condition(), sec);

        if (condition) {  // condition为真才走缓存取数据
            String cacheName = cacheDataAnno.value();
            if (StringUtils.isEmpty(cacheName)) {
                cacheName = cacheDataAnno.cacheName();
            }
            ICacheClient cacheClient = cacheManager.getCache(cacheName);

            Object cacheValue = cacheClient.get(cacheKey, m.getReturnType());
            if (cacheValue == null) {
                cacheValue = pjp.proceed();
                sec.setVariable("result", cacheValue);
                boolean unless = this.unless(cacheDataAnno.unless(), sec);
                if (!unless) {
                    cacheClient.add(cacheKey, cacheDataAnno.expire(), cacheValue);
                }
            }
            return cacheValue;

        } else {
            return pjp.proceed();
        }

    }


    private boolean condition(String condition, StandardEvaluationContext sec) {

        if (!StringUtils.isEmpty(condition)) {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(condition);
            boolean v = exp.getValue(sec, Boolean.class);
            return v;
        }
        return true;
    }

    private boolean unless(String unless, StandardEvaluationContext sec) {

        if (unless != null && !unless.isEmpty()) {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(unless);
            boolean v = exp.getValue(sec, Boolean.class);
            return v;
        }
        return false;
    }

    private StandardEvaluationContext genContext(String[] paraName, Object[] args) {
        StandardEvaluationContext sec = new StandardEvaluationContext();
        for (int i = 0; i < paraName.length; i++) {
            sec.setVariable(paraName[i], args[i]);
        }
        return sec;
    }

    private String genSpelValue(String spel, StandardEvaluationContext sec) {
        if (spel != null && !spel.isEmpty()) {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(spel);
            String v = exp.getValue(sec, String.class);
            return v;
        } else {
            return "";
        }
    }

}
