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
import javax.swing.JLabel;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;

/**
 * @author VISTALL
 * @since 04.11.13.
 */
public class TomcatSettingsEditor extends SettingsEditor<TomcatConfiguration>
{
	@Override
	protected void resetEditorFrom(TomcatConfiguration tomcatConfiguration)
	{

	}

	@Override
	protected void applyEditorTo(TomcatConfiguration tomcatConfiguration) throws ConfigurationException
	{

	}

	@NotNull
	@Override
	protected JComponent createEditor()
	{
		return new JLabel("Test");
	}
}
