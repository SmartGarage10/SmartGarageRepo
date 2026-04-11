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
import {Calendar, Euro, MoreHorizontal, Pencil, Text, Trash2} from "lucide-react";

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

import {Visit, VisitItemType} from "@/types/visit";
import { format } from "date-fns";
import React from "react";

interface ColumnsConfig {
    onEdit: (visit: Visit) => void;
    onDelete: (visitId: string) => Promise<void>;
    brandOptions?: { label: string; value: string }[];
    modelOptions?: { label: string; value: string }[];
    statusFilterOptions?: { label: string; value: string }[];
    packFilterOptions?: { label: string; value: string }[];
}

export const getColumns = ({
                               onEdit,
                               onDelete,
                               brandOptions = [],
                               statusFilterOptions = [],
                               packFilterOptions = [],
                           }: ColumnsConfig): ColumnDef<Visit>[] => [
    {
        id: "select",
        header: ({ table }) => (
            <Checkbox
                checked={table.getIsAllRowsSelected()}
                indeterminate={table.getIsSomeRowsSelected() && !table.getIsAllRowsSelected()}
                onCheckedChange={(value) => table.toggleAllRowsSelected(!!value)}
                aria-label="Select all rows"
            />
        ),
        cell: ({ row }) => (
            <Checkbox
                checked={row.getIsSelected()}
                indeterminate={row.getIsSomeSelected() && !row.getIsSelected()}
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
        meta: { label: "Client", placeholder: "Search by client...", variant: "text", icon: Text },
        enableColumnFilter: true,
    },
    {
        id: "brand",
        accessorKey: "brand",
        header: ({ column }) => <DataTableColumnHeader column={column} title="Vehicle" />,
        cell: ({ row }) => {
            const v = row.original.vehicle;
            return (
                <div>
                    <div className="font-medium">{v?.vehiclePlate ?? "—"}</div>
                    <div className="text-xs text-muted-foreground">
                        {v?.brand ?? "—"} {v?.model ?? "—"} ({v?.year ?? "—"})
                    </div>
                </div>
            );
        },
        meta: {
            label: "Vehicle",
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
        id: "employee",
        accessorFn: (row) => row.employee?.name ?? "—",
        header: ({ column }) => <DataTableColumnHeader column={column} title="Employee" />,
        cell: ({ row }) => <div>{row.original.employee?.name ?? "—"}</div>,
        meta: { label: "Employee", placeholder: "Search by employee...", variant: "text", icon: Text },
        enableColumnFilter: true,
    },
    {
        id: "status",
        accessorFn: (row) => row.status ?? "—",
        header: ({ column }) => <DataTableColumnHeader column={column} title="Status" />,
        cell: ({ row }) => <div>{row.original.status ?? "—"}</div>,
        meta: {
            label: "Status",
            placeholder: "Filter by status...",
            variant: "select", // Change from "text" to "select"
            options: statusFilterOptions // Use the passed options
        },
        enableColumnFilter: true,
    },
    {
        id: "visitDate",
        accessorFn: (row) => row.visitDate ?? "—",
        header: ({ column }) => <DataTableColumnHeader column={column} title="Visit Date" />,
        cell: ({ row }) => {
            const dateValue = row.original.visitDate;
            return dateValue ? <div>{format(new Date(dateValue), "MMM dd, yyyy - HH:mm")}</div> : <div>—</div>;
        },
        meta: {
            label: "Date",
            placeholder: "Search by date...",
            variant: "date",
            icon: Calendar
        },
        enableColumnFilter: true,
    },
    {
        id: "amount",
        accessorFn: (row) => row.amount ?? 0,
        header: ({ column }) => <DataTableColumnHeader column={column} title="Amount" />,
        cell: ({ row }) =>
            row.original.amount != null ? (
                <div>{`${row.original.amount.toFixed(2)} ${row.original.currency}`}</div>
            ) : (
                <div>—</div>
            ),
        meta: { label: "Amount", placeholder: "Search by amount...", variant: "text", icon: Text },
        enableColumnFilter: false,
    },
    {
        id: "pack",
        accessorFn: (row) => {
            const visitItems = row.visitItems || [];
            const packItem = visitItems.find(item => item && item.pack != null);
            return packItem?.pack?.packName || "Custom Pack";
        },
        header: ({ column }) => <DataTableColumnHeader column={column} title="Pack" />,
        cell: ({ row }) => {
            const visit = row.original;
            const visitItems = visit.visitItems || [];

            // Find pack item
            const packItem = visitItems.find(item => item && item.pack != null);

            // ✅ CASE 1: VISIT HAS PACK
            if (packItem?.pack) {
                const packServicesCount = packItem.pack.services
                    ? packItem.pack.services.length
                    : 0;

                return (
                    <div className="flex flex-col">
                        <div className="flex items-center text-sm font-medium">
                            <span>{packItem.pack.packName}</span>
                        </div>
                        <div className="text-xs text-muted-foreground">
                            {packServicesCount} service{packServicesCount !== 1 ? "s" : ""}
                        </div>
                    </div>
                );
            }

            // ✅ CASE 2: CUSTOM PACK (NO PACK, ONLY SERVICES)
            const serviceCount = visitItems.filter(item =>
                item && (item.serviceItem || item.itemType === "SERVICE")
            ).length;

            return (
                <div className="flex flex-col">
                    <div className="flex items-center text-sm font-medium">
                        <span>Custom Pack</span>
                    </div>
                    <div className="text-xs text-muted-foreground">
                        {serviceCount} service{serviceCount !== 1 ? "s" : ""}
                    </div>
                </div>
            );
        },
        meta: {
            label: "Pack",
            placeholder: "Filter by pack...",
            variant: "select",
            options: packFilterOptions || []
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
