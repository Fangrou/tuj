package org.talend.top.swtbot.test.analysis.columnanalysis;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.matchers.WidgetOfType;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.talend.swtbot.test.commons.SWTBotUtils;
import org.talend.swtbot.test.commons.TalendSwtbotForTdq;
import org.talend.swtbot.test.commons.TalendSwtbotTdqCommon;
import org.talend.swtbot.test.commons.TalendSwtbotTdqCommon.TalendItemTypeEnum;
import org.talend.swtbot.test.commons.TalendSwtbotTdqCommon.TalendMetadataTypeEnum;

@RunWith(SWTBotJunit4ClassRunner.class)
public class ColumnAnaWithStrTest extends TalendSwtbotForTdq {

	private final String ANALYSISLABEL = "cwithString";

	@Before
	public void beforeRunning() {
		TalendSwtbotTdqCommon.createConnection(bot,
				TalendMetadataTypeEnum.MYSQL);
	}

	@Test
	public void columnwithStringRunning() {
		String column = TalendSwtbotTdqCommon.getColumns(bot,
				TalendMetadataTypeEnum.MYSQL, "tbi", "customer", "address1")[0];
		bot.viewByTitle("DQ Repository").setFocus();
		SWTBotTree tree = new SWTBotTree((Tree) bot.widget(
				WidgetOfType.widgetOfType(Tree.class),
				bot.viewByTitle("DQ Repository").getWidget()));
		tree.expandNode("Metadata", "DB connections",
				TalendMetadataTypeEnum.MYSQL.toString(), "tbi").getNode(0)
				.expand().getNode("customer").getNode(0).expand()
				.getNode(column).select();
		SWTBotUtils.clickContextMenu(tree, "Simple analysis");
		bot.textWithLabel("Name").setText(ANALYSISLABEL);
		bot.button("Finish").click();
		bot.cTabItem(ANALYSISLABEL + " 0.1").setFocus();
		bot.toolbarButtonWithTooltip("Run").click();
		bot.waitUntil(Conditions.shellCloses(bot.shell("Run Analysis")));
		bot.editorByTitle(ANALYSISLABEL + " 0.1").close();
	}

	@After
	public void cleanSource() {
//		TalendSwtbotTdqCommon.deleteAndCleanCycleBin(bot, TalendItemTypeEnum.ANALYSIS,
//				ANALYSISLABEL);
		TalendSwtbotTdqCommon.deleteSource(bot, TalendItemTypeEnum.ANALYSIS,
				ANALYSISLABEL);
		TalendSwtbotTdqCommon.deleteSource(bot, TalendItemTypeEnum.METADATA,
				TalendMetadataTypeEnum.MYSQL.toString());
	}
}
