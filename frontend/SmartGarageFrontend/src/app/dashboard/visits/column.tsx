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
import { MoreHorizontal, Pencil, Text, Trash2 } from "lucide-react";

import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent,
  AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle,
  AlertDialogTrigger } from "@/components/ui/alert-dialog";
import {User} from "@/src/app/dashboard/vehicles/column";
import { format } from "date-fns";

export interface Vehicle {
  id: string;
  vehiclePlate: string;
  vin: string;
  client: User;
  brand: string;
  model: string;
  year: string;
}

export type Visit = {
  id: string;
  vehicle: Vehicle;
  employee: User;
  visitDate: string;
  status: string;
  amount: number;
  currency: string;
  pack: { pack: string };
};

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
    accessorFn: (row) =>
        `${row.vehicle})`,
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
    enableColumnFilter: true,
  },
  {
    id: "employee",
    accessorFn: (row) => row.employee?.name ?? "—",
    header: ({ column }) => <DataTableColumnHeader column={column} title="Employee" />,
    cell: ({ row }) => <div>{row.original.employee?.name ?? "—"}</div>,
    enableColumnFilter: true,
  },
  {
    id: "status",
    accessorFn: (row) => row.status ?? "—",
    header: ({ column }) => <DataTableColumnHeader column={column} title="Status" />,
    cell: ({ row }) => <div>{row.original.status ?? "—"}</div>,
    enableColumnFilter: true,
  },
  {
    id: "visitDate",
    header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Visit Date" />
    ),
    cell: ({ row }) => {
      const dateValue = row.original.visitDate;
      if (!dateValue) return <div>—</div>;

      return <div>{format(new Date(dateValue), "MMM dd, yyyy HH:mm")}</div>;
    }
  },
  {
    id: "amount",
    accessorFn: (row) => row.amount ?? 0,
    header: ({ column }) => <DataTableColumnHeader column={column} title="Amount" />,
    cell: ({ row }) => (
        <div>{row.original.amount != null ? `${row.original.amount.toFixed(2)} ${row.original.currency}` : "—"}</div>
    ),
    enableColumnFilter: false,
  },
  {
    id: "pack",
    header: ({ column }) => <DataTableColumnHeader column={column} title="Pack" className={undefined}/>,
    cell: ({ row }) => <div>{row.original.pack.pack ?? "—"}</div>,
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
