package com.logicaldoc.webdav;

import java.io.File;

import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.config.WebConfigurator;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;
import com.logicaldoc.webdav.web.WebdavServlet;

/**
 * This class provides initialisations needed by WebDAV-Plugin
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 4.0
 */
public class WebDAVPlugin extends LogicalDOCPlugin {
	private static final String SERVLET_NAME = "Webdav";

	@Override
	public void install() throws Exception {
		super.install();

		File dest = new File(getPluginPath());
		dest = dest.getParentFile().getParentFile();

		WebConfigurator config = new WebConfigurator(dest.getPath() + "/web.xml");

		config.addServlet(SERVLET_NAME, WebdavServlet.class.getName(), 4);
		config.writeXMLDoc();
		config.addInitParam(SERVLET_NAME, "resource-path-prefix", "/webdav", null);
		config.addInitParam(SERVLET_NAME, "resource-config", "/WEB-INF/config.xml", null);
		config.addServletMapping(SERVLET_NAME, "/webdav/*");
		config.writeXMLDoc();

		ContextProperties pbean = new ContextProperties();
		pbean.setProperty("webdav.enabled", "true");
		pbean.setProperty("webdav.depth", "1");
		pbean.write();

		setRestartRequired();
	}
}