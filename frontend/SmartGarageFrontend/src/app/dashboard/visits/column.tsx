"use client";

import { ColumnDef } from "@tanstack/react-table";
import { Checkbox } from "@/components/ui/checkbox";
import { Button } from "@/components/ui/button";

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { DataTableColumnHeader } from "@/components/data-table/data-table-column-header";
import { FileInput, MoreHorizontal, Pencil, Text, Trash2 } from "lucide-react";

export type Visits = {
  id: string;
  username: string;
  licensePlate: string;
  brand: string;
  model: string;
  employee: string;
  date: Date;
};

export const columns: ColumnDef<Visits>[] = [
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
    id: "licensePlate",
    // Required: Key to access the data, `accessorFn` can also be used
    accessorKey: "licensePlate",
    // Optional: Custom header component
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="License Plate" />
    ),
    // Optional: Custom cell component
    cell: ({ row }) => <div>{row.getValue("licensePlate")}</div>,
    // Optional: Meta options for filtering, sorting, and view options
    meta: {
      label: "licensePlate",
      placeholder: "Search License Plate...",
      variant: "text",
      icon: Text,
    },
    // By default, the column will not be filtered. Set to `true` to enable filtering.
    enableColumnFilter: true,
  },
  {
    id: "brand",
    accessorKey: "brand",
    header: ({ column }: { column: Column<Vehicles, unknown> }) => (
      <DataTableColumnHeader column={column} title="Brand" />
    ),
    cell: ({ row }) => <div>{row.getValue("brand")}</div>,
    filterFn: (row, columnId, filterValue) => {
      const cellValue = row.getValue(columnId);
      return filterValue?.includes(cellValue);
    },
    enableColumnFilter: true,
  },
  {
    id: "model",
    accessorKey: "model",
    header: ({ column }: { column: Column<Vehicles, unknown> }) => (
      <DataTableColumnHeader column={column} title="Model" />
    ),
    cell: ({ row }) => <div>{row.getValue("model")}</div>,
    filterFn: (row, columnId, filterValue) => {
      const cellValue = row.getValue(columnId);
      return filterValue?.includes(cellValue);
    },
    enableColumnFilter: true,
  },
  {
    // Required: Unique identifier for the column
    id: "employee",
    // Required: Key to access the data, `accessorFn` can also be used
    accessorKey: "employee",
    // Optional: Custom header component
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Employee" />
    ),
    // Optional: Custom cell component
    cell: ({ row }) => <div>{row.getValue("employee")}</div>,
    // By default, the column will not be filtered. Set to `true` to enable filtering.
    enableColumnFilter: true,
  },
  {
    id: "date",
    accessorKey: "date",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Visit Date" />
    ),
    cell: ({ row }) => {
      const dateStr = row.getValue("date") as string;
      const date = new Date(dateStr);
      // Format date nicely, e.g. YYYY-MM-DD
      return <div>{date.toLocaleDateString()}</div>;
    },

    enableColumnFilter: true, // if you want to filter by date
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
              <FileInput className="h-4 w-4 mr-2" />
              Report
            </DropdownMenuItem>
            <DropdownMenuItem onSelect={() => console.log("Edit clicked")}>
              <Pencil className="h-4 w-4 mr-2" />
              Edit
            </DropdownMenuItem>
            <DropdownMenuItem
              onSelect={() => console.log("Delete clicked")}
              className="text-red-600 focus:bg-red-50"
            >
              <Trash2 className="h-4 w-4 mr-2 text-red-600" />
              Delete
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      );
    },
    size: 32,
  },
];
