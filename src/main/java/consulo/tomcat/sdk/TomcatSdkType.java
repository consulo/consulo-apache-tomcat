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

package consulo.tomcat.sdk;

import java.io.File;
import java.util.Arrays;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.tomcat.TomcatIcons;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.execution.util.ExecUtil;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.util.SystemInfo;
import consulo.ui.image.Image;

/**
 * @author VISTALL
 * @since 04.11.13.
 */
public class TomcatSdkType extends SdkType
{
	@NotNull
	public static TomcatSdkType getInstance()
	{
		return EP_NAME.findExtension(TomcatSdkType.class);
	}

	private static final String VERSION_PREFIX = "Server number:";

	public TomcatSdkType()
	{
		super("APACHE_TOMCAT_SDK");
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
		return "apache-tomcat";
	}

	@NotNull
	@Override
	public String getPresentableName()
	{
		return "Apache Tomcat";
	}

	@Nullable
	@Override
	public Image getIcon()
	{
		return TomcatIcons.Tomcat;
	}
}
