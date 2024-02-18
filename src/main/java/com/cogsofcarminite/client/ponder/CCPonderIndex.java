package com.cogsofcarminite.client.ponder;

import com.cogsofcarminite.CogsOfCarminite;
import com.cogsofcarminite.reg.CCBlocks;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.infrastructure.ponder.scenes.ProcessingScenes;

public class CCPonderIndex {
    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(CogsOfCarminite.MODID);

    public static void register() {
        HELPER.addStoryBoard(CCBlocks.HORNBLOWER, "hornblower", CCScenes::hornblower);
    }
}
