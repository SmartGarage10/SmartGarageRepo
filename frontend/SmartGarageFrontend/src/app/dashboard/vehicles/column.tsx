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
import { MoreHorizontal, Pencil, Text, Trash2 } from "lucide-react";

export type Vehicles = {
  id: string;
  licensePlate: string;
  vin: string;
  brand: string;
  model: string;
};

export const columns: ColumnDef<Vehicles>[] = [
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
      label: "License Plate",
      placeholder: "Search License Plate...",
      variant: "text",
      icon: Text,
    },
    // By default, the column will not be filtered. Set to `true` to enable filtering.
    enableColumnFilter: true,
  },
  {
    // Required: Unique identifier for the column
    id: "vin",
    // Required: Key to access the data, `accessorFn` can also be used
    accessorKey: "vin",
    // Optional: Custom header component
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="VIN" />
    ),
    // Optional: Custom cell component
    cell: ({ row }) => <div>{row.getValue("vin")}</div>,
    // By default, the column will not be filtered. Set to `true` to enable filtering.
    // Optional: Meta options for filtering, sorting, and view options
    meta: {
      label: "VIN",
      placeholder: "Search VIN...",
      variant: "text",
      icon: Text,
    },
    enableColumnFilter: true,
  },
  {
    id: "brand",
    accessorKey: "brand",
    header: ({ column }: { column: Column<Vehicles, unknown> }) => (
      <DataTableColumnHeader column={column} title="Brand" />
    ),
    cell: ({ row }) => <div>{row.getValue("brand")}</div>,
    // Optional: Meta options for filtering, sorting, and view options
    meta: {
      label: "Brand",
      placeholder: "Search Brand...",
      variant: "text",
      icon: Text,
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
    // Optional: Meta options for filtering, sorting, and view options
    meta: {
      label: "Model",
      placeholder: "Search Model...",
      variant: "text",
      icon: Text,
    },
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
