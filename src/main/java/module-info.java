/**
 * @author VISTALL
 * @since 04/06/2023
 */
module consulo.apache.tomcat
{
	requires consulo.ide.api;

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