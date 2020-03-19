package pl.valueadd.report.interceptor;

import org.jetbrains.annotations.Nullable;

public interface ValueInterceptor {
    boolean supports(@Nullable Object obj);
    Object intercept(Object obj);
}
