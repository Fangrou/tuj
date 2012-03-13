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
package tosstudio.routines;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.talend.swtbot.TalendSwtBotForTos;
import org.talend.swtbot.items.TalendRoutineItem;

/**
 * DOC Administrator class global comment. Detailled comment
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class DeleteRoutineTest extends TalendSwtBotForTos {

    private TalendRoutineItem routineItem;

    private static final String ROUTINENAME = "routine1"; //$NON-NLS-1$

    @Before
    public void initialisePrivateFields() {
        routineItem = new TalendRoutineItem(ROUTINENAME);
        routineItem.create();
    }

    @Test
    public void deleteRoutine() {
        routineItem.getEditor().saveAndClose();
        routineItem.delete();
    }

}
