package com.sophia.cms.sm.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ToResModel {

    @NotNull
    private Long dataViewId;
    @NotNull
    private Long resPid;
}
