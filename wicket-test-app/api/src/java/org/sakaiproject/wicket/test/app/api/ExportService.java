package org.sakaiproject.wicket.test.app.api;

import java.util.List;

import org.sakaiproject.wicket.test.app.api.model.GradingInfo;

public interface ExportService {
    public String exportGradings(List<GradingInfo> gradings);
}
