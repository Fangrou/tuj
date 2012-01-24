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

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCTabItem;
import org.junit.After;
import org.junit.Assert;
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
public class ReadJobTest extends TalendSwtBotForTos {

    private TalendJobItem jobItem;

    private static final String JOBNAME = "test01"; //$NON-NLS-1$

    @Before
    public void createAJob() {
        jobItem = new TalendJobItem(JOBNAME);
        jobItem.create();
    }

    @Test
    public void readJob() {
        jobItem.getEditor().saveAndClose();
        jobItem.getItem().contextMenu("Read job").click();

        SWTBotCTabItem newTabItem = gefBot.cTabItem("Job " + JOBNAME + " 0.1");
        Assert.assertNotNull("job tab is not opened", newTabItem);
        jobItem.getEditor().activateTool("tMsgBox").click(100, 100);
        SWTBotGefEditPart msgBox = getTalendComponentPart(jobItem.getEditor(), "tMsgBox_1");
        Assert.assertNull("Job can be edit", msgBox);
    }

    @After
    public void removePreviouslyCreateItems() {
        jobItem.getEditor().saveAndClose();
        Utilities.cleanUpRepository(jobItem.getParentNode());
        Utilities.emptyRecycleBin();
    }
}
