package com.fast.admin.sm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DataSourceCacheModel {

    private long version;
    private DriverManagerDataSource driverManagerDataSource;
}
