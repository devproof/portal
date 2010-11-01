package org.devproof.portal.core.module;

import org.devproof.portal.core.module.box.dao.BoxDao;
import org.devproof.portal.core.module.box.entity.BoxEntity;
import org.devproof.portal.core.module.box.service.BoxServiceImpl;
import org.devproof.portal.core.module.common.dao.FinderDispatcherGenericDaoImpl;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * @author Carsten Hufe
 */
// FIXME remove file
public class SamplePostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinitionRegistry registry = ((BeanDefinitionRegistry)beanFactory);
        BeanDefinition definition = new RootBeanDefinition(BoxServiceImpl.class);
        registry.registerBeanDefinition("boxService", definition);
        
// http://www.carlobonamico.com/blog/2008/01/22/how-to-dynamicallyprogrammatically-define-spring-beans/
//            <bean id="boxDao" parent="baseGenericDao">
//        <property name="daoInterface"
//                  value="org.devproof.portal.core.module.box.dao.BoxDao"/>
//        <property name="entityClass"
//                  value="org.devproof.portal.core.module.box.entity.BoxEntity"/>
//    </bean>

        /*
        FinderDispatcherGenericDaoImpl impl = new FinderDispatcherGenericDaoImpl<BoxEntity, Integer>();
        impl.setDaoInterface(BoxDao.class);
        impl.setEntityClass(BoxEntity.class);
        impl.setSessionFactory((SessionFactory) beanFactory.getBean("sessionFactory"));
        try {
            beanFactory.registerSingleton("boxDao", impl.getObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(bean); */              
    }
}
