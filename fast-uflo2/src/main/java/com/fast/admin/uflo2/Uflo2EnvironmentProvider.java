package com.fast.admin.uflo2;

import com.bstek.uflo.env.EnvironmentProvider;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class Uflo2EnvironmentProvider implements EnvironmentProvider {

    @Autowired
    SessionFactory sessionFactory;
    @Autowired
    PlatformTransactionManager platformTransactionManager;

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public PlatformTransactionManager getPlatformTransactionManager() {
        return platformTransactionManager;
    }

    @Override
    public String getLoginUser() {
        return "anonymous";
    }

    @Override
    public String getCategoryId() {
        return null;
    }
}
