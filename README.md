# kc-utils
![](https://img.shields.io/badge/Keycloak-19.0.0-blue)
![](https://img.shields.io/badge/Java-11-white?color=5805ff)
 
kc-utils contain:
* [Keycloak OIDC protocol token mapper](#keycloak-oidc-protocol-token-mapper)
* [Keycloak REST endpoint/resource extension](#keycloak-rest-endpointresource-extension)

## Building and deploy project
To build the project simply clone it into a directory and build with maven like:
```shell
git clone https://github.com/bng-ferendia-it/kc-utils.git
cd kc-utils
mvn clean package
```
Once you have a JAR file, and you want to deploy, you just need to copy the JAR to the Keycloak providers/ directory.


## Keycloak OIDC protocol token mapper
* GroupToken

#### How to use GroupToken
Login in to Admin Console, select Realm and go to Clients, after that select wished client and click on the Client scopes, then click on the blue link (name_client-dedicated), click on the button Add Mapper (By configuration), from the list select "Group Membership++".


## Keycloak REST endpoint/resource extension

* List users by group: "/realms/{realmName}/bid-user-rest-resource/users/group/{groupId}"
* Get all user: "/realms/{realmName}/bid-user-rest-resource/users}"
* Get user by id: "/realms/{realmName}/bid-user-rest-resource/user/{userId}"
```
curl --location --request GET 'http://localhost:8080/realms/Test/bid-user-rest-resource/users/group/{groupId}' \
--header 'Authorization: Bearer token'
```
