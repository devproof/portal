package org.devproof.portal.core.module.historization.service;

import org.devproof.portal.core.module.historization.interceptor.Action;

import java.util.Date;

/**
 * @author Carsten Hufe
 */
// TODO comments
public interface Historized {
    Action getAction();
    Date getActionAt();
    Date getModifiedAt();
    String getModifiedBy();
    Integer getVersionNumber();
    Integer getRestoredFromVersion();
}
