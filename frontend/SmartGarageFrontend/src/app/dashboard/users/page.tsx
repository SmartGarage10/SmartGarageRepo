'use client';

import { useState, useEffect, useCallback } from 'react';
import { useDataTable } from '@/hooks/useDataTable';
import { getColumns } from '@/src/app/dashboard/users/column';
import { User, UserRole } from '@/types/user';
import { DataTable } from '@/components/data-table/data-table';
import { DataTableAdvancedToolbar } from '@/components/data-table/data-table-advanced-toolbar';
import { DataTableFacetedFilter } from '@/components/data-table/data-table-faceted-filter';
import { DataTableFilterList } from '@/components/data-table/data-table-filter-list';
import { SearchInput } from '@/components/data-table/data-search';
import { UserForm } from '@/components/forms/edit-create-user-form';
import { Toaster } from 'sonner';

export default function Page() {
    // Fix role options - ensure they have proper values
    const roleOptions = Object.values(UserRole).map((r) => ({
        label: r,
        value: r
    }));

    // Edit callback
    const handleEdit = useCallback((user: User) => {
        console.log('[DEBUG] Editing user', user);
        setSelectedRow(user);
    }, []);

    // Hook for generic table
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
        getColumns: ({ onDelete }) => {
            return getColumns({
                onEdit: (row: User) => {
                    handleEdit(row);
                    setIsFormOpen(true);
                },
                onDelete: async (id: string) => {
                    await onDelete(id);
                },
                roleOptions, // Pass the fixed role options
            });
        },
    });

    // Submit handler for users - ONLY IN PAGE
    const handleUserSubmit = useCallback(
        async (userData: User) => {
            try {
                const isEdit = !!selectedRow?.id;
                const endpoint = isEdit
                    ? `http://localhost:8080/api/user/${selectedRow.id}`
                    : 'http://localhost:8080/api/register';

                const method = isEdit ? 'PUT' : 'POST';

                const payload = {
                    ...userData,
                    // Add default password for new users
                    ...(!isEdit && {
                        password: "defaultPassword",
                        confirmPassword: "defaultPassword"
                    })
                };

                console.log("Submitting user payload:", payload);

                const response = await fetch(endpoint, {
                    method,
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload),
                    credentials: "include",
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || "Request failed");
                }

                return true;
            } catch (error) {
                console.error('Error saving user:', error);
                throw error;
            }
        },
        [selectedRow]
    );

    return (
        <div className="space-y-4">
            <Toaster richColors position="top-center" toastOptions={{ className: 'font-sans' }} />

            <DataTable table={table} className="px-10">
                <DataTableAdvancedToolbar
                    table={table}
                    className="px-0"
                    menuLabel="Add User"
                    onCreateClick={() => {
                        console.log('[DEBUG] Creating new user');
                        setSelectedRow(null);
                        setIsFormOpen(true);
                    }}
                    onDeleteClick={
                        table.getSelectedRowModel().rows.length > 0
                            ? handleDeleteSelected
                            : undefined
                    }
                    onClearAll={clearAllFilters}
                >
                    <DataTableFilterList table={table} onClearAll={clearAllFilters} />

                    <SearchInput
                        value={globalFilter}
                        onChange={setGlobalFilter}
                        placeholder="Search users..."
                        onClear={() => {
                            setGlobalFilter('');
                            table.resetGlobalFilter();
                        }}
                    />
                </DataTableAdvancedToolbar>
            </DataTable>

            <UserForm
                open={isFormOpen}
                onOpenChange={setIsFormOpen}
                initialData={selectedRow}
                onSuccess={() => {
                    fetchData();
                    setIsFormOpen(false);
                }}
                onSubmit={handleUserSubmit}
                isSubmitting={false}
            />
        </div>
    );
}