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
package tosstudio.projectmanagement.performance;

import junit.framework.Assert;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.talend.swtbot.TalendSwtBotForTos;
import org.talend.swtbot.Utilities;
import org.talend.swtbot.items.TalendJobItem;

/**
 * DOC Administrator class global comment. Detailled comment
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class ExpandCollapseJobDesignsTest extends TalendSwtBotForTos {

    private TalendJobItem jobItem;

    private static final String JOBNAME = "test01"; //$NON-NLS-1$

    @Before
    public void createAJob() {
        jobItem = new TalendJobItem(JOBNAME);
        jobItem.create();
    }

    @Test
    public void expandCollapseJob() {
        // Collapse tree item
        jobItem.getParentNode().contextMenu("Expand/Collapse").click();
        Assert.assertFalse("did not collapse the node 'Job Designs'", jobItem.getParentNode().isExpanded());

        // Expand tree item
        jobItem.getParentNode().contextMenu("Expand/Collapse").click();
        Assert.assertTrue("did not expand the node 'Job Designs'", jobItem.getParentNode().isExpanded());
    }

    @After
    public void removePreviouslyCreateItems() {
        jobItem.getEditor().saveAndClose();
        Utilities.cleanUpRepository(jobItem.getParentNode());
        Utilities.emptyRecycleBin();
    }
}
