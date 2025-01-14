package com.logicaldoc.core.communication;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.PersistenceException;
import com.logicaldoc.util.sql.SqlUtil;

/**
 * Hibernate implementation of <code>MessageTemplateDAO</code>
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 6.5
 */
public class HibernateMessageTemplateDAO extends HibernatePersistentObjectDAO<MessageTemplate>
		implements MessageTemplateDAO {

	private static final String NAME = ".name='";

	private static final String AND = "' and ";

	private static final String TENANT_ID = ".tenantId=";

	private static final String LANGUAGE = ".language='";

	public HibernateMessageTemplateDAO() {
		super(MessageTemplate.class);
		super.log = LoggerFactory.getLogger(HibernateMessageTemplateDAO.class);
	}

	@Override
	public List<MessageTemplate> findByLanguage(String language, long tenantId) {
		try {
			return findByWhere(
					" " + ALIAS_ENTITY + LANGUAGE + language + AND + ALIAS_ENTITY + TENANT_ID + tenantId,
					"order by " + ALIAS_ENTITY + ".name", null);
		} catch (PersistenceException e) {
			log.error(e.getMessage(), e);
			return new ArrayList<MessageTemplate>();
		}
	}

	@Override
	public List<MessageTemplate> findByTypeAndLanguage(String type, String language, long tenantId) {
		StringBuilder query = new StringBuilder(ALIAS_ENTITY + LANGUAGE + language + "' ");
		query.append(" and " + ALIAS_ENTITY + TENANT_ID + tenantId);
		if (StringUtils.isNotEmpty(type))
			query.append(" and " + ALIAS_ENTITY + ".type='" + type + "' ");

		try {
			return findByWhere(query.toString(), "order by " + ALIAS_ENTITY + ".name", null);
		} catch (PersistenceException e) {
			log.error(e.getMessage(), e);
			return new ArrayList<MessageTemplate>();
		}
	}

	@Override
	public MessageTemplate findByNameAndLanguage(String name, String language, long tenantId) {
		String lang = language;
		if (StringUtils.isEmpty(lang))
			lang = "en";

		try {
			List<MessageTemplate> buf = findByWhere(" " + ALIAS_ENTITY + LANGUAGE + lang + AND + ALIAS_ENTITY
					+ NAME + name + AND + ALIAS_ENTITY + TENANT_ID + tenantId, null, null);
			if (buf != null && !buf.isEmpty())
				return buf.get(0);

			buf = findByWhere(" " + ALIAS_ENTITY + ".language='en' and " + ALIAS_ENTITY + NAME + name + AND
					+ ALIAS_ENTITY + TENANT_ID + tenantId, null, null);
			if (buf != null && !buf.isEmpty())
				return buf.get(0);
		} catch (PersistenceException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public boolean delete(long id, int code) {
		if (!checkStoringAspect())
			return false;

		assert (code != 0);

		try {
			MessageTemplate template = (MessageTemplate) findById(id);
			if (template != null) {
				template.setDeleted(code);
				template.setName(template.getName() + "." + template.getId());
				saveOrUpdate(template);
			}
			return true;
		} catch (PersistenceException e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public List<MessageTemplate> findByName(String name, long tenantId) {
		try {
			return findByWhere(" " + ALIAS_ENTITY + NAME + SqlUtil.doubleQuotes(name) + AND + ALIAS_ENTITY
					+ TENANT_ID + tenantId, null, null);
		} catch (PersistenceException e) {
			log.error(e.getMessage(), e);
			return new ArrayList<MessageTemplate>();
		}
	}
}