package org.devproof.portal.core.module;

import org.devproof.portal.core.module.box.dao.BoxDao;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;

/**
 * @author Carsten Hufe
 */
public class SampleDefinitionParser extends AbstractSimpleBeanDefinitionParser {
    protected Class getBeanClass(Element element)
    {
      return BoxDao.class;
    }


}
