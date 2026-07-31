package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.GroupResponse;
import com.hcmut.divvy.service.model.*;
import org.springframework.data.domain.Page;

public interface GroupService {

    GroupResponse create(CreateGroupModel model);

    Page<GroupResponse> findMyGroups(FindMyGroupsModel model);

    GroupResponse findById(GetGroupByIdModel model);

    GroupResponse update(UpdateGroupModel model);

    void delete(DeleteGroupModel model);
}
