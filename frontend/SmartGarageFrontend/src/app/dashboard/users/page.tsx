"use client";

import {
    useReactTable,
    getCoreRowModel,
    getPaginationRowModel,
    getFilteredRowModel,
    getSortedRowModel,
    type ColumnFiltersState,
} from "@tanstack/react-table";
import { UserRole, getColumns, User } from "@/src/app/dashboard/users/column";
import { DataTable } from "@/components/data-table/data-table";
import { DataTableFacetedFilter } from "@/components/data-table/data-table-faceted-filter";
import { DataTableAdvancedToolbar } from "@/components/data-table/data-table-advanced-toolbar";
import { DataTableFilterList } from "@/components/data-table/data-table-filter-list";
import { SearchInput } from "@/components/data-table/data-search";
import React, { useState, useEffect, useCallback } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { debounce } from "lodash";
import { UserForm } from "@/components/forms/edit-user-form";
import { Toaster, toast } from 'sonner';
import { CheckCircle, XCircle } from "lucide-react";

export default function Page() {
    const [users, setUsers] = useState<User[]>([]);
    const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([]);
    const [globalFilter, setGlobalFilter] = useState("");
    const [isFormOpen, setIsFormOpen] = useState(false);
    const [selectedUser, setSelectedUser] = useState<User | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [rowSelection, setRowSelection] = useState({});

    const router = useRouter();
    const searchParams = useSearchParams();

    const fetchData = useCallback(async () => {
        try {
            // Convert URL params to format expected by backend
            const params = new URLSearchParams();

            // Handle role filters (multiple values)
            const roleFilters = searchParams.getAll('role');
            if (roleFilters.length > 0) {
                roleFilters.forEach(role => params.append('role', role));
            }

            // Handle other filters
            searchParams.forEach((value, key) => {
                if (key !== 'role' && key !== 'search' && value) {
                    params.append(key, value);
                }
            });

            // Handle search
            const searchValue = searchParams.get('search');
            if (searchValue) {
                params.append('search', searchValue);
            }

            const res = await fetch(`http://localhost:8080/api/users?${params.toString()}`, {
                credentials: 'include',
                cache: 'no-store',
            });

            if (res.redirected) {
                window.location.href = res.url;
                return;
            }

            if (res.status === 401) {
                router.push("/login");
                return;
            }

            if (!res.ok) {
                const errorText = await res.text();
                throw new Error(errorText || "Failed to fetch users");
            }

            const data = await res.json();
            setUsers(data);
        } catch (error) {
            toast.error("Failed to load users", {
                className: 'bg-destructive text-white',
                description: error instanceof Error ? error.message : "An unknown error occurred",
                duration: 5000,
                icon: <XCircle className="text-white" />,
            });
        }
    }, [router, searchParams]);

    const updateUrl = useCallback(
        debounce((filters: ColumnFiltersState, search: string) => {
            const params = new URLSearchParams();

            // Clear existing role params
            params.delete('role');

            // Handle role filters (multiple values)
            const roleFilter = filters.find(f => f.id === 'role');
            if (roleFilter?.value && Array.isArray(roleFilter.value)) {
                roleFilter.value.forEach(role => {
                    params.append('role', role.toString());
                });
            }

            // Handle other filters
            filters.forEach(filter => {
                if (filter.id !== 'role' && filter.value) {
                    if (Array.isArray(filter.value)) {
                        if (filter.value.length > 0) {
                            params.set(filter.id, filter.value.join(','));
                        }
                    } else {
                        params.set(filter.id, filter.value.toString());
                    }
                }
            });

            // Handle search
            if (search && search.trim() !== '') {
                params.set('search', search);
            } else {
                params.delete('search');
            }

            const newUrl = `${window.location.pathname}?${params.toString()}`;
            const currentUrl = `${window.location.pathname}?${window.location.search}`;

            if (newUrl !== currentUrl) {
                router.replace(newUrl, { scroll: false });
            }
        }, 500),
        [router]
    );

    useEffect(() => {
        updateUrl(columnFilters, globalFilter);
        return () => updateUrl.cancel();
    }, [columnFilters, globalFilter, updateUrl]);

    useEffect(() => {
        const initialFilters: ColumnFiltersState = [];
        const initialSearch = searchParams.get('search') || '';

        // Handle role filters (multiple values)
        const roleFilters = searchParams.getAll('role');
        if (roleFilters.length > 0) {
            initialFilters.push({ id: 'role', value: roleFilters });
        }

        // Handle other filters
        searchParams.forEach((value, key) => {
            if (key !== 'role' && key !== 'search') {
                initialFilters.push({ id: key, value });
            }
        });

        setColumnFilters(initialFilters);
        setGlobalFilter(initialSearch);
        fetchData();
    }, [fetchData, searchParams]);

    const handleEditUser = (user: User) => {
        setSelectedUser(user);
        setIsFormOpen(true);
    };

    const handleDelete = async (userId: string) => {
        try {
            const response = await fetch(`http://localhost:8080/api/user/${userId}`, {
                method: 'DELETE',
                credentials: 'include',
            });
            if (!response.ok) throw new Error('Delete failed');
            toast.success('User deleted');
            fetchData();
        } catch (error) {
            toast.error(error instanceof Error ? error.message : 'Delete failed');
        }
    };

    const handleDeleteSelected = useCallback(async () => {
        const selectedRows = table.getSelectedRowModel().rows;
        const selectedUsers = selectedRows.map(row => row.original);

        if (selectedUsers.length === 0) return;

        try {
            const response = await fetch('http://localhost:8080/api/users/delete', {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify(selectedUsers.map(user => user.id)),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to delete users');
            }

            toast.success("Users Deleted", {
                className: 'bg-success text-white',
                description: `${selectedUsers.length} users have been deleted successfully`,
                duration: 2500,
                icon: <CheckCircle />,
            });

            setUsers(prev => prev.filter(
                user => !selectedUsers.some(selected => selected.id === user.id)));
            setRowSelection({});
        } catch (error) {
            toast.error("Delete Failed", {
                className: 'bg-destructive text-white',
                description: error instanceof Error ? error.message : 'An unknown error occurred',
                duration: 5000,
                icon: <XCircle />,
            });
        }
    }, [users]);

    const handleUserSubmit = async (data: User) => {
        setIsSubmitting(true);

        try {
            const isEdit = !!selectedUser;
            const url = isEdit
                ? `http://localhost:8080/api/user/${selectedUser.id}`
                : 'http://localhost:8080/api/register';

            const response = await fetch(url, {
                method: isEdit ? 'PUT' : 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify({ ...data, role: data.role }),
                credentials: 'include'
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || (isEdit ? 'Update failed' : 'Registration failed'));
            }

            toast.success(isEdit ? "User Updated" : "User Created", {
                className: 'bg-success text-white',
                description: isEdit
                    ? 'User updated successfully'
                    : 'User registered successfully',
                duration: 2500,
                icon: <CheckCircle className="text-white" />
            });

            fetchData();
            setIsFormOpen(false);
        } catch (error) {
            toast.error("Operation Failed", {
                className: 'bg-destructive text-white',
                description: error instanceof Error
                    ? error.message
                    : 'An unexpected error occurred',
                duration: 5000,
                icon: <XCircle />,
            });
        } finally {
            setIsSubmitting(false);
        }
    };

    const table = useReactTable({
        data: users,
        columns: getColumns({
            onEdit: handleEditUser,
            onDelete: handleDelete
        }),
        state: {
            columnFilters,
            globalFilter,
            rowSelection,
        },
        onColumnFiltersChange: setColumnFilters,
        onGlobalFilterChange: setGlobalFilter,
        onRowSelectionChange: setRowSelection,
        getCoreRowModel: getCoreRowModel(),
        getPaginationRowModel: getPaginationRowModel(),
        getFilteredRowModel: getFilteredRowModel(),
        getSortedRowModel: getSortedRowModel(),
        enableRowSelection: true,
    });

    const handleClearAllFilters = () => {
        setColumnFilters([]);
        setGlobalFilter("");
        table.resetColumnFilters();
        table.resetGlobalFilter();
    };

    return (
        <div className="space-y-4">
            <Toaster richColors position="top-center" toastOptions={{ className: 'font-sans' }} />

            <DataTable table={table} className="px-10">
                <DataTableAdvancedToolbar
                    table={table}
                    className="px-0"
                    menuLabel="Add User"
                    onCreateClick={() => {
                        setSelectedUser(null);
                        setIsFormOpen(true);
                    }}
                    onDeleteClick={table.getSelectedRowModel().rows.length > 0 ? handleDeleteSelected : undefined}
                    onClearAll={handleClearAllFilters}
                >
                    <DataTableFilterList
                        table={table}
                        onClearAll={handleClearAllFilters}
                    />
                    <SearchInput
                        value={globalFilter}
                        onChange={(value) => {
                            setGlobalFilter(value);
                            if (value === "") {
                                table.resetGlobalFilter();
                            }
                        }}
                        placeholder="Search by Name..."
                        onClear={() => {
                            setGlobalFilter("");
                            table.resetGlobalFilter();
                        }}
                    />
                    {table.getColumn("role") && (
                        <DataTableFacetedFilter
                            column={table.getColumn("role")}
                            title="Role"
                            options={Object.values(UserRole).map(role => ({
                                label: role,
                                value: role,
                            }))}
                            onChange={(value) => {
                                table.getColumn("role")?.setFilterValue(value?.length ? value : undefined);
                            }}
                            onClear={() => {
                                table.getColumn("role")?.setFilterValue(undefined);
                            }}
                            multiple
                        />
                    )}
                </DataTableAdvancedToolbar>
            </DataTable>

            <UserForm
                open={isFormOpen}
                onOpenChange={(open) => {
                    setIsFormOpen(open);
                    if (!open) setSelectedUser(null);
                }}
                initialData={selectedUser}
                onSubmit={handleUserSubmit}
                isSubmitting={isSubmitting}
            />
        </div>
    );
}