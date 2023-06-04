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

import consulo.configurable.ConfigurationException;
import consulo.content.bundle.SdkModel;
import consulo.execution.configuration.ui.SettingsEditor;
import consulo.ide.setting.ShowSettingsUtil;
import consulo.module.ui.awt.SdkComboBox;
import consulo.project.Project;
import consulo.tomcat.sdk.TomcatSdkType;
import consulo.ui.ex.awt.IntegerField;
import consulo.util.lang.StringUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author VISTALL
 * @since 04.11.13.
 */
public class TomcatGeneralSettingsEditor extends SettingsEditor<TomcatConfiguration>
{
	private JPanel myRoot;
	private SdkComboBox myBundleList;
	private JTextField myJpdaPort;

	private Project myProject;

	public TomcatGeneralSettingsEditor(Project project)
	{
		myProject = project;
	}

	@Override
	protected void resetEditorFrom(TomcatConfiguration tomcatConfiguration)
	{
		myJpdaPort.setText(String.valueOf(tomcatConfiguration.JPDA_ADDRESS));
		myBundleList.setSelectedSdk(tomcatConfiguration.getSdkName());
	}

	@Override
	protected void applyEditorTo(TomcatConfiguration tomcatConfiguration) throws ConfigurationException
	{
		tomcatConfiguration.JPDA_ADDRESS = StringUtil.parseInt(myJpdaPort.getText(), 0);
		tomcatConfiguration.setSdkName(myBundleList.getSelectedSdkName());
	}

	@NotNull
	@Override
	protected JComponent createEditor()
	{
		return myRoot;
	}

	private void createUIComponents()
	{
		SdkModel model = ShowSettingsUtil.getInstance().getSdksModel();

		myBundleList = new SdkComboBox(model, sdkTypeId -> sdkTypeId == TomcatSdkType.getInstance(), true);
		myJpdaPort = new IntegerField();
	}
}
