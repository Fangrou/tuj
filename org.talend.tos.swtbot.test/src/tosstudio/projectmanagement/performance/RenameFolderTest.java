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

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.swtbot.TalendSwtBotForTos;
import org.talend.swtbot.Utilities;
import org.talend.swtbot.items.TalendFolderItem;
import org.talend.swtbot.items.TalendJobItem;

/**
 * DOC fzhong class global comment. Detailled comment
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class RenameFolderTest extends TalendSwtBotForTos {

    private TalendJobItem jobItem;

    private TalendFolderItem folderItem;

    private static final String JOBNAME = "jobTest";

    private static final String FOLDERNAME = "folderTest";

    private static final String NEW_FOLDERNAME = "re_folderTest";

    @Before
    public void initialisePrivateField() {
        repositories.add(ERepositoryObjectType.PROCESS);
        folderItem = Utilities.createFolder(FOLDERNAME, Utilities.TalendItemType.JOB_DESIGNS);
        jobItem = new TalendJobItem(JOBNAME);
        jobItem.setFolderPath(folderItem.getFolderPath());
        jobItem.create();
        jobItem.getEditor().saveAndClose();
    }

    @Test
    public void renameFolder() {
        folderItem.rename(NEW_FOLDERNAME);
    }

}
