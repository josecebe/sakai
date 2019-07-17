/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.portal.charon.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.office365.service.Office365Service;
import org.sakaiproject.portal.api.PortalHandlerException;
import org.sakaiproject.portal.api.PortalRenderContext;
import org.sakaiproject.tool.api.Session;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 
 */
@Slf4j
public class OfficeHandler extends BasePortalHandler {

	public static final String URL_FRAGMENT = "office365";
	private Office365Service office365Service;

	public OfficeHandler() {
		log.debug("OfficeHandler constructor");
		setUrlFragment(OfficeHandler.URL_FRAGMENT);
		office365Service = (Office365Service) ComponentManager.get(Office365Service.class);
	}

	@Override
	public int doPost(String[] parts, HttpServletRequest req, HttpServletResponse res, Session session) throws PortalHandlerException {
		return doGet(parts, req, res, session);
	}

	@Override
	public int doGet(String[] parts, HttpServletRequest req, HttpServletResponse res, Session session) throws PortalHandlerException {
		if ((parts.length == 2) && ((parts[1].equals(OfficeHandler.URL_FRAGMENT))))	{
			try {
				String code = req.getParameter("code");
				String userId = session.getUserId();
				log.debug("OfficeHandler : doGet - request code {}", code);
				log.debug("OfficeHandler : doGet - sakai user {}", userId);
				boolean configured = false;
				if(code != null && userId != null) {
					configured = office365Service.token(userId, code);
				}
				log.debug("OfficeHandler : configured token {} ", configured);
				PortalRenderContext rcontext = portal.includePortal(req, res, session,
						null, /* toolId */null, req.getContextPath() + req.getServletPath(),
						/* prefix */"site", /* doPages */false, /* resetTools */false,
						/* includeSummary */false, /* expandSite */false);
				rcontext.put("officeConfigured", configured);
				portal.sendResponse(rcontext, res, parts[1], "text/html; charset=UTF-8");
			} catch (Exception ex) {
				throw new PortalHandlerException(ex);
			}
			return END;
		} else {
			return NEXT;
		}
	}

}
