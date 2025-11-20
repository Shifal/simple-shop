package com.simpleshop.service;

import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;

import java.util.List;

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

            if (response.getStatus() != 201) {
                response.close();
                return null;
            }

            String kcId = CreatedResponseUtil.getCreatedId(response);
            response.close();

            // Assign Role
            RoleRepresentation role = keycloakAdmin.realm(realm).roles().get(roleName).toRepresentation();

            keycloakAdmin.realm(realm).users().get(kcId).roles().realmLevel().add(List.of(role));

            // Assign USER Group
            List<GroupRepresentation> groups = keycloakAdmin.realm(realm).groups().groups();

            GroupRepresentation userGroup = groups.stream().filter(g -> g.getName().equalsIgnoreCase("USER")).findFirst().orElse(null);

            if (userGroup != null) {
                keycloakAdmin.realm(realm).users().get(kcId).joinGroup(userGroup.getId());
            }

            return kcId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteUserByKeycloakId(String keycloakId) {
        try {
            keycloakAdmin.realm(realm).users().get(keycloakId).remove();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateKeycloakUser(String keycloakId, String username, String email, String firstName, String lastName, String newPassword) {
        try {
            UserResource userResource = keycloakAdmin.realm(realm).users().get(keycloakId);

            UserRepresentation rep = userResource.toRepresentation();
            rep.setUsername(username);
            rep.setEmail(email);
            rep.setFirstName(firstName);
            rep.setLastName(lastName);

            userResource.update(rep);

            // Update password ONLY if provided
            if (newPassword != null && !newPassword.isBlank()) {
                CredentialRepresentation cred = new CredentialRepresentation();
                cred.setType(CredentialRepresentation.PASSWORD);
                cred.setValue(newPassword);
                cred.setTemporary(false);
                userResource.resetPassword(cred);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
