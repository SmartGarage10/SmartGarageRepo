"use client";

import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
} from "@/components/ui/dropdown-menu";
import { Download } from "lucide-react";
import Papa from "papaparse";
import * as XLSX from "xlsx";
import { Table } from "@tanstack/react-table";

interface ExportMenuProps<TData> {
  table: Table<TData>;
  fileName?: string;
  variant?: "button" | "menuitem";
}

export function ExportMenu<TData>({ 
  table, 
  fileName = "export",
  variant = "button"
}: ExportMenuProps<TData>) {
  const getData = () => {
    const selectedRows = table.getSelectedRowModel().rows;
    return selectedRows.length > 0
      ? selectedRows.map((row) => row.original)
      : table.getFilteredRowModel().rows.map((row) => row.original);
  };

  const getFileName = (ext: string) =>
    `${fileName}-${new Date().toISOString().split("T")[0]}.${ext}`;

  const exportToCSV = () => {
    const data = getData();
    const csv = Papa.unparse(data, { header: true });
    const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
    triggerDownload(blob, getFileName("csv"));
  };

  const exportToExcel = () => {
    const data = getData();
    const worksheet = XLSX.utils.json_to_sheet(data);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Sheet1");
    worksheet["!cols"] = Object.keys(data[0] || {}).map(() => ({ wch: 20 }));
    XLSX.writeFile(workbook, getFileName("xlsx"));
  };

  const exportToJSON = () => {
    const data = getData();
    const json = JSON.stringify(data, null, 2);
    const blob = new Blob([json], { type: "application/json" });
    triggerDownload(blob, getFileName("json"));
  };

  const triggerDownload = (blob: Blob, filename: string) => {
    const link = document.createElement("a");
    const url = URL.createObjectURL(blob);
    link.setAttribute("href", url);
    link.setAttribute("download", filename);
    link.style.visibility = "hidden";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  if (variant === "menuitem") {
    return (
      <>
        <DropdownMenuItem 
          onClick={exportToCSV}
          className="flex items-center gap-2 text-sm"
        >
          Export to CSV
        </DropdownMenuItem>
        <DropdownMenuItem 
          onClick={exportToExcel}
          className="flex items-center gap-2 text-sm"
        >
          Export to Excel
        </DropdownMenuItem>
        <DropdownMenuItem 
          onClick={exportToJSON}
          className="flex items-center gap-2 text-sm"
        >
          Export to JSON
        </DropdownMenuItem>
      </>
    );
  }

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button 
          variant="ghost" 
          size="sm"
          className="flex items-center gap-2"
        >
          <Download className="h-4 w-4" />
          <span>Export</span>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-48">
        <DropdownMenuItem 
          onClick={exportToCSV}
          className="flex items-center gap-2 text-sm"
        >
          Export to CSV
        </DropdownMenuItem>
        <DropdownMenuItem 
          onClick={exportToExcel}
          className="flex items-center gap-2 text-sm"
        >
          Export to Excel
        </DropdownMenuItem>
        <DropdownMenuItem 
          onClick={exportToJSON}
          className="flex items-center gap-2 text-sm"
        >
          Export to JSON
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}