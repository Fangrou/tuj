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
package tosstudio.metadata.salesforce;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.talend.swtbot.TalendSwtBotForTos;
import org.talend.swtbot.Utilities;
import org.talend.swtbot.items.TalendSalesforceItem;

/**
 * DOC fzhong class global comment. Detailled comment
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class SetUpProxyForSalesforceTest extends TalendSwtBotForTos {

    private TalendSalesforceItem salesforceItem;

    private static final String SALESFORCENAME = "saleforce1"; //$NON-NLS-1$

    @Before
    public void initialisePrivateFields() {
        salesforceItem = new TalendSalesforceItem(SALESFORCENAME);
    }

    @Test
    public void setUpProxyForSalesforce() {
        salesforceItem.setProxyTypeAsHttp();
        salesforceItem.create();
    }

    @After
    public void removePreviouslyCreateItems() {
        Utilities.cleanUpRepository(salesforceItem.getParentNode());
        Utilities.emptyRecycleBin();
    }
}
