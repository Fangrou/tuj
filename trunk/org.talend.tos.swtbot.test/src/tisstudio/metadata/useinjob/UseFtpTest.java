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
package tisstudio.metadata.useinjob;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.talend.swtbot.TalendSwtBotForTos;
import org.talend.swtbot.Utilities;
import org.talend.swtbot.helpers.JobHelper;
import org.talend.swtbot.helpers.MetadataHelper;
import org.talend.swtbot.items.TalendFtpItem;
import org.talend.swtbot.items.TalendJobItem;

/**
 * DOC fzhong class global comment. Detailled comment
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class UseFtpTest extends TalendSwtBotForTos {

    private TalendJobItem jobItem;

    private SWTBotTreeItem metadataNode;

    private SWTBotTreeItem metadataItem;

    private SWTBotGefEditor jobEditor;

    private static final String JOBNAME = "jobTest"; // $NON-NLS-1$

    private static final String METADATA_NAME = "ftpTest"; // $NON-NLS-1$

    @Before
    public void createJobAndMetadata() throws IOException, URISyntaxException {
        jobItem = new TalendJobItem(JOBNAME);
        jobItem.create();
        jobEditor = jobItem.getEditor();
        metadataNode = Utilities.getTalendItemNode(Utilities.TalendItemType.FTP);
        metadataItem = Utilities.createFTP(METADATA_NAME, metadataNode);
    }

    @Test
    public void useMetadataInJob() throws IOException, URISyntaxException {
        TalendFtpItem ftpItem = new TalendFtpItem();
        ftpItem.setItem(metadataItem);
        ftpItem.setComponentType("tFTPConnection");
        ftpItem.setExpectResult("");
        Utilities.dndMetadataOntoJob(jobEditor, metadataItem, ftpItem.getComponentType(), new Point(100, 100));
        JobHelper.runJob(JOBNAME);

        String result = gefBot.styledText().getText();
        MetadataHelper.assertResult(result, ftpItem);
    }

    @After
    public void removePreviousCreateItems() {
        jobEditor.saveAndClose();
        Utilities.cleanUpRepository(jobItem.getParentNode());
        Utilities.cleanUpRepository(metadataNode);
        Utilities.emptyRecycleBin();
    }
}
