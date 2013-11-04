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

package org.mustbe.consulo.tomcat.sdk;

import java.io.File;
import java.util.Arrays;

import javax.swing.Icon;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.tomcat.TomcatIcons;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.execution.util.ExecUtil;
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.SystemInfo;

/**
 * @author VISTALL
 * @since 04.11.13.
 */
public class TomcatSdkType extends SdkType
{
	private static final String VERSION_PREFIX = "Server number:";

	public TomcatSdkType()
	{
		super("APACHE_TOMCAT_SDK");
	}

	@Nullable
	@Override
	public String suggestHomePath()
	{
		return null;
	}

	public static String getExecutablePath(String home)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(home);
		builder.append(File.separator);
		builder.append("bin");
		builder.append(File.separator);
		builder.append("catalina");
		if(SystemInfo.isWindows)
		{
			builder.append(".bat");
		}
		else
		{
			builder.append(".sh");
		}
		return builder.toString();
	}

	@Override
	public boolean isValidSdkHome(String s)
	{
		return new File(getExecutablePath(s)).exists();
	}

	@Nullable
	@Override
	public String getVersionString(String s)
	{
		try
		{
			ProcessOutput version = ExecUtil.execAndGetOutput(Arrays.asList(getExecutablePath(s), "version"), s);
			for(String line : version.getStdoutLines())
			{
				if(line.startsWith(VERSION_PREFIX))
				{
					return line.substring(VERSION_PREFIX.length(), line.length()).trim();
				}
			}
			return "Unknown";
		}
		catch(ExecutionException e)
		{
			return null;
		}
	}

	@Override
	public String suggestSdkName(String s, String s2)
	{
		return s2;
	}

	@Nullable
	@Override
	public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator)
	{
		return null;
	}

	@NotNull
	@Override
	public String getPresentableName()
	{
		return "Apache Tomcat";
	}

	@Override
	public void saveAdditionalData(SdkAdditionalData sdkAdditionalData, Element element)
	{

	}

	@Override
	public boolean isRootTypeApplicable(OrderRootType type)
	{
		return false;
	}

	@Nullable
	@Override
	public Icon getIcon()
	{
		return TomcatIcons.Tomcat;
	}

	@Nullable
	@Override
	public Icon getGroupIcon()
	{
		return TomcatIcons .Tomcat;
	}
}
