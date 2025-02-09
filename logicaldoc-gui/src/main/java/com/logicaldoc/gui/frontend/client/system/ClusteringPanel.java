package com.logicaldoc.gui.frontend.client.system;

import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.GuiLog;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.tab.Tab;

/**
 * The Clustering console.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 6.5
 */
public class ClusteringPanel extends AdminPanel {

	private ValuesManager vm = new ValuesManager();

	public ClusteringPanel() {
		super("clustering");
	}

	@Override
	protected void onDraw() {
		SettingService.Instance.get()
				.loadSettingsByNames(
						new String[] { "cluster.enabled", "cluster.name", "cluster.port", "cluster.multicastip",
								"cluster.cache.resources", "cluster.chunk.size", "id" },
						new AsyncCallback<GUIParameter[]>() {

							@Override
							public void onFailure(Throwable caught) {
								GuiLog.serverError(caught);
							}

							@Override
							public void onSuccess(GUIParameter[] parameters) {
								init(parameters);
							}
						});
	}

	private void init(GUIParameter[] parameters) {
		DynamicForm clusterForm = new DynamicForm();
		clusterForm.setWidth(300);
		clusterForm.setColWidths(1, "*");
		clusterForm.setValuesManager(vm);
		clusterForm.setTitleOrientation(TitleOrientation.LEFT);

		RadioGroupItem enabled = ItemFactory.newBooleanSelector("eenabled", "enabled");
		enabled.setValue("true".equals(parameters[0].getValue()) ? "yes" : "no");

		TextItem name = ItemFactory.newTextItem("name", I18N.message("name"), parameters[1].getValue());
		name.setRequired(true);

		IntegerItem baseport = ItemFactory.newIntegerItem("baseport", I18N.message("baseport"),
				Integer.parseInt(parameters[2].getValue()));
		baseport.setRequired(true);

		TextItem multicastip = ItemFactory.newTextItem("multicastip", I18N.message("multicastip"),
				parameters[3].getValue());

		RadioGroupItem cacheResources = ItemFactory.newBooleanSelector("cacheResources", "cache");
		cacheResources.setHint(I18N.message("cachesresources"));
		cacheResources.setValue("true".equals(parameters[4].getValue()) ? "yes" : "no");

		SpinnerItem chunkSize = ItemFactory.newSpinnerItem("chunkSize", I18N.message("chunksize"),
				Integer.valueOf(parameters[5].getValue()));
		chunkSize.setHint("MB");
		chunkSize.setRequired(true);
		chunkSize.setWrapTitle(false);
		chunkSize.setMin(1);
		chunkSize.setStep(10);

		StaticTextItem id = ItemFactory.newStaticTextItem("id", I18N.message("nodeid"), parameters[6].getValue());
		id.setWrap(false);
		id.setWrapTitle(false);

		ButtonItem save = new ButtonItem();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event) {
				final Map<String, Object> values = vm.getValues();

				if (vm.validate()) {
					final GUIParameter[] settings = new GUIParameter[6];
					settings[0] = new GUIParameter("cluster.enabled",
							values.get("eenabled").equals("yes") ? "true" : "false");
					settings[1] = new GUIParameter("cluster.name", vm.getValueAsString("name"));
					settings[2] = new GUIParameter("cluster.port", vm.getValueAsString("baseport"));
					settings[3] = new GUIParameter("cluster.multicastip", vm.getValueAsString("multicastip"));
					settings[4] = new GUIParameter("cluster.cache.resources",
							values.get("cacheResources").equals("yes") ? "true" : "false");
					settings[5] = new GUIParameter("cluster.chunk.size",vm.getValueAsString("chunkSize"));
					
					SettingService.Instance.get().saveSettings(settings, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							GuiLog.serverError(caught);
						}

						@Override
						public void onSuccess(Void result) {
							GuiLog.info(I18N.message("settingssaved") + " " + I18N.message("needrestart"), null);
						}
					});
				}
			}
		});

		clusterForm.setItems(enabled, id, name, multicastip, baseport, chunkSize, cacheResources, save);
		body.setMembers(clusterForm);

		Tab channels = new Tab();
		channels.setTitle(I18N.message("channels"));
		channels.setPane(new ChannelsPanel());
		tabs.addTab(channels);

		Tab settings = new Tab();
		settings.setTitle(I18N.message("settings"));
		settings.setPane(new ScopedPropertiesPanel());
		tabs.addTab(settings);
	}
}