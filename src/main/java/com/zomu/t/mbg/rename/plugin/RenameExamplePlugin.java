package com.zomu.t.mbg.rename.plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * Example系のSQLIDを任意に指定するためのプラグイン
 * 
 * @author takashimanozomu
 *
 */
public class RenameExamplePlugin extends PluginAdapter {

	/** 検索文字列 */
	private String searchString;

	/** 置換文字列 */
	private String replaceString;

	/** 検索文字列（小文字） */
	private String searchStringLower;

	/** 置換文字列（小文字） */
	private String replaceStringLower;

	private Pattern pattern;

	private Pattern patternLower;

	/**
	 * 置換処理
	 * 
	 * @param target
	 * @return
	 */
	private String replace(String target) {
		if (target == null) {
			return null;
		}
		Matcher m = pattern.matcher(target);
		while (m.find()) {
			target = target.replaceAll(m.group(1), replaceString);
			m = pattern.matcher(target);
		}
		return target;
	}

	/**
	 * 置換処理（大小文字無視）
	 * 
	 * @param target
	 * @return
	 */
	private String replaceLower(String target) {
		if (target == null) {
			return null;
		}
		Matcher m = patternLower.matcher(target);
		while (m.find()) {
			target = target.replaceAll(m.group(1), replaceStringLower);
			m = patternLower.matcher(target);
		}
		return target;
	}

	/**
	 * メソッド名および引数の名前、引数に付いているアノテーションの属性値を置換します.
	 * 
	 * @param method
	 */
	private void replaceMethod(Method method) {
		// メソッド名
		method.setName(replace(method.getName()));

		// パラメータ名
		for (Parameter parameter : method.getParameters()) {
			Field f;
			try {
				f = Parameter.class.getDeclaredField("name");
				f.setAccessible(true);
				f.set(parameter, replaceLower(parameter.getName()));
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}

			// アノテーション
			List<String> newAnnotations = new ArrayList<>();
			for (String annotation : parameter.getAnnotations()) {
				newAnnotations.add(replaceLower(annotation));
			}
			parameter.getAnnotations().clear();
			parameter.getAnnotations().addAll(newAnnotations);
		}
	}

	/**
	 * 初期化処理
	 */
	@Override
	public void initialized(IntrospectedTable introspectedTable) {

		// UPDATE
		introspectedTable.setUpdateByExampleSelectiveStatementId(
				replace(introspectedTable.getUpdateByExampleSelectiveStatementId()));
		introspectedTable.setUpdateByExampleStatementId(replace(introspectedTable.getUpdateByExampleStatementId()));
		introspectedTable.setUpdateByExampleWithBLOBsStatementId(
				replace(introspectedTable.getUpdateByExampleWithBLOBsStatementId()));

		// DELETE
		introspectedTable.setDeleteByExampleStatementId(replace(introspectedTable.getDeleteByExampleStatementId()));

		// SELECT
		introspectedTable.setSelectByExampleStatementId(replace(introspectedTable.getSelectByExampleStatementId()));
		introspectedTable.setSelectByExampleWithBLOBsStatementId(
				replace(introspectedTable.getSelectByExampleWithBLOBsStatementId()));
		introspectedTable.setCountByExampleStatementId(replace(introspectedTable.getCountByExampleStatementId()));

		// WHERE句
		introspectedTable.setExampleWhereClauseId(replace(introspectedTable.getExampleWhereClauseId()));
		introspectedTable.setMyBatis3UpdateByExampleWhereClauseId(
				replace(introspectedTable.getMyBatis3UpdateByExampleWhereClauseId()));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validate(List<String> warnings) {
		searchString = properties.getProperty("searchString");
		replaceString = properties.getProperty("replaceString");
		searchStringLower = properties.getProperty("searchStringLower");
		replaceStringLower = properties.getProperty("replaceStringLower");
		if (searchString != null && replaceString != null && searchStringLower != null && replaceStringLower != null) {
			Pattern p = Pattern.compile("(" + searchString + ")");
			pattern = p;
			Pattern p_lower = Pattern.compile("(" + searchStringLower + ")");
			patternLower = p_lower;
			return true;
		}
		return false;
	}

	/* 以下メソッド変更呼び出し */

	/**
	 * {@inheritDoc}
	 */
	public boolean clientCountByExampleMethodGenerated(Method method, Interface interfaze,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientCountByExampleMethodGenerated(method, interfaze, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientCountByExampleMethodGenerated(Method method, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientCountByExampleMethodGenerated(method, topLevelClass, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientDeleteByExampleMethodGenerated(Method method, Interface interfaze,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientDeleteByExampleMethodGenerated(method, interfaze, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientDeleteByExampleMethodGenerated(Method method, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientDeleteByExampleMethodGenerated(method, topLevelClass, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, Interface interfaze,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientDeleteByPrimaryKeyMethodGenerated(method, interfaze, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientDeleteByPrimaryKeyMethodGenerated(method, topLevelClass, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientInsertMethodGenerated(Method method, Interface interfaze,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientInsertMethodGenerated(method, interfaze, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientInsertMethodGenerated(Method method, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientInsertMethodGenerated(method, topLevelClass, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientSelectByExampleWithBLOBsMethodGenerated(method, interfaze, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientSelectByExampleWithBLOBsMethodGenerated(method, topLevelClass, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientSelectByExampleWithoutBLOBsMethodGenerated(method, interfaze, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientSelectByExampleWithoutBLOBsMethodGenerated(method, topLevelClass, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientSelectByPrimaryKeyMethodGenerated(method, interfaze, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientSelectByPrimaryKeyMethodGenerated(method, topLevelClass, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientUpdateByExampleSelectiveMethodGenerated(Method method, Interface interfaze,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientUpdateByExampleSelectiveMethodGenerated(method, interfaze, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientUpdateByExampleSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientUpdateByExampleSelectiveMethodGenerated(method, topLevelClass, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientUpdateByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientUpdateByExampleWithBLOBsMethodGenerated(method, interfaze, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientUpdateByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientUpdateByExampleWithBLOBsMethodGenerated(method, topLevelClass, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientUpdateByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientUpdateByExampleWithoutBLOBsMethodGenerated(method, interfaze, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientUpdateByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientUpdateByExampleWithoutBLOBsMethodGenerated(method, topLevelClass, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, Interface interfaze,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientUpdateByPrimaryKeySelectiveMethodGenerated(method, interfaze, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientUpdateByPrimaryKeySelectiveMethodGenerated(method, topLevelClass, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, Interface interfaze,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(method, interfaze, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(method, topLevelClass, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, Interface interfaze,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(method, interfaze, introspectedTable);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		replaceMethod(method);
		return super.clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(method, topLevelClass, introspectedTable);
	}

	/* 以下XMLの置換 */

	/**
	 * XMLの属性値の置換処理.
	 * 
	 * @param element
	 */
	private void replaceXmlElement(XmlElement element) {
		for (Element el : element.getElements()) {
			if (el instanceof XmlElement) {
				XmlElement xe = XmlElement.class.cast(el);
				for (Attribute attr : xe.getAttributes()) {
					Field f;
					try {
						f = Attribute.class.getDeclaredField("value");
						f.setAccessible(true);
						f.set(attr, replaceLower(attr.getValue()));
					} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
							| IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
				replaceXmlElement(xe);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sqlMapExampleWhereClauseElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
		replaceXmlElement(element);
		return super.sqlMapExampleWhereClauseElementGenerated(element, introspectedTable);
	}
}
