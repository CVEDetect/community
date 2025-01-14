package com.logicaldoc.gui.frontend.client.metadata.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIAttribute;
import com.logicaldoc.gui.common.client.beans.GUIAttributeSet;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.GuiLog;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.frontend.client.services.AttributeSetService;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.DropCompleteEvent;
import com.smartgwt.client.widgets.events.DropCompleteHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.CellSavedEvent;
import com.smartgwt.client.widgets.grid.events.CellSavedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

/**
 * This panel shows the properties of an attribute set.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.5
 */
public class AttributeSetPropertiesPanel extends HLayout {

	protected DynamicForm setPropertiesForm = new DynamicForm();

	protected DynamicForm attributeSettingsForm1 = new DynamicForm();

	protected DynamicForm attributeSettingsForm2 = new DynamicForm();

	protected DynamicForm attributeButtonsForm = new DynamicForm();

	protected ValuesManager vm = new ValuesManager();

	protected GUIAttributeSet attributeSet;

	protected ChangedHandler changedHandler;

	private AttributeSetDetailsPanel detailsPanel;

	public String updatingAttributeName = "";

	private ListGrid attributesList;

	private SectionStack attributesStack;

	private SelectItem type;

	private SelectItem editor;

	private TextItem group;

	private LinkItem options;

