// hooks/useDataTable.ts
import {
    getCoreRowModel,
    getPaginationRowModel,
    getFilteredRowModel,
    getSortedRowModel,
    useReactTable,
    type ColumnFiltersState,
    type ColumnDef,
} from '@tanstack/react-table';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { debounce } from 'lodash';
import { toast } from 'sonner';
import { XCircle, CheckCircle } from 'lucide-react';

type UseDataTableProps<T extends { id: string }> = {
    fetchUrl: string;
    getColumns: (callbacks: {
        onEdit: (row: T) => void;
        onDelete: (id: string) => Promise<void>;
    }) => ColumnDef<T>[];
    onDelete?: (id: string) => Promise<void>; // Optional override for delete
};

export function useDataTable<T extends { id: string }>({
                                                           fetchUrl,
                                                           getColumns,
                                                           onDelete,
                                                       }: UseDataTableProps<T>) {
    const [data, setData] = useState<T[]>([]);
    const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([]);
    const [globalFilter, setGlobalFilter] = useState('');
    const [rowSelection, setRowSelection] = useState({});
    const [isFormOpen, setIsFormOpen] = useState(false);
    const [selectedRow, setSelectedRow] = useState<T | null>(null);

    const router = useRouter();
    const searchParams = useSearchParams();

    // Fetch data based on URL query params (filters and search)
    const fetchData = useCallback(async () => {
        try {
            const params = new URLSearchParams(window.location.search);
            const res = await fetch(`${fetchUrl}?${params.toString()}`, {
                credentials: 'include',
                cache: 'no-store',
            });
            if (!res.ok) throw new Error(await res.text());
            const json = await res.json();
            setData(json);
        } catch (error) {
            toast.error('Failed to load data', {
                className: 'bg-destructive text-white',
                description: error instanceof Error ? error.message : 'Unknown error',
                icon: <XCircle className="text-red-500" />,
            });
        }
    }, [fetchUrl]);

    // Internal default delete handler
    const internalHandleSingleDelete = async (id: string): Promise<void> => {
        try {
            const res = await fetch(`${fetchUrl}/${id}`, {
                method: 'DELETE',
                credentials: 'include',
            });
            if (!res.ok) throw new Error('Delete failed');
            toast.success('Deleted successfully', { icon: <CheckCircle /> });
            await fetchData();
        } catch (error) {
            toast.error(error instanceof Error ? error.message : 'Delete failed', {
                icon: <XCircle />,
            });
        }
    };

    // Use external onDelete if provided, else internal handler
    const handleSingleDelete = onDelete ?? internalHandleSingleDelete;

    // Update URL query params when filters or global search changes (debounced)
    const updateUrlFn = useCallback(
        (filters: ColumnFiltersState, search: string) => {
            const params = new URLSearchParams();

            filters.forEach((filter) => {
                if (filter.value !== undefined && filter.value !== null && filter.value !== '') {
                    if (Array.isArray(filter.value)) {
                        filter.value.forEach((val) => {
                            params.append(`${filter.id}`, val);
                        });
                    } else {
                        if (typeof filter.value === "string") {
                            params.set(`${filter.id}`, filter.value);
                        }
                    }
                }
            });

            if (search && search.trim()) {
                params.set('search', search);
            }

            router.replace(`${window.location.pathname}?${params.toString()}`, { scroll: false });
        },
        [router]
    );

    // Persist the debounced function across renders
    const updateUrl = useMemo(() => debounce(updateUrlFn, 500), [updateUrlFn]);

    // Sync URL when filters/search change
    useEffect(() => {
        updateUrl(columnFilters, globalFilter);
    }, [columnFilters, globalFilter, updateUrl]);

    // Initialize filters and search from URL on mount and when URL changes
    useEffect(() => {
        const filters: ColumnFiltersState = [];
        const search = searchParams.get('search') || '';

        searchParams.forEach((value, key) => {
            if (key !== 'search') {
                const existingFilter = filters.find((f) => f.id === key);
                if (existingFilter) {
                    if (Array.isArray(existingFilter.value)) {
                        filters.push({ id: key, value: [value] });
                    } else {
                        filters.push({ id: key, value: value });
                    }
                } else {
                    filters.push({ id: key, value: value });
                }
            }
        });

        setColumnFilters(filters);
        setGlobalFilter(search);
        fetchData();
    }, [fetchData, searchParams]);

    // Batch delete selected rows
    const handleDeleteSelected = async () => {
        const selectedIds = table.getSelectedRowModel().rows.map((row) => row.original.id);
        if (selectedIds.length === 0) return;
        try {
            const res = await fetch(`${fetchUrl}/delete`, {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify(selectedIds),
            });
            if (!res.ok) throw new Error(await res.text());
            toast.success(`${selectedIds.length} items deleted`, { icon: <CheckCircle /> });
            fetchData();
        } catch (error) {
            toast.error(error instanceof Error ? error.message : 'Batch delete failed', {
                icon: <XCircle />,
            });
        }
    };

    // Memoize columns with correct onEdit/onDelete handlers
    const columns = useMemo(
        () =>
            getColumns({
                onEdit: (row) => {
                    setSelectedRow(row);
                    setIsFormOpen(true);
                },
                onDelete: handleSingleDelete,
            }),
        [getColumns, handleSingleDelete]
    );

    const table = useReactTable({
        data,
        columns,
        getRowId: (row) => row.id,
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

    const clearAllFilters = () => {
        setColumnFilters([]);
        setGlobalFilter('');
        table.resetColumnFilters();
        table.resetGlobalFilter();
    };

    return {
        table,
        data,
        globalFilter,
        setGlobalFilter,
        columnFilters,
        clearAllFilters,
        rowSelection,
        setRowSelection,
        isFormOpen,
        setIsFormOpen,
        selectedRow,
        setSelectedRow,
        fetchData,
        handleDeleteSelected,
    };
}