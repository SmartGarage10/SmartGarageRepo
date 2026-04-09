'use client';

import { useState, useEffect, useCallback } from 'react';
import { Toaster } from 'sonner';

import { getColumns } from '@/app/dashboard/vehicles/column';
import { Vehicle } from '@/types/vehicle';
import { User } from '@/types/user';

import { DataTable } from '@/components/data-table/data-table';
import { DataTableAdvancedToolbar } from '@/components/data-table/data-table-advanced-toolbar';
import { DataTableFilterList } from '@/components/data-table/data-table-filter-list';
import { SearchInput } from '@/components/data-table/data-search';
import { useDataTable } from '@/hooks/useDataTable';
import { VehicleForm } from '@/components/forms/edit-create-vehicle-form';
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

    const vehicleApi = createApi<Vehicle>('vehicles');
    const userApi = createApi<User>('users');

    // Load clients and vehicles
    useEffect(() => {
        const loadData = async () => {
            try {
                const [fetchedUsers, fetchedVehicles] = await Promise.all([
                    userApi.getAll(),
                    vehicleApi.getAll(),
                ]);

                // Only clients
                setClients(fetchedUsers.filter(u => u.role?.roleName === 'CLIENT'));

                // Map backend VehicleResponseDTO to frontend Vehicle type
                const mappedVehicles: Vehicle[] = fetchedVehicles.map(v => ({
                    id: v.id.toString(),
                    vehiclePlate: v.vehiclePlate,
                    vin: v.vin,
                    brand: v.brand,
                    model: v.model,
                    year: v.yearOfCreation.toString(), // DTO returns Year
                    user: v.user, // matches DTO
                }));

                // Set vehicles into table
                setVehicles(mappedVehicles);
            } catch (err) {
                console.error('Failed to load data', err);
            }
        };
        loadData();
    }, []);

    // Load brands
    useEffect(() => {
        const loadBrands = async () => {
            setIsBrandsLoading(true);
            try {
                const brands = await CarService.fetchBrands();
                setBrandOptions(brands.map(b => ({ label: b, value: b })));
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
                setModelOptions(models.map(m => ({ label: m, value: m })));
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

    // Submit handler for create/edit
    const handleSubmit = useCallback(
        async (vehicleData: Omit<Vehicle, 'id'> & { id?: string; user?: User }) => {
            try {
                const isEdit = !!selectedRow?.id;
                const endpoint = isEdit
                    ? `http://localhost:8080/api/update-vehicle/${selectedRow.id}`
                    : 'http://localhost:8080/api/create-vehicle';
                const method = isEdit ? 'PUT' : 'POST';

                const fullClient = isEdit ? selectedRow?.client : vehicleData.client;
                if (!fullClient?.id) throw new Error('Client must be selected');

                const payload = {
                    vehiclePlate: vehicleData.vehiclePlate,
                    vin: vehicleData.vin,
                    brand: vehicleData.brand,
                    model: vehicleData.model,
                    yearOfCreation: vehicleData.year, // DTO expects this
                    userId: fullClient.id, // backend expects userId to map to client
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
                return true;
            } catch (err) {
                console.error('Error saving vehicle:', err);
                throw err;
            }
        },
        [selectedRow, fetchData]
    );

    if (!isMounted) return null;

    return (
        <div className="space-y-4">
            <Toaster richColors position="top-center" />

            <DataTable table={table} className="px-10" key={`brand-${brandOptions.length}-model-${modelOptions.length}`}>
                <DataTableAdvancedToolbar
                    table={table}
                    menuLabel="Add Vehicle"
                    onCreateClick={() => {
                        setSelectedRow(null);
                        setSelectedBrand('');
                        setIsFormOpen(true);
                    }}
                    deleteMessage="This action cannot be undone. This will permanently delete the vehicles and remove all associated data."
                    onDeleteClick={table.getSelectedRowModel().rows.length > 0 ? handleDeleteSelected : undefined}
                >
                    <DataTableFilterList table={table} onClearAll={clearAllFilters} />
                    <SearchInput value={globalFilter} onChange={setGlobalFilter} placeholder="Search vehicles..." />
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