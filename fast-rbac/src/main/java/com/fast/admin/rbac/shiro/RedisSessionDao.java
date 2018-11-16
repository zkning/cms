package com.fast.admin.rbac.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;

import java.io.Serializable;

@Slf4j
public class RedisSessionDao extends CachingSessionDAO {
    private long timeout = 3600;

    public RedisSessionDao(long timeout) {
        this.timeout = timeout;
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        session.setTimeout(timeout);
        this.assignSessionId(session, sessionId);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        return null;
    }

    @Override
    protected void doUpdate(Session session) {
    }

    @Override
    protected void doDelete(Session session) {
    }
}
