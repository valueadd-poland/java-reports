package pl.valueadd.report;


import pl.valueadd.report.adapter.CsvReportAdapter;
import pl.valueadd.report.model.ReportData;
import pl.valueadd.report.model.schema.DynamicColumn;
import pl.valueadd.report.model.schema.ReportSchema;
import org.junit.Test;
import pl.valueadd.report.model.schema.Column;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class CsvReportAdapterTest {
    private final ReportSchema schema = new ReportSchema(List.of(
        new Column("id"),
        new Column("name"),
        new Column("surname")
    ), List.of(
        new DynamicColumn("tire1", "TIRE 1"),
        new DynamicColumn("tir2", "TIRE 2"),
        new DynamicColumn("tire3", "TIRE 3")
    ));

    @Test
    public void convert() {

        //#region When
        CsvReportAdapter writer = new CsvReportAdapter(List.of());
        ReportData reportData = new ReportData("foo", schema)
            .addRow(row("1", "Jakub", "Trzcinski", "10", "12", "16"))
            .addRow(row("2", "Jakub", "Jakub \"", "10", "12", "16"))
            .addRow(row("3", "Jakub", "Jakub '", "10", "12", "16"))
            .addRow(row("null", "Jakub", "Jakub '", "10", "12", "16"));

        String except = "\"id\";\"name\";\"surname\";\"TIRE 1\";\"TIRE 2\";\"TIRE 3\"\n" +
            "\"1\";\"Jakub\";\"Trzcinski\";\"10\";\"12\";\"16\"\n" +
            "\"2\";\"Jakub\";\"Jakub \"\"\";\"10\";\"12\";\"16\"\n" +
            "\"3\";\"Jakub\";\"Jakub '\";\"10\";\"12\";\"16\"\n" +
            "\"null\";\"Jakub\";\"Jakub '\";\"10\";\"12\";\"16\"\n";
        String csv = writer.convert(reportData);
        //#endregion
        //#region Then
        assertEquals(except, csv);
        //#endregion
    }

    @Test
    public void convertWithChild() {
        //#region When
        CsvReportAdapter writer = new CsvReportAdapter(List.of());

        ReportData reportData = new ReportData("foo", schema)
            .addRow(
                row("1", "Jakub", "Trzcinski", "10", "12", "16").addChild(
                    row("", "Valueadd", "Sp. zoo", "10", "12", "18")
                )
            );

        String except = "\"id\";\"name\";\"surname\";\"TIRE 1\";\"TIRE 2\";\"TIRE 3\"\n" +
            "\"1\";\"Jakub\";\"Trzcinski\";\"10\";\"12\";\"16\"\n" +
            "\"\";\"Valueadd\";\"Sp. zoo\";\"10\";\"12\";\"18\"\n";
        String csv = writer.convert(reportData);
        //#endregion
        //#region Then
        assertEquals(except, csv);
        //#endregion
    }

    private ReportData.Row row(String id, String name, String surname, String tire1, String tire2, String tire3) {
        return new ReportData.Row(
            Map.of("id", id, "name", name, "surname", surname),
            Map.of("tire1", tire1, "tir2", tire2, "tire3", tire3)
        );
    }
}
