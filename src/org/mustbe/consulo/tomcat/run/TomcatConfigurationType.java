/*
 * Copyright 2013 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mustbe.consulo.tomcat.run;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.java.web.module.extension.JavaWebModuleExtension;
import org.mustbe.consulo.module.extension.ModuleExtensionHelper;
import org.mustbe.consulo.tomcat.TomcatIcons;
import org.mustbe.consulo.tomcat.sdk.TomcatSdkType;
import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.PredefinedLogFile;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTable;

/**
 * @author VISTALL
 * @since 04.11.13.
 */
public class TomcatConfigurationType implements ConfigurationType
{
	public static final PredefinedLogFile TOMCAT_LOCALHOST_LOG = new PredefinedLogFile("TOMCAT_LOCALHOST_LOG", true);

	private final ConfigurationFactory[] myFactories = new ConfigurationFactory[]{
			new ConfigurationFactoryEx(this)
			{
				@Override
				public RunConfiguration createTemplateConfiguration(Project project)
				{
					return new TomcatConfiguration(project, this, "Unnamed");
				}

				@Override
				public boolean isApplicable(@NotNull Project project)
				{
					return ModuleExtensionHelper.getInstance(project).hasModuleExtension(JavaWebModuleExtension.class);
				}

				@Override
				public void onNewConfigurationCreated(@NotNull RunConfiguration configuration)
				{
					TomcatConfiguration tomcatConfiguration = (TomcatConfiguration) configuration;
					Sdk mostRecentSdkOfType = SdkTable.getInstance().findMostRecentSdkOfType(TomcatSdkType.getInstance());
					if(mostRecentSdkOfType != null)
					{
						tomcatConfiguration.setSdkName(mostRecentSdkOfType.getName());
					}

					tomcatConfiguration.addPredefinedLogFile(TOMCAT_LOCALHOST_LOG);
				}
			}
	};

	@Override
	public String getDisplayName()
	{
		return "Apache Tomcat";
	}

	@Override
	public String getConfigurationTypeDescription()
	{
		return "Apache Tomcat run configuration";
	}

	@Override
	public Icon getIcon()
	{
		return TomcatIcons.Tomcat;
	}

	@NotNull
	@Override
	public String getId()
	{
		return "#TomcatConfigurationType";
	}

	@Override
	public ConfigurationFactory[] getConfigurationFactories()
	{
		return myFactories;
	}
}
