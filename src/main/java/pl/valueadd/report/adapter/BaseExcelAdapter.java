package pl.valueadd.report.adapter;

import pl.valueadd.report.interceptor.ValueInterceptor;
import pl.valueadd.report.model.ReportData;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.valueadd.report.model.schema.Column;
import pl.valueadd.report.model.schema.DynamicColumn;
import pl.valueadd.report.model.schema.ReportSchema;
import pl.valueadd.report.util.ArrayUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RequiredArgsConstructor
abstract class BaseExcelAdapter {

    protected final List<ValueInterceptor> interceptors;

    protected String[] headers(ReportSchema schema) {
        final List<DynamicColumn> dynamicColumns = schema.getDynamicColumns();
        final List<Column> staticColumns = schema.getColumns();

        List<String> merged = staticColumns.stream()
            .map(Column::getKey)
            .collect(Collectors.toList());
        merged.addAll(dynamicColumns.stream()
            .map(DynamicColumn::getName)
            .collect(Collectors.toList()));

        return merged.toArray(new String[0]);
    }

    protected String[] parseRow(ReportSchema schema, ReportData.Row row) {
        final List<DynamicColumn> dynamicColumns = schema.getDynamicColumns();
        final List<Column> staticColumns = schema.getColumns();
        final Map<String, Object> dynamicData = row.getDynamicValues();
        final Map<String, Object> staticData = row.getStaticValues();


        final String[] retDynamic = new String[dynamicColumns.size()];
        for (int i = 0; i < dynamicColumns.size(); i++) {
            DynamicColumn column = dynamicColumns.get(i);
            retDynamic[i] = dynamicData.get(column.getId()).toString();
        }

        final String[] retStatic = new String[staticColumns.size()];

        for (int i = 0; i < staticColumns.size(); i++) {
            Column column = staticColumns.get(i);
            Object val = staticData.get(column.getKey());
            retStatic[i] =  intercept(val).toString();
        }

        return ArrayUtil.concat(retStatic, retDynamic);
    }

    private @NotNull Object intercept(Object value){
        for (ValueInterceptor interceptor : interceptors) {
            if(interceptor.supports(value)){
                return intercept(interceptor.intercept(value));
            }
        }
        if(value == null){
            return "";
        }
        return value.toString();
    }


}
