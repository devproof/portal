/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.test;

import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.Component;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.core.app.PortalApplication;
import org.devproof.portal.core.module.user.page.LoginPage;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Properties;

/**
 * Class contains stuff for testing convience
 * 
 * @author Carsten Hufe
 */
public class PortalTestUtil {
    public static WicketTester createWicketTester(ServletContext servletContext) {
		PortalApplication app = new TestPortalApplication(servletContext);

		// Workaround for bug in WicketTester, mounted url does not work
		// with stateless form
		app.unmount("/login");
		return new WicketTester(app);
	}


	public static void destroy(WicketTester tester) {
		tester.destroy();
	}

	/**
	 * Login the default admin user
	 */
	public static void loginDefaultAdminUser(WicketTester tester) {
		tester.startPage(LoginPage.class);
		FormTester form = tester.newFormTester("loginForm");
		form.setValue("username", "admin");
		form.setValue("password", "admin");
		form.submit();
	}

	public static String[] getMessage(String key, Component component) {
		return new String[] { new StringResourceModel(key, component, null).getString() };
	}

	public static String[] getMessage(String key, Class<?> clazz) {
		Properties prop = new Properties();
		try {
			prop.load(clazz.getResourceAsStream(clazz.getSimpleName() + ".properties"));
		} catch (IOException e) {
			// do nothing
		}
		return new String[] { prop.getProperty(key) };
	}

	public static void callOnBeginRequest() {
		try {
			Method method = RequestCycle.class.getDeclaredMethod("onBeginRequest", (Class<?>[]) null);
			method.setAccessible(true);
			method.invoke(RequestCycle.get(), (Object[]) null);
		} catch (Exception e) {
			throw new UnhandledException(e);
		}

	}

	private static class TestPortalApplication extends PortalApplication {
		private final ServletContext sandbox;

		public TestPortalApplication(ServletContext sandbox) {
			this.sandbox = sandbox;
		}

		@Override
		public ServletContext getServletContext() {
			return sandbox;
		}

		@Override
		public org.apache.wicket.Session newSession(Request request, Response response) {
			org.apache.wicket.Session session = super.newSession(request, response);
			session.setLocale(Locale.ENGLISH);
			return session;
		}
	}
}
