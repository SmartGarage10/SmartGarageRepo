'use client';

import { useState, useEffect, useCallback } from 'react';

import { getColumns } from '@/app/dashboard/vehicles/column';
import { Vehicle } from '@/types/vehicle';
import { User } from '@/types/user';

import { DataTable } from '@/components/data-table/data-table';
import { DataTableAdvancedToolbar } from '@/components/data-table/data-table-advanced-toolbar';
import { DataTableFilterList } from '@/components/data-table/data-table-filter-list';
import { SearchInput } from '@/components/data-table/data-search';
import { Toaster } from 'sonner';

import { useDataTable } from '@/hooks/useDataTable';
import { VehicleForm } from '@/components/forms/edit-create-vehicle-form';
import { CarService } from '@/services/CarService';
import { createApi } from '@/api/genericApi';

export default function VehiclesPage() {
    const [isMounted, setIsMounted] = useState(false);
    const [clients, setClients] = useState<User[]>([]);
    const [vehicles, setVehicles] = useState<Vehicle[]>([]);

    const [brandOptions, setBrandOptions] = useState<{ label: string; value: string }[]>([]);
    const [modelOptions, setModelOptions] = useState<{ label: string; value: string }[]>([]);
    const [selectedBrand, setSelectedBrand] = useState<string>('');

    const [isBrandsLoading, setIsBrandsLoading] = useState(false);
    const [isModelsLoading, setIsModelsLoading] = useState(false);
    const [isDataLoading, setIsDataLoading] = useState(true);

    const [selectedRow, setSelectedRow] = useState<Vehicle | null>(null);

    const vehicleApi = createApi<Vehicle>('vehicles');
    const userApi = createApi<User>('users');

    useEffect(() => {
        const loadData = async () => {
            setIsDataLoading(true);
            try {
                const [fetchedUsers, fetchedVehicles] = await Promise.all([
                    userApi.getAll(),
                    vehicleApi.getAll(),
                ]);

                setClients(fetchedUsers.filter(u => u.role?.roleName === 'CLIENT'));
                setVehicles(fetchedVehicles);
            } catch (error) {
                console.error('Failed to load initial vehicle data', error);
            } finally {
                setIsDataLoading(false);
            }
        };
        loadData();
    }, []);

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

    useEffect(() => {
        setIsMounted(true);
        return () => setIsMounted(false);
    }, []);

    const handleEdit = useCallback((vehicle: Vehicle) => {
        setSelectedRow(vehicle);
        setSelectedBrand(vehicle.brand);
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

    const handleSubmit = useCallback(
        async (vehicleData: Omit<Vehicle, 'id'> & { id?: string }) => {
            try {
                const isEdit = !!selectedRow?.id;

                const endpoint = isEdit
                    ? `http://localhost:8080/api/update-vehicle/${selectedRow.id}`
                    : 'http://localhost:8080/api/create-vehicle';

                const method = isEdit ? 'PUT' : 'POST';

                // FIX: use selectedRow.user, not selectedRow.client
                const fullUser = isEdit ? selectedRow?.user : vehicleData.client;

                const payload = {
                    vehiclePlate: vehicleData.vehiclePlate,
                    vin: vehicleData.vin,
                    brand: vehicleData.brand,
                    model: vehicleData.model,
                    yearOfCreation: vehicleData.year,
                    userId: fullUser.id, // FIX: backend expects userId
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

                await fetchData();
                setIsFormOpen(false);

                console.log(vehicleData);

                return true;
            } catch (error) {
                console.error('Error saving vehicle:', error);
                throw error;
            }
        },
        [selectedRow, fetchData]
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
                    menuLabel="Add Vehicle"
                    onCreateClick={() => {
                        setSelectedRow(null);
                        setSelectedBrand('');
                        setIsFormOpen(true);
                    }}
                    deleteMessage={"This action cannot be undone. This will permanently delete the vehicles and remove all associated data."}
                    onDeleteClick={
                        table.getSelectedRowModel().rows.length > 0 ? handleDeleteSelected : undefined
                    }
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
                brandOptions={brandOptions}
                modelOptions={modelOptions}
                selectedBrand={selectedBrand}
                onBrandChange={setSelectedBrand}
                isBrandsLoading={isBrandsLoading}
                isModelsLoading={isModelsLoading}
            />
        </div>
    );
}
