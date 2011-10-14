// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package tosstudio.metadata.filemanipulation;

import junit.framework.Assert;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.talend.swtbot.TalendSwtBotForTos;
import org.talend.swtbot.Utilities;
import org.talend.swtbot.items.TalendGenericSchemaItem;
import org.talend.swtbot.items.TalendJobItem;
import org.talend.swtbot.items.TalendSchemaItem;

/**
 * DOC fzhong class global comment. Detailled comment
 */
// @Ignore("developing")
@RunWith(SWTBotJunit4ClassRunner.class)
public class TMapGetSchemaFromRepositoryTest extends TalendSwtBotForTos {

    private TalendJobItem jobItem;

    private TalendGenericSchemaItem genericSchemaItem;

    private static final String JOB_NAME = "jobTest";

    private static final String SCHEMA_NAME = "schemaTest";

    @Before
    public void createJob() {
        jobItem = new TalendJobItem(JOB_NAME);
        jobItem.create();
        genericSchemaItem = new TalendGenericSchemaItem(SCHEMA_NAME);
        genericSchemaItem.create();
    }

    @Test
    public void tMapGetSchemaFromRepositoryTest() {
        SWTBotGefEditor jobEditor = jobItem.getJobEditor();
        Utilities.dndPaletteToolOntoJob(jobEditor, "tMap", new Point(100, 100));
        getTalendComponentPart(jobEditor, "tMap_1").doubleClick();
        SWTBotShell tMapShell = gefBot.shell(System.getProperty("buildTitle") + " - tMap - tMap_1");
        SWTBotShell tempShell = null;
        try {
            gefBot.toolbarButtonWithTooltip("Add output table").click();
            tempShell = gefBot.shell("Add a output").activate();
            gefBot.button("OK").click();
            gefBot.toolbarToggleButtonWithTooltip("tMap settings").click();
            gefBot.tableWithLabel("out1", 0).doubleClick(2, 2);
            gefBot.button("...").click();
            tempShell = gefBot.shell("Options").activate();
            gefBot.table(0).select("Repository");
            gefBot.button("OK").click();
            gefBot.tableWithLabel("out1", 0).doubleClick(3, 2);
            gefBot.button("...").click();
            tempShell = gefBot.shell("Repository Content").activate();
            SWTBotTreeItem schema = gefBot.tree(0).expandNode("Generic schemas", genericSchemaItem.getItemFullName());
            schema.expand().select("metadata");
            gefBot.button("OK").click();
            Assert.assertEquals("schemas did not add", 3, gefBot.tableWithLabel("out1", 1).rowCount());
            gefBot.button("Ok").click();

            TalendSchemaItem schemaItem = genericSchemaItem.getSchema("metadata");
            schemaItem.editSchema();
            gefBot.buttonWithTooltip("Add").click();
            gefBot.button("Finish").click();
            tempShell = gefBot.shell("Modification").activate();
            gefBot.button("Yes").click();
            tempShell = gefBot.shell("Update Detection").activate();
            gefBot.button("OK").click();

            getTalendComponentPart(jobEditor, "tMap_1").doubleClick();
            Assert.assertEquals("schemas did not update", 4, gefBot.tableWithLabel("out1", 1).rowCount());
            gefBot.button("Ok").click();
        } catch (WidgetNotFoundException wnfe) {
            tempShell.close();
            tMapShell.close();
            Assert.fail(wnfe.getCause().getMessage());
        } catch (Exception e) {
            tempShell.close();
            tMapShell.close();
            Assert.fail(e.getMessage());
        }
    }

    @After
    public void removePreviousCreateItems() {
        jobItem.getJobEditor().saveAndClose();
        Utilities.cleanUpRepository(jobItem.getParentNode());
        Utilities.cleanUpRepository(genericSchemaItem.getParentNode());
        Utilities.emptyRecycleBin();
    }
}
