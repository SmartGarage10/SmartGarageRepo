"use client";
import {
    useReactTable,
    getCoreRowModel,
    getPaginationRowModel,
    getFilteredRowModel,
    getSortedRowModel,
    type ColumnFiltersState,
} from "@tanstack/react-table";
import { UserRole, getColumns, Users } from "./column";
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
    const [users, setUsers] = useState<Users[]>([]);
    const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([]);
    const [globalFilter, setGlobalFilter] = useState("");
    const [isFormOpen, setIsFormOpen] = useState(false);
    const [selectedUser, setSelectedUser] = useState<Users | null>(null);
    const [selectedUsers, setSelectedUsers] = useState<Users[]>([]);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false);
    const router = useRouter();
    const searchParams = useSearchParams();

    const fetchData = useCallback(async () => {
        try {
            const params = new URLSearchParams(window.location.search);
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
            console.error("Fetch error:", error);
            toast.error(
                "Failed to load users",
                {
                    className: 'bg-destructive text-white',
                    description: error instanceof Error ? error.message : "An unknown error occurred",
                    duration: 5000,
                    icon: <XCircle className="text-white" />,
                }
            );
        }
    }, [router]);

    const updateUrl = useCallback(
        debounce((filters: ColumnFiltersState, search: string) => {
            const params = new URLSearchParams();
            filters.forEach(filter => {
                if (typeof filter.value === 'string' || typeof filter.value === 'number') {
                    params.set(filter.id, filter.value.toString());
                } else if (Array.isArray(filter.value)) {
                    filter.value.forEach(val => params.append(filter.id, val.toString()));
                }
            });

            if (search) params.set('search', search);
            router.replace(`${window.location.pathname}?${params.toString()}`, { scroll: false });
        }, 500),
        [router]
    );

    useEffect(() => {
        updateUrl(columnFilters, globalFilter);
        return () => updateUrl.cancel();
    }, [columnFilters, globalFilter, updateUrl]);

    useEffect(() => {
        const initialFilters: ColumnFiltersState = [];
        searchParams.forEach((value, key) => {
            if (key !== 'search') initialFilters.push({ id: key, value });
        });

        if (initialFilters.length > 0) setColumnFilters(initialFilters);
        if (searchParams.get('search')) setGlobalFilter(searchParams.get('search') || '');
        fetchData();
    }, [fetchData, searchParams]);

    const handleEditUser = (user: Users) => {
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
        if (selectedUsers.length === 0) return;

        try {
            setIsDeleting(true);
            const response = await fetch('http://localhost:8080/api/users/batch-delete', {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify({
                    userIds: selectedUsers.map(user => user.id)
                }),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to delete users');
            }

            toast.success(
                "Users Deleted",
                {
                    className: 'bg-success text-white',
                    description: `${selectedUsers.length} users have been deleted successfully`,
                    duration: 2500,
                    icon: <CheckCircle />,
                }
            );

            // Refresh data
            setUsers(prev => prev.filter(
                user => !selectedUsers.some(selected => selected.id === user.id)
            ));
            setSelectedUsers([]);
        } catch (error) {
            toast.error(
                "Delete Failed",
                {
                    className: 'bg-destructive text-white',
                    description: error instanceof Error ? error.message : 'An unknown error occurred',
                    duration: 5000,
                    icon: <XCircle />,
                }
            );
        } finally {
            setIsDeleting(false);
        }
    }, [selectedUsers]);

    const handleUserSubmit = async (data: Users) => {
        setIsSubmitting(true);

        try {
            const isEdit = !!selectedUser;
            const toastId = toast(
                isEdit ? "Updating User" : "Creating User",
                {
                    className: 'bg-success text-white',
                    description: isEdit
                        ? 'User details are being updated...'
                        : 'New user is being registered...',
                    duration: 2500,
                    icon: <CheckCircle className="text-white" />,
                }
            );

            const url = isEdit
                ? `http://localhost:8080/api/user/${selectedUser.id}`
                : 'http://localhost:8080/api/register';

            const response = await fetch(url, {
                method: isEdit ? 'PUT' : 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify({
                    ...data,
                    role: data.role
                }),
                credentials: 'include'
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || (isEdit ? 'Update failed' : 'Registration failed'));
            }

            toast.success(
                isEdit ? "User Updated" : "User Created",
                {
                    className: 'bg-success text-white',
                    description: isEdit
                        ? 'User details have been updated successfully'
                        : 'New user has been registered successfully',
                    duration: 2500,
                    icon: <CheckCircle />,
                    id: toastId
                }
            );

            fetchData();
            setIsFormOpen(false);

        } catch (error) {
            toast.error(
                "Operation Failed",
                {
                    className: 'bg-destructive text-white',
                    description: error instanceof Error
                        ? error.message
                        : 'An unexpected error occurred',
                    duration: 5000,
                    icon: <XCircle />,
                }
            );
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
        state: { columnFilters, globalFilter },
        onColumnFiltersChange: setColumnFilters,
        onGlobalFilterChange: setGlobalFilter,
        getCoreRowModel: getCoreRowModel(),
        getPaginationRowModel: getPaginationRowModel(),
        getFilteredRowModel: getFilteredRowModel(),
        getSortedRowModel: getSortedRowModel(),
    });

    return (
        <div className="space-y-4">
            <Toaster
                richColors={true}
                position="top-center"
                toastOptions={{
                    className: 'font-sans',
                }}
            />

            <DataTable table={table} className="px-10">
                <DataTableAdvancedToolbar
                    table={table}
                    className="px-0"
                    menuLabel="Add User"
                    onCreateClick={() => {
                        setSelectedUser(null);
                        setIsFormOpen(true);
                    }}
                    onDeleteClick={selectedUsers.length > 0 ? handleDeleteSelected : undefined}
                    isDeleteLoading={isDeleting}
                >
                    <DataTableFilterList table={table} />
                    <SearchInput
                        value={globalFilter}
                        onChange={setGlobalFilter}
                        placeholder="Search by Name..."
                    />
                    <DataTableFacetedFilter
                        column={table.getColumn("role")}
                        title="Role"
                        options={Object.values(UserRole).map(role => ({
                            label: role,
                            value: role,
                        }))}
                        multiple
                    />
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