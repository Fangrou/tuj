package org.talend.tdq.swtbot.test.etljob;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.matchers.WidgetOfType;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.swtbot.test.commons.ContextMenuHelper;
import org.talend.swtbot.test.commons.DndUtil;
import org.talend.swtbot.test.commons.JobHelper;
import org.talend.swtbot.test.commons.TalendSwtbotForTdq;
import org.talend.swtbot.test.commons.TalendSwtbotTdqCommon;
import org.talend.swtbot.test.commons.TalendSwtbotTdqCommon.TalendAnalysisTypeEnum;
import org.talend.swtbot.test.commons.TalendSwtbotTdqCommon.TalendMetadataTypeEnum;

public class GenerateAnELTJobToGetOnlyValidRowsTest extends TalendSwtbotForTdq{
	
	@Before
	public void beforeClass(){
		TalendSwtbotTdqCommon.createConnection(bot, TalendMetadataTypeEnum.MYSQL);
		bot.editorByTitle(TalendMetadataTypeEnum.MYSQL.toString()+" 0.1").close();
		TalendSwtbotTdqCommon.createAnalysis(bot, TalendAnalysisTypeEnum.COLUMN);
	
	}
	
	@Test
	public void GenerateAnELTJobToGetOnlyValidRows(){
		
		String column = TalendSwtbotTdqCommon.getColumns(bot,
				TalendMetadataTypeEnum.MYSQL, "tbi", "customer", "customer_id")[0];
		String col1 = column.substring(0, column.indexOf("(")) + " "
		+ column.substring(column.indexOf("("));
		bot.editorByTitle(TalendAnalysisTypeEnum.COLUMN.toString()+" 0.1").show();
		formBot.hyperlink("Select columns to analyze").click();
		try {
			bot.waitUntil(Conditions.shellIsActive("Column Selection"));
		} catch (Exception e1) {
			
		}
		SWTBotTree tree= new SWTBotTree((Tree)bot.widget(WidgetOfType.widgetOfType(Tree.class)));
		tree.expandNode("tbi").getNode(0).expand().select("customer");
		bot.table().getTableItem(column).check();
		bot.button("OK").click();
		formBot.ccomboBox(2).setSelection("Interval");
		bot.toolbarButtonWithTooltip("Save").click();
//		formBot.hyperlink("Select indicators for each column").click();
//		bot.waitUntil(Conditions.shellIsActive("Indicator Selection"));
//		tree = new SWTBotTree((Tree)bot.widget(WidgetOfType.widgetOfType(Tree.class)));
//		tree.getTreeItem("Advanced Statistics").click();
//		bot.checkBox(30).click();
//		bot.button("OK").click();
		formBot.section("Analyzed Columns").setFocus();
		tree = new SWTBotTree((Tree)bot.widget(
				WidgetOfType.widgetOfType(Tree.class),
				bot.editorByTitle(
				TalendAnalysisTypeEnum.COLUMN.toString() + " 0.1")
				.getWidget()));
		SWTBotTreeItem targetItem =tree.expandNode(col1).select();
	    bot.viewByTitle("DQ Repository").setFocus();
		tree = new SWTBotTree((Tree) bot.widget(
				WidgetOfType.widgetOfType(Tree.class),
				bot.viewByTitle("DQ Repository").getWidget()));
		SWTBotView view = bot.viewByTitle("DQ Repository");
		SWTBotTreeItem source = tree.expandNode("Libraries","Patterns","Regex","internet").getNode(0).select();
		DndUtil dndUtil = new DndUtil(view.getWidget().getDisplay());
		dndUtil.dragAndDrop(source, targetItem);
		bot.toolbarButtonWithTooltip("Run").click();
		try {
			SWTBotShell shell = bot.shell("Run Analysis");
			bot.waitUntil(Conditions.shellCloses(shell));
		} catch (Exception e) {
			
		}
		bot.cTabItem("Analysis Results").activate();
		formBot.section("Analysis Results").setFocus();
		formBot.expandableComposite("Column:customer.customer_id").setFocus();
		formBot.expandableComposite("Pattern Matching").setFocus();
//	
		SWTBotTableItem tableItem = bot.tableWithLabel("Pattern Matching").getTableItem("Email Address").select();
//		tableItem.click();
		bot.sleep(3000);
	//	tableItem.contextMenu("Generate Job");
		bot.sleep(3000);
		
		tableItem.contextMenu("Generate Job").click();
		
	//	SWTBotTreeItem Item =tree.expandNode("Email Adress").select();

//		ContextMenuHelper.clickContextMenu(table, "Generate Job");
		
		SWTBotShell shell = bot.shell("Job Selector");
		bot.waitUntil(Conditions.shellIsActive("Job Selector"));
		bot.radio("generate an ELT job to get only valid rows").click();
		bot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		bot.editorByTitle(TalendAnalysisTypeEnum.COLUMN.toString()+" 0.1").close();
		JobHelper.runJob(6000);
	    String result = JobHelper.execResultFilter(JobHelper.getExecutionResult());
	    if(result != null &&  !"".equals(result))
	        	Assert.fail("this job can't run correctly"+result+"***************");
	    
		
		
	}
	@After
	public void afterClass(){
		JobHelper.deletleJob();
	}


}
