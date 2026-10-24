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

import consulo.compiler.artifact.ArtifactPointerManager;
import consulo.compiler.artifact.ArtifactPointerUtil;
import consulo.container.boot.ContainerPathManager;
import consulo.content.bundle.Sdk;
import consulo.content.bundle.SdkUtil;
import consulo.execution.configuration.*;
import consulo.execution.configuration.log.LogFileOptions;
import consulo.execution.configuration.log.PredefinedLogFile;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.execution.executor.Executor;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.process.ExecutionException;
import consulo.project.Project;
import consulo.util.io.FileUtil;
import consulo.util.lang.StringUtil;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jdom.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * @author VISTALL
 * @since 04.11.13.
 */
public class TomcatConfiguration extends LocatableConfigurationBase implements ModuleRunConfiguration {
    public static final int DEFAULT_JPDA_ADDRESS = 8000;
public TomcatConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @Override
    protected Class<TomcatConfigurationOptions> getOptionsClass() {
        return TomcatConfigurationOptions.class;
    }

    @Override
    public TomcatConfigurationOptions getOptions() {
        return (TomcatConfigurationOptions) super.getOptions();
    }

    @Override
    protected void convertLegacyState(LegacyRunConfigurationConverter converter) {
        super.convertLegacyState(converter);

        Element element = converter.getElement();
        TomcatConfigurationOptions options = new TomcatConfigurationOptions();
        options.setJpdaAddress(StringUtil.parseInt(element.getAttributeValue("jpda-address"), DEFAULT_JPDA_ADDRESS));
        options.setSdkName(element.getAttributeValue("sdk-name"));
        List<TomcatArtifactDeployState> artifacts = new ArrayList<>();
        for (Element artifactElement : element.getChildren("artifact")) {
            String name = artifactElement.getAttributeValue("name");
            if (name != null) {
                artifacts.add(new TomcatArtifactDeployState(name, StringUtil.notNullize(artifactElement.getAttributeValue("path"))));
            }
        }
        options.setArtifacts(artifacts);
        converter.setOptions(options);
    }

    @Override
    public List<RunConfigurationFragment<?>> createFragments() {
        return List.of(new TomcatBundleFragment(), new TomcatJpdaPortFragment(), new TomcatDeploymentFragment());
    }

    @Nullable
    @Override
    public LogFileOptions getOptionsForPredefinedLogFile(PredefinedLogFile predefinedLogFile) {
        if (Objects.equals(predefinedLogFile, TomcatConfigurationType.TOMCAT_LOCALHOST_LOG)) {
            Calendar calendar = Calendar.getInstance();

            StringBuilder builder = new StringBuilder();
            builder.append(ContainerPathManager.get().getSystemPath());
            builder.append("/apache-tomcat/");
            builder.append(getName());
            builder.append("_");
            builder.append(getProject().getName());
            builder.append("/logs/localhost.");
            builder.append(calendar.get(Calendar.YEAR));
            builder.append("-");
            builder.append(calendar.get(Calendar.MONTH) + 1);
            builder.append("-");
            builder.append(calendar.get(Calendar.DAY_OF_MONTH));
            builder.append(".log");

            File filePath = new File(builder.toString());
            if (!filePath.exists()) {
                try {
                    // write empty data
                    FileUtil.writeToFile(filePath, "");
                }
                catch (IOException ignored) {
                }
            }

            return new LogFileOptions("localhost.log", builder.toString(), true, true, false);
        }
        return super.getOptionsForPredefinedLogFile(predefinedLogFile);
    }

    @Nullable
    @Override
    public RunProfileState getState(@Nonnull Executor executor, @Nonnull final ExecutionEnvironment executionEnvironment) throws ExecutionException {
        return new TomcatRunState(executionEnvironment);
    }

    @Nullable
    public Sdk getSdk() {
        String sdkName = getSdkName();
        return sdkName == null ? null : SdkUtil.createPointer(sdkName).get();
    }

    @Nullable
    public String getSdkName() {
        return getOptions().getSdkName();
    }

    public void setSdkName(@Nullable String sdkName) {
        getOptions().setSdkName(sdkName);
    }

    public int getJpdaAddress() {
        return getOptions().getJpdaAddress();
    }

    public void setJpdaAddress(int jpdaAddress) {
        getOptions().setJpdaAddress(jpdaAddress);
    }

    public List<TomcatArtifactDeployItem> getDeploymentItems() {
        ArtifactPointerManager pointerManager = ArtifactPointerUtil.getPointerManager(getProject());
        List<TomcatArtifactDeployItem> items = new ArrayList<>();
        for (TomcatArtifactDeployState artifact : getOptions().getArtifacts()) {
            String name = artifact.getName();
            if (name != null) {
                items.add(new TomcatArtifactDeployItem(pointerManager.create(name), StringUtil.notNullize(artifact.getPath())));
            }
        }
        return items;
    }

    public void setDeploymentItems(List<TomcatArtifactDeployItem> items) {
        List<TomcatArtifactDeployState> artifacts = new ArrayList<>();
        for (TomcatArtifactDeployItem item : items) {
            artifacts.add(new TomcatArtifactDeployState(item.getArtifactPointer().getName(), item.getPath()));
        }
        getOptions().setArtifacts(artifacts);
    }

    @Nonnull
    @Override
    public Module[] getModules() {
        return ModuleManager.getInstance(getProject()).getModules();
    }
}
