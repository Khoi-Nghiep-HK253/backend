package com.hcmut.divvy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGroupRequest {

    @NotBlank(message = "Group name is required")
    @Size(max = 150, message = "Group name must not exceed 150 characters")
    private String name;

    /** Pick an existing category by ID. Mutually exclusive with {@link #categoryName}. */
    private Integer categoryId;

    /**
     * Pick a category by name: matched case-insensitively against existing
     * categories, or created on the spot if none matches. Mutually exclusive with
     * {@link #categoryId}.
     */
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String categoryName;

    private String note;

    private LocalDate startDate;

    private LocalDate endDate;
}
