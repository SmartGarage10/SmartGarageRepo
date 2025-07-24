"use client";

import {
  useReactTable,
  getCoreRowModel,
  getPaginationRowModel,
  getFilteredRowModel,
  getSortedRowModel,
} from "@tanstack/react-table";

import { columns, Visits } from "./column";
import { DataTable } from "@/components/data-table/data-table";
import { DataTableAdvancedToolbar } from "@/components/data-table/data-table-advanced-toolbar";
import { DataTableFilterList } from "@/components/data-table/data-table-filter-list";
import { SearchInput } from "@/components/data-table/data-search";
import { DataTableDateFilter } from "@/components/data-table/data-table-date-filter";

import * as React from "react";

export const data: Visits[] = [
{
    id: "1",
    username: "john_doe",
    licensePlate: "ABC-1234",
    brand: "Toyota",
    model: "Camry",
    employee: "Alice",
    date: new Date("2025-07-22"),
  },
  {
    id: "2",
    username: "jane_smith",
    licensePlate: "XYZ-5678",
    brand: "Honda",
    model: "Civic",
    employee: "Bob",
    date: new Date("2025-07-20"),
  },
  {
    id: "3",
    username: "mike_jones",
    licensePlate: "JKL-4321",
    brand: "Ford",
    model: "Fusion",
    employee: "Charlie",
    date: new Date("2025-06-15"),
  },
  {
    id: "4",
    username: "emma_watson",
    licensePlate: "MNO-8765",
    brand: "Jeep",
    model: "Grand Cherokee",
    employee: "Alice",
    date: new Date("2025-07-10"),
  },
  {
    id: "5",
    username: "chris_evans",
    licensePlate: "PQR-3456",
    brand: "Tesla",
    model: "Model S",
    employee: "Diana",
    date: new Date("2025-07-05"),
  },
  {
    id: "6",
    username: "linda_parker",
    licensePlate: "STU-7890",
    brand: "Chevrolet",
    model: "Malibu",
    employee: "Bob",
    date: new Date("2025-06-25"),
  },
  {
    id: "7",
    username: "daniel_lee",
    licensePlate: "VWX-6543",
    brand: "Toyota",
    model: "Corolla",
    employee: "Charlie",
    date: new Date("2025-07-21"),
  },
  {
    id: "8",
    username: "sara_connor",
    licensePlate: "DEF-2109",
    brand: "Audi",
    model: "A4",
    employee: "Diana",
    date: new Date("2025-06-30"),
  },
  {
    id: "9",
    username: "peter_parker",
    licensePlate: "GHI-8762",
    brand: "BMW",
    model: "320i",
    employee: "Alice",
    date: new Date("2025-07-19"),
  },
  {
    id: "10",
    username: "nancy_drew",
    licensePlate: "LMN-9087",
    brand: "Ford",
    model: "F-150",
    employee: "Bob",
    date: new Date("2025-07-01"),
  },
  {
    id: "11",
    username: "oliver_queen",
    licensePlate: "ZXC-1122",
    brand: "Honda",
    model: "CR-V",
    employee: "Charlie",
    date: new Date("2025-06-28"),
  },
  {
    id: "12",
    username: "harry_potter",
    licensePlate: "BNM-3344",
    brand: "Nissan",
    model: "Rogue",
    employee: "Diana",
    date: new Date("2025-07-03"),
  },
  {
    id: "13",
    username: "tony_stark",
    licensePlate: "QWE-5566",
    brand: "Toyota",
    model: "Sienna",
    employee: "Alice",
    date: new Date("2025-07-12"),
  },
  {
    id: "14",
    username: "bruce_wayne",
    licensePlate: "RTY-7788",
    brand: "Nissan",
    model: "Altima",
    employee: "Bob",
    date: new Date("2025-07-07"),
  },
  {
    id: "15",
    username: "clark_kent",
    licensePlate: "UIO-9900",
    brand: "Honda",
    model: "Accord",
    employee: "Charlie",
    date: new Date("2025-06-20"),
  },
  {
    id: "16",
    username: "diana_prince",
    licensePlate: "PAS-1188",
    brand: "Chevrolet",
    model: "Tahoe",
    employee: "Diana",
    date: new Date("2025-07-09"),
  },
  {
    id: "17",
    username: "steve_rogers",
    licensePlate: "LOL-2233",
    brand: "Honda",
    model: "Pilot",
    employee: "Alice",
    date: new Date("2025-07-17"),
  },
  {
    id: "18",
    username: "natasha_romanoff",
    licensePlate: "XOX-3344",
    brand: "Ram",
    model: "1500",
    employee: "Bob",
    date: new Date("2025-06-22"),
  },
  {
    id: "19",
    username: "bruce_banner",
    licensePlate: "BRB-4455",
    brand: "Ford",
    model: "Explorer",
    employee: "Charlie",
    date: new Date("2025-07-11"),
  },
  {
    id: "20",
    username: "wanda_maximoff",
    licensePlate: "OMG-5566",
    brand: "BMW",
    model: "X3",
    employee: "Diana",
    date: new Date("2025-07-14"),
  },
];

export default function Page() {
  const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    getSortedRowModel: getSortedRowModel(),
  });

  return (
    <DataTable table={table} className={"px-10"}>
      <DataTableAdvancedToolbar table={table} className={"px-0"}>
        <DataTableFilterList table={table} />

        <SearchInput
          value={(table.getColumn("licensePlate")?.getFilterValue() as string) ?? ""}
          onChange={(value) =>
            table.getColumn("licensePlate")?.setFilterValue(value)
          }
          placeholder="Search by License Plate..."
        />

        <DataTableDateFilter column={table.getColumn("date")} title={"Visit Date"} multiple={true}/>
      </DataTableAdvancedToolbar>
    </DataTable>
  );
}
