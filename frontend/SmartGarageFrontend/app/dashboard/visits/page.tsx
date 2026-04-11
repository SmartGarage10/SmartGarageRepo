'use client';

import { useState, useEffect, useCallback } from 'react';
import { useSearchParams } from 'next/navigation';

import { getColumns } from '@/app/dashboard/visits/column';
import { Vehicle } from '@/types/vehicle';
import { User } from '@/types/user';
import { Pack } from '@/types/pack';
import { Service } from '@/types/service';
import { Visit, VISIT_STATUS_OPTIONS, VisitItem, VisitItemType } from '@/types/visit';

import { DataTable } from '@/components/data-table/data-table';
import { DataTableAdvancedToolbar } from '@/components/data-table/data-table-advanced-toolbar';
import { DataTableFilterList } from '@/components/data-table/data-table-filter-list';
import { SearchInput } from '@/components/data-table/data-search';
import { Toaster } from 'sonner';

import { useDataTable } from '@/hooks/useDataTable';
import { CarService } from '@/services/CarService';
import { VisitForm } from '@/components/forms/edit-create-visit-form';
import { createApi } from '@/api/genericApi';

export default function VehiclesPage() {
    const [clients, setClients] = useState<User[]>([]);
    const [employees, setEmployees] = useState<User[]>([]);
    const [vehicles, setVehicles] = useState<Vehicle[]>([]);
    const [packs, setPacks] = useState<Pack[]>([]);
    const [services, setServices] = useState<Service[]>([]);
    const [brandOptions, setBrandOptions] = useState<{ label: string; value: string }[]>([]);
    const [modelOptions, setModelOptions] = useState<{ label: string; value: string }[]>([]);
    const [selectedBrand, setSelectedBrand] = useState<string>('');
    const [isBrandsLoading, setIsBrandsLoading] = useState(false);
    const [isModelsLoading, setIsModelsLoading] = useState(false);
    const [isDataLoading, setIsDataLoading] = useState(true);

    const [selectedRow, setSelectedRow] = useState<Visit | null>(null);

    const packApi = createApi<Pack>("packs");
    const userApi = createApi<User>("users");
    const vehicleApi = createApi<Vehicle>("vehicles");
    const serviceApi = createApi<Service>("services");

    const statusFilterOptions = VISIT_STATUS_OPTIONS.map(status => ({
        value: status,
        label: status.replace('_', ' ')
    }));

    const packFilterOptions = [
        ...packs.map(pack => ({
            label: pack.packName,
            value: pack.packName
        })),
        { label: "CUSTOM PACK", value: "CUSTOM PACK" }
    ];

    useEffect(() => {
        const loadAllData = async () => {
            setIsDataLoading(true);
            try {
                const [fetchedPacks, fetchedUsers, fetchedVehicles, fetchedServices] =
                    await Promise.all([
                        packApi.getAll(),
                        userApi.getAll(),
                        vehicleApi.getAll(),
                        serviceApi.getAll(),
                    ]);

                setPacks(fetchedPacks);
                setClients(fetchedUsers.filter((u: User) => u.role?.roleName === 'CLIENT'));
                setEmployees(fetchedUsers.filter((u: User) =>
                    u.role?.roleName === 'ADMIN' || u.role?.roleName === 'EMPLOYEE'
                ));
                setVehicles(fetchedVehicles);
                setServices(fetchedServices);
            } catch (error) {
                console.error("Failed to load initial data", error);
            } finally {
                setIsDataLoading(false);
            }
        };
        loadAllData();
    }, []);

    useEffect(() => {
        const loadBrands = async () => {
            setIsBrandsLoading(true);
            try {
                const brands = await CarService.fetchBrands();
                setBrandOptions(brands.map((b: string) => ({ label: b, value: b })));
            } catch (err) {
                console.error(err);
            } finally {
                setIsBrandsLoading(false);
            }
        };
        loadBrands();
    }, []);

    const handleEdit = useCallback((visit: Visit) => {
        setSelectedRow(visit);
        setSelectedBrand(visit.vehicle?.brand || '');
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
    } = useDataTable<Visit>({
        fetchUrl: 'http://localhost:8080/api/visits',
        getColumns: ({ onDelete }) =>
            getColumns({
                onEdit: (row: Visit) => {
                    handleEdit(row);
                    setIsFormOpen(true);
                },
                onDelete: async (id: string) => {
                    await onDelete(id);
                },
                brandOptions,
                statusFilterOptions,
                packFilterOptions
            }),
    });

    // =========================
    // ✅ FIXED SUBMIT FUNCTION
    // =========================
    const handleVisitSubmit = useCallback(
        async (visitData: any) => {
            try {
                const isEdit = !!selectedRow?.id;

                const endpoint = isEdit
                    ? `http://localhost:8080/api/update-visit/${selectedRow.id}`
                    : `http://localhost:8080/api/create-visit`;

                const method = isEdit ? 'PUT' : 'POST';

                const visitDate = new Date(visitData.visitDate);

                const servicesFromForm = visitData.visitServices || [];

                let visitItems: any[] = [];

                // CASE 1: CUSTOM PACK
                if (visitData.pack?.packName === "CUSTOM PACK") {
                    visitItems = servicesFromForm.map((service: any) => ({
                        serviceItemId: service.id,
                        quantity: 1,
                        price: service.price || 0
                    }));
                }

                // CASE 2: REGULAR PACK
                else if (visitData.pack && visitData.pack.packName !== "CUSTOM PACK") {
                    const selectedPackObj = packs.find(
                        p => p.packName === visitData.pack?.packName
                    );

                    if (!selectedPackObj) {
                        throw new Error("Pack not found");
                    }

                    visitItems = [{
                        packId: selectedPackObj.id,
                        quantity: 1,
                        price: selectedPackObj.amount || 0
                    }];
                }

                // CASE 3: INDIVIDUAL SERVICES
                else {
                    visitItems = servicesFromForm.map((service: any) => ({
                        serviceItemId: service.id,
                        quantity: 1,
                        price: service.price || 0
                    }));
                }

                // CLEAN VALIDATION
                visitItems = visitItems.filter(item =>
                    (item.serviceItemId && !item.packId) ||
                    (item.packId && !item.serviceItemId)
                );

                const payload = {
                    vehicleId: visitData.vehicle?.id,
                    employeeId: visitData.employee?.id,
                    visitDate: visitDate.toISOString(),
                    status: visitData.status || "SCHEDULED",
                    visitItems
                };

                console.log("📤 PAYLOAD:", payload);

                const response = await fetch(endpoint, {
                    method,
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload),
                    credentials: "include",
                });

                const text = await response.text();

                if (!response.ok) {
                    throw new Error(text);
                }

                console.log("✅ SUCCESS");
                return true;

            } catch (error) {
                console.error("❌ ERROR:", error);
                throw error;
            }
        },
        [selectedRow, packs]
    );

    return (
        <div className="space-y-4">
            <Toaster richColors position="top-center" />

            <DataTable
                key={packs.length}
                table={table}
                className="px-10"
            >
                <DataTableAdvancedToolbar
                    table={table}
                    className="px-0"
                    menuLabel="Add Visit"
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
                        placeholder="Search visits..."
                    />
                </DataTableAdvancedToolbar>
            </DataTable>

            <VisitForm
                open={isFormOpen}
                onOpenChange={setIsFormOpen}
                initialData={selectedRow}
                onSuccess={() => {
                    fetchData();
                    setIsFormOpen(false);
                }}
                onSubmit={handleVisitSubmit}
                clients={clients}
                employees={employees}
                vehicles={vehicles}
                packs={packs}
                services={services}
                isLoading={isDataLoading}
                statusOptions={VISIT_STATUS_OPTIONS}
            />
        </div>
    );
}