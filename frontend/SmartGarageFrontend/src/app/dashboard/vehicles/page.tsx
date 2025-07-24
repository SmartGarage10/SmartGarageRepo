"use client";

import {
  useReactTable,
  getCoreRowModel,
  getPaginationRowModel,
  getFilteredRowModel,
  getSortedRowModel,
} from "@tanstack/react-table";

import { columns, Vehicles } from "./column";
import { DataTable } from "@/components/data-table/data-table";
import { DataTableFacetedFilter } from "@/components/data-table/data-table-faceted-filter";
import { DataTableAdvancedToolbar } from "@/components/data-table/data-table-advanced-toolbar";
import { DataTableFilterList } from "@/components/data-table/data-table-filter-list";
import { SearchInput } from "@/components/data-table/data-search";

import * as React from "react";

export const data: Vehicles[] = [
  {
    id: "1",
    licensePlate: "ABC-1234",
    vin: "1HGCM82633A123456",
    brand: "Toyota",
    model: "Camry",
  },
  {
    id: "2",
    licensePlate: "XYZ-5678",
    vin: "2T1BU4EE9AC567890",
    brand: "Honda",
    model: "Civic",
  },
  {
    id: "3",
    licensePlate: "JKL-4321",
    vin: "3FA6P0H72DR123456",
    brand: "Ford",
    model: "Fusion",
  },
  {
    id: "4",
    licensePlate: "MNO-8765",
    vin: "1C4RJFAG2FC567890",
    brand: "Jeep",
    model: "Grand Cherokee",
  },
  {
    id: "5",
    licensePlate: "PQR-3456",
    vin: "5YJSA1E26JF123456",
    brand: "Tesla",
    model: "Model S",
  },
  {
    id: "6",
    licensePlate: "STU-7890",
    vin: "1G1ZT51826F123456",
    brand: "Chevrolet",
    model: "Malibu",
  },
  {
    id: "7",
    licensePlate: "VWX-6543",
    vin: "4T1BE46K67U123456",
    brand: "Toyota",
    model: "Corolla",
  },
  {
    id: "8",
    licensePlate: "DEF-2109",
    vin: "WAUZZZF49JA123456",
    brand: "Audi",
    model: "A4",
  },
  {
    id: "9",
    licensePlate: "GHI-8762",
    vin: "WBA3A5C51CF123456",
    brand: "BMW",
    model: "320i",
  },
  {
    id: "10",
    licensePlate: "LMN-9087",
    vin: "1FTFW1ET6EKE12345",
    brand: "Ford",
    model: "F-150",
  },
  {
    id: "11",
    licensePlate: "ZXC-1122",
    vin: "3CZRE38539G123456",
    brand: "Honda",
    model: "CR-V",
  },
  {
    id: "12",
    licensePlate: "BNM-3344",
    vin: "JN8AS5MT0CW123456",
    brand: "Nissan",
    model: "Rogue",
  },
  {
    id: "13",
    licensePlate: "QWE-5566",
    vin: "5TDKK3DC8DS123456",
    brand: "Toyota",
    model: "Sienna",
  },
  {
    id: "14",
    licensePlate: "RTY-7788",
    vin: "1N4AL3APXDC123456",
    brand: "Nissan",
    model: "Altima",
  },
  {
    id: "15",
    licensePlate: "UIO-9900",
    vin: "JHMFA36206S123456",
    brand: "Honda",
    model: "Accord",
  },
  {
    id: "16",
    licensePlate: "PAS-1188",
    vin: "1GNSCBKC0HR123456",
    brand: "Chevrolet",
    model: "Tahoe",
  },
  {
    id: "17",
    licensePlate: "LOL-2233",
    vin: "2HKRM4H75FH123456",
    brand: "Honda",
    model: "Pilot",
  },
  {
    id: "18",
    licensePlate: "XOX-3344",
    vin: "1C6RR7LT5GS123456",
    brand: "Ram",
    model: "1500",
  },
  {
    id: "19",
    licensePlate: "BRB-4455",
    vin: "1FM5K8B81FG123456",
    brand: "Ford",
    model: "Explorer",
  },
  {
    id: "20",
    licensePlate: "OMG-5566",
    vin: "5UXWX9C50E0D12345",
    brand: "BMW",
    model: "X3",
  },
  {
    id: "21",
    licensePlate: "WTF-6677",
    vin: "WA1DGBFE6BD123456",
    brand: "Audi",
    model: "Q7",
  },
  {
    id: "22",
    licensePlate: "YAS-7788",
    vin: "1G1JC5244R7251234",
    brand: "Chevrolet",
    model: "Spark",
  },
  {
    id: "23",
    licensePlate: "NOP-8899",
    vin: "4S4BP62C567123456",
    brand: "Subaru",
    model: "Outback",
  },
  {
    id: "24",
    licensePlate: "POI-9900",
    vin: "1C3CCCABXGN123456",
    brand: "Chrysler",
    model: "200",
  },
  {
    id: "25",
    licensePlate: "GHJ-1011",
    vin: "JTMZF33V166123456",
    brand: "Toyota",
    model: "RAV4",
  },
  {
    id: "26",
    licensePlate: "LKA-1221",
    vin: "3N1AB7APXJY123456",
    brand: "Nissan",
    model: "Sentra",
  },
  {
    id: "27",
    licensePlate: "ZXC-1332",
    vin: "1HGCR2F3XFA123456",
    brand: "Honda",
    model: "Insight",
  },
  {
    id: "28",
    licensePlate: "BVC-1443",
    vin: "WDDHF5KB3FB123456",
    brand: "Mercedes-Benz",
    model: "E350",
  },
  {
    id: "29",
    licensePlate: "TYU-1554",
    vin: "2C3CDZBT5HH123456",
    brand: "Dodge",
    model: "Challenger",
  },
  {
    id: "30",
    licensePlate: "MNB-1665",
    vin: "1N6AD0EV3FN123456",
    brand: "Nissan",
    model: "Frontier",
  },
  {
    id: "31",
    licensePlate: "WSX-1776",
    vin: "4JGDA5JB0FB123456",
    brand: "Mercedes-Benz",
    model: "GLE350",
  },
  {
    id: "32",
    licensePlate: "EDC-1887",
    vin: "1FTEX1EP8KFA12345",
    brand: "Ford",
    model: "Ranger",
  },
  {
    id: "33",
    licensePlate: "RFV-1998",
    vin: "2GCEK19T9X1123456",
    brand: "Chevrolet",
    model: "Silverado",
  },
  {
    id: "34",
    licensePlate: "TGB-2009",
    vin: "JN1CV6EK4BM123456",
    brand: "Infiniti",
    model: "G37",
  },
  {
    id: "35",
    licensePlate: "YHN-2110",
    vin: "3GNDA23D76S123456",
    brand: "Chevrolet",
    model: "HHR",
  },
  {
    id: "36",
    licensePlate: "UJM-2221",
    vin: "1G4HP57247U123456",
    brand: "Buick",
    model: "Lucerne",
  },
  {
    id: "37",
    licensePlate: "IKM-2332",
    vin: "KMHDH4AE1DU123456",
    brand: "Hyundai",
    model: "Elantra",
  },
  {
    id: "38",
    licensePlate: "OLP-2443",
    vin: "1N4BA41E04C123456",
    brand: "Nissan",
    model: "Maxima",
  },
  {
    id: "39",
    licensePlate: "AQS-2554",
    vin: "4T1BF1FK7FU123456",
    brand: "Toyota",
    model: "Avalon",
  },
  {
    id: "40",
    licensePlate: "WED-2665",
    vin: "WAUBFAFL5CN123456",
    brand: "Audi",
    model: "A3",
  },
  {
    id: "41",
    licensePlate: "SDF-2776",
    vin: "2T3ZF4DV5BW123456",
    brand: "Toyota",
    model: "Venza",
  },
  {
    id: "42",
    licensePlate: "GHJ-2887",
    vin: "1C6RR7FT1FS123456",
    brand: "Ram",
    model: "Rebel",
  },
  {
    id: "43",
    licensePlate: "PLM-2998",
    vin: "JN8AZ2NE8D9123456",
    brand: "Nissan",
    model: "Murano",
  },
  {
    id: "44",
    licensePlate: "KJU-3009",
    vin: "5FNRL38768B123456",
    brand: "Honda",
    model: "Odyssey",
  },
  {
    id: "45",
    licensePlate: "POK-3110",
    vin: "1GKKRRKD7FJ123456",
    brand: "GMC",
    model: "Acadia",
  },
  {
    id: "46",
    licensePlate: "LOP-3221",
    vin: "2GNALDEK6E1123456",
    brand: "Chevrolet",
    model: "Equinox",
  },
  {
    id: "47",
    licensePlate: "MNH-3332",
    vin: "1J4PN2GK2AW123456",
    brand: "Jeep",
    model: "Liberty",
  },
  {
    id: "48",
    licensePlate: "IKJ-3443",
    vin: "JTHBE1BL5FA123456",
    brand: "Lexus",
    model: "GS350",
  },
  {
    id: "49",
    licensePlate: "UJY-3554",
    vin: "3VWDP7AJ7DM123456",
    brand: "Volkswagen",
    model: "Jetta",
  },
  {
    id: "50",
    licensePlate: "REW-3665",
    vin: "1C4PJMDX9JD123456",
    brand: "Jeep",
    model: "Cherokee",
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
      <DataTableAdvancedToolbar table={table} className={"px-0"} menuLabel={"Add Vehicle"}>
        <DataTableFilterList table={table} />

        <SearchInput
          value={(table.getColumn("licensePlate")?.getFilterValue() as string) ?? ""}
          onChange={(value) =>
            table.getColumn("licensePlate")?.setFilterValue(value)
          }
          placeholder="Search by License Plate..."
        />
        {/* <DataTableSortList table={table} /> */}
      </DataTableAdvancedToolbar>
    </DataTable>
  );
}
