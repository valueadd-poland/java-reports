package pl.valueadd.report.interceptor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateInterceptor implements ValueInterceptor {
    @Override
    public boolean supports(Object obj) {
        return obj instanceof LocalDate;
    }

    @Override
    public String intercept(Object obj) {
        return ((LocalDate) obj).format(DateTimeFormatter.ofPattern("dd.MM.Y"));
    }
}
