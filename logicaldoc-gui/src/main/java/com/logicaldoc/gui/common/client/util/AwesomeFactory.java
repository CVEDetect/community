package com.logicaldoc.gui.common.client.util;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.toolbar.ToolStripMenuButton;

/**
 * Factory of objects that make use of font awesome
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 8.0
 */
public class AwesomeFactory {
	/**
	 * Creates a ToolStripButton using font-awesome icon
	 * 
	 * @param icon the icon file name
	 * @param toolTip the message to display when the user moves the cursor over
	 *        the button
	 * 
	 * @return the new button
	 */
	public static ToolStripButton newToolStripButton(String icon, String toolTip) {
		return newToolStripButton(icon, toolTip, null);
	}

	/**
	 * Creates a ToolStripButton using font-awesome icon
	 * 
	 * @param icon the icon file name
	 * @param toolTip the message to display when the user moves the cursor over
	 *        the button
	 * @param text title of the button
	 * 
	 * @return the new button
	 */
	public static ToolStripButton newToolStripButton(String icon, String toolTip, String text) {
		ToolStripButton button = new ToolStripButton();
		button.setTooltip(I18N.message(toolTip));
		button.setTitle(getIconHtml(icon, text));
		button.setAutoFit(true);
		return button;
	}

	/**
	 * Creates a ToolStripButton using font-awesome icon
	 * 
	 * @param icon the icon file name
	 * @param rotation rotation specification for the icon
	 * @param title title of the button
	 * @param toolTip the message to display when the user moves the cursor over
	 *        the button
	 * @param menu the menu to display
	 * 
	 * @return the new button
	 */
	public static ToolStripMenuButton newToolStripToolStripMenuButton(String icon, String rotation, String title,
			String toolTip, Menu menu) {
		ToolStripMenuButton menuButton = new ToolStripMenuButton(AwesomeFactory.getIconHtml(icon), menu);
		menuButton.setTooltip(I18N.message(toolTip));
		menuButton.setTitle(getIconHtml(icon, rotation, title));
		menuButton.setAutoFit(true);
		return menuButton;
	}

	/**
	 * Creates a ToolStripButton using font-awesome icon useful for small icons
	 * (16x16)
	 * 
	 * @param icon the icon file name
	 * @param toolTip the message to display when the user moves the cursor over
	 *        the button
	 * 
	 * @return the new button
	 */
	public static Button newIconButton(String icon, String toolTip) {
		return newIconButton(icon, toolTip, null);
	}

	/**
	 * Creates a ToolStripButton using font-awesome icon useful for small icons
	 * (16x16)
	 * 
	 * @param icon the icon file name
	 * @param toolTip the message to display when the user moves the cursor over
	 *        the button
	 * @param color the color of the icon
	 * 
	 * @return the new button
	 */
	public static Button newColoredIconButton(String icon, String toolTip, String color) {
		Button button = newIconButton(icon, toolTip, null);
		if (color != null && !color.isEmpty())
			button.setTitle("<span style='color: " + color + "'>" + getIconHtml(icon, null, null) + "</span>");
		return button;
	}

	/**
	 * Creates a ToolStripButton using font-awesome icon useful for small icons
	 * (16x16)
	 * 
	 * @param icon the icon file name
	 * @param toolTip the message to display when the user moves the cursor over
	 *        the button
	 * @param text title of the button
	 * 
	 * @return the new button
	 */
	public static Button newIconButton(String icon, String toolTip, String text) {
		Button button = newToolStripButton(icon, toolTip, text);
		button.setShowDown(false);
		button.setShowRollOver(false);
		button.setLayoutAlign(Alignment.CENTER);
		button.setHeight(16);
		button.setWidth(16);
		button.setMargin(0);
		return button;
	}

	public static String getCssClassPrefix() {
		return "fa" + (Util.isCommunity() ? "s" : "l");
	}

	public static String getIconHtml(String icon) {
		return getIconHtml(icon, null);
	}

