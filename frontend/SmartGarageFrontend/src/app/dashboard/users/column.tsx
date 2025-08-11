"use client";

import { ColumnDef } from "@tanstack/react-table";
import { Badge } from "@/components/ui/badge";
import { Checkbox } from "@/components/ui/checkbox";
import { Button } from "@/components/ui/button";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
    AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import { DataTableColumnHeader } from "@/components/data-table/data-table-column-header";
import {
    Briefcase,
    MoreHorizontal,
    Pencil,
    ShieldCheck,
    Trash2,
    User,
    Text, // Added missing import
} from "lucide-react";

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
    role: UserRoleType | UserRole;
    address: string;
    phone: string;
};

interface ColumnsConfig {
    onEdit: (user: User) => void;
    onDelete: (userId: string) => Promise<void>;
}

/**
 * Normalizes role to UserRole enum value
 * Handles both string and UserRoleType inputs
 */
const normalizeRole = (role: UserRoleType | UserRole): UserRole => {
    // If role is already a string (UserRole), return it directly
    if (typeof role === "string") {
        // Validate it's actually a valid UserRole
        return Object.values(UserRole).includes(role)
            ? role
            : UserRole.CLIENT; // Default to CLIENT if invalid
    }
    // If role is UserRoleType, return the roleName
    return role.roleName;
};

/**
 * Returns the appropriate icon for each role
 */
const getRoleIcon = (role: UserRole) => {
    switch (role) {
        case UserRole.ADMIN:
            return ShieldCheck;
        case UserRole.EMPLOYEE:
            return Briefcase;
        case UserRole.CLIENT:
        default:
            return User;
    }
};

export const getColumns = ({
                               onEdit,
                               onDelete,
                           }: ColumnsConfig): ColumnDef<User>[] => [
    {
        id: "select",
        header: ({ table }) => (
            <Checkbox
                checked={
                    table.getIsAllRowsSelected() ||
                    (table.getIsSomeRowsSelected() && "indeterminate")
                }
                onCheckedChange={(value) => table.toggleAllRowsSelected(!!value)}
                aria-label="Select all rows"
            />
        ),
        cell: ({ row }) => (
            <Checkbox
                checked={row.getIsSelected()}
                onCheckedChange={(value) => row.toggleSelected(!!value)}
                aria-label="Select row"
            />
        ),
        size: 32,
        enableSorting: false,
        enableHiding: false,
    },
    {
        id: "name",
        accessorKey: "name",
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Full Name" />
        ),
        cell: ({ row }) => <div className="font-medium">{row.getValue("name")}</div>,
        meta: {
            label: "Full Name",
            placeholder: "Search by name...",
            variant: "text",
            icon: Text,
        },
        enableColumnFilter: true,
    },
    {
        id: "username",
        accessorKey: "username",
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Username" />
        ),
        cell: ({ row }) => <div>{row.getValue("username")}</div>,
        meta: {
            label: "Username",
            placeholder: "Search by username...",
            variant: "text",
            icon: Text,
        },
        enableColumnFilter: true,
    },
    {
        id: "email",
        accessorKey: "email",
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Email" />
        ),
        cell: ({ row }) => <div>{row.getValue("email")}</div>,
        meta: {
            label: "Email",
            placeholder: "Search by email...",
            variant: "text",
            icon: Text,
        },
        enableColumnFilter: true,
    },
    {
        id: "role",
        accessorKey: "role",
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Role" />
        ),
        cell: ({ row }) => {
            const role = normalizeRole(row.getValue("role"));
            const Icon = getRoleIcon(role);
            return (
                <Badge variant="outline" className="capitalize flex items-center gap-1">
                    <Icon className="h-4 w-4" />
                    {role.toLowerCase()}
                </Badge>
            );
        },
        // NEW: Added accessorFn to normalize role data before sorting/filtering
        accessorFn: (row) => normalizeRole(row.role),
        // NEW: Custom filter function to handle normalized roles
        filterFn: (row, id, value) => {
            const role = normalizeRole(row.getValue(id));
            return value.includes(role);
        },
        // NEW: Custom sorting function to properly compare roles
        sortingFn: (rowA, rowB, columnId) => {
            const roleA = normalizeRole(rowA.getValue(columnId));
            const roleB = normalizeRole(rowB.getValue(columnId));
            return roleA.localeCompare(roleB);
        },
        meta: {
            filterVariant: "multi-select",
            filterOptions: Object.values(UserRole).map((role) => ({
                label: role,
                value: role,
            })),
        },
    },
    {
        id: "address",
        accessorKey: "address",
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Address" />
        ),
        cell: ({ row }) => (
            <div className="truncate max-w-[200px]">{row.getValue("address")}</div>
        ),
        meta: {
            label: "Address",
            placeholder: "Search by address...",
            variant: "text",
            icon: Text,
        },
        enableColumnFilter: true,
    },
    {
        id: "phone",
        accessorKey: "phone",
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Phone" />
        ),
        cell: ({ row }) => <div>{row.getValue("phone")}</div>,
        meta: {
            label: "Phone",
            placeholder: "Search by phone...",
            variant: "text",
            icon: Text,
        },
        enableColumnFilter: true,
    },
    {
        id: "actions",
        cell: ({ row }) => {
            const user = row.original;

            return (
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <Button variant="ghost" size="icon">
                            <MoreHorizontal className="h-4 w-4" />
                        </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                        <DropdownMenuItem
                            onClick={() => onEdit(user)}
                            className="cursor-pointer"
                        >
                            <Pencil className="h-4 w-4 mr-2" />
                            Edit
                        </DropdownMenuItem>

                        <AlertDialog>
                            <AlertDialogTrigger asChild>
                                <DropdownMenuItem
                                    onSelect={(e) => e.preventDefault()}
                                    className="text-red-600 cursor-pointer"
                                >
                                    <Trash2 className="h-4 w-4 mr-2" />
                                    Delete
                                </DropdownMenuItem>
                            </AlertDialogTrigger>
                            <AlertDialogContent>
                                <AlertDialogHeader>
                                    <AlertDialogTitle>Confirm Delete</AlertDialogTitle>
                                    <AlertDialogDescription>
                                        This action cannot be undone. This will permanently delete
                                        the user account and remove all associated data.
                                    </AlertDialogDescription>
                                </AlertDialogHeader>
                                <AlertDialogFooter>
                                    <AlertDialogCancel>Cancel</AlertDialogCancel>
                                    <AlertDialogAction
                                        onClick={async () => await onDelete(user.id)}
                                        className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
                                    >
                                        Delete
                                    </AlertDialogAction>
                                </AlertDialogFooter>
                            </AlertDialogContent>
                        </AlertDialog>
                    </DropdownMenuContent>
                </DropdownMenu>
            );
        },
        size: 32
    },
];