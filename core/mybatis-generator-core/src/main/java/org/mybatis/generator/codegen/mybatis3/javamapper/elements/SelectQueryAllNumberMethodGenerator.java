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
package org.mybatis.generator.codegen.mybatis3.javamapper.elements;

import org.mybatis.generator.api.dom.java.*;

import java.util.Set;
import java.util.TreeSet;

public class SelectQueryAllNumberMethodGenerator extends
        AbstractJavaMapperMethodGenerator {

    private boolean isSimple;

    public SelectQueryAllNumberMethodGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }

    @Override
    public void addInterfaceElements(Interface interfaze) {//这里
        Method method = new Method("queryAllNumber");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setAbstract(true);

        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("Long");
        method.setReturnType(returnType);

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
        importedTypes.add(returnType);
        String baseRecordType = introspectedTable.getBaseRecordType();
        Parameter parameter = new Parameter(new FullyQualifiedJavaType(baseRecordType), "record");
        method.addParameter(parameter);
        addMapperAnnotations(interfaze, method);

        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        if (context.getPlugins().clientSelectByPrimaryKeyMethodGenerated(
                method, interfaze, introspectedTable)) {
            addExtraImports(interfaze);
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }

    public void addMapperAnnotations(Interface interfaze, Method method) {
        // extension point for subclasses
    }

    public void addExtraImports(Interface interfaze) {
        // extension point for subclasses
    }
}

