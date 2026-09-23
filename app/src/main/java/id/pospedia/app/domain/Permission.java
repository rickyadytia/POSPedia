package id.pospedia.app.domain;
public final class Permission {
    public static boolean canManageCatalog(Role r){return r==Role.PLATFORM_ADMIN||r==Role.TENANT_OWNER||r==Role.TENANT_ADMIN||r==Role.OUTLET_MANAGER;}
    public static boolean canAdjustStock(Role r){return canManageCatalog(r);}
    public static boolean canViewReports(Role r){return r!=Role.CASHIER;}
    public static boolean canManageUsers(Role r){return r==Role.PLATFORM_ADMIN||r==Role.TENANT_OWNER||r==Role.TENANT_ADMIN;}
    private Permission(){}
}
