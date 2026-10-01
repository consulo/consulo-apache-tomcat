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

import consulo.apache.tomcat.localize.TomcatLocalize;
import consulo.configurable.ConfigurationException;
import consulo.execution.configuration.ui.SettingsEditor;
import consulo.module.ui.BundleBox;
import consulo.module.ui.BundleBoxBuilder;
import consulo.tomcat.sdk.TomcatSdkType;
import consulo.ui.Component;
import consulo.ui.IntBox;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.util.FormBuilder;
import jakarta.annotation.Nullable;

import java.util.Objects;

/**
 * @author VISTALL
 * @since 04.11.13.
 */
public class TomcatGeneralSettingsEditor extends SettingsEditor<TomcatConfiguration>
{
	private static final int MAX_PORT = 65535;

	@Nullable
	private BundleBox myBundleBox;
	@Nullable
	private IntBox myJpdaPortBox;

	@Override
	@RequiredUIAccess
	protected Component createUIComponent()
	{
		BundleBox bundleBox = BundleBoxBuilder.create(this)
				.withSdkTypeFilterByType(TomcatSdkType.getInstance())
				.withNoneItem()
				.build();
		myBundleBox = bundleBox;

		IntBox jpdaPortBox = IntBox.create(TomcatConfiguration.DEFAULT_JPDA_ADDRESS).withRange(0, MAX_PORT);
		myJpdaPortBox = jpdaPortBox;

		return FormBuilder.create()
				.addLabeled(TomcatLocalize.runConfigurationLabelBundle(), bundleBox.getComponent())
				.addLabeled(TomcatLocalize.runConfigurationLabelJpdaPort(), jpdaPortBox)
				.build();
	}

	@Override
	@RequiredUIAccess
	protected void resetEditorFrom(TomcatConfiguration tomcatConfiguration)
	{
		BundleBox bundleBox = myBundleBox;
		IntBox jpdaPortBox = myJpdaPortBox;
		if(bundleBox == null || jpdaPortBox == null)
		{
			return;
		}

		jpdaPortBox.setValue(tomcatConfiguration.JPDA_ADDRESS, false);
		bundleBox.setSelectedBundle(tomcatConfiguration.getSdkName());
	}

	@Override
	@RequiredUIAccess
	protected void applyEditorTo(TomcatConfiguration tomcatConfiguration) throws ConfigurationException
	{
		BundleBox bundleBox = myBundleBox;
		IntBox jpdaPortBox = myJpdaPortBox;
		if(bundleBox == null || jpdaPortBox == null)
		{
			return;
		}

		Integer jpdaPort = jpdaPortBox.getValue();
		tomcatConfiguration.JPDA_ADDRESS = jpdaPort == null ? 0 : jpdaPort;

		String sdkName = bundleBox.getSelectedBundleName();
		if(!Objects.equals(sdkName, tomcatConfiguration.getSdkName()))
		{
			tomcatConfiguration.setSdkName(sdkName);
		}
	}
}
