/*
 * Copyright 2009 Carsten Hufe devproof.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.devproof.portal.core.module.print.page;

import org.apache.wicket.Component;

/**
 * @author Carsten Hufe
 */
public interface PrintablePage<PK> {
	/**
	 * Creates and return a wicket component part.
	 * 
	 * @param wicketId wicket component id
	 * @param primaryKey the primary key oof the part which should returned
	 * @return component to print
	 */
	public Component createPrintablePart(String wicketId, PK primaryKey);
}
