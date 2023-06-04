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

import com.intellij.java.execution.configurations.DebuggingRunnerData;
import consulo.compiler.artifact.Artifact;
import consulo.container.boot.ContainerPathManager;
import consulo.content.bundle.Sdk;
import consulo.execution.DefaultExecutionResult;
import consulo.execution.ExecutionResult;
import consulo.execution.configuration.RunProfileState;
import consulo.execution.executor.Executor;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.execution.runner.ProgramRunner;
import consulo.execution.ui.console.ConsoleView;
import consulo.execution.ui.console.TextConsoleBuilder;
import consulo.execution.ui.console.TextConsoleBuilderFactory;
import consulo.process.ExecutionException;
import consulo.process.ProcessHandler;
import consulo.process.ProcessHandlerBuilder;
import consulo.process.cmd.GeneralCommandLine;
import consulo.tomcat.sdk.TomcatSdkType;
import consulo.util.io.FilePermissionCopier;
import consulo.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * @author VISTALL
 * @since 04.11.13.
 */
public class TomcatRunState implements RunProfileState
{
	private ExecutionEnvironment myExecutionEnvironment;

	public TomcatRunState(ExecutionEnvironment executionEnvironment)
	{
		myExecutionEnvironment = executionEnvironment;
	}

	@Nullable
	@Override
	public ExecutionResult execute(Executor executor, @NotNull ProgramRunner programRunner) throws ExecutionException
	{
		TomcatConfiguration runProfile = (TomcatConfiguration) myExecutionEnvironment.getRunProfile();
		Sdk sdk = runProfile.getSdk();
		if(sdk == null)
		{
			throw new ExecutionException("No apache tomcat not set");
		}

		File tomcatBaseHome = new File(ContainerPathManager.get().getSystemPath(), "apache-tomcat/" + runProfile.getName() + "_" + myExecutionEnvironment.getProject().getName());

		preparingDeploy(tomcatBaseHome, runProfile);

		GeneralCommandLine commandLine = new GeneralCommandLine();
		commandLine.setExePath(TomcatSdkType.getExecutablePath(sdk.getHomePath()));
		if(executor.getId().equals(DebuggingRunnerData.DEBUGGER_RUNNER_ID))
		{
			commandLine.getEnvironment().put("JPDA_ADDRESS", String.valueOf(runProfile.JPDA_ADDRESS));
			commandLine.addParameter("jpda");
		}

		commandLine.getEnvironment().put("CATALINA_BASE", FileUtil.toSystemIndependentName(tomcatBaseHome.getAbsolutePath()));
		commandLine.addParameter("run");

		commandLine.setWorkDirectory(sdk.getHomePath());

		ProcessHandler processHandler = ProcessHandlerBuilder.create(commandLine).build();

		TextConsoleBuilder builder = TextConsoleBuilderFactory.getInstance().createBuilder(myExecutionEnvironment.getProject());
		ConsoleView console = builder.getConsole();
		console.attachToProcess(processHandler);

		return new DefaultExecutionResult(console, processHandler);
	}

	private static void preparingDeploy(File tomcatBaseHome, TomcatConfiguration configuration) throws ExecutionException
	{
		Sdk sdk = configuration.getSdk();

		assert sdk != null;

		if(tomcatBaseHome.exists())
		{
			FileUtil.delete(tomcatBaseHome);
		}

		FileUtil.createDirectory(tomcatBaseHome);
		FileUtil.createDirectory(new File(tomcatBaseHome, "temp"));

		try
		{
			FileUtil.copyDir(new File(sdk.getHomePath(), "conf"), new File(tomcatBaseHome, "conf"), FilePermissionCopier.BY_NIO2);

			File webAppsDir = new File(tomcatBaseHome, "webapps");
			for(TomcatArtifactDeployItem artifactDeployItem : configuration.getDeploymentItems())
			{
				Artifact artifact = artifactDeployItem.getArtifactPointer().get();
				if(artifact == null)
				{
					continue;
				}

				File outDir = null;
				String path = artifactDeployItem.getPath();
				if(path.equals("/"))
				{
					outDir = new File(webAppsDir, "ROOT");
				}
				else
				{
					outDir = new File(webAppsDir, path);
				}

				FileUtil.copyDir(new File(artifact.getOutputPath()), outDir, FilePermissionCopier.BY_NIO2);
			}
		}
		catch(IOException e)
		{
			throw new ExecutionException(e);
		}
	}
}
