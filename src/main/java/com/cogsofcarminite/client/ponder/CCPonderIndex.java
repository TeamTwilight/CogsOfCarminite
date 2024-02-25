package com.cogsofcarminite.client.ponder;

import com.cogsofcarminite.CogsOfCarminite;
import com.cogsofcarminite.reg.CCBlocks;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;

public class CCPonderIndex {
    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(CogsOfCarminite.MODID);

    public static void register() {
        HELPER.addStoryBoard(CCBlocks.HORNBLOWER, "hornblower", CCScenes::hornblower, AllPonderTags.KINETIC_APPLIANCES);
        HELPER.addStoryBoard(CCBlocks.MECHANICAL_ROOT_PULLER, "root_puller", CCScenes::rootPuller, AllPonderTags.KINETIC_APPLIANCES);
    }
}
