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
package tosstudio.metadata.filemanipulation;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.talend.swtbot.TalendSwtBotForTos;
import org.talend.swtbot.items.TalendPositionalFileItem;

/**
 * DOC Administrator class global comment. Detailled comment
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class DuplicatePositionalFileTest extends TalendSwtBotForTos {

    private TalendPositionalFileItem fileItem;

    private static final String FILENAME = "test_positional"; // $NON-NLS-1$

    private static final String NEW_FILENAME = "duplicate_positional"; // $NON-NLS-1$

    @Before
    public void createPositionalFile() throws IOException, URISyntaxException {
        fileItem = new TalendPositionalFileItem(FILENAME);
        fileItem.create();
    }

    @Test
    public void duplicatePositionalFile() {
        fileItem.duplicate(NEW_FILENAME);
    }

}
