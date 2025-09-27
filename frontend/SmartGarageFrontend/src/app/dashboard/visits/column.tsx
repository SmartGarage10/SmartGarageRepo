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
import {Euro, MoreHorizontal, Pencil, Text, Trash2} from "lucide-react";

import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent,
  AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle,
  AlertDialogTrigger } from "@/components/ui/alert-dialog";
import { Visit } from "@/types/visit";
import { format } from "date-fns";
import React from "react"; // Added missing import

interface ColumnsConfig {
  onEdit: (visit: Visit) => void;
  onDelete: (visitId: string) => Promise<void>;
  brandOptions?: { label: string; value: string }[];
  modelOptions?: { label: string; value: string }[];
}

export const getColumns = ({
                             onEdit,
                             onDelete,
                             brandOptions = [],
                             modelOptions = [],
                           }: ColumnsConfig): ColumnDef<Visit>[] => [
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
    id: "client",
    accessorFn: (row) => row.vehicle.client?.name ?? "—",
    header: ({ column }) => <DataTableColumnHeader column={column} title="Client" />,
    cell: ({ row }) => {
      const client = row.original.vehicle.client;
      return (
          <div>
            <div className="font-medium">{client?.name ?? "—"}</div>
            <div className="text-xs text-muted-foreground">{client?.email ?? "—"}</div>
          </div>
      );
    },
    meta: {
      label: "Client",
      placeholder: "Search by client...",
      variant: "text",
      icon: Text,
    },
    enableColumnFilter: true,
  },
  {
    id: "vehicle",
    accessorFn: (row) => `${row.vehicle.brand} ${row.vehicle.model}`, // Fixed the template literal
    header: ({ column }) => <DataTableColumnHeader column={column} title="Vehicle" />,
    cell: ({ row }) => {
      const vehicle = row.original.vehicle;
      return (
          <div>
            <div className="font-medium">{vehicle?.vehiclePlate ?? "—"}</div>
            <div className="text-xs text-muted-foreground">
              {vehicle?.brand ?? "—"} {vehicle?.model ?? "—"} ({vehicle?.year ?? "—"})
            </div>
          </div>
      );
    },
    meta: {
      label: "Vehicle",
      placeholder: "Search by vehicle...",
      variant: "text",
      icon: Text,
    },
    enableColumnFilter: true,
  },
  {
    id: "employee",
    accessorFn: (row) => row.employee?.name ?? "—",
    header: ({ column }) => <DataTableColumnHeader column={column} title="Employee" />,
    cell: ({ row }) => <div>{row.original.employee?.name ?? "—"}</div>,
    meta: {
      label: "Employee",
      placeholder: "Search by employee...",
      variant: "text",
      icon: Text,
    },
    enableColumnFilter: true,
  },
  {
    id: "status",
    accessorFn: (row) => row.status ?? "—",
    header: ({ column }) => <DataTableColumnHeader column={column} title="Status" />,
    cell: ({ row }) => <div>{row.original.status ?? "—"}</div>,
    meta: {
      label: "Status",
      placeholder: "Search by status...",
      variant: "text",
      icon: Text,
    },
    enableColumnFilter: true,
  },
  {
    id: "visitDate",
    accessorFn: (row) => row.visitDate ?? "—",
    header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Visit Date" />
    ),
    cell: ({ row }) => {
      const dateValue = row.original.visitDate;
      if (!dateValue) return <div>—</div>;

      return <div>{format(new Date(dateValue), "MMM dd, yyyy - HH:mm")}</div>;
    },
    meta: {
      label: "Date",
      placeholder: "Search by date...",
      variant: "text",
      icon: Text,
    },
  },
  {
    id: "amount",
    accessorFn: (row) => row.amount ?? 0,
    header: ({ column }) => <DataTableColumnHeader column={column} title="Amount" />,
    cell: ({ row }) => (
        <div>{row.original.amount != null ? `${row.original.amount.toFixed(2)} ${row.original.currency}` : "—"}</div>
    ),
    meta: {
      label: "Amount",
      placeholder: "Search by amount...",
      variant: "text",
      icon: Text,
    },
    enableColumnFilter: false,
  },
  {
    id: "pack",
    header: ({ column }) => <DataTableColumnHeader column={column} title="Pack" />,
    cell: ({ row }) => {
      const pack = row.original.pack;

      console.log('Pack data for row:', {
        hasPack: !!pack,
        packType: typeof pack,
        packValue: pack,
        packKeys: pack ? Object.keys(pack) : 'no pack'
      });

      if (!pack || typeof pack !== 'object') {
        return <div className="text-muted-foreground">—</div>;
      }

      return (
          <div className="flex flex-col">
            <div className="flex items-center text-sm font-medium capitalize">
              <span>{pack.packName?.toLowerCase() ?? 'Unnamed Pack'}</span>
            </div>
            <div className="text-xs text-muted-foreground">
              {(pack.services?.length ?? 0)} services
            </div>
          </div>
      );
    },
    meta: {
      label: "Pack",
      placeholder: "Search by pack...",
      variant: "text",
      icon: Text,
    },
    enableColumnFilter: true,
  },
  {
    id: "actions",
    cell: ({ row }) => {
      const visit = row.original;
      return (
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" size="icon">
                <MoreHorizontal className="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem onClick={() => onEdit(visit)} className="cursor-pointer">
                <Pencil className="h-4 w-4 mr-2" />
                Edit
              </DropdownMenuItem>
              <AlertDialog>
                <AlertDialogTrigger asChild>
                  <DropdownMenuItem onSelect={(e) => e.preventDefault()} className="text-red-600 cursor-pointer">
                    <Trash2 className="h-4 w-4 mr-2" />
                    Delete
                  </DropdownMenuItem>
                </AlertDialogTrigger>
                <AlertDialogContent>
                  <AlertDialogHeader>
                    <AlertDialogTitle>Confirm Delete</AlertDialogTitle>
                    <AlertDialogDescription>
                      This action cannot be undone. This will permanently delete the visit and remove all associated data.
                    </AlertDialogDescription>
                  </AlertDialogHeader>
                  <AlertDialogFooter>
                    <AlertDialogCancel>Cancel</AlertDialogCancel>
                    <AlertDialogAction
                        onClick={async () => await onDelete(visit.id)}
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
    size: 32,
  },
];