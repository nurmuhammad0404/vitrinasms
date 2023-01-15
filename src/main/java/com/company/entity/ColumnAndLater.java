package com.company.entity;

import lombok.Data;
import org.apache.commons.math3.optim.nonlinear.scalar.LineSearch;

import java.util.List;

@Data
public class ColumnAndLater {
    private List<String> columnList;
    private List<String> laterList;
}
