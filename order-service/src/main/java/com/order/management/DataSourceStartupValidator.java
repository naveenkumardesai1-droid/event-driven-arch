
package com.order.management;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSourceStartupValidator implements ApplicationRunner {
	private final DataSource dataSource;

	public DataSourceStartupValidator(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		try (Connection ignored = dataSource.getConnection()) {
			// successful connection -> nothing to do
		} catch (Exception ex) {
			// fail application startup on DB connection error
			throw new IllegalStateException("Failed to obtain database connection on startup", ex);
		}
	}
}