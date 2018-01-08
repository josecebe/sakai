package org.sakaiproject.wicket.test.app.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.sakaiproject.wicket.test.app.api.GradingsService;
import org.sakaiproject.wicket.test.app.api.model.GradingInfo;
import org.sakaiproject.db.api.SqlReader;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.memory.api.Cache;
import org.sakaiproject.memory.api.MemoryService;
import org.sakaiproject.site.api.SiteService;

@Slf4j
@Getter @Setter
public class GradingsServiceImpl implements GradingsService {
    private SqlService sqlService;
    private SiteService siteService;
    private MemoryService memoryService;
    private Cache cache;

    private final static String CACHE_NAME = "org.sakaiproject.wicket.test.app.cache";

    private final static String ASSESSMENT_GRADING_TABLE = "SAM_ASSESSMENTGRADING_T";
    private final static String SAKAI_USER = "SAKAI_USER";
    private final static String SELECT_GRADINGS = String.format("SELECT u.FIRST_NAME, u.LAST_NAME, u.EMAIL, ag.FINALSCORE, ag.ATTEMPTDATE FROM %s ag, %s u WHERE ag.AGENTID = u.USER_ID GROUP BY u.USER_ID", ASSESSMENT_GRADING_TABLE, SAKAI_USER);

    public void init() {
        log.debug("{} init", this);
        cache = memoryService.getCache(CACHE_NAME);
    }

    @Override
    public Set<GradingInfo> getGradingsFromSite(String siteId, Date startDate, Date endDate) {
        Set<GradingInfo> gradings = new HashSet<GradingInfo>();

        try {
            final Connection connection = sqlService.borrowConnection();
            sqlService.dbRead(connection, SELECT_GRADINGS, new Object[] {}, new SqlReader() {
                public Object readSqlResultRecord(ResultSet result) {
                    try {
                        GradingInfo gradingInfo = new GradingInfo();

                        String firstName = result.getString(1);
                        String lastName = result.getString(2);
                        String email = (result.getString(3) != null) ? result.getString(3) : "";
                        String finalScore = result.getString(4);
                        Date attemptDate = result.getDate(5);

                        gradingInfo.setStudentName(firstName + " " + lastName);
                        gradingInfo.setStudentId(email);
                        gradingInfo.setMark(finalScore);
                        gradingInfo.setDateCompletion(attemptDate);
                        gradings.add(gradingInfo);
                    } catch (SQLException ex) {
                        log.error("errorsiño");
                        ex.printStackTrace();
                    }
                    return null;
                }
            });

        } catch (SQLException ex) {
            log.error("errorsito sql");
            ex.printStackTrace();
        }

        return gradings;
    }
}
