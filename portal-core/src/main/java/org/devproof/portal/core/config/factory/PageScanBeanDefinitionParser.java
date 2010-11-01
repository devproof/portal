package org.devproof.portal.core.config.factory;

import org.devproof.portal.core.config.DevproofPage;
import org.devproof.portal.core.config.PageConfiguration;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ComponentScanBeanDefinitionParser;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.UUID;

/**
 * @author Carsten Hufe
 */
public class PageScanBeanDefinitionParser extends ComponentScanBeanDefinitionParser {
    private static final String BASE_PACKAGE_ATTRIBUTE = "base-package";

//    @Override
//    public BeanDefinition parse(Element element, ParserContext parserContext) {
//        String[] basePackages = StringUtils.tokenizeToStringArray(element.getAttribute(BASE_PACKAGE_ATTRIBUTE),
//				ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
//
//		// Actually scan for bean definitions and register them.
//		ClassPathBeanDefinitionScanner scanner = configureScanner(parserContext, element);
//		scanner.scan(basePackages);
//        return null;
//    }

    @Override
    protected ClassPathBeanDefinitionScanner createScanner(XmlReaderContext readerContext, boolean useDefaultFilters) {
        // FIXME als echte klasse
        return new ClassPathBeanDefinitionScanner(readerContext.getRegistry(), useDefaultFilters) {
            @Override
            protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
                try {
                    Class<?> clazz = Class.forName(definitionHolder.getBeanDefinition().getBeanClassName());
                    if(clazz.isAnnotationPresent(DevproofPage.class)) {
                        DevproofPage annotation = clazz.getAnnotation(DevproofPage.class);
                        MutablePropertyValues v = new MutablePropertyValues();
                        v.addPropertyValue("mountPath", annotation.mountPath());
                        v.addPropertyValue("pageClass", clazz);
                        RootBeanDefinition rbd = new RootBeanDefinition(PageConfiguration.class, v);
                        BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(rbd, "helloWorld");
                        registerBeanDefinition(beanDefinitionHolder, registry); 
                        System.out.println("Clazz found: " +  clazz);
                    }
                    else {
                        super.registerBeanDefinition(definitionHolder, registry);
                    }

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void registerDefaultFilters() {
//                super.registerDefaultFilters();
                addIncludeFilter(new AnnotationTypeFilter(DevproofPage.class));
            }
        };
    }
}
