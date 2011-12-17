package org.talend.tdq.swtbot.test.reportwithoracle;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.matchers.WidgetOfType;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.talend.swtbot.test.commons.TalendSwtbotForTdq;
import org.talend.swtbot.test.commons.TalendSwtbotTdqCommon;
import org.talend.swtbot.test.commons.TalendSwtbotTdqCommon.TalendAnalysisTypeEnum;
import org.talend.swtbot.test.commons.TalendSwtbotTdqCommon.TalendItemTypeEnum;
import org.talend.swtbot.test.commons.TalendSwtbotTdqCommon.TalendMetadataTypeEnum;
import org.talend.swtbot.test.commons.TalendSwtbotTdqCommon.TalendReportDBType;
import org.talend.swtbot.test.commons.TalendSwtbotTdqCommon.TalendReportTemplate;

@RunWith(SWTBotJunit4ClassRunner.class)

public class BasicTypeReportForBusinessRuleAnalysisOnORACLETest extends TalendSwtbotForTdq{
	
	private final String  REPORTLABEL = "report_basic_rule_ORACLE";
	private final String DQRULENAME = "dqrule1";
	private final String RULEEXPRESSION = "customer_id>2500";
	@Before
	public void beforeClass(){
		
		TalendSwtbotTdqCommon.setReportDB(bot, TalendReportDBType.Oracle_with_SID);
		TalendSwtbotTdqCommon.createConnection(bot, TalendMetadataTypeEnum.MYSQL);
		bot.editorByTitle(TalendMetadataTypeEnum.MYSQL.toString()+" 0.1").close();
		TalendSwtbotTdqCommon.createDQRule(bot, DQRULENAME, RULEEXPRESSION);
		TalendSwtbotTdqCommon.createAnalysis(bot, TalendAnalysisTypeEnum.DQRULE);
		
	
	}
	@Test
	public void basicTypeReportForBusinessRuleAnalysisOnORACLE(){
		
		 	bot.editorByTitle(TalendAnalysisTypeEnum.DQRULE.toString() + " 0.1")
				.show();
		formBot.hyperlink("Select tables/views to analyze").click();
		bot.waitUntil(Conditions.shellIsActive("Table/view Selection"));
		SWTBotTree tree = new SWTBotTree((Tree) bot.widget(WidgetOfType
				.widgetOfType(Tree.class)));
		tree.expandNode("DB connections",
				TalendMetadataTypeEnum.MYSQL.toString(), "tbi").getNode(0).select();
			//	.select("Tables");
		//tree.expandNode("tbi").getNode(0).expand().select("customer");
		//tree.expandNode("tbi", "Tables").select("customer");
		bot.table().getTableItem("customer").check();
		bot.button("OK").click();
		bot.editorByTitle(TalendAnalysisTypeEnum.DQRULE.toString() + " 0.1")
				.setFocus();
		formBot.section("Analyzed Tables and views").setFocus();
		tree = new SWTBotTree((Tree) bot.widget(
				WidgetOfType.widgetOfType(Tree.class),
				bot.editorByTitle(
						TalendAnalysisTypeEnum.DQRULE.toString() + " 0.1")
						.getWidget()));
		tree.getTreeItem("customer").doubleClick();
		try {
			bot.waitUntil(Conditions.shellIsActive("DQ Rule Selector"));
		} catch (TimeoutException e) {
		}
		tree = new SWTBotTree((Tree) bot.widget(WidgetOfType
				.widgetOfType(Tree.class)));
		tree.expandNode("Rules", "SQL").getNode(DQRULENAME).check();
		bot.button("OK").click();
		bot.editorByTitle(TalendAnalysisTypeEnum.DQRULE.toString() + " 0.1")
				.save();
		bot.toolbarButtonWithTooltip("Run").click();
//		try {
//			bot.waitUntil(Conditions.shellCloses(bot.shell("Run Analysis")));
//		} catch (TimeoutException e) {
//		}finally{
//			bot.shell("Run Analysis").close();
//		}
		bot.editorByTitle(TalendAnalysisTypeEnum.DQRULE.toString()+" 0.1").close();
		TalendSwtbotTdqCommon.createReport(bot, REPORTLABEL);
		TalendSwtbotTdqCommon.generateReport(bot, formBot, REPORTLABEL,
				TalendReportTemplate.Basic,
				TalendAnalysisTypeEnum.DQRULE.toString());

		 
		
	}
	@After
	public void afterClass(){
		
		TalendSwtbotTdqCommon.deleteSource(bot, TalendItemTypeEnum.REPORT, REPORTLABEL);
		TalendSwtbotTdqCommon.deleteSource(bot, TalendItemTypeEnum.ANALYSIS, TalendAnalysisTypeEnum.DQRULE.toString());
		TalendSwtbotTdqCommon.deleteSource(bot, TalendItemTypeEnum.LIBRARY_DQRULE, DQRULENAME);
		TalendSwtbotTdqCommon.deleteSource(bot, TalendItemTypeEnum.METADATA, TalendMetadataTypeEnum.MYSQL.toString());
	
		
	}
	

}
