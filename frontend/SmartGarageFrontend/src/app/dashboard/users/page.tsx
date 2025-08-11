'use client';

import { useState, useEffect } from 'react';
import { useDataTable } from '@/hooks/useDataTable';
import { getColumns, User, UserRole } from '@/src/app/dashboard/users/column';
import { DataTable } from '@/components/data-table/data-table';
import { DataTableAdvancedToolbar } from '@/components/data-table/data-table-advanced-toolbar';
import { DataTableFacetedFilter } from '@/components/data-table/data-table-faceted-filter';
import { DataTableFilterList } from '@/components/data-table/data-table-filter-list';
import { SearchInput } from '@/components/data-table/data-search';
import { UserForm } from '@/components/forms/edit-create-user-form';
import { Toaster } from 'sonner';
import { useRouter, useSearchParams } from 'next/navigation';

export default function Page() {
    const router = useRouter();
    const searchParams = useSearchParams();

    // Extract role filter from URL (or empty)
    const [roleFilter, setRoleFilter] = useState<string[]>([]);

    useEffect(() => {
        const roles = searchParams.getAll('role');
        setRoleFilter(roles.length ? roles : []);
    }, [searchParams]);

    // Hook for generic table (excluding role)
    const {
        table,
        globalFilter,
        setGlobalFilter,
        clearAllFilters,
        isFormOpen,
        setIsFormOpen,
        selectedRow,
        setSelectedRow,
        fetchData,
        handleDeleteSelected,
    } = useDataTable<User>({
        fetchUrl: 'http://localhost:8080/api/users',
        getColumns,
    });

    // Sync role filter changes to URL and table filters
    const onRoleChange = (values: string[]) => {
        setRoleFilter(values);

        // Update URL params manually
        const params = new URLSearchParams(window.location.search);
        params.delete('role');
        values.forEach((val) => params.append('role', val));

        router.replace(`${window.location.pathname}?${params.toString()}`, { scroll: false });
    };

    // When roleFilter changes, update the filter in table
    useEffect(() => {
        if (!table) return;
        const roleCol = table.getColumn('role');
        if (!roleCol) return;

        roleCol.setFilterValue(roleFilter.length > 0 ? roleFilter : undefined);
    }, [roleFilter, table]);

    return (
        <div className="space-y-4">
            <Toaster richColors position="top-center" toastOptions={{ className: 'font-sans' }} />

            <DataTable table={table} className="px-10">
                <DataTableAdvancedToolbar
                    table={table}
                    className="px-0"
                    menuLabel="Add User"
                    onCreateClick={() => {
                        setSelectedRow(null);
                        setIsFormOpen(true);
                    }}
                    onDeleteClick={
                        table.getSelectedRowModel().rows.length > 0 ? handleDeleteSelected : undefined
                    }
                    onClearAll={() => {
                        clearAllFilters();
                        onRoleChange([]);
                    }}
                >
                    <DataTableFilterList table={table} onClearAll={clearAllFilters} />

                    <SearchInput
                        value={globalFilter}
                        onChange={setGlobalFilter}
                        placeholder="Search by name..."
                        onClear={() => {
                            setGlobalFilter('');
                            table.resetGlobalFilter();
                        }}
                    />

                    <DataTableFacetedFilter
                        column={table.getColumn('role')}
                        title="Role"
                        options={Object.values(UserRole).map((r) => ({
                            label: r,
                            value: r,
                        }))}
                        multiple
                        value={roleFilter}
                        onChange={onRoleChange}
                        onClear={() => onRoleChange([])}
                    />
                </DataTableAdvancedToolbar>
            </DataTable>

            <UserForm
                open={isFormOpen}
                onOpenChange={(open) => {
                    setIsFormOpen(open);
                    if (!open) setSelectedRow(null);
                }}
                initialData={selectedRow}
                onSubmit={async (user: User) => {
                    const isEdit = !!selectedRow;

                    const res = await fetch(
                        isEdit
                            ? `http://localhost:8080/api/user/${selectedRow?.id}`
                            : 'http://localhost:8080/api/register',
                        {
                            method: isEdit ? 'PUT' : 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            credentials: 'include',
                            body: JSON.stringify(user),
                        }
                    );

                    if (res.ok) {
                        fetchData();
                        setIsFormOpen(false);
                    }
                }}
                isSubmitting={false}
            />
        </div>
    );
}
