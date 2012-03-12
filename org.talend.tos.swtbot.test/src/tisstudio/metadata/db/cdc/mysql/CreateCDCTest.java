// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package tisstudio.metadata.db.cdc.mysql;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.talend.swtbot.TalendSwtBotForTos;
import org.talend.swtbot.Utilities.DbConnectionType;
import org.talend.swtbot.items.TalendDBItem;

/**
 * DOC fzhong class global comment. Detailled comment
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class CreateCDCTest extends TalendSwtBotForTos {

    private TalendDBItem dbItem, copy_of_dbItem;

    private static final String DB_NAME = "mysql";

    private static final String TABLE_NAME = "autotest";

    private boolean isTableCreated = false;

    private boolean isSubscriberCreated = false;

    @Before
    public void createDb() {
        dbItem = new TalendDBItem(DB_NAME, DbConnectionType.MYSQL);
        dbItem.create();
        copy_of_dbItem = (TalendDBItem) dbItem.copyAndPaste();
        dbItem.executeSQL("create table " + TABLE_NAME + "(id int, name varchar(20));");
        isTableCreated = true;
        dbItem.retrieveDbSchema(TABLE_NAME);
    }

    @Test
    public void createCDCTest() {
        dbItem.getItem().expand().getNode("CDC Foundation").contextMenu("Create CDC").click();
        gefBot.shell("Create Change Data Capture").activate();
        gefBot.button("...").click();
        gefBot.shell("Repository Content").activate();
        gefBot.tree().expandNode("Db Connections").select(copy_of_dbItem.getItemFullName());
        gefBot.button("OK").click();
        gefBot.button("Create Subscriber").click();
        gefBot.shell("Create Subscriber and Execute SQL Script").activate();
        gefBot.button("Execute").click();
        gefBot.shell("Execute SQL Statement").activate();
        if ("Table 'tsubscribers' already exists".equals(gefBot.label(1).getText())) {
            isSubscriberCreated = true;
            gefBot.button("Ignore").click();
        }
        gefBot.button("OK").click();
        isSubscriberCreated = true;
        gefBot.button("Close").click();
        gefBot.button("Finish").click();

        Assert.assertNotNull(dbItem.getItem().expandNode("CDC Foundation", copy_of_dbItem.getItemName()).getNode("TSUBSCRIBERS"));

        SWTBotTreeItem temp = dbItem.getSchema(TABLE_NAME).getItem();
        Assert.assertTrue(temp.contextMenu("add CDC").isEnabled());
    }

    @After
    public void cleanUp() {
        String sql = "";
        if (isTableCreated)
            sql = sql + "drop table " + TABLE_NAME + ";\n";
        if (isSubscriberCreated)
            sql = sql + "drop table tsubscribers;\n";
        dbItem.executeSQL(sql);
    }
}
