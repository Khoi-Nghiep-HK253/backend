package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.GroupPreviewResponse;
import com.hcmut.divvy.dto.response.ShareLinkResponse;
import com.hcmut.divvy.service.model.CreateShareLinkModel;
import com.hcmut.divvy.service.model.GetGroupPreviewModel;
import com.hcmut.divvy.service.model.JoinViaLinkModel;
import com.hcmut.divvy.service.model.RevokeShareLinkModel;

import java.util.List;

public interface GroupShareLinkService {

    ShareLinkResponse createShareLink(CreateShareLinkModel model);

    List<ShareLinkResponse> getGroupShareLinks(Integer groupId, String currentUsername);

    ShareLinkResponse revokeShareLink(RevokeShareLinkModel model);

    GroupPreviewResponse getGroupPreview(GetGroupPreviewModel model);

    ShareLinkResponse joinGroupViaLink(JoinViaLinkModel model);
}
