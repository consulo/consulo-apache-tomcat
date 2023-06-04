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

import com.intellij.java.debugger.impl.GenericDebuggerRunner;
import com.intellij.java.execution.configurations.RemoteConnection;
import consulo.annotation.component.ExtensionImpl;
import consulo.execution.configuration.RunProfile;
import consulo.execution.configuration.RunProfileState;
import consulo.execution.debug.DefaultDebugExecutor;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.execution.ui.RunContentDescriptor;
import consulo.process.ExecutionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author VISTALL
 * @since 04.11.13.
 */
@ExtensionImpl(order = "before defaultDebugRunner")
public class TomcatDebugProgramRunner extends GenericDebuggerRunner
{
	@NotNull
	@Override
	public String getRunnerId()
	{
		return "Tomcat";
	}

	@Override
	public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile)
	{
		return profile instanceof TomcatConfiguration && DefaultDebugExecutor.EXECUTOR_ID.equals(executorId);
	}

	@Nullable
	@Override
	protected RunContentDescriptor createContentDescriptor(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException
	{
		TomcatConfiguration runProfile = (TomcatConfiguration) env.getRunProfile();
		RemoteConnection connection = new RemoteConnection(true, "127.0.0.1", String.valueOf(runProfile.JPDA_ADDRESS), false);
		return attachVirtualMachine(state, env, connection, true);
	}
}
