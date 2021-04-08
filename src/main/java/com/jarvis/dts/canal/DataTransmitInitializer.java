package com.jarvis.dts.canal;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.jarvis.dts.canal.task.AbstractDataTransmitTask;
import com.jarvis.dts.canal.task.BaseDataTransmitTask;
import com.jarvis.dts.canal.task.DataTransmitExecutorManager;
import com.jarvis.dts.factory.DefaultStrategyFactory;
import com.jarvis.dts.factory.StrategyFactory;

/**
 * 数据传输任务 初始化器
 * 
 * @Author: Hu Xin
 * @Date: 2021/4/8 12:39
 * @Desc: 默认所有的task和factory都是单例
 **/

@Component
public class DataTransmitInitializer implements CommandLineRunner, ApplicationContextAware, DisposableBean {

    private static final DataTransmitTaskConfig DEFAULT_DATA_TRANSMIT_TASK_CONFIG = new DataTransmitTaskConfig();

    private static final StrategyFactory DEFAULT_STRATEGY_FACTORY = new DefaultStrategyFactory();

    private static final String DEFAULT_FACTORY_ALIAS_NAME = DataTransmitTaskConfig.DEFAULT_FACTORY_ALIAS_NAME;

    @Autowired
    private List<AbstractDataTransmitTask> dtsTaskList;

    @Autowired
    private List<StrategyFactory> strategyFactoryList;

    @Autowired
    private CanalServerConfigProperties properties;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) {
        this.init();
    }

    private void init() {
        if (!CollectionUtils.isEmpty(dtsTaskList)) {
            DataTransmitExecutorManager DataTransmitExecutorManager =
                new DataTransmitExecutorManager(dtsTaskList.size());

            dtsTaskList.stream().forEach(t -> {
                DataTransmitTaskConfig config = getConfigOrDefault(t);

                if (t instanceof BaseDataTransmitTask) {
                    // 初始化需要 配置信息和策略工厂
                    BaseDataTransmitTask task = (BaseDataTransmitTask)t;
                    StrategyFactory factory = getFactoryOrDefault(config);
                    task.init(config.getServerConfig(), factory);
                } else {
                    // 初始化需要配置
                    t.init(config.getServerConfig());
                }
                DataTransmitExecutorManager.addTask(t);
            });
        }
    }

    /**
     * 根据"配置信息"找到对应策略工厂，找不到则使用默认的工厂
     * 
     * @param config
     * @return
     */
    private StrategyFactory getFactoryOrDefault(DataTransmitTaskConfig config) {
        if (config.getFactory().equals(DEFAULT_FACTORY_ALIAS_NAME)) {
            return DEFAULT_STRATEGY_FACTORY;
        }
        String factoryName = config.getFactory();

        for (StrategyFactory strategyFactory : strategyFactoryList) {
            String[] beanNamesForType = applicationContext.getBeanNamesForType(strategyFactory.getClass());
            assert beanNamesForType.length == 1 : strategyFactory.getClass() + " must be singleton bean....";

            String beanName = beanNamesForType[0];
            if (factoryName.equalsIgnoreCase(beanName)) {
                return strategyFactory;
            }

        }
        return DEFAULT_STRATEGY_FACTORY;

    }

    /**
     * 根据"任务的BeanName"找到对应的"配置"信息，找不到则使用默认的配置信息
     * 
     * @param dataTransmitTask
     * @return
     */
    private DataTransmitTaskConfig getConfigOrDefault(AbstractDataTransmitTask dataTransmitTask) {
        String[] beanNamesForType = applicationContext.getBeanNamesForType(dataTransmitTask.getClass());
        assert beanNamesForType.length == 1 : dataTransmitTask.getClass() + " must be singleton bean....";
        String beanName = beanNamesForType[0];
        DataTransmitTaskConfig dataTransmitTaskConfig =
            properties.getMappings().stream().filter(p -> p.getTaskName().equalsIgnoreCase(beanName)).findFirst()
                .orElse(DEFAULT_DATA_TRANSMIT_TASK_CONFIG);

        return dataTransmitTaskConfig;

    }

    @Override
    public void destroy() throws Exception {

    }
}
