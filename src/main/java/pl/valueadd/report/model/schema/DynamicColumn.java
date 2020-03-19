package pl.valueadd.report.model.schema;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class DynamicColumn {
    private final String id;
    private final String name;

    public DynamicColumn(String id, String name) {
        this.id = id;
        this.name = name;
    }

}
