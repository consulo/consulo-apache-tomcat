/**
 * @author VISTALL
 * @since 04/06/2023
 */
module consulo.apache.tomcat {
    requires consulo.application.api;
    requires consulo.application.content.api;
    requires consulo.compiler.artifact.api;
    requires consulo.component.api;
    requires consulo.configurable.api;
    requires consulo.container.api;
    requires consulo.execution.api;
    requires consulo.execution.debug.api;
    requires consulo.ide.api;
    requires consulo.localize.api;
    requires consulo.module.api;
    requires consulo.module.ui.api;
    requires consulo.process.api;
    requires consulo.project.api;
    requires consulo.ui.api;
    requires consulo.ui.ex.api;
    requires consulo.ui.ex.awt.api;
    requires consulo.util.io;
    requires consulo.util.lang;
    requires consulo.util.xml.serializer;
    requires consulo.virtual.file.system.api;

    requires consulo.jakartaee.api;

    requires consulo.java.execution.api;

    requires consulo.java.debugger.api;
    requires consulo.java.debugger.impl;

    requires consulo.jakartaee.web.api;
    requires consulo.jakartaee.web.impl;

    // TODO remove in future
    requires java.desktop;
    requires forms.rt;
}
