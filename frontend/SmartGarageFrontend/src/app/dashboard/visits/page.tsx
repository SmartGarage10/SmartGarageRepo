// frontend/SmartGarageFrontend/src/app/dashboard/vehicles/page.tsx
'use client';

import { useState, useEffect, useCallback, useMemo } from 'react';
import { useSearchParams } from 'next/navigation';

import { getColumns, Visit } from '@/src/app/dashboard/visits/column';
import { Vehicle } from '@/types/vehicle';
import { User } from '@/types/user';

import { DataTable } from '@/components/data-table/data-table';
import { DataTableAdvancedToolbar } from '@/components/data-table/data-table-advanced-toolbar';
import { DataTableFilterList } from '@/components/data-table/data-table-filter-list';
import { SearchInput } from '@/components/data-table/data-search';
import { Toaster } from 'sonner';

import { useDataTable } from '@/hooks/useDataTable';
import { CarService } from '@/services/CarService';
import { VisitForm } from '@/components/forms/edit-create-visit-form';

interface FilterItem {
    id: string;
    value: string | string[];
    variant: string;
    operator: string;
    filterId: string;
}

export default function VehiclesPage() {
    const searchParams = useSearchParams();

    const [isMounted, setIsMounted] = useState(false);
    const [clients, setClients] = useState<User[]>([]);
    const [brandOptions, setBrandOptions] = useState<{ label: string; value: string }[]>([]);
    const [modelOptions, setModelOptions] = useState<{ label: string; value: string }[]>([]);
    const [selectedBrand, setSelectedBrand] = useState<string>('');
    const [isModelsLoading, setIsModelsLoading] = useState(false);
    const [isBrandsLoading, setIsBrandsLoading] = useState(false);

    // Local selected row state for edit form
    const [selectedRow, setSelectedRow] = useState<Visit | null>(null);

    // Parse brand filter from URL
    useEffect(() => {
        const filtersParam = searchParams.get('filters');
        if (filtersParam) {
            try {
                const filters: FilterItem[] = JSON.parse(decodeURIComponent(filtersParam));
                const brandFilter = filters.find((f) => f.id === 'brand');
                const brand = brandFilter?.value
                    ? Array.isArray(brandFilter.value)
                        ? brandFilter.value[0] || ''
                        : brandFilter.value
                    : '';
                setSelectedBrand(brand);
            } catch (error) {
                console.error('Error parsing filters:', error);
            }
        }
    }, [searchParams]);

    // Load brands
    useEffect(() => {
        const loadBrands = async () => {
            setIsBrandsLoading(true);
            try {
                const brands = await CarService.fetchBrands();
                setBrandOptions(brands.map((brand: string) => ({ label: brand, value: brand })));
            } catch (err) {
                console.error('Error loading brands:', err);
            } finally {
                setIsBrandsLoading(false);
            }
        };
        loadBrands();
    }, []);

    // Load models when brand changes
    useEffect(() => {
        const loadModels = async () => {
            if (!selectedBrand) {
                setModelOptions([]);
                return;
            }
            setIsModelsLoading(true);
            try {
                const models = await CarService.fetchModels(selectedBrand);
                setModelOptions(models.map((model: string) => ({ label: model, value: model })));
            } catch (err) {
                console.error('Error loading models:', err);
                setModelOptions([]);
            } finally {
                setIsModelsLoading(false);
            }
        };
        loadModels();
    }, [selectedBrand]);

    // Fetch clients
    useEffect(() => {
        setIsMounted(true);
        const fetchClients = async () => {
            try {
                const res = await fetch('http://localhost:8080/api/users', {
                    credentials: 'include',
                });
                if (!res.ok) {
                    console.error('Failed to fetch clients:', res.status, res.statusText);
                    throw new Error('Failed to fetch clients');
                }
                const data = await res.json();
                setClients(data);
            } catch (error) {
                console.error('Error fetching clients:', error);
            }
        };
        fetchClients();
        return () => setIsMounted(false);
    }, []);

    // Edit callback
    const handleEdit = useCallback((visit: Visit) => {
        console.log('[DEBUG] Editing visit', visit);
        setSelectedRow(visit);
        setSelectedBrand(visit.brand);
    }, []);

    // DataTable hook
    const {
        table,
        globalFilter,
        setGlobalFilter,
        clearAllFilters,
        fetchData,
        handleDeleteSelected,
        isFormOpen,
        setIsFormOpen,
    } = useDataTable<Visit>({
        fetchUrl: 'http://localhost:8080/api/visits',
        getColumns: ({ onDelete }) => {
            return getColumns({
                onEdit: (row: Visit) => {
                    handleEdit(row);
                    setIsFormOpen(true);
                },
                onDelete: async (id: string) => {
                    await onDelete(id);
                },
            });
        },
    });

    // Submit handler
    const handleSubmit = useCallback(
        async (vehicleData: Omit<Vehicle, 'id'> & { id?: string }) => {
            if (!isMounted) return false;
            try {
                const isEdit = !!selectedRow;
                const endpoint = isEdit
                    ? `http://localhost:8080/api/visits/${selectedRow!.id}`
                    : 'http://localhost:8080/api/visits';

                const method = isEdit ? 'PUT' : 'POST';

                const payload = {
                    vehiclePlate: vehicleData.vehiclePlate,
                    vin: vehicleData.vin,
                    client: { id: vehicleData.client.id },
                    brand: vehicleData.brand,
                    model: vehicleData.model,
                    year: vehicleData.year,
                };

                const res = await fetch(endpoint, {
                    method,
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload),
                });

                if (!res.ok) {
                    const errorData = await res.json().catch(() => ({}));
                    throw new Error(
                        errorData.message ||
                        `Failed to ${isEdit ? 'update' : 'create'} vehicle (${res.status})`
                    );
                }

                await fetchData();
                setIsFormOpen(false);
                return true;
            } catch (error) {
                console.error('Error saving vehicle:', error);
                throw error;
            }
        },
        [selectedRow, fetchData, isMounted, setIsFormOpen]
    );

    const brandSelectOptions = useMemo(
        () => brandOptions.map((opt) => ({ ...opt, disabled: isModelsLoading })),
        [brandOptions, isModelsLoading]
    );

    if (!isMounted) return null;

    return (
        <div className="space-y-4">
            <Toaster richColors position="top-center" />

            <DataTable
                table={table}
                className="px-10"
                key={`brand-${brandOptions.length}-model-${modelOptions.length}`}
            >
                <DataTableAdvancedToolbar
                    table={table}
                    className="px-0"
                    menuLabel="Add Vehicle"
                    onCreateClick={() => {
                        console.log('[DEBUG] Creating new visit');
                        setSelectedRow(null);
                        setSelectedBrand('');
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
                        placeholder="Search vehicles..."
                    />
                </DataTableAdvancedToolbar>
            </DataTable>

            <VisitForm
                open={isFormOpen}
                onOpenChange={setIsFormOpen}
                initialData={selectedRow}
                clients={clients}
                onSubmit={handleSubmit}
                onSuccess={() => {
                    fetchData();
                    setIsFormOpen(false);
                }}
                brandOptions={brandSelectOptions}
                modelOptions={modelOptions}
                isModelsLoading={isModelsLoading}
                isBrandsLoading={isBrandsLoading}
                selectedBrand={selectedBrand}
                onBrandChange={(brand: string) => {
                    setModelOptions([]);
                    setSelectedBrand(brand);
                }}
            />
        </div>
    );
}
