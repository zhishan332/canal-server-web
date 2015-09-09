package com.jd.ql.canal.server;

import com.alibaba.otter.canal.deployer.CanalController;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * server端的启动类
 *
 * @author wangqing
 * @since 15-8-24 下午7:01
 * @see com.alibaba.otter.canal.deployer.CanalLauncher
 */
public class WebCanalLauncher implements ServletContextListener {

    private static final String CLASSPATH_URL_PREFIX = "classpath:";
    private static final Logger logger = LoggerFactory.getLogger(WebCanalLauncher.class);
    private  CanalController controller;


    public  void startup() {
        try {
            String conf = System.getProperty("canal.conf", "classpath:canal.properties");
            Properties properties = new Properties();
            if (conf.startsWith(CLASSPATH_URL_PREFIX)) {
                conf = StringUtils.substringAfter(conf, CLASSPATH_URL_PREFIX);
                properties.load(WebCanalLauncher.class.getClassLoader().getResourceAsStream(conf));
            } else {
                properties.load(new FileInputStream(conf));
            }

            logger.info("## start the canal server.");
            controller = new CanalController(properties);
            controller.start();
            logger.info("## the canal server is running now ......");
        } catch (Throwable e) {
            logger.error("## Something goes wrong when starting up the canal Server:\n{}",
                    ExceptionUtils.getFullStackTrace(e));
        }
    }

    public void shutdown() {
        try {
            logger.info("## stop the canal server");
            controller.stop();
        } catch (Throwable e) {
            logger.warn("##something goes wrong when stopping canal Server:\n{}", ExceptionUtils.getFullStackTrace(e));
            System.exit(0);
        } finally {
            logger.info("## canal server is down.");
        }
    }

    /**
     * Notification that the web application initialization
     * process is starting.
     * All ServletContextListeners are notified of context
     * initialisation before any filter or servlet in the web
     * application is initialized.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.setProperty("canal.conf", "classpath:canal.properties");
        startup();
    }

    /**
     * Notification that the servlet context is about to be shut down. All servlets
     * have been destroy()ed before any ServletContextListeners are notified of context
     * destruction.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        shutdown();
    }
}
