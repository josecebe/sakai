package org.sakaiproject.wicket.test.app.api;

import java.util.Date;
import java.util.Set;

import org.sakaiproject.wicket.test.app.api.model.GradingInfo;

public interface GradingsService {
    public Set<GradingInfo> getGradingsFromSite(String siteId, Date startDate, Date endDate);
}