	public AttributeSetPropertiesPanel(GUIAttributeSet attributeSet, final ChangedHandler changedHandler,
			AttributeSetDetailsPanel detailsPanel) {
		if (attributeSet == null) {
			setMembers(AttributeSetsPanel.SELECT_SET);
			return;
		}

		this.attributeSet = attributeSet;
		this.changedHandler = changedHandler;
		this.detailsPanel = detailsPanel;
		setWidth100();
		setHeight100();
		setMembersMargin(5);

		attributesList = new ListGrid();
		attributesList.setEmptyMessage(I18N.message("notitemstoshow"));
		attributesList.setWidth100();
		attributesList.setHeight100();
		attributesList.setEmptyMessage(I18N.message("norecords"));
		attributesList.setCanReorderRecords(false);
		attributesList.setCanSort(false);
		attributesList.setCanReorderRecords(!attributeSet.isReadonly());
		attributesList.setCanAcceptDroppedRecords(!attributeSet.isReadonly());
		attributesList.setCanFreezeFields(false);
		attributesList.setCanGroupBy(false);
		attributesList.setLeaveScrollbarGap(false);
		attributesList.setShowHeader(true);
		attributesList.setCanEdit(!attributeSet.isReadonly());
		attributesList.setShowRowNumbers(true);
		attributesList.setSelectionType(SelectionStyle.SINGLE);

		ListGridField name = new ListGridField("name", I18N.message("name"));
		name.setWidth(150);
		name.setCanEdit(false);
		name.setCanSort(false);
		attributesList.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				if (!AttributeSetPropertiesPanel.this.attributeSet.isReadonly())
					showContextMenu();
				event.cancel();
			}
		});

		ListGridField label = new ListGridField("label", I18N.message("label"));
		label.setCanEdit(true);
		label.setCanSort(false);
		label.addCellSavedHandler(new CellSavedHandler() {

			@Override
			public void onCellSaved(CellSavedEvent event) {
				AttributeSetPropertiesPanel.this.attributeSet.getAttribute(event.getRecord().getAttribute("name"))
						.setLabel((String) event.getNewValue());
				AttributeSetPropertiesPanel.this.changedHandler.onChanged(null);
			}
		});

		attributesList.setFields(name, label);

		attributesList.addDropCompleteHandler(new DropCompleteHandler() {

			@Override
			public void onDropComplete(DropCompleteEvent event) {
				List<String> attributes = new ArrayList<String>();
				for (int i = 0; i < attributesList.getTotalRows(); i++) {
					ListGridRecord record = attributesList.getRecord(i);
					attributes.add(record.getAttributeAsString("name"));
				}

				AttributeSetPropertiesPanel.this.attributeSet.reorderAttributes(attributes);
				changedHandler.onChanged(null);
			}
		});

		attributesStack = new SectionStack();
		attributesStack.setHeight100();
		attributesStack.setWidth(400);

		SectionStackSection section = new SectionStackSection("<b>" + I18N.message("attributes") + "</b>");
		section.setCanCollapse(false);
		section.setExpanded(true);

		section.setItems(attributesList);
		attributesStack.setSections(section);

		fillAttributesList();

		refresh();
	}

	protected void fillAttributesList() {
		if (attributeSet != null && attributeSet.getAttributes() != null) {
			for (GUIAttribute att : attributeSet.getAttributesOrderedByPosition()) {
				ListGridRecord record = new ListGridRecord();
				record.setAttribute("name", att.getName());
				record.setAttribute("label", att.getLabel());
				attributesList.addData(record);
			}
		}
	}

	protected void refresh() {
		vm.clearValues();
		vm.clearErrors(false);

		if (setPropertiesForm != null)
			setPropertiesForm.destroy();

		if (contains(setPropertiesForm))
			removeChild(setPropertiesForm);
		addMetadata();
		addMember(setPropertiesForm);

		attributesList.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				Record record = attributesList.getSelectedRecord();
				onChangeSelectedAttribute(record);
			}
		});

		HLayout setInfo = new HLayout();
		setInfo.setMembers(attributesStack);
		setInfo.setMembersMargin(3);
		setInfo.setWidth(200);

		addMember(setInfo);

		/*
		 * Prepare the second form for adding or updating the extended
		 * attributes
		 */
		VLayout attributesLayout = new VLayout();

		if (attributeSettingsForm1 != null)
			attributeSettingsForm1.destroy();
		if (contains(attributeSettingsForm1))
			removeChild(attributeSettingsForm1);
		attributeSettingsForm1 = new DynamicForm();
		attributeSettingsForm1.setTitleOrientation(TitleOrientation.LEFT);
		attributeSettingsForm1.setNumCols(9);
		attributeSettingsForm1.setWidth(1);

		if (attributeSettingsForm2 != null)
			attributeSettingsForm2.destroy();
		if (contains(attributeSettingsForm2))
			removeChild(attributeSettingsForm2);
		attributeSettingsForm2 = new DynamicForm();
		attributeSettingsForm2.setTitleOrientation(TitleOrientation.TOP);
		attributeSettingsForm2.setNumCols(5);
		attributeSettingsForm2.setWidth100();
		attributeSettingsForm2.setHeight100();

		if (attributeButtonsForm != null)
			attributeButtonsForm.destroy();
		if (contains(attributeButtonsForm))
			removeChild(attributeButtonsForm);
		attributeButtonsForm = new DynamicForm();
		attributeButtonsForm.setTitleOrientation(TitleOrientation.TOP);
		attributeButtonsForm.setNumCols(2);
		attributeButtonsForm.setWidth(1);

		// Attribute Name
		final TextItem attributeName = ItemFactory.newSimpleTextItem("attributeName", "attributename", null);
		attributeName.setRequired(true);
		attributeName.setWidth(180);
		PickerIcon cleanPicker = new PickerIcon(PickerIcon.CLEAR, new FormItemClickHandler() {
			public void onFormItemClick(FormItemIconClickEvent event) {
				clean();
				attributeSettingsForm1.getField("mandatory").setDisabled(false);
				attributeSettingsForm1.getField("hidden").setDisabled(false);
				attributeSettingsForm1.getField("multiple").setDisabled(false);
				attributeSettingsForm2.getField("type").setDisabled(false);
				attributeSettingsForm2.getField("editor").setDisabled(false);
				attributeSettingsForm2.getField("group").setDisabled(true);
				attributeSettingsForm2.getField("validation").setDisabled(false);
				attributeSettingsForm2.getField("initialization").setDisabled(false);
				refreshFieldForm();
			}
		});
		if (!attributeSet.isReadonly()) {
			cleanPicker.setNeverDisable(true);
			attributeName.setIcons(cleanPicker);
		} else
			attributeName.setDisabled(true);

		// Attribute Label
		final TextItem label = ItemFactory.newTextItem("label", "label", null);
		label.setWidth(400);

		// Mandatory
		final CheckboxItem mandatory = new CheckboxItem();
		mandatory.setName("mandatory");
		mandatory.setTitle(I18N.message("mandatory"));
		mandatory.setRedrawOnChange(true);
		mandatory.setWidth(50);
		mandatory.setDefaultValue(false);
		mandatory.setDisabled(attributeSet.isReadonly());

		// Hidden
		final CheckboxItem hidden = new CheckboxItem();
		hidden.setName("hidden");
		hidden.setTitle(I18N.message("hidden"));
		hidden.setRedrawOnChange(true);
		hidden.setWidth(50);
		hidden.setDefaultValue(false);
		hidden.setDisabled(attributeSet.isReadonly());

		// Multiple
		final CheckboxItem multiple = new CheckboxItem();
		multiple.setName("multiple");
		multiple.setTitle(I18N.message("multiplevalues"));
		multiple.setRedrawOnChange(true);
		multiple.setWidth(50);
		multiple.setDefaultValue(false);
		multiple.setDisabled(attributeSet.isReadonly());
		multiple.setEndRow(true);

		// Editor
		editor = new SelectItem("editor", I18N.message("inputmode"));
		LinkedHashMap<String, String> editors = new LinkedHashMap<String, String>();
		editors.put("" + GUIAttribute.EDITOR_DEFAULT, I18N.message("free"));
		editors.put("" + GUIAttribute.EDITOR_TEXTAREA, I18N.message("freetextarea"));
		editors.put("" + GUIAttribute.EDITOR_LISTBOX, I18N.message("preset"));
		editor.setValueMap(editors);
		editor.setWrapTitle(false);
		editor.setDefaultValue("" + GUIAttribute.EDITOR_DEFAULT);
		editor.setDisabled(attributeSet.isReadonly());
		editor.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				refreshFieldForm();
			}
		});

		// Type
		type = new SelectItem("type", I18N.message("type"));
		LinkedHashMap<String, String> types = new LinkedHashMap<String, String>();
		types.put("" + GUIAttribute.TYPE_STRING, I18N.message("string"));
		types.put("" + GUIAttribute.TYPE_INT, I18N.message("integer"));
		types.put("" + GUIAttribute.TYPE_DOUBLE, I18N.message("decimal"));
		types.put("" + GUIAttribute.TYPE_DATE, I18N.message("date"));
		types.put("" + GUIAttribute.TYPE_BOOLEAN, I18N.message("boolean"));
		types.put("" + GUIAttribute.TYPE_USER, I18N.message("user"));
		types.put("" + GUIAttribute.TYPE_FOLDER, I18N.message("folder"));
		type.setValueMap(types);
		type.setWrapTitle(false);
		type.setDefaultValue("" + GUIAttribute.TYPE_STRING);
		type.setValue("" + GUIAttribute.TYPE_STRING);
		type.setDisabled(attributeSet.isReadonly());
		type.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				refreshFieldForm();
			}
		});

		// Values (for preset editor)
		group = ItemFactory.newTextItem("group", "group", null);
		group.setHint(I18N.message("groupname"));
		group.setDisabled(attributeSet.isReadonly());

		// Options (for preset editor)
		options = ItemFactory.newLinkItem("options", I18N.message("options"));
		options.setLinkTitle(I18N.message("attributeoptions"));
		options.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				if (attributeSet.getId() == 0L) {
					SC.say(I18N.message("saveattributesetfirst"));
				} else {
					Options options = new Options(attributeSet.getId(), attributeName.getValueAsString(),
							attributeSet.isReadonly());
					options.show();
				}
			}
		});

		TextAreaItem validation = ItemFactory.newTextAreaItemForAutomation("validation", "validation", null, null,
				false);
		validation.setWidth("*");
		validation.setDisabled(attributeSet.isReadonly());
		validation.setStartRow(true);
		validation.setColSpan(7);

		FormItemIcon validationComposer = new FormItemIcon();
		validationComposer.setName("composer");
		validationComposer.setWidth(16);
		validationComposer.setHeight(16);
		validationComposer.setSrc("[SKIN]/cog.png");
		validationComposer.setPrompt(I18N.message("openvalidatorcomposer"));
		validationComposer.addFormItemClickHandler(new FormItemClickHandler() {

			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				AttributeValidatorComposer composer = new AttributeValidatorComposer(validation,
						type.getValue() != null && !type.getValue().toString().isEmpty()
								? Integer.parseInt(type.getValueAsString())
								: GUIAttribute.TYPE_STRING);
				composer.show();
			}
		});
		List<FormItemIcon> validationIcons = new ArrayList<FormItemIcon>();
		validationIcons.addAll(Arrays.asList(validation.getIcons()));
		validationIcons.add(validationComposer);
		validation.setIcons(validationIcons.toArray(new FormItemIcon[0]));

		TextAreaItem initialization = ItemFactory.newTextAreaItemForAutomation("initialization", "initialization", null,
				null, false);
		initialization.setWidth("*");
		initialization.setDisabled(attributeSet.isReadonly());
		initialization.setStartRow(true);
		initialization.setColSpan(6);

		FormItemIcon initializationComposer = new FormItemIcon();
		initializationComposer.setName("composer");
		initializationComposer.setWidth(16);
		initializationComposer.setHeight(16);
		initializationComposer.setSrc("[SKIN]/cog.png");
		initializationComposer.setPrompt(I18N.message("openinitializatorcomposer"));
		initializationComposer.addFormItemClickHandler(new FormItemClickHandler() {

			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				AttributeInitializerComposer composer = new AttributeInitializerComposer(initialization,
						type.getValue() != null && !type.getValue().toString().isEmpty()
								? Integer.parseInt(type.getValueAsString())
								: GUIAttribute.TYPE_STRING);
				composer.show();
			}
		});
		List<FormItemIcon> initializationIcons = new ArrayList<FormItemIcon>();
		initializationIcons.addAll(Arrays.asList(initialization.getIcons()));
		initializationIcons.add(initializationComposer);
		initialization.setIcons(initializationIcons.toArray(new FormItemIcon[0]));

		ButtonItem save = new ButtonItem();
		save.setTitle(I18N.message("save"));
		save.setAutoFit(true);
		save.setEndRow(false);
		save.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				if (!attributeSettingsForm1.validate()) {
					return;
				} else {
					String name = (String) attributeName.getValue();
					if (GUIAttribute.isForbidden(name.trim())) {
						final String message = I18N.message("attributenameforbidden",
								Arrays.asList(GUIAttribute.FORBIDDEN_NAMES).toString().substring(1).replace("]", ""));
						SC.warn(I18N.message("error"), message);
						return;
					}
				}

				if (attributeName.getValue() != null && !((String) attributeName.getValue()).trim().isEmpty()) {
					if (updatingAttributeName.trim().isEmpty()) {
						GUIAttribute att = new GUIAttribute();
						att.setName(attributeName.getValueAsString());
						att.setLabel(label.getValueAsString());
						att.setMandatory((Boolean) mandatory.getValue());
						att.setHidden((Boolean) hidden.getValue());
						att.setMultiple((Boolean) multiple.getValue());
						att.setType(Integer.parseInt((String) type.getValueAsString()));
						att.setEditor(Integer.parseInt((String) editor.getValueAsString()));
						att.setValidation(validation.getValueAsString());
						att.setInitialization(initialization.getValueAsString());

						if (att.getType() == GUIAttribute.TYPE_USER)
							att.setStringValue(group.getValueAsString());

						if (attributeSettingsForm1.validate()) {
							changedHandler.onChanged(null);
							addAttribute(att);
						}
					} else {
						GUIAttribute att = attributeSet.getAttribute(updatingAttributeName.trim());
						if (att != null) {
							att.setName(attributeName.getValueAsString());
							att.setLabel(label.getValueAsString());
							att.setMandatory((Boolean) mandatory.getValue());
							att.setHidden((Boolean) hidden.getValue());
							att.setMultiple((Boolean) multiple.getValue());
							att.setType(Integer.parseInt(type.getValueAsString()));
							att.setEditor(Integer.parseInt((String) editor.getValueAsString()));

							if (att.getType() == GUIAttribute.TYPE_USER)
								att.setStringValue(group.getValueAsString());
							else
								att.setStringValue(null);
							att.setValidation(validation.getValueAsString());
							att.setInitialization(initialization.getValueAsString());

							ListGridRecord record = attributesList.getSelectedRecord();
							record.setAttribute("name", att.getName());
							record.setAttribute("label", att.getLabel());
							record.setAttribute("validation", att.getValidation());
							record.setAttribute("initialization", att.getInitialization());

							changedHandler.onChanged(null);
						}
					}
				}
			}
		});

		ButtonItem clean = new ButtonItem();
		clean.setTitle(I18N.message("clean"));
		clean.setAutoFit(true);
		clean.setStartRow(false);
		clean.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				clean();
			}
		});

		attributeSettingsForm1.setItems(attributeName, new SpacerItem(), mandatory, hidden, multiple);
		attributeSettingsForm2.setItems(label, type, editor, group, options, initialization, validation);
		attributeButtonsForm.setItems(save, clean);

		attributesLayout.setMembers(attributeSettingsForm1, attributeSettingsForm2, attributeButtonsForm);
		attributesLayout.setMembersMargin(10);
		attributesLayout.setWidth100();
		addMember(attributesLayout);

		refreshFieldForm();
	}

	protected void addMetadata() {
		setPropertiesForm = new DynamicForm();
		setPropertiesForm.setNumCols(1);
		setPropertiesForm.setValuesManager(vm);
		setPropertiesForm.setTitleOrientation(TitleOrientation.LEFT);

		StaticTextItem id = ItemFactory.newStaticTextItem("id", "id", Long.toString(attributeSet.getId()));
		id.setDisabled(true);

		TextItem name = ItemFactory.newSimpleTextItem("name", I18N.message("name"), attributeSet.getName());
		name.setRequired(true);
		name.setDisabled(attributeSet.isReadonly());
		if (!attributeSet.isReadonly())
			name.addChangedHandler(changedHandler);

		TextAreaItem description = ItemFactory.newTextAreaItem("description", "description",
				attributeSet.getDescription());
		description.setDisabled(attributeSet.isReadonly());

		if (!attributeSet.isReadonly())
			description.addChangedHandler(changedHandler);

		setPropertiesForm.setItems(id, name, description);

		setPropertiesForm.setWidth(200);
	}

	@SuppressWarnings("unchecked")
	protected boolean validate() {
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			attributeSet.setName((String) values.get("name"));
			attributeSet.setDescription((String) values.get("description"));
		}
		return !vm.hasErrors();
	}

	private void addAttribute(GUIAttribute att) {
		ListGridRecord record = new ListGridRecord();
		record.setAttribute("name", att.getName());
		record.setAttribute("label", att.getLabel());
		updatingAttributeName = att.getName();
		attributesList.getDataAsRecordList().add(record);
		attributeSet.appendAttribute(att);
		detailsPanel.enableSave();
		attributesList.selectRecord(record);

	}

	private void clean() {
		attributeSettingsForm1.clearValues();
		attributeSettingsForm1.getField("attributeName").setDisabled(false);
		attributeSettingsForm1.setValue("attributeName", "");
		updatingAttributeName = "";

		attributeSettingsForm2.setValue("label", (String) null);
		attributeSettingsForm1.setValue("mandatory", false);
		attributeSettingsForm1.setValue("hidden", false);
		attributeSettingsForm1.setValue("multiple", false);
		attributeSettingsForm2.setValue("type", GUIAttribute.TYPE_STRING);
		attributeSettingsForm2.setValue("editor", GUIAttribute.EDITOR_DEFAULT);
		attributeSettingsForm2.setValue("group", (String) null);
		attributeSettingsForm2.setValue("validation", (String) null);
		attributeSettingsForm2.setValue("initialization", (String) null);

		attributesList.deselectAllRecords();
	}

	protected void showContextMenu() {
		Menu contextMenu = new Menu();

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.message("ddelete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				final ListGridRecord[] selection = attributesList.getSelectedRecords();
				if (selection == null || selection.length == 0)
					return;
				final String[] names = new String[selection.length];
				for (int i = 0; i < selection.length; i++) {
					names[i] = selection[i].getAttribute("name");
				}

				LD.ask(I18N.message("ddelete"), I18N.message("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							for (String attrName : names)
								attributeSet.removeAttribute(attrName);
							attributesList.removeSelectedData();
							clean();
							detailsPanel.enableSave();
						}
					}
				});
			}
		});

		MenuItem applyValidationToTemplates = new MenuItem();
		applyValidationToTemplates.setTitle(I18N.message("applyvalidationtotemplates"));
		applyValidationToTemplates.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				final ListGridRecord selection = attributesList.getSelectedRecord();

				LD.ask(I18N.message("applyvalidationtotemplates"), I18N.message("applyvalidationtotemplatesquestion"),
						new BooleanCallback() {
							@Override
							public void execute(Boolean value) {
								if (value) {
									LD.contactingServer();
									AttributeSetService.Instance.get().applyValidationToTemplates(attributeSet.getId(),
											selection.getAttributeAsString("name"), new AsyncCallback<Void>() {
												@Override
												public void onFailure(Throwable caught) {
													GuiLog.serverError(caught);
													LD.clearPrompt();
												}

												@Override
												public void onSuccess(Void arg0) {
													LD.clearPrompt();
												}
											});
								}
							}
						});
			}
		});
		applyValidationToTemplates.setEnabled(attributeSet.getId() != 0L);

		MenuItem applyInitializationToTemplates = new MenuItem();
		applyInitializationToTemplates.setTitle(I18N.message("applyinitializationtotemplates"));
		applyInitializationToTemplates.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				final ListGridRecord selection = attributesList.getSelectedRecord();

				LD.ask(I18N.message("applyinitializationtotemplates"),
						I18N.message("applyinitializationtotemplatesquestion"), new BooleanCallback() {
							@Override
							public void execute(Boolean value) {
								if (value) {
									LD.contactingServer();
									AttributeSetService.Instance.get().applyInitializationToTemplates(
											attributeSet.getId(), selection.getAttributeAsString("name"),
											new AsyncCallback<Void>() {
												@Override
												public void onFailure(Throwable caught) {
													GuiLog.serverError(caught);
													LD.clearPrompt();
												}

												@Override
												public void onSuccess(Void arg0) {
													LD.clearPrompt();
												}
											});
								}
							}
						});
			}
		});
		applyInitializationToTemplates.setEnabled(attributeSet.getId() != 0L);

		contextMenu.setItems(applyInitializationToTemplates, applyValidationToTemplates, delete);
		contextMenu.showContextMenu();
	}

	protected void onChangeSelectedAttribute(Record record) {
		if (record != null) {
			String selectedAttributeName = record.getAttributeAsString("name");
			GUIAttribute extAttr = attributeSet.getAttribute(selectedAttributeName);
			attributeSettingsForm1.setValue("attributeName", extAttr.getName());
			attributeSettingsForm1.setValue("mandatory", extAttr.isMandatory());
			attributeSettingsForm1.setValue("hidden", extAttr.isHidden());
			attributeSettingsForm1.setValue("multiple", extAttr.isMultiple());
			attributeSettingsForm2.setValue("label", extAttr.getLabel());
			attributeSettingsForm2.setValue("type", extAttr.getType());
			attributeSettingsForm2.setValue("editor", extAttr.getEditor());
			attributeSettingsForm2.setValue("group", extAttr.getStringValue());
			attributeSettingsForm2.setValue("validation", extAttr.getValidation());
			attributeSettingsForm2.setValue("initialization", extAttr.getInitialization());
			updatingAttributeName = selectedAttributeName;
			refreshFieldForm();
		}
	}

	public void refreshFieldForm() {
		if (type.getValueAsString().equals("" + GUIAttribute.TYPE_STRING)) {
			editor.setVisible(true);
			group.setVisible(false);
			group.setValue("");

			if (editor.getValueAsString().equals("" + GUIAttribute.EDITOR_LISTBOX)) {
				options.setVisible(true);
			} else {
				options.setVisible(false);
			}
		} else if (type.getValueAsString().equals("" + GUIAttribute.TYPE_USER)) {
			editor.setVisible(false);
			group.setVisible(true);
			options.setVisible(false);
		} else if (type.getValueAsString().equals("" + GUIAttribute.TYPE_FOLDER)) {
			editor.setVisible(false);
			options.setVisible(false);
			group.setVisible(false);
			group.setValue("");
		} else {
			editor.setVisible(false);
			group.setVisible(false);
			options.setVisible(false);
			group.setValue("");
		}

		if (!updatingAttributeName.isEmpty())
			attributeSettingsForm1.getItem("attributeName").setDisabled(true);

		attributeSettingsForm1.markForRedraw();
		attributeSettingsForm2.markForRedraw();
	}

	protected boolean isMandatory(int category, String attributeName) {
		return false;
	}
}