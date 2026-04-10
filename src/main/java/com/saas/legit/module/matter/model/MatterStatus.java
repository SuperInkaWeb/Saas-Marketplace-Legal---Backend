package com.saas.legit.module.matter.model;

public enum MatterStatus {
    OPEN,           // Newly opened matter
    IN_PROGRESS,    // Active work ongoing
    PENDING_CLIENT, // Waiting for client input/documents
    IN_LITIGATION,  // Case is in court/hearings
    SETTLED,        // Agreement reached
    CLOSED          // Matter finalized and archived
}
