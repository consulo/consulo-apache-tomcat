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

import consulo.apache.tomcat.localize.TomcatLocalize;
import consulo.compiler.artifact.Artifact;
import consulo.compiler.artifact.ArtifactManager;
import consulo.compiler.artifact.ArtifactPointer;
import consulo.compiler.artifact.ArtifactPointerManager;
import consulo.execution.configuration.fragment.RunConfigurationFragmentEditor;
import consulo.execution.configuration.fragment.RunConfigurationFragmentLayout;
import consulo.javaee.artifact.ExplodedWarArtifactType;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.project.Project;
import consulo.ui.Component;
import consulo.ui.Table;
import consulo.ui.TableItemEditor;
import consulo.ui.TextAttribute;
import consulo.ui.TextBox;
import consulo.ui.TextItemPresentation;
import consulo.ui.ValueComponent;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.popup.JBPopupFactory;
import consulo.ui.ex.popup.MultiSelectionListPopupStep;
import consulo.ui.ex.popup.PopupStep;
import consulo.ui.ex.toolbar.AddAction;
import consulo.ui.ex.toolbar.DownMoveAction;
import consulo.ui.ex.toolbar.EditAction;
import consulo.ui.ex.toolbar.ToolbarDecoratorBuilderFactory;
import consulo.ui.ex.toolbar.UpMoveAction;
import consulo.ui.image.Image;
import consulo.ui.model.FlatDataModel;
import consulo.ui.model.MutableFlatDataModel;
import consulo.util.lang.StringUtil;
import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author VISTALL
 * @since 2013-11-04
 */
final class TomcatDeploymentFragmentEditor implements RunConfigurationFragmentEditor<TomcatConfiguration> {
    private final Project myProject;

    private final MutableFlatDataModel<TomcatArtifactDeployItem> myItems = FlatDataModel.of(List.of());

    private final Table<TomcatArtifactDeployItem> myTable;
    private final Component myComponent;

    @RequiredUIAccess
    TomcatDeploymentFragmentEditor(Project project) {
        myProject = project;
        Table<TomcatArtifactDeployItem> table = Table.create(myItems);
        table.setAllowMultipleSelect(true);

        table.addColumn(TomcatLocalize.runConfigurationColumnArtifact(), item -> item)
            .setRenderer((presentation, value) -> renderArtifact(presentation, value.getValue()));

        table.addColumn(TomcatLocalize.runConfigurationColumnPath(), TomcatArtifactDeployItem::getPath)
            .setEditor(new TableItemEditor<>() {
                @Override
                @RequiredUIAccess
                public ValueComponent<String> createComponent(TomcatArtifactDeployItem item) {
                    return TextBox.create(StringUtil.notNullize(item.getPath()));
                }

                @Override
                @RequiredUIAccess
                public void commit(TomcatArtifactDeployItem item, @Nullable String value) {
                    item.setPath(StringUtil.notNullize(value));
                }
            });
        myTable = table;

        myComponent = ToolbarDecoratorBuilderFactory.getInstance()
            .create(table)
            .addOrReplaceAction(new DeployItemAddAction())
            .disableAction(EditAction.class)
            .disableAction(UpMoveAction.class)
            .disableAction(DownMoveAction.class)
            .build();
    }

    @RequiredUIAccess
    private static void renderArtifact(TextItemPresentation presentation, @Nullable TomcatArtifactDeployItem item) {
        if (item == null) {
            return;
        }

        ArtifactPointer artifactPointer = item.getArtifactPointer();
        Artifact artifact = artifactPointer.get();
        if (artifact != null) {
            presentation.withIcon(artifact.getArtifactType().getIcon());
            presentation.append(artifact.getName());
        }
        else {
            presentation.withIcon(PlatformIconGroup.toolbarUnknown());
            presentation.append(artifactPointer.getName(), TextAttribute.ERROR);
        }
    }

    @RequiredUIAccess
    @Override
    public void addRows(RunConfigurationFragmentLayout layout) {
        layout.addBottom(myComponent);
    }

    @Override
    @RequiredUIAccess
    public void reset(TomcatConfiguration tomcatConfiguration) {
        List<TomcatArtifactDeployItem> items = new ArrayList<>();
        for (TomcatArtifactDeployItem item : tomcatConfiguration.getDeploymentItems()) {
            items.add(item.clone());
        }
        myItems.replaceAll(items);
    }

    @Override
    @RequiredUIAccess
    public void apply(TomcatConfiguration tomcatConfiguration) {
        List<TomcatArtifactDeployItem> deploymentItems = new ArrayList<>();
        for (TomcatArtifactDeployItem item : myItems) {
            deploymentItems.add(item.clone());
        }
        tomcatConfiguration.setDeploymentItems(deploymentItems);
    }

    private List<Artifact> collectDeployableArtifacts() {
        Set<Artifact> deployed = new HashSet<>();
        for (TomcatArtifactDeployItem item : myItems) {
            Artifact artifact = item.getArtifactPointer().get();
            if (artifact != null) {
                deployed.add(artifact);
            }
        }

        List<Artifact> artifacts = new ArrayList<>();
        for (Artifact artifact : ArtifactManager.getInstance(myProject).getArtifacts()) {
            if (artifact.getArtifactType() == ExplodedWarArtifactType.getInstance() && !deployed.contains(artifact)) {
                artifacts.add(artifact);
            }
        }
        artifacts.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        return artifacts;
    }

    @RequiredUIAccess
    private void addArtifacts(List<Artifact> artifacts) {
        ArtifactPointerManager pointerManager = ArtifactPointerManager.getInstance(myProject);
        TomcatArtifactDeployItem lastItem = null;
        for (Artifact artifact : artifacts) {
            lastItem = new TomcatArtifactDeployItem(pointerManager.create(artifact), artifact.getName() + "/");
            myItems.add(lastItem);
        }

        if (lastItem != null) {
            myTable.select(lastItem);
        }
    }

    private class DeployItemAddAction extends AddAction<TomcatArtifactDeployItem> {
        @Override
        @RequiredUIAccess
        protected void doAdd(AnActionEvent e) {
            List<Artifact> artifacts = collectDeployableArtifacts();
            if (artifacts.isEmpty()) {
                return;
            }

            String title = TomcatLocalize.runConfigurationTitleChooseArtifact().get();
            MultiSelectionListPopupStep<Artifact> step = new MultiSelectionListPopupStep<>(title, artifacts) {
                @Override
                public String getTextFor(Artifact value) {
                    return value.getName();
                }

                @Override
                public Image getIconFor(Artifact value) {
                    return value.getArtifactType().getIcon();
                }

                @Override
                public boolean isSpeedSearchEnabled() {
                    return true;
                }

                @Override
                @RequiredUIAccess
                public PopupStep<?> onChosen(List<Artifact> selectedValues, boolean finalChoice) {
                    return doFinalStep(() -> addArtifacts(selectedValues));
                }
            };

            JBPopupFactory.getInstance().createListPopup(myProject, step).showUnderneathOf(e);
        }
    }
}
