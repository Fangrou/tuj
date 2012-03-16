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
package tisstudio.metadata.useinjob;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.swtbot.TalendSwtBotForTos;
import org.talend.swtbot.Utilities;
import org.talend.swtbot.helpers.MetadataHelper;
import org.talend.swtbot.items.TalendHL7Item;
import org.talend.swtbot.items.TalendJobItem;

/**
 * DOC fzhong class global comment. Detailled comment
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class UseHL7Test extends TalendSwtBotForTos {

    private TalendJobItem jobItem;

    private SWTBotTreeItem metadataNode;

    private SWTBotTreeItem metadataItem;

    private SWTBotGefEditor jobEditor;

    private static final String JOBNAME = "jobTest"; // $NON-NLS-1$

    private static final String METADATA_NAME = "hl7Test"; // $NON-NLS-1$

    @Before
    public void createJobAndMetadata() throws IOException, URISyntaxException {
        repositories.add(ERepositoryObjectType.PROCESS);
        repositories.add(ERepositoryObjectType.METADATA_FILE_HL7);
        jobItem = new TalendJobItem(JOBNAME);
        jobItem.create();
        jobEditor = jobItem.getEditor();
        metadataNode = Utilities.getTalendItemNode(Utilities.TalendItemType.HL7);
        metadataItem = Utilities.createHL7("input", metadataNode, METADATA_NAME);
    }

    @Test
    public void useMetadataInJob() throws IOException, URISyntaxException {
        TalendHL7Item hl7Item = new TalendHL7Item();
        hl7Item.setItem(metadataItem);
        hl7Item.setComponentType("tHL7Input");
        MetadataHelper.output2Console(jobEditor, hl7Item, "row_MSH_1");

        String result = gefBot.styledText().getText();
        MetadataHelper.assertResult(result, hl7Item);
    }

}
