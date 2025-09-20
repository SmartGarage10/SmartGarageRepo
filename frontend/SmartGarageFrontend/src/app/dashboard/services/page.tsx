'use client';

import { useState, useEffect } from 'react';
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
import { useRouter, useSearchParams } from 'next/navigation';
import { PackCards } from "@/components/dashboard-components/pack-cards";

// Define the Pack type
export interface Pack {
    id: number;
    name: string;
    description: string;
    price: number;
    features: string[];
    isPopular?: boolean;
}

// Sample data to use as fallback
const samplePacks: Pack[] = [
    {
        id: 1,
        name: "Basic Pack",
        description: "Essential features for getting started",
        price: 9.99,
        features: ["Feature 1", "Feature 2", "Feature 3"],
    },
    {
        id: 2,
        name: "Pro Pack",
        description: "Advanced features for power users",
        price: 19.99,
        features: ["Feature 1", "Feature 2", "Feature 3", "Feature 4"],
        isPopular: true,
    },
    {
        id: 3,
        name: "Enterprise Pack",
        description: "Complete solution for businesses",
        price: 49.99,
        features: ["All Features", "Priority Support", "Customization"],
    },
];

export default function Page() {
    const router = useRouter();
    const searchParams = useSearchParams();

    // Extract role filter from URL (or empty)
    const [roleFilter, setRoleFilter] = useState<string[]>([]);
    const [packs, setPacks] = useState<Pack[]>([]);
    const [isLoadingPacks, setIsLoadingPacks] = useState(true);
    const [packError, setPackError] = useState<string | null>(null);

    // Fetch packs data
    useEffect(() => {
        const fetchPacks = async () => {
            try {
                setIsLoadingPacks(true);
                setPackError(null);

                const response = await fetch('http://localhost:8080/api/packs', {
                    credentials: 'include',
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                const text = await response.text();

                // Try to parse the JSON, but handle malformed JSON gracefully
                let data;
                try {
                    data = JSON.parse(text);
                } catch (parseError) {
                    console.warn('Malformed JSON from API, using sample data', parseError);
                    setPackError('Received malformed data from server. Showing sample packs.');
                    setPacks(samplePacks);
                    return;
                }

                setPacks(data);
            } catch (error) {
                console.error('Error fetching packs:', error);
                setPackError('Failed to load packs. Showing sample data.');
                setPacks(samplePacks);
            } finally {
                setIsLoadingPacks(false);
            }
        };

        fetchPacks();
    }, []);

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

            {/* PackCards with data passed as prop */}
            <PackCards packs={packs} isLoading={isLoadingPacks} error={packError} />

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