"use client";

import {
  useReactTable,
  getCoreRowModel,
  getPaginationRowModel,
  getFilteredRowModel,
  getSortedRowModel,
} from "@tanstack/react-table";

import { columns, UserRole, Users } from "./column";
import { DataTable } from "@/components/data-table/data-table";
import { DataTableFacetedFilter } from "@/components/data-table/data-table-faceted-filter";
import { DataTableAdvancedToolbar } from "@/components/data-table/data-table-advanced-toolbar";
import { DataTableFilterList } from "@/components/data-table/data-table-filter-list";
import { SearchInput } from "@/components/data-table/data-search";

import * as React from "react";

export const data: Users[] = [
  { id: "1", username: "alice", email: "alice@example.com", role: UserRole.ADMIN, phone: "+1-555-0101" },
  { id: "2", username: "bob", email: "bob@example.com", role: UserRole.EMPLOYEE, phone: "+1-555-0102" },
  { id: "3", username: "carol", email: "carol@example.com", role: UserRole.CLIENT, phone: "+1-555-0103" },
  { id: "4", username: "david", email: "david@example.com", role: UserRole.ADMIN, phone: "+1-555-0104" },
  { id: "5", username: "emma", email: "emma@example.com", role: UserRole.EMPLOYEE, phone: "+1-555-0105" },
  { id: "6", username: "frank", email: "frank@example.com", role: UserRole.CLIENT, phone: "+1-555-0106" },
  { id: "7", username: "grace", email: "grace@example.com", role: UserRole.ADMIN, phone: "+1-555-0107" },
  { id: "8", username: "henry", email: "henry@example.com", role: UserRole.EMPLOYEE, phone: "+1-555-0108" },
  { id: "9", username: "isabella", email: "isabella@example.com", role: UserRole.CLIENT, phone: "+1-555-0109" },
  { id: "10", username: "jack", email: "jack@example.com", role: UserRole.ADMIN, phone: "+1-555-0110" },
  { id: "11", username: "kate", email: "kate@example.com", role: UserRole.EMPLOYEE, phone: "+1-555-0111" },
  { id: "12", username: "leo", email: "leo@example.com", role: UserRole.CLIENT, phone: "+1-555-0112" },
  { id: "13", username: "mia", email: "mia@example.com", role: UserRole.ADMIN, phone: "+1-555-0113" },
  { id: "14", username: "nate", email: "nate@example.com", role: UserRole.EMPLOYEE, phone: "+1-555-0114" },
  { id: "15", username: "olivia", email: "olivia@example.com", role: UserRole.CLIENT, phone: "+1-555-0115"},
  { id: "16", username: "paul", email: "paul@example.com", role: UserRole.ADMIN, phone: "+1-555-0116" },
  { id: "17", username: "quinn", email: "quinn@example.com", role: UserRole.EMPLOYEE, phone: "+1-555-0117" },
  { id: "18", username: "rachel", email: "rachel@example.com", role: UserRole.CLIENT, phone: "+1-555-0118" },
  { id: "19", username: "sam", email: "sam@example.com", role: UserRole.ADMIN, phone: "+1-555-0119" },
  { id: "20", username: "tina", email: "tina@example.com", role: UserRole.EMPLOYEE, phone: "+1-555-0120" },
  { id: "21", username: "ursula", email: "ursula@example.com", role: UserRole.CLIENT, phone: "+1-555-0121" },
  { id: "22", username: "victor", email: "victor@example.com", role: UserRole.ADMIN, phone: "+1-555-0122" },
  { id: "23", username: "wendy", email: "wendy@example.com", role: UserRole.EMPLOYEE, phone: "+1-555-0123" },
  { id: "24", username: "xavier", email: "xavier@example.com", role: UserRole.CLIENT, phone: "+1-555-0124" },
  { id: "25", username: "yasmin", email: "yasmin@example.com", role: UserRole.ADMIN, phone: "+1-555-0125" },
  { id: "26", username: "zane", email: "zane@example.com", role: UserRole.EMPLOYEE, phone: "+1-555-0126" },
  { id: "27", username: "abby", email: "abby@example.com", role: UserRole.CLIENT, phone: "+1-555-0127" },
  { id: "28", username: "bruce", email: "bruce@example.com", role: UserRole.ADMIN, phone: "+1-555-0128" },
  { id: "29", username: "claire", email: "claire@example.com", role: UserRole.EMPLOYEE, phone: "+1-555-0129" },
  { id: "30", username: "derek", email: "derek@example.com", role: UserRole.CLIENT, phone: "+1-555-0130" },
  { id: "31", username: "ella", email: "ella@example.com", role: UserRole.ADMIN, phone: "+1-555-0131" },
  { id: "32", username: "felix", email: "felix@example.com", role: UserRole.EMPLOYEE, phone: "+1-555-0132" },
  { id: "33", username: "gwen", email: "gwen@example.com", role: UserRole.CLIENT, phone: "+1-555-0133" },
  { id: "34", username: "harry", email: "harry@example.com", role: UserRole.ADMIN, phone: "+1-555-0134" },
  { id: "35", username: "ivy", email: "ivy@example.com", role: UserRole.EMPLOYEE, phone: "+1-555-0135" },
  { id: "36", username: "james", email: "james@example.com", role: UserRole.CLIENT, phone: "+1-555-0136" },
  { id: "37", username: "kara", email: "kara@example.com", role: UserRole.ADMIN, phone: "+1-555-0137" },
  { id: "38", username: "liam", email: "liam@example.com", role: UserRole.EMPLOYEE, phone: "+1-555-0138" },
  { id: "39", username: "mia2", email: "mia2@example.com", role: UserRole.CLIENT, phone: "+1-555-0139" },
  { id: "40", username: "noah", email: "noah@example.com", role: UserRole.ADMIN, phone: "+1-555-0140" },
  { id: "41", username: "oliver", email: "oliver@example.com", role: UserRole.EMPLOYEE, phone: "+1-555-0141" },
  { id: "42", username: "piper", email: "piper@example.com", role: UserRole.CLIENT, phone: "+1-555-0142" },
  { id: "43", username: "quincy", email: "quincy@example.com", role: UserRole.ADMIN, phone: "+1-555-0143" },
  { id: "44", username: "rosie", email: "rosie@example.com", role: UserRole.EMPLOYEE, phone: "+1-555-0144" },
  { id: "45", username: "steve", email: "steve@example.com", role: UserRole.CLIENT, phone: "+1-555-0145" },
  { id: "46", username: "tracy", email: "tracy@example.com", role: UserRole.ADMIN, phone: "+1-555-0146" },
  { id: "47", username: "uma", email: "uma@example.com", role: UserRole.EMPLOYEE, phone: "+1-555-0147" },
  { id: "48", username: "vince", email: "vince@example.com", role: UserRole.CLIENT, phone: "+1-555-0148" },
  { id: "49", username: "will", email: "will@example.com", role: UserRole.ADMIN, phone: "+1-555-0149" },
  { id: "50", username: "zoe", email: "zoe@example.com", role: UserRole.EMPLOYEE, phone: "+1-555-0150" },
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
      <DataTableAdvancedToolbar table={table} className={"px-0"} menuLabel={"Create User"}>
        <DataTableFilterList table={table} />
        <SearchInput
          value={
            (table.getColumn("username")?.getFilterValue() as string) ?? ""
          }
          onChange={(value) =>
            table.getColumn("username")?.setFilterValue(value)
          }
          placeholder="Search by username..."
        />
        <DataTableFacetedFilter
          column={table.getColumn("role")}
          title={"Role"}
          options={Object.values(UserRole).map((role) => ({
            label: role,
            value: role,
          }))}
          multiple={true}
        />
        {/* <DataTableSortList table={table} /> */}
      </DataTableAdvancedToolbar>
    </DataTable>
  );
}
