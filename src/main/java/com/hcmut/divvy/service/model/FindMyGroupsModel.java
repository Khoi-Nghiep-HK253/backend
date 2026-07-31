package com.hcmut.divvy.service.model;

import lombok.*;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindMyGroupsModel {
    private String currentUsername;
    private Pageable pageable;
}
