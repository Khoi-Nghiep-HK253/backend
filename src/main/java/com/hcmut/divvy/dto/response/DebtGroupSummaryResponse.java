package com.hcmut.divvy.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtGroupSummaryResponse {
    private List<DebtPairSummaryResponse> pairs;
}
