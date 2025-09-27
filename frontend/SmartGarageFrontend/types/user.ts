export enum UserRole {
    ADMIN = "ADMIN",
    EMPLOYEE = "EMPLOYEE",
    CLIENT = "CLIENT",
}

export type UserRoleType = {
    roleId: string;
    roleName: UserRole;
};

export type User = {
    id: string;
    name: string;
    username: string;
    email: string;
    role?: UserRoleType;
    address: string;
    phone: string;
    avatar?: string
};