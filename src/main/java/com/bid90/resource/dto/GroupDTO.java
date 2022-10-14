package com.bid90.resource.dto;

import lombok.Data;
import org.keycloak.models.GroupModel;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class GroupDTO {
    String id;
    String name;
    List<GroupDTO> groups;

    public GroupDTO(GroupModel groupModel) {
        this.id = groupModel.getId();
        this.name = groupModel.getName();
        this.groups = groupModel.getSubGroupsStream()
                .map(gm -> new GroupDTO(gm)).collect(Collectors.toList());
        if(this.groups.size() == 0) this.groups = null;
    }
}
