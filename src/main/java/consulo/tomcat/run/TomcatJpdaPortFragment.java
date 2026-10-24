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
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.ui.IntBox;
import consulo.ui.annotation.RequiredUIAccess;

/**
 * The port the debugger connects to; shown when it differs from {@link TomcatConfiguration#DEFAULT_JPDA_ADDRESS}.
 */
public class TomcatJpdaPortFragment implements RunConfigurationFragment<TomcatConfiguration> {
    private static final int MAX_PORT = 65535;

    @Override
    public String getId() {
        return "tomcatJpdaPort";
    }

    @Override
    public LocalizeValue getName() {
        return TomcatLocalize.runConfigurationFragmentJpdaPort();
    }

    @Override
    public boolean isVisibleByDefault(TomcatConfiguration target) {
        return target.getJpdaAddress() != TomcatConfiguration.DEFAULT_JPDA_ADDRESS;
    }

    @Override
    public void applyHidden(TomcatConfiguration target) {
        target.setJpdaAddress(TomcatConfiguration.DEFAULT_JPDA_ADDRESS);
    }

    @RequiredUIAccess
    @Override
    public RunConfigurationFragmentEditor<TomcatConfiguration> createEditor(Project project, Disposable parent) {
        IntBox port = IntBox.create(TomcatConfiguration.DEFAULT_JPDA_ADDRESS).withRange(0, MAX_PORT);

        return new RunConfigurationFragmentEditor<>() {
            @RequiredUIAccess
            @Override
            public void addRows(RunConfigurationFragmentLayout layout) {
                layout.addLabeled(TomcatLocalize.runConfigurationLabelJpdaPort(), port);
            }

            @RequiredUIAccess
            @Override
            public void reset(TomcatConfiguration target) {
                port.setValue(target.getJpdaAddress(), false);
            }

            @RequiredUIAccess
            @Override
            public void apply(TomcatConfiguration target) {
                Integer value = port.getValue();
                target.setJpdaAddress(value == null ? TomcatConfiguration.DEFAULT_JPDA_ADDRESS : value);
            }
        };
    }
}
