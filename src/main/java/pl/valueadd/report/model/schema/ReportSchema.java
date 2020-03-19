package pl.valueadd.report.model.schema;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ReportSchema {

    private final List<Column> columns;

    private final List<DynamicColumn> dynamicColumns;

    private final boolean shiftChild;

    public static ReportSchema of(String ...columns) {
        return new ReportSchema(Arrays.stream(columns).map(Column::new).collect(Collectors.toList()));
    }

    public ReportSchema(List<Column> columns) {
        this.columns = columns;
        this.dynamicColumns = List.of();
        this.shiftChild = false;
    }

    public ReportSchema(List<Column> columns, List<DynamicColumn> dynamicColumns) {
        this.columns = columns;
        this.dynamicColumns = dynamicColumns;
        this.shiftChild = false;
    }

    public List<Column> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public List<DynamicColumn> getDynamicColumns() {
        return Collections.unmodifiableList(dynamicColumns);
    }

    public boolean shiftChild() {
        return shiftChild;
    }
}

