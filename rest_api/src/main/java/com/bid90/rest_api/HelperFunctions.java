package com.bid90.rest_api;

import com.bid90.rest_api.dto.GroupDTO;
import org.keycloak.models.GroupModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HelperFunctions {
    public static List<String> getIds(Stream<GroupModel> groupsStream) {
        return groupsStream.map(groupModel -> HelperFunctions.groupModelToList(groupModel))
                       .flatMap(List::stream)
         .collect(Collectors.toList());
    }

    public static Boolean checkIfContainIds(List<GroupDTO> list, List<String> ids) {
        for (var g: list){
            if(ids.contains(g.getId())){
                return true;
            }else {
                if(g.getGroups() == null || g.getGroups().size() == 0) continue;
                if(checkIfContainIds(g.getGroups(),ids)) {
                    return true;
                }
            }
        }
        return false;
    }
    private static List<String> groupModelToList(GroupModel groupModel){
        List<String> ids = new ArrayList<>();
        ids.add(groupModel.getId());
        if(groupModel.getSubGroupsStream() != null && groupModel.getSubGroupsStream().count()> 0){
            var sub = groupModel.getSubGroupsStream().map(sg-> groupModelToList(sg))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            ids.addAll(sub);
        }
        return ids;
    }


}
