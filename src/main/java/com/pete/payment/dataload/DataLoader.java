package com.pete.payment.dataload;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class DataLoader {

	public static void main(String[] args) {
		new SpringApplicationBuilder(DataLoader.class).profiles("dataload").web(WebApplicationType.NONE).application().run(args);
	}

}
