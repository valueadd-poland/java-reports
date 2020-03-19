package pl.valueadd.report.adapter;

import pl.valueadd.report.interceptor.ValueInterceptor;
import pl.valueadd.report.model.ReportData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.valueadd.report.exception.ReportException;
import pl.valueadd.report.model.schema.ReportSchema;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


public class XlsxReportAdapter extends BaseExcelAdapter {

    public interface WorkbookFactory {
        Workbook getWorkbook();
    }

    private WorkbookFactory factory;

    public XlsxReportAdapter(List<ValueInterceptor> interceptors, WorkbookFactory factory) {
        super(interceptors);
        this.factory = factory;
    }

    public XlsxReportAdapter(List<ValueInterceptor> interceptors) {
        super(interceptors);
        factory = XSSFWorkbook::new;
    }

    public void convert(final ReportData report, OutputStream outputStream) {
        final ReportSchema schema = report.getSchema();
        try (final Workbook workbook = factory.getWorkbook()) {
            int rowNum = 0;

            final Sheet sheet = workbook.createSheet(report.getName());
            final Row headerRow = sheet.createRow(rowNum++);
            String[] headers = headers(schema);

            for (int i = 0; i < headers(schema).length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            for (ReportData.Row data : report.getData()) {
                final Row row = sheet.createRow(rowNum++);
                final String[] rowData = parseRow(schema, data);

                for (int col = 0; col < rowData.length; col++) {
                    row.createCell(col).setCellValue(rowData[col]);
                }
                for (ReportData.Row child : data.getChildren()) {
                    final Row childRow = sheet.createRow(rowNum++);
                    final String[] childRowData = parseRow(schema, child);
                    for (int col = 0; col < childRowData.length; col++) {
                        childRow.createCell(col).setCellValue(childRowData[col]);
                    }
                }

            }

            workbook.write(outputStream);
        } catch (IOException e) {
            throw new ReportException(e);
        }
    }


}
