package com.school.report.dto;

import java.util.List;

public class ChartDataDTO {
    private List<String> labels;
    private List<Number> data;

    public ChartDataDTO(List<String> labels, List<Number> data) {
        this.labels = labels;
        this.data = data;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<Number> getData() {
        return data;
    }

    public void setData(List<Number> data) {
        this.data = data;
    }
}
