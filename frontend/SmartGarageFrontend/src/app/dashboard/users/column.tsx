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
import { DataTableColumnHeader } from "@/components/data-table/data-table-column-header";
import {
  Briefcase,
  MoreHorizontal,
  Pencil,
  ShieldCheck,
  Text,
  Trash2,
  User,
} from "lucide-react";

export enum UserRole {
  ADMIN = "ADMIN",
  EMPLOYEE = "EMPLOYEE",
  CLIENT = "CLIENT",
}

export type Users = {
  id: string;
  username: string;
  email: string;
  role: UserRole;
  phone: string;
};

export const columns: ColumnDef<Users>[] = [
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
    // Required: Unique identifier for the column
    id: "username",
    // Required: Key to access the data, `accessorFn` can also be used
    accessorKey: "username",
    // Optional: Custom header component
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Username" />
    ),
    // Optional: Custom cell component
    cell: ({ row }) => <div>{row.getValue("username")}</div>,
    // Optional: Meta options for filtering, sorting, and view options
    meta: {
      label: "Username",
      placeholder: "Search users...",
      variant: "text",
      icon: Text,
    },
    // By default, the column will not be filtered. Set to `true` to enable filtering.
    enableColumnFilter: true,
  },
  {
    // Required: Unique identifier for the column
    id: "email",
    // Required: Key to access the data, `accessorFn` can also be used
    accessorKey: "email",
    // Optional: Custom header component
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Email" />
    ),
    // Optional: Custom cell component
    cell: ({ row }) => <div>{row.getValue("email")}</div>,
    // By default, the column will not be filtered. Set to `true` to enable filtering.
    enableColumnFilter: true,
  },
  {
    id: "role",
    accessorKey: "role",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Roles" />
    ),
    cell: ({ cell }) => {
      const value = cell.getValue() as Users["role"];

      // Choose the icon based on the role
      let Icon;
      switch (value) {
        case UserRole.ADMIN.toString():
          Icon = ShieldCheck;
          break;
        case UserRole.EMPLOYEE.toString():
          Icon = Briefcase; // replace with actual import
          break;
        case UserRole.CLIENT.toString():
          Icon = User; // replace with actual import
          break;
      }

      return (
        <Badge variant="outline" className="capitalize flex items-center gap-1">
          <Icon className="h-4 w-4" />
          {value}
        </Badge>
      );
    },
    filterFn: (row, columnId, filterValue) => {
      const cellValue = row.getValue(columnId);
      return filterValue?.includes(cellValue);
    },
    enableColumnFilter: true,
  },
  {
    id: "phone",
    accessorKey: "phone",
    header: ({ column }: { column: Column<Users, unknown> }) => (
      <DataTableColumnHeader column={column} title="Phone" />
    ),
    cell: ({ row }) => <div>{row.getValue("phone")}</div>,
    enableColumnFilter: true,
  },
  {
    id: "actions",
    cell: function Cell() {
      return (
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="icon">
              <MoreHorizontal className="h-4 w-4" />
              <span className="sr-only">Open menu</span>
            </Button>
          </DropdownMenuTrigger>

          <DropdownMenuContent align="end">
            <DropdownMenuItem onSelect={() => console.log("Edit clicked")}>
              <Pencil className="h-4 w-4 mr-2" />
              Edit
            </DropdownMenuItem>
            <DropdownMenuItem
              onSelect={() => console.log("Delete clicked")}
              className="text-red-600 focus:bg-red-50"
            >
              <Trash2 className="h-4 w-4 mr-2 text-red-600"/>
              Delete
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      );
    },
    size: 32,
  },
];
