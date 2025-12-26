// frontend/SmartGarageFrontend/src/app/dashboard/vehicles/page.tsx
'use client';

import { useState, useEffect, useCallback, useMemo } from 'react';

import { getColumns } from '@/app/dashboard/vehicles/column';
import { Vehicle } from '@/types/vehicle';
import { User } from '@/types/user';

import { DataTable } from '@/components/data-table/data-table';
import { DataTableAdvancedToolbar } from '@/components/data-table/data-table-advanced-toolbar';
import { DataTableFilterList } from '@/components/data-table/data-table-filter-list';
import { SearchInput } from '@/components/data-table/data-search';
import { Toaster } from 'sonner';

import { VehicleForm } from '@/components/forms/edit-create-vehicle-form';
import { useDataTable } from '@/hooks/useDataTable';
import { CarService } from '@/services/CarService';
import { createApi } from '@/api/genericApi';

export default function VehiclesPage() {
    const [isMounted, setIsMounted] = useState(false);

    const [clients, setClients] = useState<User[]>([]);

    const [brandOptions, setBrandOptions] = useState<{ label: string; value: string }[]>([]);
    const [modelOptions, setModelOptions] = useState<{ label: string; value: string }[]>([]);
    const [selectedBrand, setSelectedBrand] = useState<string>('');

    const [isBrandsLoading, setIsBrandsLoading] = useState(false);
    const [isModelsLoading, setIsModelsLoading] = useState(false);

    const [selectedRow, setSelectedRow] = useState<Vehicle | null>(null);

    const userApi = createApi<User>('users');

    // -------------------------
    // Load clients
    useEffect(() => {
        const loadClients = async () => {
            try {
                const users = await userApi.getAll();
                setClients(users.filter(u => u.role?.roleName === 'CLIENT'));
            } catch (error) {
                console.error('Failed to load clients', error);
            }
        };
        loadClients();
    }, []);

    // -------------------------
    // Load brands
    useEffect(() => {
        const loadBrands = async () => {
            setIsBrandsLoading(true);
            try {
                const brands = await CarService.fetchBrands();
                setBrandOptions(brands.map(b => ({ label: b, value: b })));
            } catch (error) {
                console.error('Failed to load brands', error);
            } finally {
                setIsBrandsLoading(false);
            }
        };
        loadBrands();
    }, []);

    // -------------------------
    // Load models on brand change
    useEffect(() => {
        const loadModels = async () => {
            if (!selectedBrand) {
                setModelOptions([]);
                return;
            }
            setIsModelsLoading(true);
            try {
                const models = await CarService.fetchModels(selectedBrand);
                setModelOptions(models.map(m => ({ label: m, value: m })));
            } catch (error) {
                console.error('Failed to load models', error);
                setModelOptions([]);
            } finally {
                setIsModelsLoading(false);
            }
        };
        loadModels();
    }, [selectedBrand]);

    useEffect(() => {
        setIsMounted(true);
        return () => setIsMounted(false);
    }, []);

    // -------------------------
    // Edit handler
    const handleEdit = useCallback((vehicle: Vehicle) => {
        setSelectedRow(vehicle);
        setSelectedBrand(vehicle.brand);
    }, []);

    // -------------------------
    // Data table
    const {
        table,
        globalFilter,
        setGlobalFilter,
        clearAllFilters,
        fetchData,
        handleDeleteSelected,
        isFormOpen,
        setIsFormOpen,
    } = useDataTable<Vehicle>({
        fetchUrl: 'http://localhost:8080/api/vehicles',
        getColumns: ({ onDelete }) =>
            getColumns({
                onEdit: (row: Vehicle) => {
                    handleEdit(row);
                    setIsFormOpen(true);
                },
                onDelete,
                brandOptions,
                modelOptions,
            }),
    });

    // -------------------------
    // Submit handler
    const handleSubmit = useCallback(
        async (vehicleData: Omit<Vehicle, 'id'> & { id?: string }) => {
            try {
                const isEdit = !!selectedRow?.id;
                const endpoint = isEdit
                    ? `http://localhost:8080/api/vehicles/${selectedRow!.id}`
                    : 'http://localhost:8080/api/vehicles';

                const method = isEdit ? 'PUT' : 'POST';

                const payload = {
                    vehiclePlate: vehicleData.vehiclePlate,
                    vin: vehicleData.vin,
                    brand: vehicleData.brand,
                    model: vehicleData.model,
                    year: vehicleData.year,
                    client: { id: vehicleData.client.id },
                };

                const res = await fetch(endpoint, {
                    method,
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload),
                    credentials: 'include',
                });

                if (!res.ok) {
                    const errorData = await res.json().catch(() => ({}));
                    throw new Error(errorData.message || 'Failed to save vehicle');
                }

                return true;
            } catch (error) {
                console.error('Error saving vehicle:', error);
                throw error;
            }
        },
        [selectedRow]
    );

    const brandSelectOptions = useMemo(
        () => brandOptions.map(opt => ({ ...opt, disabled: isModelsLoading })),
        [brandOptions, isModelsLoading]
    );

    if (!isMounted) return null;

    return (
        <div className="space-y-4">
            <Toaster richColors position="top-center" />

            <DataTable table={table} className="px-10">
                <DataTableAdvancedToolbar
                    table={table}
                    menuLabel="Add Vehicle"
                    onCreateClick={() => {
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

            <VehicleForm
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
                isBrandsLoading={isBrandsLoading}
                isModelsLoading={isModelsLoading}
                selectedBrand={selectedBrand}
                onBrandChange={(brand: string) => {
                    setSelectedBrand(brand);
                    setModelOptions([]);
                }}
            />
        </div>
    );
}
