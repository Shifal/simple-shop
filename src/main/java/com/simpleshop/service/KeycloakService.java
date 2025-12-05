package com.simpleshop.service;

import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;

import java.util.*;

@Service
public class KeycloakService {

    private final Keycloak keycloakAdmin;
    private final String realm;

    public KeycloakService(Keycloak keycloakAdmin, @Value("${keycloak.realm}") String realm) {
        this.keycloakAdmin = keycloakAdmin;
        this.realm = realm;
    }

    public String createKeycloakUser(String username, String email, String password, String roleName, boolean active, String firstName, String lastName) {
        try {
            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setEnabled(active);
            user.setEmailVerified(true);
            user.setFirstName(firstName);
            user.setLastName(lastName);

            CredentialRepresentation cred = new CredentialRepresentation();
            cred.setTemporary(false);
            cred.setType(CredentialRepresentation.PASSWORD);
            cred.setValue(password);
            user.setCredentials(List.of(cred));

            Response response = keycloakAdmin.realm(realm).users().create(user);
            System.out.println("Keycloak response status: " + response.getStatus());

            if (response.getStatus() != 201) {
                response.close();
                return null;
            }

            String kcId = CreatedResponseUtil.getCreatedId(response);
            System.out.println("createKeycloakUser : kcID 1 " + kcId);

            response.close();

            // Assign Role
            RoleRepresentation role = keycloakAdmin.realm(realm).roles().get(roleName).toRepresentation();

            keycloakAdmin.realm(realm).users().get(kcId).roles().realmLevel().add(List.of(role));

            // Assign USER Group
            List<GroupRepresentation> groups = keycloakAdmin.realm(realm).groups().groups();

            GroupRepresentation userGroup = groups.stream().filter(g -> g.getName().equalsIgnoreCase("USER")).findFirst().orElse(null);

            System.out.println("userGroup : userGroup" + userGroup);

            if (userGroup != null) {
                System.out.println("userGroup : userGroup" + userGroup);
                keycloakAdmin.realm(realm).users().get(kcId).joinGroup(userGroup.getId());
            }
            System.out.println("createKeycloakUser : kcID" + kcId);

            return kcId;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean deleteUserByKeycloakId(String keycloakId) {
        try {
            keycloakAdmin.realm(realm).users().get(keycloakId).remove();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public void updateKeycloakUser(String keycloakId, String username, String email, String firstName, String lastName) {
        try {
            UserRepresentation user = new UserRepresentation();

            // Only these 4 fields will be sent to Keycloak
            if (username != null) user.setUsername(username);
            if (firstName != null) user.setFirstName(firstName);
            if (lastName != null) user.setLastName(lastName);
            if (email != null) {
                user.setEmail(email);
                user.setEmailVerified(true);
            }
            user.setEnabled(true);
            keycloakAdmin.realm(realm).users().get(keycloakId).update(user);

        } catch (Exception ex) {
            System.err.println("Keycloak update failed: " + ex.getMessage());
            throw new RuntimeException("Keycloak update failed", ex);
        }
    }


    public void disableUser(String keycloakId) {
        UsersResource usersResource = keycloakAdmin.realm(realm).users();
        UserRepresentation user = usersResource.get(keycloakId).toRepresentation();
        user.setEnabled(false);
        usersResource.get(keycloakId).update(user);
    }

    public void enableUser(String keycloakId) {
        UsersResource usersResource = keycloakAdmin.realm(realm).users();
        UserRepresentation user = usersResource.get(keycloakId).toRepresentation();
        user.setEnabled(true);
        usersResource.get(keycloakId).update(user);
    }

}
