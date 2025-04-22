package com.example.rbac.Component;

import com.example.rbac.Model.Permissions;
import com.example.rbac.Model.Roles;
import com.example.rbac.Repository.PermissionsRepository;
import com.example.rbac.Repository.RolesRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class PermissionScanner {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PermissionsRepository permissionsRepository;

    @Autowired
    private RolesRepository rolesRepository;
    static class PermissionInfo {
        String permission;
        String url;
        String method;

        PermissionInfo(String permission, String url, String method) {
            this.permission = permission;
            this.url = url;
            this.method = method;
        }
    }

    @PostConstruct
    public void scanPermissions() {
        Set<PermissionInfo> permissionInfo = new HashSet<>();
        Set<String> roleSet = new HashSet<>();
        Map<String, Object> controllers = applicationContext.getBeansWithAnnotation(RestController.class);

        Pattern authorityPattern = Pattern.compile("hasAuthority\\('(.+?)'\\)");
        Pattern rolePattern = Pattern.compile("hasRole\\('(.+?)'\\)");

        for (Object controller : controllers.values()) {
            Class<?> clazz = AopUtils.getTargetClass(controller); // Handles Spring proxies

            String basePath = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping baseMapping = clazz.getAnnotation(RequestMapping.class);
                if (baseMapping.value().length > 0) {
                    basePath = baseMapping.value()[0];
                }
            }

            for (Method method : clazz.getDeclaredMethods()) {
                String fullPath = basePath;
                String httpMethod = "UNKNOWN";

                if (method.isAnnotationPresent(GetMapping.class)) {
                    GetMapping mapping = method.getAnnotation(GetMapping.class);
                    fullPath += mapping.value().length > 0 ? mapping.value()[0] : "";
                    httpMethod = "GET";
                } else if (method.isAnnotationPresent(PostMapping.class)) {
                    PostMapping mapping = method.getAnnotation(PostMapping.class);
                    fullPath += mapping.value().length > 0 ? mapping.value()[0] : "";
                    httpMethod = "POST";
                } else if (method.isAnnotationPresent(PutMapping.class)) {
                    PutMapping mapping = method.getAnnotation(PutMapping.class);
                    fullPath += mapping.value().length > 0 ? mapping.value()[0] : "";
                    httpMethod = "PUT";
                } else if (method.isAnnotationPresent(DeleteMapping.class)) {
                    DeleteMapping mapping = method.getAnnotation(DeleteMapping.class);
                    fullPath += mapping.value().length > 0 ? mapping.value()[0] : "";
                    httpMethod = "DELETE";
                }

                if (method.isAnnotationPresent(PreAuthorize.class)) {
                    String preAuthValue = method.getAnnotation(PreAuthorize.class).value();

                    Matcher authMatcher = authorityPattern.matcher(preAuthValue);
                    while (authMatcher.find()) {
                        String permission = authMatcher.group(1);
                        permissionInfo.add(new PermissionInfo(permission, fullPath, httpMethod));
                    }

                    Matcher roleMatcher = rolePattern.matcher(preAuthValue);
                    while (roleMatcher.find()) {
                        roleSet.add(roleMatcher.group(1));
                    }
                }
            }
        }

        // Fetch all existing permissions in one query

        List<Permissions> existingPermissions = permissionsRepository.findAll();
        Set<String> existingPermissionValues = existingPermissions.stream()
                .map(Permissions::getPermission)
                .collect(Collectors.toSet());

        // Fetch all existing roles in one query

        List<Roles> existingRoles = rolesRepository.findAll();
        Set<String> existingRoleValues = existingRoles.stream()
                .map(Roles::getName)
                .collect(Collectors.toSet());

        // Filter only new ones
        List<Permissions> newPermissions = permissionInfo.stream()
                .filter(p -> !existingPermissionValues.contains(p.permission))
                .map(p -> {
                    Permissions permission = new Permissions();
                    permission.setPermission(p.permission);

                    // Generate name from permission string
                    String[] parts = p.permission.split("_");
                    StringBuilder nameBuilder = new StringBuilder();

                    if (parts.length > 1) {
                        switch (parts[1].toLowerCase()) {
                            case "create":
                                nameBuilder.append("Create new ");
                                break;
                            case "delete":
                                nameBuilder.append("Delete ");
                                break;
                            case "update":
                                nameBuilder.append("Update ");
                                break;
                            case "view":
                                nameBuilder.append("View ");
                                break;
                            default:
                                nameBuilder.append(capitalize(parts[1])).append(" ");
                                break;
                        }
                        nameBuilder.append(parts[0].replace("_", " "));
                    } else {
                        nameBuilder.append(capitalize(p.permission.replace("_", " ")));
                    }

                    permission.setName(nameBuilder.toString().trim());
                    permission.setDescription("URL: " + p.url + ", Method: " + p.method + ", Authority: " + p.permission );

                    return permission;
                })
                .collect(Collectors.toList());

        List<Roles> newRoles = roleSet.stream()
                .filter(r -> !existingRoleValues.contains(r))
                .map(roleName -> {
                    Roles role = new Roles();
                    role.setName(roleName);

                    // Generate a description: remove "ROLE_", capitalize nicely
                    String cleanName = roleName.replace("ROLE_", "").replace("_", " ");
                    String description = "Role for " + cleanName.substring(0, 1).toUpperCase() + cleanName.substring(1).toLowerCase();
                    role.setDescription(description);

                    return role;
                })
                .collect(Collectors.toList());

        // Save new permissions
        if (!newPermissions.isEmpty()) {
            permissionsRepository.saveAll(newPermissions);
            newPermissions.forEach(p -> System.out.println("Inserted permission: " + p.getPermission()));
        } else {
            System.out.println("No new permissions to insert.");
        }
        // Save new roles
        if (!newRoles.isEmpty()) {
            rolesRepository.saveAll(newRoles);
            newRoles.forEach(r -> System.out.println("Inserted role: " + r.getName()));
        } else {
            System.out.println("No new roles to insert.");
        }
    }

    private String capitalize(String word) {
        if (word == null || word.isEmpty()) return word;
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
}

