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

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.tomcat.sdk.TomcatSdkType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ui.configuration.SdkComboBox;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.text.StringUtilRt;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.NumberDocument;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;

/**
 * @author VISTALL
 * @since 04.11.13.
 */
public class TomcatSettingsEditor extends SettingsEditor<TomcatConfiguration>
{
	private JPanel myRoot;
	private SdkComboBox myBundleList;
	private JTextField myJpdaPort;
	private JPanel myDeploymentPane;
	private Project myProject;

	public TomcatSettingsEditor(Project project)
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
		tomcatConfiguration.JPDA_ADDRESS = StringUtilRt.parseInt(myJpdaPort.getText(), 0);
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
		ProjectSdksModel model = new ProjectSdksModel();
		if(!model.isInitialized())
		{
			model.reset(myProject);
		}

		myBundleList = new SdkComboBox(model, new Condition<SdkTypeId>()
		{
			@Override
			public boolean value(SdkTypeId sdkTypeId)
			{
				return sdkTypeId == TomcatSdkType.getInstance();
			}
		}, false);
		myJpdaPort = new JTextField();
		myJpdaPort.setDocument(new NumberDocument());

		JBList list = new JBList();

		ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(list);
		toolbarDecorator.setAddAction(new AnActionButtonRunnable()
		{
			@Override
			public void run(AnActionButton anActionButton)
			{

			}
		});
		toolbarDecorator.disableUpDownActions();

		myDeploymentPane = toolbarDecorator.createPanel();
	}
}
