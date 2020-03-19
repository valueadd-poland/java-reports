package pl.valueadd.report;


import pl.valueadd.report.adapter.XlsxReportAdapter;
import pl.valueadd.report.model.ReportData;
import pl.valueadd.report.model.schema.DynamicColumn;
import pl.valueadd.report.model.schema.ReportSchema;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import pl.valueadd.report.model.schema.Column;

import java.io.*;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class XlsxReportAdapterTest {

    private final ReportSchema schema = new ReportSchema(List.of(
        new Column("id"),
        new Column("name"),
        new Column("surname")
    ), List.of(
        new DynamicColumn("tire1", "TIRE 1"),
        new DynamicColumn("tir2", "TIRE 2"),
        new DynamicColumn("tire3", "TIRE 3")
    ));


    @Mock Workbook workbook;

    @Mock
    Sheet sheet;


    List<Row> rows = List.of(
        mock(Row.class),
        mock(Row.class),
        mock(Row.class),
        mock(Row.class),
        mock(Row.class),
        mock(Row.class)
    );
    List<List<Cell>> cells = List.of(
        List.of(mock(Cell.class), mock(Cell.class),mock(Cell.class),mock(Cell.class),mock(Cell.class),mock(Cell.class),mock(Cell.class)),
        List.of(mock(Cell.class), mock(Cell.class),mock(Cell.class),mock(Cell.class),mock(Cell.class),mock(Cell.class),mock(Cell.class)),
        List.of(mock(Cell.class), mock(Cell.class),mock(Cell.class),mock(Cell.class),mock(Cell.class),mock(Cell.class),mock(Cell.class)),
        List.of(mock(Cell.class), mock(Cell.class),mock(Cell.class),mock(Cell.class),mock(Cell.class),mock(Cell.class),mock(Cell.class)),
        List.of(mock(Cell.class), mock(Cell.class),mock(Cell.class),mock(Cell.class),mock(Cell.class),mock(Cell.class),mock(Cell.class)),
        List.of(mock(Cell.class), mock(Cell.class),mock(Cell.class),mock(Cell.class),mock(Cell.class),mock(Cell.class),mock(Cell.class))
    );
    @Before
    public void before(){
        // #region Given
        when(sheet.createRow(anyInt())).thenAnswer((Answer<Row>) invocationOnMock -> {
            int rowIndex = (int)invocationOnMock.getArguments()[0];
            return rows.get(rowIndex);
        });
        for (int i = 0; i < rows.size(); i++) {
            int finalI = i;
            when(rows.get(i).createCell(anyInt())).thenAnswer((Answer<Cell>) invocationOnMock -> {
                int cellIndex = (int) invocationOnMock.getArguments()[0];
                return cells.get(finalI).get(cellIndex);
            });
        }
        when(workbook.createSheet(eq("foo"))).thenReturn(sheet);
        //#endregion
    }

    @SneakyThrows
    @Test
    public void convert() {
        //#region When
        XlsxReportAdapter writer = new XlsxReportAdapter(List.of(), ()-> workbook);

        ReportData reportData = new ReportData("foo", schema)
            .addRow(row("1", "Jakub", "Trzcinski", "10", "12", "16"))
            .addRow(row("2", "Jakub", "Jakub \"", "10", "12", "16"))
            .addRow(row("3", "Jakub", "Jakub '", "10", "12", "16"))
            .addRow(row("null", "Jakub", "Jakub '", "10", "12", "16"));

        writer.convert(reportData, new ByteArrayOutputStream());
        //#endregion
        //#region Then
        verify(cells.get(0).get(0), times(1)).setCellValue("id");
        verify(cells.get(0).get(1), times(1)).setCellValue("name");
        verify(cells.get(0).get(2), times(1)).setCellValue("surname");
        verify(cells.get(0).get(3), times(1)).setCellValue("TIRE 1");
        verify(cells.get(0).get(4), times(1)).setCellValue("TIRE 2");
        verify(cells.get(0).get(5), times(1)).setCellValue("TIRE 3");
        verify(cells.get(0).get(6), times(0)).setCellValue(anyString());

        verify(cells.get(1).get(0), times(1)).setCellValue("1");
        verify(cells.get(1).get(1), times(1)).setCellValue("Jakub");
        verify(cells.get(1).get(2), times(1)).setCellValue("Trzcinski");
        verify(cells.get(1).get(3), times(1)).setCellValue("10");
        verify(cells.get(1).get(4), times(1)).setCellValue("12");
        verify(cells.get(1).get(5), times(1)).setCellValue("16");
        verify(cells.get(1).get(6), times(0)).setCellValue(anyString());

        verify(cells.get(2).get(0), times(1)).setCellValue("2");
        verify(cells.get(2).get(1), times(1)).setCellValue("Jakub");
        verify(cells.get(2).get(2), times(1)).setCellValue("Jakub \"");
        verify(cells.get(2).get(3), times(1)).setCellValue("10");
        verify(cells.get(2).get(4), times(1)).setCellValue("12");
        verify(cells.get(2).get(5), times(1)).setCellValue("16");
        verify(cells.get(2).get(6), times(0)).setCellValue(anyString());

        verify(cells.get(3).get(0), times(1)).setCellValue("3");
        verify(cells.get(3).get(1), times(1)).setCellValue("Jakub");
        verify(cells.get(3).get(2), times(1)).setCellValue("Jakub '");
        verify(cells.get(3).get(3), times(1)).setCellValue("10");
        verify(cells.get(3).get(4), times(1)).setCellValue("12");
        verify(cells.get(3).get(5), times(1)).setCellValue("16");
        verify(cells.get(3).get(6), times(0)).setCellValue(anyString());

        verify(cells.get(4).get(0), times(1)).setCellValue("null");
        verify(cells.get(4).get(1), times(1)).setCellValue("Jakub");
        verify(cells.get(4).get(2), times(1)).setCellValue("Jakub '");
        verify(cells.get(4).get(3), times(1)).setCellValue("10");
        verify(cells.get(4).get(4), times(1)).setCellValue("12");
        verify(cells.get(4).get(5), times(1)).setCellValue("16");
        verify(cells.get(4).get(6), times(0)).setCellValue(anyString());

        verify(cells.get(5).get(0), times(0)).setCellValue(anyString());
        verify(cells.get(5).get(1), times(0)).setCellValue(anyString());
        verify(cells.get(5).get(2), times(0)).setCellValue(anyString());
        verify(cells.get(5).get(3), times(0)).setCellValue(anyString());
        verify(cells.get(5).get(4), times(0)).setCellValue(anyString());
        verify(cells.get(5).get(5), times(0)).setCellValue(anyString());
        verify(cells.get(5).get(6), times(0)).setCellValue(anyString());
        //#endregion
    }

    @SneakyThrows
    @Test
    public void convertWithChild() {
        //#region When
        XlsxReportAdapter writer = new XlsxReportAdapter(List.of(), ()-> workbook);
        ReportData reportData = new ReportData("foo", schema)
            .addRow(
                row("1", "Jakub", "Trzcinski", "10", "12", "16").addChild(
                    row("", "Valueadd", "Sp. zoo", "10", "12", "18")
                )
            );

        writer.convert(reportData, new ByteArrayOutputStream());

        //#endregion
        //#region Then
        verify(cells.get(0).get(0), times(1)).setCellValue("id");
        verify(cells.get(0).get(1), times(1)).setCellValue("name");
        verify(cells.get(0).get(2), times(1)).setCellValue("surname");
        verify(cells.get(0).get(3), times(1)).setCellValue("TIRE 1");
        verify(cells.get(0).get(4), times(1)).setCellValue("TIRE 2");
        verify(cells.get(0).get(5), times(1)).setCellValue("TIRE 3");
        verify(cells.get(0).get(6), times(0)).setCellValue(anyString());


        verify(cells.get(1).get(0), times(1)).setCellValue("1");
        verify(cells.get(1).get(1), times(1)).setCellValue("Jakub");
        verify(cells.get(1).get(2), times(1)).setCellValue("Trzcinski");
        verify(cells.get(1).get(3), times(1)).setCellValue("10");
        verify(cells.get(1).get(4), times(1)).setCellValue("12");
        verify(cells.get(1).get(5), times(1)).setCellValue("16");
        verify(cells.get(1).get(6), times(0)).setCellValue(anyString());


        verify(cells.get(2).get(0), times(1)).setCellValue("");
        verify(cells.get(2).get(1), times(1)).setCellValue("Valueadd");
        verify(cells.get(2).get(2), times(1)).setCellValue("Sp. zoo");
        verify(cells.get(2).get(3), times(1)).setCellValue("10");
        verify(cells.get(2).get(4), times(1)).setCellValue("12");
        verify(cells.get(2).get(5), times(1)).setCellValue("18");
        verify(cells.get(2).get(6), times(0)).setCellValue(anyString());

        verify(cells.get(3).get(0), times(0)).setCellValue(anyString());
        verify(cells.get(3).get(1), times(0)).setCellValue(anyString());
        verify(cells.get(3).get(2), times(0)).setCellValue(anyString());
        verify(cells.get(3).get(3), times(0)).setCellValue(anyString());
        verify(cells.get(3).get(4), times(0)).setCellValue(anyString());
        verify(cells.get(3).get(5), times(0)).setCellValue(anyString());
        verify(cells.get(3).get(6), times(0)).setCellValue(anyString());
        //#endregion
    }


    private ReportData.Row row(String id, String name, String surname, String tire1, String tire2, String tire3) {
        return new ReportData.Row(
            Map.of("id", id, "name", name, "surname", surname),
            Map.of("tire1", tire1, "tir2", tire2, "tire3", tire3)
        );
    }
}
