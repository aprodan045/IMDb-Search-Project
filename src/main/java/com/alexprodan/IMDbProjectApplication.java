package com.alexprodan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

//(exclude = {DataSourceAutoConfiguration.class})
//@EnableJpaRepositories(basePackages = "com.alexprodan.Persistance", entityManagerFactoryRef = "sessionFactory")
@SpringBootApplication
public class IMDbProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(IMDbProjectApplication.class, args);
	}

//	@Bean(name="entityManagerFactory")
//	public LocalSessionFactoryBean sessionFactory() {
//		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
//
//		return sessionFactory;
//	}
}
