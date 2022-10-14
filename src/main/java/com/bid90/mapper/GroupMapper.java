package com.bid90.mapper;

import org.keycloak.models.GroupModel;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GroupMapper {

    public static Object newGroup(GroupModel groupModel,String groupName, String fields){
        HashMap<String,Object> group =  new HashMap<>();
        switch (fields){
            case "ID":
                group.put("id",groupModel.getId());
                break;
            case "NAME":
                group.put("name",groupModel.getName());
                break;
            default:
                group.put("id",groupModel.getId());
                group.put("name",groupModel.getName());
        }
        group.put(groupName,groupModel.getSubGroupsStream().map(sub-> GroupMapper.newGroup(sub,groupName,fields))
                .collect(Collectors.toList()));
        if(((List<Object>)group.get(groupName)).size()==0) group.replace(groupName, null);
        return group;
    }
}
