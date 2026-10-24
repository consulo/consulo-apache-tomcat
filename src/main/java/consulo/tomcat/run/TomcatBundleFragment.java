/*
 * Copyright 2013-2026 consulo.io
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
import consulo.disposer.Disposable;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.execution.configuration.fragment.RunConfigurationFragmentEditor;
import consulo.execution.configuration.fragment.RunConfigurationFragmentLayout;
import consulo.execution.configuration.fragment.StandardRunConfigurationFragmentOrder;
import consulo.localize.LocalizeValue;
import consulo.module.ui.BundleBox;
import consulo.module.ui.BundleBoxBuilder;
import consulo.project.Project;
import consulo.tomcat.sdk.TomcatSdkType;
import consulo.ui.annotation.RequiredUIAccess;

public class TomcatBundleFragment implements RunConfigurationFragment<TomcatConfiguration> {
    @Override
    public String getId() {
        return "tomcatBundle";
    }

    @Override
    public LocalizeValue getName() {
        return TomcatLocalize.runConfigurationTabGeneral();
    }

    @Override
    public int getOrder() {
        return StandardRunConfigurationFragmentOrder.FIRST;
    }

    @Override
    public boolean isRemovable() {
        return false;
    }

    @RequiredUIAccess
    @Override
    public RunConfigurationFragmentEditor<TomcatConfiguration> createEditor(Project project, Disposable parent) {
        BundleBox bundle = BundleBoxBuilder.create(parent)
            .withSdkTypeFilterByType(TomcatSdkType.getInstance())
            .withNoneItem()
            .build();

        return new RunConfigurationFragmentEditor<>() {
            @RequiredUIAccess
            @Override
            public void addRows(RunConfigurationFragmentLayout layout) {
                layout.addLabeled(TomcatLocalize.runConfigurationLabelBundle(), bundle.getComponent());
            }

            @RequiredUIAccess
            @Override
            public void reset(TomcatConfiguration target) {
                bundle.setSelectedBundle(target.getSdkName());
            }

            @RequiredUIAccess
            @Override
            public void apply(TomcatConfiguration target) {
                target.setSdkName(bundle.getSelectedBundleName());
            }
        };
    }
}
