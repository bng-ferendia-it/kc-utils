package com.bid90.mapper;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
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
    public static final String PROPERTY_INCLUDE_ID = "include-id";
    public static final String PROPERTY_INCLUDE_NAME = "include-name";

    static {
        // The builtin protocol mapper let the user define under which claim name (key)
        // the protocol mapper writes its value. To display this option in the generic dialog
        // in keycloak, execute the following method.
        OIDCAttributeMapperHelper.addTokenClaimNameConfig(configProperties);
        // The builtin protocol mapper let the user define for which tokens the protocol mapper
        // is executed (access token, id token, user info). To add the config options for the different types
        // to the dialog execute the following method. Note that the following method uses the interfaces
        // this token mapper implements to decide which options to add to the config. So if this token
        // mapper should never be available for some sort of options, e.g. like the id token, just don't
        // implement the corresponding interface.
        ProviderConfigProperty idProperty = new ProviderConfigProperty();
        idProperty.setName(PROPERTY_INCLUDE_ID);
        idProperty.setLabel("Include Group UUID");
        idProperty.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        idProperty.setDefaultValue(false);
        idProperty.setHelpText("Include Group UUID to the token");
        idProperty.setSecret(true);

        ProviderConfigProperty nameProperty = new ProviderConfigProperty();
        nameProperty.setName(PROPERTY_INCLUDE_NAME);
        nameProperty.setLabel("Include Group Name");
        nameProperty.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        nameProperty.setDefaultValue(true);
        nameProperty.setHelpText("Include Group Name to the token");
        nameProperty.setSecret(true);

        configProperties.add(nameProperty);
        configProperties.add(idProperty);
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
        boolean includeId = mappingModel.getConfig().get(PROPERTY_INCLUDE_ID).toLowerCase().contains("true");
        boolean includeName = mappingModel.getConfig().get(PROPERTY_INCLUDE_NAME).toLowerCase().contains("true");
        String claimName = mappingModel.getConfig().get("claim.name");
        if (includeId || includeName) {
            List<Object> groups = userSession.getUser().getGroupsStream().map(groupModel -> {
                if (includeId && includeName) {
                    Map<String, String> group = new HashMap<>();
                    group.put("id", groupModel.getId());
                    group.put("name", groupModel.getName());
                    return group;
                } else {
                    String group = null;
                    if (includeId) {
                        group = groupModel.getId();
                    }
                    if (includeName) {
                        group = groupModel.getName();
                    }
                    return group;
                }
            }).collect(Collectors.toList());
            token.getOtherClaims().put(claimName, groups);

        }
    }


}