	public static String getSpinnerIconHtml(String icon, String text) {
		if (text == null || text.isEmpty())
			return "<i class='" + getCssClassPrefix() + " fa-" + icon + " fa-lg fa-spinner' aria-hidden='true'></i>";
		else
			return "<div><i class='" + getCssClassPrefix() + " fa-" + icon
					+ " fa-lg fa-fw fa-spinner' aria-hidden='true'></i>&nbsp;" + I18N.message(text) + "</div>";
	}

	public static String getIconButtonHTML(String icon, String text, String tooltip, String color, String url) {
		String button = "<div class='statusIcon' "
				+ (tooltip != null && !tooltip.isEmpty() ? "title='" + I18N.message(tooltip) + "'" : "")
				+ (color != null && !color.isEmpty() ? " style='color: " + color + "'" : "")
				+ (url != null && !url.isEmpty() ? " onclick=\"download('" + url + "')\"" : "") + " >";
		button += getColoredIconHtml(icon, text, color);
		button += "</div>";

		return button;
	}

	public static String getIndexedIconButtonHTML(long docId, boolean download, Integer indexed, String color) {
		String button = "<div class='statusIcon' "
				+ (indexed != null && indexed != Constants.INDEX_SKIP ? "title='" + I18N.message("indexed") + "' " : "")
				+ (color != null && !color.isEmpty() ? " style='color: " + color + "'" : "");
		if (download)
			button += " onclick=\"download('" + Util.downloadURL(docId) + "&downloadText=true')\"";
		button += " >";
		button += getIndexedIcon(indexed);
		button += "</div>";
		return button;
	}

	public static String getLockedButtonHTML(Integer status, String user, String color) {
		String button = "<div class='statusIcon' "
				+ (status == Constants.DOC_CHECKED_OUT || status == Constants.DOC_LOCKED
						? "title='" + I18N.message("lockedby") + " " + user + "' "
						: "")
				+ (color != null && !color.isEmpty() ? " style='color: " + color + "'" : "");
		button += " >";
		button += DocUtil.getLockedIcon(status);
		button += "</div>";
		return button;
	}
	
	public static String getIndexedIcon(Integer indexed) {
		if (indexed == null)
			return "";
		String html = AwesomeFactory.getIconHtml("database");
		if (indexed == Constants.INDEX_SKIP) {		
			html = "<span class='fa-stack'><i class='" + getCssClassPrefix() + " fa-database fa-stack-1x' aria-hidden='true' data-fa-transform='grow-6'></i>";
			html += "<i class='" + AwesomeFactory.getCssClassPrefix()
					+ " fa-times fa-stack-1x' style='color: red' data-fa-transform='grow-2'></i></span>";
		}
		return html;
	}

	public static String getIconHtml(String icon, String text) {
		return getColoredIconHtml(icon, text, null);
	}

	public static String getColoredIconHtml(String icon, String text, String color) {
		if (text == null || text.isEmpty())
			return "<i class='" + getCssClassPrefix() + " fa-" + icon + " fa-lg' aria-hidden='true'  "
					+ (color != null && !color.isEmpty() ? "style='color: " + color + "'" : "") + "></i>";
		else
			return "<div><i class='" + getCssClassPrefix() + " fa-" + icon + " fa-lg fa-fw' aria-hidden='true'"
					+ (color != null && !color.isEmpty() ? "style='color: " + color + "'" : "") + "></i> "
					+ I18N.message(text) + "</div>";
	}

	public static String getIconHtml(String icon, String rotation, String text) {
		if (text == null || text.isEmpty())
			return "<i class='" + getCssClassPrefix() + " fa-" + icon + (rotation != null ? " " + rotation : "")
					+ " fa-lg' aria-hidden='true'></i>";
		else
			return "<div><i class='" + getCssClassPrefix() + " fa-" + icon + (rotation != null ? " " + rotation : "")
					+ " fa-lg fa-fw' aria-hidden='true'></i> " + I18N.message(text) + "</div>";
	}
}