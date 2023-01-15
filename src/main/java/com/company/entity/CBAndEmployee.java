package com.company.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CBAndEmployee {
    private String employee;
    private List<String> CBList;
}
