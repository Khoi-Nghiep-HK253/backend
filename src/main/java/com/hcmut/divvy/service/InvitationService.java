package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.AcceptInvitationResponse;
import com.hcmut.divvy.dto.response.InvitationResponse;
import com.hcmut.divvy.dto.response.InvitationStatusResponse;
import com.hcmut.divvy.service.model.*;

import java.util.List;

public interface InvitationService {

    InvitationResponse sendInvitation(SendInvitationModel model);

    List<InvitationResponse> getGroupInvitations(GetGroupInvitationsModel model);

    List<InvitationResponse> getMyInvitations(GetMyInvitationsModel model);

    AcceptInvitationResponse acceptInvitation(AcceptInvitationModel model);

    InvitationStatusResponse declineInvitation(DeclineInvitationModel model);

    InvitationStatusResponse revokeInvitation(RevokeInvitationModel model);

    InvitationResponse getInvitationByToken(String token);

    AcceptInvitationResponse acceptInvitationByToken(String token, String currentUsername);
}
