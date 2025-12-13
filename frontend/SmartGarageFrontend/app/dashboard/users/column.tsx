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
    User2,
    Text,
} from "lucide-react";
import { User, UserRole } from "@/types/user";

interface ColumnsConfig {
    onEdit: (user: User) => void;
    onDelete: (userId: string) => Promise<void>;
    roleOptions?: { label: string; value: string }[];
}

/**
 * Normalizes role to UserRole enum value
 * Handles both string and object role inputs
 */
const normalizeRole = (role: any): UserRole => {
    if (typeof role === "string") {
        // Validate it's actually a valid UserRole
        return Object.values(UserRole).includes(role as UserRole)
            ? role as UserRole
            : UserRole.CLIENT; // Default to CLIENT if invalid
    }
    // If role is an object, return the roleName
    if (role && typeof role === "object" && role.roleName) {
        return role.roleName as UserRole;
    }
    return UserRole.CLIENT; // Default fallback
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
            return User2;
        default:
            return User2;
    }
};

export const getColumns = ({
                               onEdit,
                               onDelete,
                               roleOptions = [],
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
        filterFn: (row, columnId, filterValue) => {
            if (!filterValue || filterValue.length === 0) return true;
            const role = normalizeRole(row.getValue(columnId));
            return filterValue.includes(role);
        },
        meta: {
            label: "Role",
            placeholder: "Filter by role...",
            variant: "multiSelect",
            icon: User2,
            options: roleOptions,
        },
        enableColumnFilter: true,
    },
    {
        id: "address",
        accessorKey: "address",
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Address" />
        ),
        cell: ({ row }) => (
            <div className="truncate max-w-[200px]">{row.getValue("address") || "-"}</div>
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
        cell: ({ row }) => <div>{row.getValue("phone") || "-"}</div>,
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