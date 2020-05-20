/**
 * Copyright 2006-2019 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

public class SelectQueryAllNumberElementGenerator extends
        AbstractXmlElementGenerator {

    public SelectQueryAllNumberElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("select"); //$NON-NLS-1$

        answer.addAttribute(new Attribute(
                "id", "queryAllNumber")); //$NON-NLS-1$
        answer.addAttribute(new Attribute("resultType", //$NON-NLS-1$
                "java.lang.Long"));

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();

        sb.append("select count(*)\r\n");
        sb.append("     from " + introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        XmlElement dynamicElement = new XmlElement("where"); //$NON-NLS-1$
        answer.addElement(dynamicElement);

        for (IntrospectedColumn introspectedColumn :
                ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getBaseColumns())) {
            String javaProperty = introspectedColumn.getJavaProperty();
            if (StringUtils.equals("updateTime", javaProperty)) {
                continue;
            }
            if (StringUtils.equals("createTime", javaProperty)) {
                sb.setLength(0);
                sb.append("createStartTime");
                sb.append(" != null");
                XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
                isNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
                dynamicElement.addElement(isNotNullElement);

                sb.setLength(0);
                sb.append("AND ");
                sb.append(MyBatis3FormattingUtilities
                        .getEscapedColumnName(introspectedColumn));
                sb.append("  <![CDATA[>=]]>  "); //$NON-NLS-1$
                sb.append("#{createStartTime}");
                isNotNullElement.addElement(new TextElement(sb.toString()));

                sb.setLength(0);
                sb.append("createEndTime");
                sb.append(" != null");
                XmlElement isNotNullElement2 = new XmlElement("if"); //$NON-NLS-1$
                isNotNullElement2.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
                dynamicElement.addElement(isNotNullElement2);

                sb.setLength(0);
                sb.append("AND ");
                sb.append(MyBatis3FormattingUtilities
                        .getEscapedColumnName(introspectedColumn));
                sb.append("  <![CDATA[<=]]>  ");
                sb.append("#{createEndTime}");
                isNotNullElement2.addElement(new TextElement(sb.toString()));
            } else {
                sb.setLength(0);
                sb.append(javaProperty);
                sb.append(" != null");
                if (StringUtils.equals("String", introspectedColumn.getFullyQualifiedJavaType().getShortName())) {
                    sb.append(" and " + javaProperty + " !=''");
                }
                XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
                isNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
                dynamicElement.addElement(isNotNullElement);

                sb.setLength(0);
                sb.append("AND ");
                sb.append(MyBatis3FormattingUtilities
                        .getEscapedColumnName(introspectedColumn));
                sb.append(" = "); //$NON-NLS-1$
                sb.append("#{" + javaProperty + "}");

                isNotNullElement.addElement(new TextElement(sb.toString()));
            }


//            if(introspectedColumn.){
//
//            }

        }

        if (context.getPlugins()
                .sqlMapUpdateByPrimaryKeySelectiveElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
