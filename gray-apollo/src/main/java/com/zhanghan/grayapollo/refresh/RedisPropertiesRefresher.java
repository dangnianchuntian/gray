package com.zhanghan.grayapollo.refresh;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class RedisPropertiesRefresher implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(RedisPropertiesRefresher.class);

    private ApplicationContext applicationContext;

    @Autowired
    private RefreshScope refreshScope;

    //此处配置的value值为Apollo的namespace名称
    @ApolloConfigChangeListener(value = "grayapollo")
    public void onChange(ConfigChangeEvent changeEvent) {

        boolean propertiesChanged = false;

        for (String changedKey : changeEvent.changedKeys()) {
            logger.info("===============================================================");
            logger.info("changedKey:{} value:{}", changedKey, changeEvent.getChange(changedKey));
            ConfigChange configChange = changeEvent.getChange(changedKey);
            configChange.getOldValue();
            propertiesChanged = true;
            break;
        }
        refreshProperties(changeEvent);
        if (propertiesChanged) {
            refreshProperties(changeEvent);
        }

    }

    private void refreshProperties(ConfigChangeEvent changeEvent) {
        this.applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
        refreshScope.refreshAll();
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}