/******************************************************************************
 * Copyright 2015 sakaiproject.org Licensed under the Educational
 * Community License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/ECL-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.sakaiproject.coursedates.tool;

import java.util.ArrayList;
import java.util.Locale;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.sakaiproject.coursedates.api.SakaiProxy;
import org.sakaiproject.coursedates.api.model.CourseDatesError;
import org.sakaiproject.coursedates.api.model.CourseDatesValidation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * MainController
 *
 * This is the controller used by Spring MVC to handle requests
 *
 */
@Slf4j
@Controller
public class MainController {

	@Inject SakaiProxy sakaiProxy;

	@RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
	public String showIndex(@RequestParam(required=false) String code, Model model) {
		Locale locale = sakaiProxy.getUserLocale();
		String siteId = sakaiProxy.getCurrentSiteId();

		model.addAttribute("userCountry", locale.getCountry());
		model.addAttribute("userLanguage", locale.getLanguage());
		model.addAttribute("userLocale", locale.toString());

		JSONArray assignmentsJson = sakaiProxy.getAssignmentsForContext(siteId);
		model.addAttribute("assignments", assignmentsJson);
		JSONArray assessmentsJson = sakaiProxy.getAssessmentsForContext(siteId);
		model.addAttribute("assessments", assessmentsJson);
		//System.out.println("assessments " + assessmentsJson);
		JSONArray gradebookItemsJson = sakaiProxy.getGradebookItemsForContext(siteId);
		model.addAttribute("gradebookItems", gradebookItemsJson);//TODO CONTROLAR SI EXISTE LA TOOOOOL
		JSONArray signupMeetingsJson = sakaiProxy.getSignupMeetingsForContext(siteId);
		model.addAttribute("signupMeetings", signupMeetingsJson);
		JSONArray resourcesJson = sakaiProxy.getResourcesForContext(siteId);
		//System.out.println("resourcesJson " + resourcesJson);
		model.addAttribute("resources", resourcesJson);
		JSONArray calendarJson = sakaiProxy.getCalendarEventsForContext(siteId);
		model.addAttribute("calendarEvents", calendarJson);
		JSONArray forumsJson = sakaiProxy.getForumsForContext(siteId);
		model.addAttribute("forums", forumsJson);
		//System.out.println("forums " + forumsJson);
		JSONArray announcementsJson = sakaiProxy.getAnnouncementsForContext(siteId);
		model.addAttribute("announcements", announcementsJson);
		//System.out.println("announcementsJson " + announcementsJson);
		// and lessons

		return "index";
	}

	@RequestMapping(value = {"/date-manager/update"}, method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody String dateManagerUpdate(HttpServletRequest req, Model model) {
		String jsonResponse = "";
		try {
			String siteId = req.getRequestURI().split("/")[3];

			String jsonParam = req.getParameter("json");
			if (StringUtils.isBlank(jsonParam)) jsonParam = "[]";
			Object json = new JSONParser().parse(jsonParam);

			if (!(json instanceof JSONObject)) {
				throw new RuntimeException("Parse failed");
			}

			JSONArray assignments = (JSONArray) ((JSONObject) json).get("assignments");
			CourseDatesValidation assignmentValidate = sakaiProxy.validateAssignments(siteId, assignments);
			JSONArray assessments = (JSONArray) ((JSONObject) json).get("assessments");
			CourseDatesValidation assessmentValidate = sakaiProxy.validateAssessments(siteId, assessments);
			JSONArray gradebookItems = (JSONArray) ((JSONObject) json).get("gradebookItems");
			CourseDatesValidation gradebookValidate = sakaiProxy.validateGradebookItems(siteId, gradebookItems);
			JSONArray signupMeetings = (JSONArray) ((JSONObject) json).get("signupMeetings");
			CourseDatesValidation signupValidate = sakaiProxy.validateSignupMeetings(siteId, signupMeetings);
			JSONArray resources = (JSONArray) ((JSONObject) json).get("resources");
			CourseDatesValidation resourcesValidate = sakaiProxy.validateResources(siteId, resources);
			JSONArray calendarEvents = (JSONArray) ((JSONObject) json).get("calendarEvents");
			CourseDatesValidation calendarValidate = sakaiProxy.validateCalendarEvents(siteId, calendarEvents);
			JSONArray forums = (JSONArray) ((JSONObject) json).get("forums");
			CourseDatesValidation forumValidate = sakaiProxy.validateForums(siteId, forums);
			JSONArray announcements = (JSONArray) ((JSONObject) json).get("announcements");
			CourseDatesValidation announcementValidate = sakaiProxy.validateAnnouncements(siteId, announcements);

			if (assignmentValidate.getErrors().isEmpty() && assessmentValidate.getErrors().isEmpty() && gradebookValidate.getErrors().isEmpty() && signupValidate.getErrors().isEmpty() && resourcesValidate.getErrors().isEmpty()
					 && calendarValidate.getErrors().isEmpty() && forumValidate.getErrors().isEmpty() && announcementValidate.getErrors().isEmpty()) {
				sakaiProxy.updateAssignments(assignmentValidate);//if !empty
				sakaiProxy.updateAssessments(assessmentValidate);
				sakaiProxy.updateGradebookItems(gradebookValidate);//if !empty
				sakaiProxy.updateSignupMeetings(signupValidate);//if !empty
				sakaiProxy.updateResources(resourcesValidate);//if !empty
				sakaiProxy.updateCalendarEvents(calendarValidate);//if !empty
				sakaiProxy.updateForums(forumValidate);//if !empty
				sakaiProxy.updateAnnouncements(announcementValidate);//if !empty
				jsonResponse = "{\"status\": \"OK\"}";
			} else {
				JSONArray errorReport = new JSONArray();
				List<CourseDatesError> errors = new ArrayList<>();
				errors.addAll(assignmentValidate.getErrors());
				errors.addAll(assessmentValidate.getErrors());
				errors.addAll(gradebookValidate.getErrors());
				errors.addAll(signupValidate.getErrors());
				errors.addAll(resourcesValidate.getErrors());
				errors.addAll(calendarValidate.getErrors());
				errors.addAll(forumValidate.getErrors());
				errors.addAll(announcementValidate.getErrors());

				for (CourseDatesError error : errors) {
					JSONObject jsonError = new JSONObject();
					jsonError.put("field", error.field);
					jsonError.put("msg", error.msg);
					jsonError.put("toolId", error.toolId);
					jsonError.put("toolTitle", error.toolTitle);
					jsonError.put("idx", error.idx);
					errorReport.add(jsonError);
				}

				jsonResponse = String.format("{\"status\": \"ERROR\", \"errors\": %s}", errorReport.toJSONString());
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return jsonResponse;
	}
}
