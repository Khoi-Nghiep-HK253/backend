package com.hcmut.divvy.entity.enums;

/**
 * Represents the lifecycle status of a debt record between two users.
 */
public enum DebtStatus {

    /** Debt has been created but not yet settled. */
    PENDING,

    /** Debt has been fully settled by the debtor. */
    SETTLED,

    /** Debt was cancelled (e.g. expense deleted or adjusted). */
    CANCELLED
}
