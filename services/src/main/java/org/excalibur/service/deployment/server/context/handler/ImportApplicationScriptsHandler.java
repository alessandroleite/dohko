/**
 *     Copyright (C) 2013-2014  the original author or authors.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License,
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.excalibur.service.deployment.server.context.handler;

import static com.google.common.collect.Lists.newArrayList;
import static org.excalibur.core.cloud.api.Platform.LINUX;
import static org.excalibur.core.util.YesNoEnum.YES;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.servlet.ServletContextEvent;

import org.excalibur.core.domain.User;
import org.excalibur.core.execution.domain.ApplicationExecDescription;
import org.excalibur.core.execution.domain.ScriptStatement;
import org.excalibur.core.execution.domain.repository.ApplicationDescriptionRepository;
import org.excalibur.core.execution.domain.repository.ScriptStatementRepository;
import org.excalibur.core.io.utils.IOUtils2;
import org.excalibur.service.manager.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;

import com.google.common.base.Predicate;

public class ImportApplicationScriptsHandler extends AbstractApplicationInitializedHandler
{

    @Override
    public void handlerApplicationInitializedEvent(Configuration configuration, ApplicationContext context, ServletContextEvent sce)
    {
        Reflections reflections = new Reflections(new ConfigurationBuilder().addUrls(ClasspathHelper.forPackage("org.excalibur")).addScanners(
                new ResourcesScanner()));

        List<String> scripts = newArrayList(reflections.getResources(new Predicate<String>()
        {
            @Override
            public boolean apply(@Nullable String input)
            {
                return input != null && input.endsWith(".sh");
            }
        }));

        Collections.sort(scripts);
        ScriptStatementRepository scriptStatementRepository = context.getBean(ScriptStatementRepository.class);
        ApplicationDescriptionRepository applicationDescriptionRepository = context.getBean(ApplicationDescriptionRepository.class);

        if (scriptStatementRepository.listActiveStatements().isEmpty())
        {
            for (String script : scripts)
            {
                try
                {
                    String content = IOUtils2.readLines(ClassUtils.getDefaultClassLoader().getResourceAsStream(script));
                    String[] parts = script.split(File.separator);
                    String name = parts[parts.length - 1].substring(0, parts[parts.length - 1].indexOf('.'));

                    ScriptStatement sh = new ScriptStatement().setActive(YES).setName(name).setPlatform(LINUX).setStatement(content);
                    ApplicationExecDescription description = new ApplicationExecDescription().setApplication(sh).setName(sh.getName())
                            .setResource("all-instance-types").setUser(new User(1));

                    sh.setId(scriptStatementRepository.insertScriptStatement(sh));
                    applicationDescriptionRepository.insert(description);

                    // HashFunction.getHashFunction().digest(script);
                }
                catch (IOException e)
                {
                }
            }
        }
    }
}
