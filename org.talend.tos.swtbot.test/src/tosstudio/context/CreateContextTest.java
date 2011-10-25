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
package tosstudio.context;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.talend.swtbot.TalendSwtBotForTos;
import org.talend.swtbot.Utilities;
import org.talend.swtbot.items.TalendContextItem;

/**
 * DOC Administrator class global comment. Detailled comment
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class CreateContextTest extends TalendSwtBotForTos {

    private TalendContextItem contextItem;

    private static final String CONTEXTNAME = "context1"; //$NON-NLS-1$

    @Before
    public void initialisePrivateFields() {
        contextItem = new TalendContextItem(CONTEXTNAME);
    }

    @Test
    public void createContext() {
        contextItem.create();
    }

    @After
    public void removePreviouslyCreateItems() {
        Utilities.cleanUpRepository(contextItem.getParentNode());
        Utilities.emptyRecycleBin();
    }
}
