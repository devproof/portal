package org.devproof.portal.core.config.factory;

import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.w3c.dom.Element;

public class DevproofNamespaceHandler extends NamespaceHandlerSupport
{
  public DevproofNamespaceHandler()
  {
    registerBeanDefinitionParser("component", new FoobarBeanDefinitionParser());
  }

  public void init()
  {
  }

  private static class FoobarBeanDefinitionParser
    extends AbstractSimpleBeanDefinitionParser
  {
    protected Class getBeanClass(Element element)
    {
      return null;
    }
  }
}