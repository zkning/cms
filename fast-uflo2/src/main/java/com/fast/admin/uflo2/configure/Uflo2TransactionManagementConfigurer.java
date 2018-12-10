package com.fast.admin.uflo2.configure;

import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.persistence.EntityManagerFactory;

@Configuration
@ImportResource(locations = {"classpath:uflo-console-context.xml"})
@EnableTransactionManagement
public class Uflo2TransactionManagementConfigurer implements TransactionManagementConfigurer {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public SessionFactory sessionFactory(HibernateEntityManagerFactory hemf) {
        return hemf.getSessionFactory();
    }
}

