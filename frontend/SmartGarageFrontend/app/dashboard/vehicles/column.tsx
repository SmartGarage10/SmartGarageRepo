"use client";

import { ColumnDef } from "@tanstack/react-table";
import { Checkbox } from "@/components/ui/checkbox";
import { DataTableColumnHeader } from "@/components/data-table/data-table-column-header";
import { Text } from "lucide-react";
import { Vehicle } from "@/types/vehicle";
import {Dropdown} from "@/components/custom-components/item-drop-down";

interface ColumnsConfig {
    onEdit: (vehicle: Vehicle) => void;
    onDelete: (vehicleId: string) => Promise<void>;
    brandOptions?: { label: string; value: string }[];
    modelOptions?: { label: string; value: string }[];
    isLoading?: boolean;
}

const currentYear = new Date().getFullYear();

export const getColumns = ({
                               onEdit,
                               onDelete,
                               brandOptions = [],
                               modelOptions = [],
                           }: ColumnsConfig): ColumnDef<Vehicle>[] => [
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
        id: "user",
        accessorFn: (row) => row.user?.name,
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Client" />
        ),
        cell: ({ row }) => {
            const user = row.original.user;
            return (
                <div>
                    <div className="font-medium">{user?.name ?? "—"}</div>
                    <div className="text-xs text-muted-foreground">
                        {user?.email ?? ""}
                    </div>
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
            <DataTableColumnHeader column={column} title="License Plate" />
        ),
        cell: ({ row }) => (
            <div className="font-medium">{row.getValue("vehiclePlate")}</div>
        ),
        meta: {
            label: "License Plate",
            placeholder: "Search by license plate...",
            variant: "text",
            icon: Text,
        },
        enableColumnFilter: true,
    },
    {
        id: "vin",
        accessorKey: "vin",
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="VIN" />
        ),
        cell: ({ row }) => (
            <div className="font-medium">{row.getValue("vin")}</div>
        ),
        meta: {
            label: "VIN",
            placeholder: "Search by VIN...",
            variant: "text",
            icon: Text,
        },
        enableColumnFilter: true,
    },
    {
        id: "brand",
        accessorKey: "brand",
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Brand" />
        ),
        cell: ({ row }) => (
            <div className="font-medium">{row.getValue("brand")}</div>
        ),
        meta: {
            label: "Brand",
            placeholder: "Filter brands...",
            variant: "select",
            options: [
                ...new Map(
                    brandOptions.map((item) => [item.value, item])
                ).values(),
            ].map((option) => ({
                label: option.label,
                value: option.value,
            })),
        },
        enableColumnFilter: true,
    },
    {
        id: "model",
        accessorKey: "model",
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Model" />
        ),
        cell: ({ row }) => (
            <div className="font-medium">{row.getValue("model")}</div>
        ),
        meta: {
            label: "Model",
            placeholder: "Filter models...",
            variant: "multiSelect",
            options: [
                ...new Map(
                    modelOptions.map((item) => [item.value, item])
                ).values(),
            ].map((option) => ({
                label: option.label,
                value: option.value,
            })),
        },
        enableColumnFilter: true,
    },
    {
        id: "yearOfCreation",
        accessorKey: "yearOfCreation",
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Year" />
        ),
        cell: ({ row }) => (
            <div className="font-medium">
                {row.getValue("yearOfCreation")}
            </div>
        ),
        meta: {
            label: "Year",
            placeholder: "Search by year...",
            variant: "multiSelect",
            options: Array.from({ length: 30 }, (_, i) => {
                const year = currentYear - i;
                return { label: year.toString(), value: year.toString() };
            }),
        },
        enableColumnFilter: true,
    },
    {
        id: "actions",
        cell: ({ row }) => {
            const vehicle = row.original;
            return (
                <Dropdown
                    itemType="Vehicle"
                    onEdit={() => onEdit(vehicle)}
                    onDelete={() => onDelete(vehicle.id)}
                    showDuplicate={false}
                />
            );
        },
        size: 32,
    },
];
