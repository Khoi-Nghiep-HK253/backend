package com.hcmut.divvy.entity.enums;

/**
 * Represents the type of domain entity that a
 * {@link com.hcmut.divvy.entity.MediaAttachment}
 * is associated with.
 *
 * <ul>
 * <li>{@link #EXPENSE} – Receipt / invoice images attached to an expense
 * record.</li>
 * <li>{@link #SETTLEMENT} – Proof-of-payment images (bank transfer screenshots)
 * for a settlement.</li>
 * <li>{@link #USER_AVATAR} – Profile photo of a user.</li>
 * <li>{@link #GROUP_AVATAR} – Thumbnail / logo image of a group.</li>
 * <li>{@link #GROUP_COVER} – Wide cover / banner image of a group.</li>
 * </ul>
 */
public enum MediaEntityType {
    EXPENSE,
    SETTLEMENT,
    USER_AVATAR,
    GROUP_AVATAR,
    GROUP_COVER
}
