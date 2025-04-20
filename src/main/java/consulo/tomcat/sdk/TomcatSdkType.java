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

import consulo.annotation.component.ExtensionImpl;
import consulo.apache.tomcat.icon.ApacheTomcatIconGroup;
import consulo.application.Application;
import consulo.application.util.SystemInfo;
import consulo.content.bundle.SdkType;
import consulo.process.ExecutionException;
import consulo.process.cmd.GeneralCommandLine;
import consulo.process.util.CapturingProcessUtil;
import consulo.process.util.ProcessOutput;
import consulo.ui.image.Image;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * @author VISTALL
 * @since 04.11.13.
 */
@ExtensionImpl
public class TomcatSdkType extends SdkType {
    @NotNull
    public static TomcatSdkType getInstance() {
        return Application.get().getExtensionPoint(SdkType.class).findExtensionOrFail(TomcatSdkType.class);
    }

    private static final String VERSION_PREFIX = "Server number:";

    public TomcatSdkType() {
        super("APACHE_TOMCAT_SDK");
    }

    public static String getExecutablePath(String home) {
        StringBuilder builder = new StringBuilder();
        builder.append(home);
        builder.append(File.separator);
        builder.append("bin");
        builder.append(File.separator);
        builder.append("catalina");
        if (SystemInfo.isWindows) {
            builder.append(".bat");
        }
        else {
            builder.append(".sh");
        }
        return builder.toString();
    }

    @Override
    public boolean isValidSdkHome(String s) {
        return new File(getExecutablePath(s)).exists();
    }

    @Nullable
    @Override
    public String getVersionString(String home) {
        try {
            GeneralCommandLine commandLine = new GeneralCommandLine();
            commandLine.withExePath(getExecutablePath(home));
            commandLine.withParameters("version");

            ProcessOutput version = CapturingProcessUtil.execAndGetOutput(commandLine);
            for (String line : version.getStdoutLines()) {
                if (line.startsWith(VERSION_PREFIX)) {
                    return line.substring(VERSION_PREFIX.length(), line.length()).trim();
                }
            }
            return "Unknown";
        }
        catch (ExecutionException e) {
            return null;
        }
    }

    @Override
    public String suggestSdkName(String s, String s2) {
        return "apache-tomcat";
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "Apache Tomcat";
    }

    @Nonnull
    @Override
    public Image getIcon() {
        return ApacheTomcatIconGroup.tomcat();
    }
}
