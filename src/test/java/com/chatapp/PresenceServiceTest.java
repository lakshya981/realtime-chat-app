package com.chatapp;

import com.chatapp.service.PresenceService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PresenceServiceTest {

    @Test
    void userIsOnlineAfterJoining() {
        PresenceService presenceService = new PresenceService();
        presenceService.userJoined("general", "Aman");

        assertTrue(presenceService.isUserOnline("general", "Aman"));
    }

    @Test
    void userIsOfflineAfterLeaving() {
        PresenceService presenceService = new PresenceService();
        presenceService.userJoined("general", "Aman");
        presenceService.userLeft("general", "Aman");

        assertFalse(presenceService.isUserOnline("general", "Aman"));
    }

    @Test
    void userInOneRoomIsNotConsideredOnlineInAnotherRoom() {
        PresenceService presenceService = new PresenceService();
        presenceService.userJoined("general", "Aman");

        assertFalse(presenceService.isUserOnline("tech-talk", "Aman"));
    }
}
