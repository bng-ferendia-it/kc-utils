package com.bid90;

import lombok.Data;
import org.keycloak.models.UserModel;

import java.util.List;
import java.util.stream.Collectors;
@Data
public class UserDTO {
    String id;
    String email;
    String username;
    String firstname;
    String lastname;
    List<GroupDTO> groups;

    public UserDTO(UserModel userModel) {
        this.id= userModel.getId();
        this.email = userModel.getEmail();
        this.username = userModel.getUsername();
        this.firstname = userModel.getFirstName();
        this.lastname = userModel.getLastName();
        this.groups = userModel.getGroupsStream().map(groupModel -> new GroupDTO(groupModel.getId(),groupModel.getName()))
                .collect(Collectors.toList());
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public List<GroupDTO> getGroups() {
        return groups;
    }
}
