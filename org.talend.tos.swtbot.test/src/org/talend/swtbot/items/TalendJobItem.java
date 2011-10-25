package org.talend.swtbot.items;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.talend.swtbot.Utilities;

public class TalendJobItem extends TalendItem {

    public TalendJobItem() {
        super(Utilities.TalendItemType.JOB_DESIGNS);
    }

    public TalendJobItem(String itemName) {
        super(itemName, Utilities.TalendItemType.JOB_DESIGNS);
    }

    @Override
    public void create() {
        SWTBotShell shell = beginCreationWizard("Create job", "New job");
        finishCreationWizard(shell);
    }

    public SWTBotGefEditor getEditor() {
        return gefBot.gefEditor("Job " + itemFullName);
    }
}
