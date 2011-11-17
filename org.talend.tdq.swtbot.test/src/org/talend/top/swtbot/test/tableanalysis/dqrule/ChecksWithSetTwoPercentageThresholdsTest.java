package org.talend.top.swtbot.test.tableanalysis.dqrule;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swtbot.swt.finder.matchers.WidgetOfType;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.swtbot.test.commons.TalendSwtbotForTdq;
import org.talend.swtbot.test.commons.TalendSwtbotTdqCommon;
import org.talend.swtbot.test.commons.TalendSwtbotTdqCommon.TalendAnalysisTypeEnum;
import org.talend.swtbot.test.commons.TalendSwtbotTdqCommon.TalendItemTypeEnum;
import org.talend.swtbot.test.commons.TalendSwtbotTdqCommon.TalendMetadataTypeEnum;

public class ChecksWithSetTwoPercentageThresholdsTest extends TalendSwtbotForTdq{
	
	private final String DQRULENAME = "dqrule1";

	private final String RULEEXPRESSION = "customer_id>500";
	
	@Before
	public void beforeClass(){
		TalendSwtbotTdqCommon.createConnection(bot,
				TalendMetadataTypeEnum.MYSQL);
		bot.editorByTitle(TalendMetadataTypeEnum.MYSQL.toString()+" 0.1").close();
		TalendSwtbotTdqCommon
				.createAnalysis(bot, TalendAnalysisTypeEnum.DQRULE);
		TalendSwtbotTdqCommon.createDQRule(bot, DQRULENAME, RULEEXPRESSION);
	}
	
	@Test
	public void checksWithSetTwoPercentageThresholds(){
		bot.editorByTitle(TalendAnalysisTypeEnum.DQRULE.toString() + " 0.1")
		.show();
		formBot.hyperlink("Select tables/views to analyze").click();
		bot.waitUntil(Conditions.shellIsActive("Table/view Selection"));
		SWTBotTree tree = new SWTBotTree((Tree) bot.widget(WidgetOfType
				.widgetOfType(Tree.class)));
		tree.expandNode("DB connections",
				TalendMetadataTypeEnum.MYSQL.toString(), "tbi").getNode(0).select();
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
		tree = new SWTBotTree(bot.widget(WidgetOfType.widgetOfType(Tree.class),
				bot.editorByTitle(TalendAnalysisTypeEnum.DQRULE.toString()+" 0.1").getWidget()));
		tree.expandNode("customer").getNode(DQRULENAME).doubleClick();
	bot.waitUntil(Conditions.shellIsActive("Indicator"));
	SWTBotShell shell = bot.shell("Indicator");
	bot.textWithLabel("Lower threshold(%)").setText("90");
	bot.textWithLabel("Higher threshold(%)").setText("100");
	bot.button("Finish").click();
	try {
		bot.waitUntil(Conditions.shellCloses(shell));
	} catch (Exception e1) {

	}
	bot.editorByTitle(TalendAnalysisTypeEnum.DQRULE.toString() + " 0.1")
			.save();
	bot.toolbarButtonWithTooltip("Run").click();
	try {
		shell = bot.shell("Run Analysis");
		bot.waitUntil(Conditions.shellCloses(shell));
	} catch (Exception e) {
	}
	bot.sleep(2000);
	bot.cTabItem("Analysis Results").activate();
	bot.sleep(2000);
	bot.captureScreenshot(System
			.getProperty("tdq.analysis.result.screenshot.path")
			+ "dqrule_threshold.jpeg");		
		
	bot.editorByTitle(TalendAnalysisTypeEnum.DQRULE.toString() + " 0.1").close();	
	}
	
	@After 
	public void afterClass(){
		TalendSwtbotTdqCommon.deleteAndCleanCycleBin(bot, TalendItemTypeEnum.ANALYSIS,
				TalendAnalysisTypeEnum.DQRULE.toString());
		TalendSwtbotTdqCommon.deleteAndCleanCycleBin(bot,
				TalendItemTypeEnum.LIBRARY_DQRULE, DQRULENAME);
		TalendSwtbotTdqCommon.deleteAndCleanCycleBin(bot, TalendItemTypeEnum.METADATA,
				TalendMetadataTypeEnum.MYSQL.toString());
		
	}

}
