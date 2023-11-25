package com.supplemateservice.data;


import com.supplemateservice.model.Role;

import java.util.List;

public interface RoleDao {
    Role getRoleById(int id);
    Role getRoleByRole(String role);
    List<Role> getAllRoles();
    void deleteRole(int id);
    void updateRole(Role role);
    Role createRole(Role role);
}
