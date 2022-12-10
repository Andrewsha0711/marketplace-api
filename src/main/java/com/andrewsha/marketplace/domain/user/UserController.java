package com.andrewsha.marketplace.domain.user;

import java.util.UUID;
import javax.validation.Valid;
import com.andrewsha.marketplace.domain.user.request.AddPermissionForm;
import com.andrewsha.marketplace.domain.user.request.CreateUserForm;
import com.andrewsha.marketplace.domain.user.request.UpdateUserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getUsers(int page, int size) {
        Page<User> usersPage = this.userService.getUsers(page, size);
        return ResponseEntity.ok(usersPage.getContent());
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getUser(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(this.userService.getUser(id));
    }

    @GetMapping(path = "/me")
    public ResponseEntity<?> getAuthenticatedUser(Authentication authentication) {
        return ResponseEntity.ok(this.userService.getAuthenticatedUser(authentication));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserForm user) {
        return ResponseEntity.ok(this.userService.createUser(user));
    }

    @PatchMapping(path = "{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> patchUser(@PathVariable("id") UUID id,
            @Valid @RequestBody UpdateUserForm userDetails) {
        return ResponseEntity.ok(this.userService.patchUser(id, userDetails));
    }

    @PutMapping(path = "{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<?> putUser(
            @PathVariable("id") UUID id, @Valid @RequestBody UpdateUserForm userDetails) {
        return ResponseEntity.ok(this.userService.putUser(id, userDetails));
    }

    @DeleteMapping(path = "{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<?> deleteUser(@PathVariable("id") UUID id) {
        this.userService.deleteUser(id);
        return ResponseEntity.ok("user with id " + id + " successfully deleted");
    }

    @PutMapping(path = "{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> addRole(@PathVariable("userId") UUID userId,
            @PathVariable("roleId") Long roleId, Authentication authentication) {
        return ResponseEntity.ok(this.userService.addRole(userId, roleId, authentication));
    }

    @PutMapping(path = "{userId}/permissions/{permissionId}")
    public ResponseEntity<?> addPermission(@PathVariable("userId") UUID userId,
            @PathVariable("permissionId") @Valid @RequestBody AddPermissionForm form){
        return ResponseEntity.ok(this.userService.addPermission(userId, form));
    }
}
