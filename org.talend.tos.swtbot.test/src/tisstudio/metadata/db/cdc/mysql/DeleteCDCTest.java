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
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.swtbot.TalendSwtBotForTos;
import org.talend.swtbot.Utilities.DbConnectionType;
import org.talend.swtbot.items.TalendDBItem;

/**
 * DOC fzhong class global comment. Detailled comment
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class DeleteCDCTest extends TalendSwtBotForTos {

    private TalendDBItem dbItem, copy_of_dbItem;

    private static final String DB_NAME = "mysql";

    private static final String TABLE_NAME = "autotest";

    private boolean isTableCreated = false;

    private boolean isSubscriberCreated = false;

    @Before
    public void createDb() {
        repositories.add(ERepositoryObjectType.METADATA_CONNECTIONS);
        dbItem = new TalendDBItem(DB_NAME, DbConnectionType.MYSQL);
        dbItem.create();
        copy_of_dbItem = (TalendDBItem) dbItem.copyAndPaste();
        dbItem.executeSQL("create table " + TABLE_NAME + "(id int, name varchar(20));");
        isTableCreated = true;
        dbItem.retrieveDbSchema(TABLE_NAME);
    }

    @Test
    public void deleteCDCTest() {
        isSubscriberCreated = dbItem.createCDC(copy_of_dbItem);
        if (dbItem.deleteCDC())
            isSubscriberCreated = false;

        Assert.assertNull("did not delete cdc foundation", dbItem.getCDCFoundation());
        SWTBotTreeItem temp = dbItem.getSchema(TABLE_NAME).getItem();
        long defaultTimeout = SWTBotPreferences.TIMEOUT;
        try {
            SWTBotPreferences.TIMEOUT = 500;
            if (temp.contextMenu("add CDC").isEnabled())
                Assert.fail("context menu 'add CDC' still avialable");
        } catch (TimeoutException e) {
            // ignore, it's right if the context menu could not be found.
        } finally {
            SWTBotPreferences.TIMEOUT = defaultTimeout;
        }
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
