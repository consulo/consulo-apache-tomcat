/*
 * Copyright 2013-2017 consulo.io
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

package consulo.tomcat.run;

import consulo.annotation.component.ExtensionImpl;
import consulo.apache.tomcat.icon.ApacheTomcatIconGroup;
import consulo.content.bundle.Sdk;
import consulo.content.bundle.SdkTable;
import consulo.execution.configuration.RunConfiguration;
import consulo.execution.configuration.SimpleConfigurationType;
import consulo.execution.configuration.log.PredefinedLogFile;
import consulo.jakartaee.web.module.extension.JavaWebModuleExtension;
import consulo.localize.LocalizeValue;
import consulo.module.extension.ModuleExtensionHelper;
import consulo.project.Project;
import consulo.tomcat.sdk.TomcatSdkType;
import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 04.11.13.
 */
@ExtensionImpl
public class TomcatConfigurationType extends SimpleConfigurationType
{
	public static final PredefinedLogFile TOMCAT_LOCALHOST_LOG = new PredefinedLogFile("TOMCAT_LOCALHOST_LOG", true);

	public TomcatConfigurationType()
	{
		super("#TomcatConfigurationType", LocalizeValue.localizeTODO("Apache Tomcat"), LocalizeValue.localizeTODO("Apache Tomcat run configuration"), ApacheTomcatIconGroup.tomcat());
	}

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
