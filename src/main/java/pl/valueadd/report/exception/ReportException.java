package pl.valueadd.report.exception;


import java.util.UUID;

public class ReportException extends RuntimeException {
    public ReportException(String message) {
        super(message);
    }

    public ReportException(Throwable cause) {
        super(cause);
    }

    public static ReportException missingColumn(String name){
        return new ReportException(String.format("Report has missing '%s' column", name));
    }
    public static ReportException missingColumn(UUID id){
        return new ReportException(String.format("Report has missing column with id '%s' ", id));
    }
}
