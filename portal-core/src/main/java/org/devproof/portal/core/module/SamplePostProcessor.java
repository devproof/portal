package org.devproof.portal.core.module;

import org.devproof.portal.core.module.box.dao.BoxDao;
import org.devproof.portal.core.module.box.entity.BoxEntity;
import org.devproof.portal.core.module.common.dao.FinderDispatcherGenericDaoImpl;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @author Carsten Hufe
 */
public class SamplePostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Object bean = beanFactory.getBean("boxService");
//            <bean id="boxDao" parent="baseGenericDao">
//        <property name="daoInterface"
//                  value="org.devproof.portal.core.module.box.dao.BoxDao"/>
//        <property name="entityClass"
//                  value="org.devproof.portal.core.module.box.entity.BoxEntity"/>
//    </bean>
        FinderDispatcherGenericDaoImpl impl = new FinderDispatcherGenericDaoImpl<BoxEntity, Integer>();
        impl.setDaoInterface(BoxDao.class);
        impl.setEntityClass(BoxEntity.class);
        impl.setSessionFactory((SessionFactory) beanFactory.getBean("sessionFactory"));
        try {
            beanFactory.registerSingleton("boxDao", impl.getObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(bean);               
    }
}
