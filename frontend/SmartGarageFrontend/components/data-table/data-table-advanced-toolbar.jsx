"use client";

import { DataTableViewOptions } from "@/components/data-table/data-table-view-options";
import { ExportMenu } from "@/components/data-table/data-export";
import { cn } from "@/lib/utils";
import { Button } from "../ui/button";
import { MoreHorizontal, PlusCircleIcon, Trash2, Download } from "lucide-react";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
  DropdownMenuSub,
  DropdownMenuSubTrigger,
  DropdownMenuPortal,
  DropdownMenuSubContent,
} from "../ui/dropdown-menu";

export function DataTableAdvancedToolbar({
  table,
  children,
  className,
  menuLabel,
  ...props
}) {
  return (
    <div
      role="toolbar"
      aria-orientation="horizontal"
      className={cn(
        "flex w-full items-start justify-between gap-2 p-1",
        className
      )}
      {...props}
    >
      <div className="flex flex-1 flex-wrap items-center gap-2">{children}</div>

      <div className="flex items-center gap-2">
        <DataTableViewOptions table={table} />

        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="outline" size="sm">
              <MoreHorizontal />
              <span className="sr-only">Open menu</span>
            </Button>
          </DropdownMenuTrigger>

          <DropdownMenuContent align="end" className="w-44">
            {/* Create / Add */}
            <DropdownMenuItem className="flex items-center gap-2 px-3 py-2 text-sm cursor-pointer">
              <PlusCircleIcon size={"sm"} className="text-foreground"/>
              <span>{menuLabel}</span>
            </DropdownMenuItem>

            {/* Export Submenu */}
            <DropdownMenuSub>
              <DropdownMenuSubTrigger className="flex items-center gap-2 px-3 py-2 text-sm cursor-pointer">
                <Download size={16} />
                <span>Export</span>
              </DropdownMenuSubTrigger>
              <DropdownMenuPortal>
                <DropdownMenuSubContent>
                  <ExportMenu
                    table={table}
                    fileName="data-export"
                    variant="menuitem"
                  />
                </DropdownMenuSubContent>
              </DropdownMenuPortal>
            </DropdownMenuSub>

            {/* Delete */}
            <DropdownMenuItem className="flex items-center gap-2 px-3 py-2 text-sm text-red-600 hover:bg-red-50/50 data-[highlighted]:bg-red-50/50 data-[highlighted]:text-red-600">
              <Trash2 className="text-red-600" size={"sm"}/>
              <span>Delete</span>
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </div>
  );
}
