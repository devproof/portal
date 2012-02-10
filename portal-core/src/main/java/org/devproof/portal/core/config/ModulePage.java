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
package org.devproof.portal.core.config;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marking a devproof page for mounting etc.
 *
 * @author Carsten Hufe
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface ModulePage {
    /**
     * Mount path with place holders, like the wicket 1.5 default....
     */
    String mountPath();

    /**
     * Register main navigation link for this page
     */
    boolean registerMainNavigationLink() default false;

    /**
     * Register global administration link
     */
    boolean registerGlobalAdminLink() default false;

    /**
     * Should this page the default start page when you call /
     */
    boolean defaultStartPage() default false;

    /**
     * Register this page in the administration links
     */
    boolean registerPageAdminLink() default false;
}
