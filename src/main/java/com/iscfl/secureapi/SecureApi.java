package com.iscfl.secureapi;

import com.iscfl.secureapi.api.UserController;
import com.iscfl.secureapi.dao.PermissionRepository;
import com.iscfl.secureapi.dao.RolePermissionRepository;
import com.iscfl.secureapi.dao.RoleRepository;
import com.iscfl.secureapi.model.Permission;
import com.iscfl.secureapi.model.Role;
import com.iscfl.secureapi.model.RolePermission;
import com.iscfl.secureapi.utils.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class SecureApi {

	private static final Logger logger = LoggerFactory.getLogger(SecureApi.class);

	public static void main(String[] args) {
		SpringApplication.run(SecureApi.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(RoleRepository roleRepository,
								   PermissionRepository permissionRepository,
								   RolePermissionRepository rolePermissionRepository) {

		return args -> {
			List<Permission> permissionList = createPermissions(permissionRepository);

			Role defaultRole = null;
			Optional<Role> roleOptional =  roleRepository.findByName("DEFAULT_ROLE");
			if(roleOptional.isEmpty()){
				logger.debug("Creating default role");
				defaultRole = roleRepository.save(new Role("DEFAULT_ROLE", "DEFAULT_ROLE"));
				logger.debug("Default role created");
			}
			else{
				defaultRole = roleOptional.get();
			}

			for(Permission permission: permissionList){
				Optional<RolePermission> rolePermissionOptional = rolePermissionRepository
						.findByRoleIdAndPermissionId(permission.getId(), defaultRole.getId());
				if(rolePermissionOptional.isEmpty()) {
					rolePermissionRepository.save(new RolePermission(defaultRole, permission));
				}
			}
			rolePermissionRepository.flush();
			AppConstants.defaultRole = roleRepository.findById(defaultRole.getId()).get();
		};
	}

	private List<Permission> createPermissions(PermissionRepository permissionRepository){
		List<Permission> permissionList = Arrays.asList(
				new Permission("GET_ALL_USERS", "GET_ALL_USERS")
		);
		for(Permission permission: permissionList){
			Optional<Permission> permissionOptional = permissionRepository.findByName(permission.getName());
			if(permissionOptional.isEmpty()){
				logger.debug(String.format("Creating permission %s", permission.getName()));
				Permission insertedPermission = permissionRepository.save(permission);
				permission.setId(insertedPermission.getId());
				logger.debug(String.format("Created permission %s", permission.getName()));
			}
			else{
				permission.setId(permissionOptional.get().getId());
			}
		}
		return permissionList;
	}

}
