package edu.missouri.operations.reportcenter.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;
import c10n.annotations.Es;
import c10n.annotations.Fr;
import c10n.annotations.Zh;

@C10NMessages
public interface TopBarText {
	
	@En("Projects")
	@Es("Proyectos")
	@Fr("Projets")
	@Zh("工程")
	public String projects();
	
	@En("Open Projects Screen") 
	public String projects_help();
		
	
	@En("Projex Home Screen")
	public String projectsHome();
	
	@En("Configuration")
	@Es("Configuración")
	@Fr("Configuration")
	@Zh("体系")
	public String configuration();
	
	@En("System Configuration, Security, Common Databases, Toolboxes and Advertisements")
	public String configuration_help();
	
	@En("Reports")
	@Es("Informes")
	@Fr("Reports")
	@Zh("报告")
	public String reports();
	
	@En("Run Reports")
	public String reports_help();
	
	@En("Document Search")
	@Es("Búsqueda de Documentos")
	@Fr("Recherche des Documents")
	@Zh("文档搜索")
	public String documentSearch();
	
	@En("Search attached documents")
	public String documentSearch_help();
	
	@En("User Settings")
	@Es("Ajustes del Usuario")
	@Fr("Paramètres de l'Utilisateur")
	@Zh("用户设置")
	public String userSettings();
	
	@En("Update your personal information and system settings")
	public String userSettings_help();
	
	@En("Sign Off")
	@Es("Acabar el Programa")
	@Fr("Déconnecter")
	@Zh("注销")
	public String signOff();
	
	@En("Exit Projex")
	public String signOff_help();
	
	@En("Help")
	@Es("De Ayuda del Programa")
	@Fr("L'Aide du Logiciel")
	@Zh("软件帮助")
	public String help();

}
