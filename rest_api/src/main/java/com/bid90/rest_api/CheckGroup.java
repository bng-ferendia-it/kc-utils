package com.bid90.rest_api;

import com.bid90.rest_api.dto.GroupDTO;

import java.util.List;

public class CheckGroup {
    public static Boolean check(List<GroupDTO> list, String id) {
       for (var g: list){
           if(g.getId().contains(id)){
               return true;
           }else {
               if(g.getGroups() == null || g.getGroups().size() == 0) continue;
               if(check(g.getGroups(),id)) {
                   return true;
               }
           }
       }
        return false;
    }
}
