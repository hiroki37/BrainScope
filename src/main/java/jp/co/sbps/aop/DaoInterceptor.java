/*
package jp.co.sbps.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class DaoInterceptor {

	@Around("execution(* jp.co.sbps.nikocale.dao.*.*(..))")
	public Object invoke(ProceedingJoinPoint point) throws Throwable {
		long start = System.currentTimeMillis();
		log.trace("AOP - 前処理 {}#{}", getClassName(point), getMethodName(point));
		Object returnObject = point.proceed();
		log.info("AOP - 後処理 {}#{}, time:{}ms", getClassName(point), getMethodName(point),
				System.currentTimeMillis() - start);
		return returnObject;
	}

	private String getClassName(ProceedingJoinPoint point) {
		return point.getTarget().getClass().getName();
	}

	private String getMethodName(ProceedingJoinPoint point) {
		return point.getSignature().getName();
	}
}
*/