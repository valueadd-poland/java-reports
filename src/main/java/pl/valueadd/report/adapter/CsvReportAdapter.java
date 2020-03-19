package pl.valueadd.report.adapter;

import pl.valueadd.report.interceptor.ValueInterceptor;
import pl.valueadd.report.model.ReportData;
import pl.valueadd.report.model.schema.ReportSchema;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;

import java.util.List;


public class CsvReportAdapter extends BaseExcelAdapter {

    public CsvReportAdapter(List<ValueInterceptor> interceptors) {
        super(interceptors);
    }

    public String convert(final ReportData report) {
        final ReportSchema schema = report.getSchema();
        final CSVParser parser = new CSVParserBuilder()
                .withSeparator(';')
                .withIgnoreQuotations(true)
                .build();

        StringBuilder sb = new StringBuilder();
        sb.append(parser.parseToLine(headers(schema), true)).append("\n");
        for (ReportData.Row row : report.getData()) {
            sb.append(parser.parseToLine(parseRow(schema, row), true)).append("\n");
            for (ReportData.Row child : row.getChildren()) {
                sb.append(schema.shiftChild() ? "  " : "").append(parser.parseToLine(parseRow(schema, child), true)).append("\n");
            }

        }

        return sb.toString();
    }

}
