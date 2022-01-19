package edu.course.dbcourse;

import lombok.Getter;

import java.util.*;

public enum Role {
    REGULAR("Добавить акудама","akudama"),
    LAWYER("Внести протокол", "protocol", "Справочник", "criminal_code"),
    TECHNICIAN("Гараж", "garage", "Оружейная", "armory"),
    PRISON_STAFF("Тюрьма", "prison"),
    MANAGEMENT ("Персонал", "personnel", "Операции", "mission_management"),
    MISSION_COMMANDER("Меню заданий", "missions");

    private final @Getter
    Map<String, String> availablePages;

    private static final Map<String, Set<Role>> divisionsToRoles;

    Role(String... availablePages){
       if(availablePages.length % 2 != 0 || availablePages.length == 0){
           this.availablePages = null;
           return;
       }
       this.availablePages = new HashMap<>();
        for (int i = 0; i < availablePages.length - 1; i+=2) {
            this.availablePages.put(availablePages[i], availablePages[i+1]);
        }
    }

    static {
        divisionsToRoles = new HashMap<>();
        final Set<Role> regularRole = new HashSet<>();
        regularRole.add(REGULAR);
        divisionsToRoles.put("Патрульный отдел", regularRole);
        divisionsToRoles.put("Отдел спецопераций", regularRole);
        divisionsToRoles.put("Палачи", regularRole);
        final Set<Role> lawyerRole = new HashSet<>();
        lawyerRole.add(REGULAR);
        lawyerRole.add(LAWYER);
        divisionsToRoles.put("Юридический отдел", lawyerRole);
        final Set<Role> technicianRole = new HashSet<>();
        technicianRole.add(REGULAR);
        technicianRole.add(TECHNICIAN);
        divisionsToRoles.put("Технический отдел", technicianRole);
        final Set<Role> prisonStaffRole = new HashSet<>();
        prisonStaffRole.add(PRISON_STAFF);
        divisionsToRoles.put("Тюремный персонал", prisonStaffRole);
        final Set<Role> managementRole = new HashSet<>();
        managementRole.add(REGULAR);
        managementRole.add(MANAGEMENT);
        divisionsToRoles.put("Управляющий отдел", managementRole);
    }

    public static Set<Role> getRolesByDivision(String division){
        return new HashSet<>(divisionsToRoles.get(division));
    }
}
