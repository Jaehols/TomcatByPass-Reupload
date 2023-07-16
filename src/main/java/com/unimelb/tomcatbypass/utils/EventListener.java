package com.unimelb.tomcatbypass.utils;

import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class EventListener implements ServletContextListener {
    private static final Logger log = Logger.getLogger(EventListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info(
                "Server startup EventListener starting up DB connection pool with hikariEnabled=" + Pool.hikariEnabled);
        if (Pool.hikariEnabled) {
            HikariPool.testConnection();
        } else {
            DefaultPool.testConnection();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (Pool.hikariEnabled) {
            log.info("Server shutdown EventListener de-registering db driver.");
            HikariPool.shutdownHook();
        }
    }
}
