package org.sakaiproject.wicket.test.app.api.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GradingInfo {
    private String studentName;
    private String studentId;
    private String group;
    private Date dateInvited;
    private String courseGrade;
    private String mark;
    private String percentage;
    private Date dateCompletion;
    private int attempts;
}
