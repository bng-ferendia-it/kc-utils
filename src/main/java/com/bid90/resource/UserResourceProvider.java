package com.bid90.resource;

import com.bid90.UserDTO;
import com.bid90.util.Pagination;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager.AuthResult;
import org.keycloak.services.resource.RealmResourceProvider;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.stream.Collectors;


public class UserResourceProvider implements RealmResourceProvider {
    KeycloakSession session;

    public UserResourceProvider(KeycloakSession keycloakSession) {
        this.session = keycloakSession;
    }

    @Override
    public Object getResource() {
        return this;
    }

    @Override
    public void close() {
    }

    @GET
    @Path("users/group/{groupId}")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    public Object groupUsers(@PathParam("groupId") String groupId,
                             @DefaultValue("1") @QueryParam("pageIndex") int pageIndex,
                             @DefaultValue("100") @QueryParam("pageSize") int pageSize) {
        var auth = authResult();
        var realmModel = auth.getSession().getRealm();
        var userGroupIdList = auth.getUser().getGroupsStream()
                .map(GroupModel::getId).collect(Collectors.toList());
        if (!userGroupIdList.contains(groupId)) {
            throw new ForbiddenException("Does not have permission to fetch users");
        }
        var users = session.users().getUsersStream(realmModel)
                .map(userModel -> new UserDTO(userModel))
                .filter(userDTO -> userDTO.getGroups().stream().filter(groupDTO -> groupDTO.getId().contains(groupId)).count() > 0)
                .collect(Collectors.toList());
        return Pagination.paginate(users, pageIndex, pageSize);
    }

    private AuthResult authResult() {
        AuthResult auth = new AppAuthManager.BearerTokenAuthenticator(session).authenticate();
        if (auth == null) {
            throw new NotAuthorizedException("Bearer");
        }
        if (auth.getToken().getRealmAccess() == null) {
            throw new ForbiddenException("Does not have permission to fetch users");
        }
        return auth;
    }


}
