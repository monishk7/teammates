package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.UserType;

@SuppressWarnings("serial")
public class StudentEvalEditHandlerServlet extends EvalSubmissionEditHandlerServlet {

	@Override
	protected String getSuccessMessage(HttpServletRequest req, Helper helper){
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		return String.format(Common.MESSAGE_STUDENT_EVALUATION_SUBMISSION_RECEIVED,EvalSubmissionEditHelper.escapeForHTML(evalName), courseID);
	}

	@Override
	protected String getSuccessUrl() {
		return Common.PAGE_STUDENT_HOME;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_STUDENT_EVAL_SUBMISSION_EDIT;
	}

	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		String params;
		servletName = servletName.equals("EditHandler") ? Common.STUDENT_EVAL_EDIT_HANDLER_SERVLET : "";
		action = action.equals("EditHandler") ? Common.STUDENT_EVAL_EDIT_HANDLER_SERVLET_EDIT_SUBMISSION : action;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.STUDENT_EVAL_EDIT_HANDLER_SERVLET_EDIT_SUBMISSION){
			try {
				params = "<span class=\"bold\">(" + (String)data.get(2) + ") " + (String)data.get(3) + "'s</span> Submission for Evaluation <span class=\"bold\">(" + (String)data.get(1) + ")</span> for Course <span class=\"bold\">[" + (String)data.get(0) + "]</span> edited.<br><br>";
				
				String[] toEmails = (String[])data.get(4);
				String[] points = (String[])data.get(5);
				String[] justifications = (String[])data.get(6);
				String[] comments = (String[])data.get(7);
				
				for (int i = 0; i < toEmails.length; i++){
					params += "<span class=\"bold\">To:</span> " + toEmails[i] + "<br>";
					params += "<span class=\"bold\">Points:</span> " + points[i] + "<br>";
					if (comments == null){	//p2pDisabled
						params += "<span class=\"bold\">Comments: </span>Disabled<br>";
					} else {
						params += "<span class=\"bold\">Comments:</span> " + comments[i].replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>") + "<br>";
					}
					params += "<span class=\"bold\">Justification:</span> " + justifications[i].replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
					params += "<br><br>";
				}    
				
			} catch (NullPointerException e) {
				params = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>"; 
			} catch (IndexOutOfBoundsException e) {
				params = "<span class=\"color_red\">Varlable index exceeded in " + servletName + ": " + action + ".</span>";    
			}
		}else if (action == Common.LOG_SERVLET_ACTION_FAILURE) {
			String e = (String)data.get(0);
	        params = "<span class=\"color_red\">Servlet Action failure in " + servletName + "<br>";
	        params += e + "</span>";
		} else {
			params = "<span class=\"color_red\">Unknown Action - " + servletName + ": " + action + ".</span>";
		}
			
		return new ActivityLogEntry(servletName, action, true, account, params, url);
	}
	
}
