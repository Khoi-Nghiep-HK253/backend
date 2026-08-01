package com.hcmut.divvy.service.model;

import lombok.*;
import org.springframework.data.domain.Pageable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindMyGroupsModel {
    private String currentUsername;
    private Pageable pageable;
}
