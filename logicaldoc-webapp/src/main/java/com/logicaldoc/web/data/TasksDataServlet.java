package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.logicaldoc.core.PersistenceException;
import com.logicaldoc.core.security.Session;
import com.logicaldoc.core.task.Task;
import com.logicaldoc.core.task.TaskManager;
import com.logicaldoc.core.task.TaskTrigger;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;

/**
 * This servlet is responsible for tasks data.
 * 
 * @author Matteo Caruso - LogicalDOC
 * @since 6.0
 */
public class TasksDataServlet extends AbstractDataServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response, Session session, int max,
			Locale locale) throws PersistenceException, IOException {

		TaskManager manager = (TaskManager) Context.get().getBean(TaskManager.class);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		for (Task task : manager.getTasks()) {
			writer.print("<task>");
			writer.print("<name><![CDATA[" + task.getName() + "]]></name>");
			writer.print("<label><![CDATA[" + I18N.message("task.name." + task.getName(), locale) + "]]></label>");
			writer.print("<description><![CDATA[" + I18N.message("task.description." + task.getName(), locale)
					+ "]]></description>");
			if (task.getScheduling().isEnabled()) {
				writer.print("<eenabled>true</eenabled>");
				writer.print("<enabledIcon>bullet_green</enabledIcon>");
			} else {
				writer.print("<eenabled>false</eenabled>");
				writer.print("<enabledIcon>bullet_red</enabledIcon>");
			}
			writer.print("<status>" + task.getStatus() + "</status>");
			if (task.getScheduling().getMode().equals(TaskTrigger.MODE_CRON))
				writer.print("<scheduling>" + task.getScheduling().getCronExpression() + "</scheduling>");
			else if (task.getScheduling().getMode().equals(TaskTrigger.MODE_SIMPLE))
				writer.print(
						"<scheduling>" + I18N.message("each", locale) + " " + task.getScheduling().getIntervalSeconds()
								+ " " + I18N.message("seconds", locale).toLowerCase() + "</scheduling>");
			if (!task.isIndeterminate())
				writer.print("<completion>" + task.getCompletionPercentage() + "</completion>");
			writer.print("<progress>" + task.getProgress() + "</progress>");
			writer.print("<size>" + task.getSize() + "</size>");
			if (task.getStatus() == Task.STATUS_IDLE)
				writer.print("<runningIcon>idle_task</runningIcon>");
			else
				writer.print("<runningIcon>running_task</runningIcon>");

			if (task.getScheduling().getPreviousFireTime() != null) {
				writer.print("<lastStart>" + df.format(task.getScheduling().getPreviousFireTime()) + "</lastStart>");
			}

			if (task.getScheduling().getNextFireTime() != null) {
				writer.print("<nextStart>" + df.format(task.getScheduling().getNextFireTime()) + "</nextStart>");
			}
			writer.print("<indeterminate>" + "" + task.isIndeterminate() + "</indeterminate>");

			writer.print("</task>");
		}
		writer.write("</list>");
	}
}