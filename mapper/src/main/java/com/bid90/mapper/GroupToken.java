package com.bid90.mapper;

import com.google.auto.service.AutoService;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;

import java.util.*;
import java.util.stream.Collectors;

import org.keycloak.protocol.ProtocolMapper;

@AutoService(ProtocolMapper.class)
public class GroupToken extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, UserInfoTokenMapper {

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    /*
     * The ID of the token mapper. Is public, because we need this id in our data-setup project to
     * configure the protocol mapper in keycloak.
     */
    public static final String PROVIDER_ID = "bid-group-mapper";

    public static final String PROPERTY_GROUP_NAME = "group-name";
    public static final String PROPERTY_SUBGROUP_NAME = "subgroup-name";
    public static final String PROPERTY_INCLUDE_FIELDS = "include-fields";


    static {
        // The builtin protocol mapper let the user define under which claim name (key)
        // the protocol mapper writes its value. To display this option in the generic dialog
        // in keycloak, execute the following method.
        // OIDCAttributeMapperHelper.addTokenClaimNameConfig(configProperties);
        // The builtin protocol mapper let the user define for which tokens the protocol mapper
        // is executed (access token, id token, user info). To add the config options for the different types
        // to the dialog execute the following method. Note that the following method uses the interfaces
        // this token mapper implements to decide which options to add to the config. So if this token
        // mapper should never be available for some sort of options, e.g. like the id token, just don't
        // implement the corresponding interface.
        ProviderConfigProperty groupNameProperty = new ProviderConfigProperty();
        groupNameProperty.setName(PROPERTY_GROUP_NAME);
        groupNameProperty.setLabel("Group name");
        groupNameProperty.setType(ProviderConfigProperty.STRING_TYPE);
        groupNameProperty.setHelpText("Group name");
        groupNameProperty.setDefaultValue("group");
        groupNameProperty.setSecret(false);

        ProviderConfigProperty subgroupNameProperty = new ProviderConfigProperty();
        subgroupNameProperty.setName(PROPERTY_SUBGROUP_NAME);
        subgroupNameProperty.setLabel("Subgroup name");
        subgroupNameProperty.setType(ProviderConfigProperty.STRING_TYPE);
        subgroupNameProperty.setHelpText("Subgroup name");
        subgroupNameProperty.setDefaultValue("group");
        subgroupNameProperty.setSecret(false);

        ProviderConfigProperty includeProperty = new ProviderConfigProperty();
        includeProperty.setName(PROPERTY_INCLUDE_FIELDS);
        includeProperty.setLabel("Group fields");
        includeProperty.setType(ProviderConfigProperty.LIST_TYPE);
        List<String> type = new ArrayList<>(3);
        type.add("Id & Name");
        type.add("Id");
        type.add("Name");
        includeProperty.setOptions(type);
        includeProperty.setHelpText("Include Group UUID or Group Name or both to the token");
        includeProperty.setSecret(false);



        configProperties.add(groupNameProperty);
        configProperties.add(subgroupNameProperty);
        configProperties.add(includeProperty);


        OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, GroupToken.class);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Group Membership++";
    }

    public String getDisplayCategory() {
        return TOKEN_MAPPER_CATEGORY;
    }

    @Override
    public String getHelpText() {
        return "Map user group membership";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    protected void setClaim(final IDToken token,
                            final ProtocolMapperModel mappingModel,
                            final UserSessionModel userSession,
                            final KeycloakSession keycloakSession,
                            final ClientSessionContext clientSessionCtx) {

        String groupName = mappingModel.getConfig().get(PROPERTY_GROUP_NAME);
        String subgroupName = mappingModel.getConfig().get(PROPERTY_SUBGROUP_NAME);

        String fields = mappingModel.getConfig().get(PROPERTY_INCLUDE_FIELDS).toLowerCase();

        var group = userSession.getUser().getGroupsStream()
                .map(groupModel -> GroupMapper.newGroup(groupModel,subgroupName,fields.toUpperCase())).collect(Collectors.toList());
        token.getOtherClaims().put(groupName, group);

    }


}
