package app.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class ActionLoggingAspect {

    // Log BEFORE controller methods
    @Before("execution(* app.web..*(..))")
    public void logBefore(JoinPoint jp) {
        String username = getUsername();
        log.info("BEFORE: user [{}] calls: {} with args: {}",
                username,
                jp.getSignature().toShortString(),
                Arrays.toString(jp.getArgs()));
    }

    // Log AFTER controller methods
    @AfterReturning(pointcut = "execution(* app.web..*(..))", returning = "result")
    public void logAfter(JoinPoint jp, Object result) {
        String username = getUsername();
        log.info("AFTER RETURN: user [{}] finished: {} returned: {}",
                username,
                jp.getSignature().toShortString(),
                result);
    }

    // Log EXCEPTIONS from controller methods
    @AfterThrowing(pointcut = "execution(* app.web..*(..))", throwing = "ex")
    public void logException(JoinPoint jp, Throwable ex) {
        String username = getUsername();
        log.error("CONTROLLER ERROR: user [{}], method [{}], message: {}",
                username,
                jp.getSignature().toShortString(),
                ex.getMessage(),
                ex);
    }

    // Measure execution time of service methods
    @Around("execution(* app.service..*(..))")
    public Object measureTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        String username = getUsername();

        try {
            Object returned = pjp.proceed();
            long diff = System.currentTimeMillis() - start;

            log.info("PERFORMANCE: user [{}] executed {} in {} ms",
                    username,
                    pjp.getSignature().toShortString(),
                    diff);

            return returned;
        } catch (Throwable ex) {
            log.error("SERVICE ERROR: user [{}] in {} | message: {}",
                    username,
                    pjp.getSignature().toShortString(),
                    ex.getMessage(),
                    ex);
            throw ex;
        }
    }

    private String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "ANONYMOUS";
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            return userDetails.getUsername();
        }

        return principal.toString();
    }
}
