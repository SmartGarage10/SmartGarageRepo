// frontend/SmartGarageFrontend/src/app/dashboard/users/page.tsx
'use client';

import { useCallback, useEffect, useState } from 'react';

import { useDataTable } from '@/hooks/useDataTable';
import { getColumns } from '@/app/dashboard/users/column';
import { User, UserRole } from '@/types/user';

import { DataTable } from '@/components/data-table/data-table';
import { DataTableAdvancedToolbar } from '@/components/data-table/data-table-advanced-toolbar';
import { DataTableFilterList } from '@/components/data-table/data-table-filter-list';
import { SearchInput } from '@/components/data-table/data-search';
import { UserForm } from '@/components/forms/edit-create-user-form';

import { Toaster, toast } from 'sonner';

export default function UsersPage() {
    const [selectedRow, setSelectedRow] = useState<User | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const roleOptions = Object.values(UserRole).map(role => ({
        label: role,
        value: role,
    }));

    const handleEdit = useCallback((user: User) => {
        setSelectedRow(user);
        setIsFormOpen(true);
    }, []);

    const {
        table,
        globalFilter,
        setGlobalFilter,
        clearAllFilters,
        fetchData,
        handleDeleteSelected,
        isFormOpen,
        setIsFormOpen,
    } = useDataTable<User>({
        fetchUrl: 'http://localhost:8080/api/users',
        getColumns: ({ onDelete }) =>
            getColumns({
                onEdit: (row: User) => {
                    handleEdit(row);
                },
                onDelete: async (id: string) => {
                    const toastId = toast.loading('Deleting user...');
                    try {
                        await onDelete(id);
                        toast.success('User deleted successfully!', { id: toastId });
                        fetchData();
                    } catch (error) {
                        toast.error('Failed to delete user', { id: toastId });
                    }
                },
                roleOptions,
            }),
    });

    const handleUserSubmit = useCallback(
        async (userData: User) => {
            setIsSubmitting(true);
            const toastId = toast.loading(
                selectedRow?.id ? 'Updating user...' : 'Creating user...'
            );

            try {
                const isEdit = !!selectedRow?.id;
                const endpoint = isEdit
                    ? `http://localhost:8080/api/user/${selectedRow!.id}`
                    : 'http://localhost:8080/api/register';

                const method = isEdit ? 'PUT' : 'POST';

                const payload = {
                    ...userData,
                    ...(!isEdit && {
                        password: 'defaultPassword',
                        confirmPassword: 'defaultPassword',
                    }),
                };

                const response = await fetch(endpoint, {
                    method,
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload),
                    credentials: 'include',
                });

                if (!response.ok) {
                    const errorData = await response.json().catch(() => ({}));
                    throw new Error(errorData.message || 'Request failed');
                }

                toast.success(
                    isEdit ? 'User updated successfully!' : 'User created successfully!',
                    { id: toastId }
                );

                fetchData();
                setIsFormOpen(false);
                setSelectedRow(null);

                console.log("Submitting user:", userData);

                return true;
            } catch (error) {
                toast.error(
                    error instanceof Error ? error.message : 'Failed to save user',
                    { id: toastId }
                );
                throw error;
            } finally {
                setIsSubmitting(false);
            }
        },
        [selectedRow, fetchData, setIsFormOpen]
    );

    useEffect(() => {
        fetchData();
    }, [fetchData]);

    return (
        <div className="space-y-4">
            <Toaster richColors position="top-center" />

            <DataTable table={table} className="px-10">
                <DataTableAdvancedToolbar
                    table={table}
                    menuLabel="Add User"
                    onCreateClick={() => {
                        setSelectedRow(null);
                        setIsFormOpen(true);
                    }}
                    deleteMessage={"This action cannot be undone. This will permanently delete the user accounts and remove all associated data."}
                    onDeleteClick={
                        table.getSelectedRowModel().rows.length > 0
                            ? async () => {
                                const toastId = toast.loading('Deleting selected users...');
                                try {
                                    await handleDeleteSelected();
                                    toast.success('Users deleted successfully!', {
                                        id: toastId,
                                    });
                                    fetchData();
                                } catch {
                                    toast.error('Failed to delete users', { id: toastId });
                                }
                            }
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
                onSubmit={handleUserSubmit}
                onSuccess={() => {
                    fetchData();
                    setIsFormOpen(false);
                    setSelectedRow(null);
                }}
                isSubmitting={isSubmitting}
            />
        </div>
    );
}
