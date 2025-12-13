'use client';

import { useCallback, useState, useEffect } from 'react';
import { useDataTable } from '@/hooks/useDataTable';
import { getColumns } from '@/app/dashboard/users/column';
import { User, UserRole } from '@/types/user';
import { DataTable } from '@/components/data-table/data-table';
import { DataTableAdvancedToolbar } from '@/components/data-table/data-table-advanced-toolbar';
import { DataTableFilterList } from '@/components/data-table/data-table-filter-list';
import { SearchInput } from '@/components/data-table/data-search';
import { UserForm } from '@/components/forms/edit-create-user-form';
import { Toaster, toast } from 'sonner';

export default function Page() {
    // Add a refresh key to force re-renders
    const [refreshKey, setRefreshKey] = useState(0);
    const [isSubmitting, setIsSubmitting] = useState(false);

    // Fix role options - ensure they have proper values
    const roleOptions = Object.values(UserRole).map((r) => ({
        label: r,
        value: r
    }));

    // Edit callback
    const handleEdit = useCallback((user: User) => {
        console.log('[DEBUG] Editing user', user);
        setSelectedRow(user);
        setIsFormOpen(true);
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
                    const toastId = toast.loading('Deleting user...');
                    try {
                        await onDelete(id);
                        toast.success('User deleted successfully!', { id: toastId });
                        // Refresh after delete
                        fetchData();
                        setRefreshKey(prev => prev + 1);
                    } catch (error) {
                        toast.error('Failed to delete user', { id: toastId });
                    }
                },
                roleOptions, // Pass the fixed role options
            });
        },
    });

    // Submit handler for users - ONLY IN PAGE
    const handleUserSubmit = useCallback(
        async (userData: User) => {
            setIsSubmitting(true);
            const toastId = toast.loading(selectedRow?.id ? 'Updating user...' : 'Creating user...');

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

                // Show success message
                toast.success(
                    isEdit ? 'User updated successfully!' : 'User created successfully!',
                    {
                        id: toastId,
                        duration: 3000 // Keep for 3 seconds
                    }
                );

                // Force refresh after success
                fetchData();
                setRefreshKey(prev => prev + 1);

                // Reset form state
                setIsFormOpen(false);
                setSelectedRow(null);

                return true;
            } catch (error) {
                console.error('Error saving user:', error);
                toast.error(
                    error instanceof Error ? error.message : 'Failed to save user',
                    {
                        id: toastId,
                        duration: 4000 // Show errors longer
                    }
                );
                throw error;
            } finally {
                setIsSubmitting(false);
            }
        },
        [selectedRow, fetchData, setIsFormOpen, setSelectedRow]
    );

    // Handle form success
    const handleSuccess = useCallback(() => {
        const toastId = toast.success('Operation completed successfully!', {
            duration: 3000
        });
        fetchData();
        setIsFormOpen(false);
        setSelectedRow(null);
        setRefreshKey(prev => prev + 1);
    }, [fetchData, setIsFormOpen, setSelectedRow]);

    // Force initial fetch
    useEffect(() => {
        fetchData();
    }, [fetchData, refreshKey]);

    return (
        <div key={refreshKey} className="space-y-4">
            <Toaster
                richColors
                position="top-center"
                toastOptions={{
                    className: 'font-sans',
                    duration: 3000, // Default duration
                }}
                closeButton
                expand
                visibleToasts={3}
            />

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
                            ? async () => {
                                const toastId = toast.loading('Deleting selected users...');
                                try {
                                    await handleDeleteSelected();
                                    toast.success('Users deleted successfully!', { id: toastId });
                                    fetchData();
                                    setRefreshKey(prev => prev + 1);
                                } catch (error) {
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
                onSuccess={handleSuccess}
                onSubmit={handleUserSubmit}
                isSubmitting={isSubmitting}
                key={selectedRow?.id || 'new'} // Force re-render when data changes
            />
        </div>
    );
}