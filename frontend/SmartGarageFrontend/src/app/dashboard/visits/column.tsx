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

import { Vehicle } from "@/src/app/dashboard/vehicles/column";
import {
  AlertDialog, AlertDialogAction, AlertDialogCancel,
  AlertDialogContent, AlertDialogDescription, AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger
} from "@/components/ui/alert-dialog";

interface ColumnsConfig {
  onEdit: (visit: Visit) => void;
  onDelete: (visitId: string) => Promise<void>;
  brandOptions?: { label: string; value: string }[];
  modelOptions?: { label: string; value: string }[];
  isLoading?: boolean;
}

export type Visit = {
  id: string;
  username: string;
  licensePlate: string;
  brand: string;
  model: string;
  employee: string;
  date: Date;
};

export const getColumns = ({
                             onEdit,
                             onDelete,
                             brandOptions = [],
                             modelOptions = [],
                           }: ColumnsConfig): ColumnDef<Visit>[] =>
    [
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
        accessorFn: (row) => row.client.name,
        header: ({ column }) => <DataTableColumnHeader column={column} title="Client" className={undefined}/>,
        cell: ({ row }) => {
          const client = row.original.client;
          return (
              <div>
                <div className="font-medium">{client.name}</div>
                <div className="text-xs text-muted-foreground">{client.email}</div>
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
        id: "vehiclePlate",
        accessorKey: "vehiclePlate",
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="License Plate" className={undefined}/>
        ),
        cell: ({ row }) => <div className="font-medium">{row.getValue("vehiclePlate")}</div>,
        meta: {
          label: "License Plate",
          placeholder: "Search by license plate...",
          variant: "text",
          icon: Text,
        },
        enableColumnFilter: true,
      },
      {
        id: "brand",
        accessorKey: "brand",
        header: ({ column }) => <DataTableColumnHeader column={column} title="Brand" className={undefined}/>,
        cell: ({ row }) => <div className="font-medium">{row.getValue("brand")}</div>,
        meta: {
          label: "Brand",
          placeholder: "Filter brands...",
          variant: "select",
          options: [...new Map(brandOptions.map(item => [item.value, item]))].map(([_, option]) => ({
            label: option.label,
            value: option.value
          })),
        },
        enableColumnFilter: true,
      },
      {
        id: "model",
        accessorKey: "model",
        header: ({ column }) => <DataTableColumnHeader column={column} title="Model" className={undefined}/>,
        cell: ({ row }) => <div className="font-medium">{row.getValue("model")}</div>,
        meta: {
          label: "Model",
          placeholder: "Filter models...",
          variant: "multiSelect",
          icon: Text,
          options: [...new Map(modelOptions.map(item => [item.value, item]))].map(([_, option]) => ({
            label: option.label,
            value: option.value
          })),
        },
        enableColumnFilter: true,
      },
      {
        // Required: Unique identifier for the column
        id: "employee",
        // Required: Key to access the data, `accessorFn` can also be used
        accessorKey: "employee",
        // Optional: Custom header component
        header: ({column}) => (
            <DataTableColumnHeader column={column} title="Employee" className={undefined}/>
        ),
        // Optional: Custom cell component
        cell: ({ row }) => <div>{row.getValue("employee")}</div>,
        // By default, the column will not be filtered. Set to `true` to enable filtering.
        enableColumnFilter: true,

      },
      {
        id: "date",
        accessorKey: "date",
        header: ({column}) => (
            <DataTableColumnHeader column={column} title="Visit Date" className={undefined}/>
        ),
        cell: ({row}) => {
          const dateStr = row.getValue("date") as string;
          const date = new Date(dateStr);
          // Format date nicely, e.g. YYYY-MM-DD
          return <div>{date.toLocaleDateString()}</div>;
        },
      },
      {
        id: "actions",
        cell: ({ row }) => {
          const vehicle = row.original;
          return (
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" size="icon">
                    <MoreHorizontal className="h-4 w-4" />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  <DropdownMenuItem
                      onClick={() => onEdit(vehicle)}
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
                          This action cannot be undone. This will permanently delete the vehicle and remove all associated data.
                        </AlertDialogDescription>
                      </AlertDialogHeader>
                      <AlertDialogFooter>
                        <AlertDialogCancel>Cancel</AlertDialogCancel>
                        <AlertDialogAction
                            onClick={async () => await onDelete(vehicle.id)}
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
