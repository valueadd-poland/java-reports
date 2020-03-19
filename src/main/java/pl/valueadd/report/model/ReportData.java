package pl.valueadd.report.model;

import lombok.EqualsAndHashCode;
import lombok.Setter;
import pl.valueadd.report.exception.ReportException;
import pl.valueadd.report.model.schema.Column;
import pl.valueadd.report.model.schema.DynamicColumn;
import pl.valueadd.report.model.schema.ReportSchema;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@EqualsAndHashCode(of={"schema", "data"})
public class ReportData {

    @Getter
    @Setter
    private final String name;

    @JsonIgnore
    @Getter
    private final ReportSchema schema;

    private List<Row> data = new LinkedList<>();

    public ReportData addRow(Row row){
        validate(row);
        data.add(row);
        return this;
    }

    public List<Row> getData() {
        return Collections.unmodifiableList(data);
    }

    public Map<String, DynamicColumn> getDynamicColumnDef(){
        Map<String, DynamicColumn> map = new HashMap<>();
        for (DynamicColumn dynamicColumn : schema.getDynamicColumns()) {
            map.put(dynamicColumn.getId(), dynamicColumn);
        }
        return map;
    }
    public List<String> getDynamicColumnIds(){
        return schema.getDynamicColumns().stream().map(DynamicColumn::getId).collect(Collectors.toList());
    }

    private void validate(Row row){
        for (Row child : row.children) {
            validate(child);
        }
        Map<String, ?> staticValues = row.getStaticValues();
        Map<String, ?> dynamicValues = row.getDynamicValues();

        for (Column column : schema.getColumns()) {
            if(!staticValues.containsKey(column.getKey())){
                throw ReportException.missingColumn(column.getKey());
            }
        }
        for (DynamicColumn column : schema.getDynamicColumns()) {
            if(!dynamicValues.containsKey((column.getId()))){
                throw ReportException.missingColumn(column.getId());
            }
        }
    }

    public ReportData addRows(List<Row> rows) {
        for (Row row : rows) {
            addRow(row);
        }
        return this;
    }

    @Getter
    @EqualsAndHashCode(of={"staticValues", "dynamicValues", "children"})
    public static class Row {
        @JsonProperty("static")
        private final Map<String, Object> staticValues;
        @JsonProperty("dynamic")
        private final Map<String, Object> dynamicValues;

        private List<Row> children = new LinkedList<>();

        public Row(Map<String, Object> staticValues, Map<String, Object> dynamicValues) {
            this.staticValues = new HashMap<>(staticValues);
            this.dynamicValues = new HashMap<>(dynamicValues);
        }
        public Row(Map<String, Object> staticValues) {
            this.staticValues = new HashMap<>(staticValues);
            this.dynamicValues = Map.of();
        }

        public List<Row> getChildren() {
            return Collections.unmodifiableList(children);
        }
        public Row addChild(Row row){
            children.add(row);
            return this;
        }
        public Row addChildren(List<Row> row){
            children.addAll(row);
            return this;
        }
        public String getStaticString(String key){
            return (String)staticValues.get(key);
        }
        public Object getStatic(String key){
            return staticValues.get(key);
        }
        public Integer getStaticInteger(String key){
            if(!staticValues.containsKey(key)){
                return 0;
            }
            return (Integer)staticValues.get(key);
        }
        public String getDynamicString(String key){
            return (String)dynamicValues.get(key);
        }
        public Integer getDynamicInteger(String key){
            return (Integer)dynamicValues.get(key);
        }

    }
}

