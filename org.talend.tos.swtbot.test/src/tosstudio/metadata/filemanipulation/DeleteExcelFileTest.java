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

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.talend.swtbot.TalendSwtBotForTos;
import org.talend.swtbot.Utilities;
import org.talend.swtbot.items.TalendExcelFileItem;

/**
 * DOC Administrator class global comment. Detailled comment
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class DeleteExcelFileTest extends TalendSwtBotForTos {

    private TalendExcelFileItem fileItem;

    private static final String FILENAME = "test_excel"; //$NON-NLS-1$

    @Before
    public void createExcelFile() throws IOException, URISyntaxException {
        fileItem = new TalendExcelFileItem(FILENAME);
        fileItem.create();
    }

    @Test
    public void deleteExcelFile() {
        fileItem.delete();
    }

    @After
    public void cleanUp() {
        Utilities.emptyRecycleBin();
    }
}
