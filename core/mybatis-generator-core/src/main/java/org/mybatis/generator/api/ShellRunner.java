/**
 *    Copyright 2006-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.api;

import com.alibaba.fastjson.JSON;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.logging.LogFactory;

/**
 * This class allows the code generator to be run from the command line.
 * 
 * @author Jeff Butler
 */
public class ShellRunner {
    private static final String CONFIG_FILE = "-configfile"; //$NON-NLS-1$
    private static final String OVERWRITE = "-overwrite"; //$NON-NLS-1$
    private static final String CONTEXT_IDS = "-contextids"; //$NON-NLS-1$
    private static final String TABLES = "-tables"; //$NON-NLS-1$
    private static final String VERBOSE = "-verbose"; //$NON-NLS-1$
    private static final String FORCE_JAVA_LOGGING = "-forceJavaLogging"; //$NON-NLS-1$
    private static final String HELP_1 = "-?"; //$NON-NLS-1$
    private static final String HELP_2 = "-h"; //$NON-NLS-1$

    public static void main(String[] args) {
        //创建一个警告列表，整个MBG运行过程中的所有警告信息都放在这个列表中，执行完成后统一System.out
        List<String> warnings = new ArrayList<>();
        //得到generatorConfig.xml文件
        String configfile = "/Users/melody/Desktop/generatorConfig.xml";
        File configurationFile = new File(configfile);
        if (!configurationFile.exists()) {
            writeLine(getString("RuntimeError.1", configfile)); //$NON-NLS-1$
            return;
        }
        //如果参数有tables，得到table名称列表
        Set<String> fullyqualifiedTables = new HashSet<>();

        Set<String> contexts = new HashSet<>();
        try {
            //创建配置解析器
            ConfigurationParser cp = new ConfigurationParser(warnings);
            //将xml配置load到Configuration中
            Configuration config = cp.parseConfiguration(configurationFile);
            System.out.println(JSON.toJSONString(config));
            //创建一个默认的ShellCallback对象，之前说过，shellcallback接口主要用来处理文件的创建和合并，传入overwrite参数；默认的shellcallback是不支持文件合并的；
            DefaultShellCallback shellCallback = new DefaultShellCallback(false);
            //创建一个MyBatisGenerator对象。MyBatisGenerator类是真正用来执行生成动作的类
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, shellCallback, warnings);
            //创建一个默认的ProgressCallback对象，之前说过，在MBG执行过程中在一定的执行步骤结束后调用ProgressCallback对象的方法，达到执行过程监控的效果；
            ProgressCallback progressCallback = null;

            //执行真正的MBG创建过程
            myBatisGenerator.generate(progressCallback, contexts, fullyqualifiedTables);
        } catch (XMLParserException e) {
            writeLine(getString("Progress.3")); //$NON-NLS-1$
            writeLine();
            for (String error : e.getErrors()) {
                writeLine(error);
            }

            return;
        } catch (SQLException | IOException e) {
            e.printStackTrace(System.out);
            return;
        } catch (InvalidConfigurationException e) {
            writeLine(getString("Progress.16")); //$NON-NLS-1$
            for (String error : e.getErrors()) {
                writeLine(error);
            }
            return;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        //输出警告信息
        for (String warning : warnings) {
            writeLine(warning);
        }

        if (warnings.isEmpty()) {
            writeLine(getString("Progress.4")); //$NON-NLS-1$
        } else {
            writeLine();
            writeLine(getString("Progress.5")); //$NON-NLS-1$
        }

    }

    private static void usage() {
        writeLine(getString("Usage")); //$NON-NLS-1$
    }

    private static void writeLine(String message) {
        System.out.println(message);
    }

    private static void writeLine() {
        System.out.println();
    }

    private static Map<String, String> parseCommandLine(String[] args) {
        List<String> errors = new ArrayList<>();
        Map<String, String> arguments = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            if (CONFIG_FILE.equalsIgnoreCase(args[i])) {
                if ((i + 1) < args.length) {
                    arguments.put(CONFIG_FILE, args[i + 1]);
                } else {
                    errors.add(getString(
                            "RuntimeError.19", CONFIG_FILE)); //$NON-NLS-1$
                }
                i++;
            } else if (OVERWRITE.equalsIgnoreCase(args[i])) {
                arguments.put(OVERWRITE, "Y"); //$NON-NLS-1$
            } else if (VERBOSE.equalsIgnoreCase(args[i])) {
                arguments.put(VERBOSE, "Y"); //$NON-NLS-1$
            } else if (HELP_1.equalsIgnoreCase(args[i])) {
                arguments.put(HELP_1, "Y"); //$NON-NLS-1$
            } else if (HELP_2.equalsIgnoreCase(args[i])) {
                // put HELP_1 in the map here too - so we only
                // have to check for one entry in the mainline
                arguments.put(HELP_1, "Y"); //$NON-NLS-1$
            } else if (FORCE_JAVA_LOGGING.equalsIgnoreCase(args[i])) {
                LogFactory.forceJavaLogging();
            } else if (CONTEXT_IDS.equalsIgnoreCase(args[i])) {
                if ((i + 1) < args.length) {
                    arguments.put(CONTEXT_IDS, args[i + 1]);
                } else {
                    errors.add(getString(
                            "RuntimeError.19", CONTEXT_IDS)); //$NON-NLS-1$
                }
                i++;
            } else if (TABLES.equalsIgnoreCase(args[i])) {
                if ((i + 1) < args.length) {
                    arguments.put(TABLES, args[i + 1]);
                } else {
                    errors.add(getString("RuntimeError.19", TABLES)); //$NON-NLS-1$
                }
                i++;
            } else {
                errors.add(getString("RuntimeError.20", args[i])); //$NON-NLS-1$
            }
        }

        if (!errors.isEmpty()) {
            for (String error : errors) {
                writeLine(error);
            }

            System.exit(-1);
        }

        return arguments;
    }
}
