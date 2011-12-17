package org.talend.tdq.swtbot.test.report;

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
import org.talend.swtbot.test.commons.TalendSwtbotTdqCommon.TalendReportDBType;
import org.talend.swtbot.test.commons.TalendSwtbotTdqCommon.TalendReportTemplate;

public class NewReportTextIndicatorTest extends TalendSwtbotForTdq {

	private final String REPORTLABEL = "text_indicator_basic_report";

	@Before
	public void beforeClass() {
		TalendSwtbotTdqCommon.setReportDB(bot, TalendReportDBType.MySQL);
		TalendSwtbotTdqCommon.createConnection(bot,
				TalendMetadataTypeEnum.MYSQL);
		TalendSwtbotTdqCommon
				.createAnalysis(bot, TalendAnalysisTypeEnum.COLUMN);
		TalendSwtbotTdqCommon.createReport(bot, REPORTLABEL);
	}

	@Test
	public void newReportTextIndicator() {
		
		String address1= TalendSwtbotTdqCommon.getColumns(bot,
				TalendMetadataTypeEnum.MYSQL, "tbi", "customer", "address1")[0];
		bot.editorByTitle(TalendAnalysisTypeEnum.COLUMN.toString() + " 0.1")
				.show();
		formBot.hyperlink("Select columns to analyze").click();
		bot.waitUntil(Conditions.shellIsActive("Column Selection"));
		SWTBotTree tree = new SWTBotTree((Tree) bot.widget(WidgetOfType
				.widgetOfType(Tree.class)));
		tree.expandNode("tbi").getNode(0).expand().select("customer");
		bot.table().getTableItem(address1).check();
		bot.button("OK").click();
		formBot.ccomboBox(2).setSelection("Nominal");
		bot.toolbarButtonWithTooltip("Save").click();
		formBot.hyperlink("Select indicators for each column").click();
		bot.waitUntil(Conditions.shellIsActive("Indicator Selection"));

		tree = new SWTBotTree((Tree) bot.widget(WidgetOfType
				.widgetOfType(Tree.class)));
		tree.getTreeItem("Text Statistics").click(1);
		bot.checkBox(8).click();
		bot.button("OK").click();
		bot.toolbarButtonWithTooltip("Run").click();

		try {
			SWTBotShell shell = bot.shell("Run Analysis");
			bot.waitUntil(Conditions.shellCloses(shell),50000);
		} catch (TimeoutException e) {
		}
		
		bot.editorByTitle(TalendAnalysisTypeEnum.COLUMN.toString() + " 0.1")
				.close();

		TalendSwtbotTdqCommon.generateReport(bot, formBot, REPORTLABEL,
				TalendReportTemplate.Basic,
				TalendAnalysisTypeEnum.COLUMN.toString());
	}

	@After
	public void cleanSource() {
		TalendSwtbotTdqCommon.deleteSource(bot, TalendItemTypeEnum.REPORT,
				REPORTLABEL);
		TalendSwtbotTdqCommon.deleteSource(bot, TalendItemTypeEnum.ANALYSIS,
				TalendAnalysisTypeEnum.COLUMN.toString());
		TalendSwtbotTdqCommon.deleteSource(bot, TalendItemTypeEnum.METADATA,
				TalendReportDBType.MySQL.toString());
	}

}
