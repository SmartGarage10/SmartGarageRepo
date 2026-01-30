// frontend/SmartGarageFrontend/src/app/dashboard/vehicles/page.tsx
'use client';

import { useState, useEffect, useCallback } from 'react';
import { useSearchParams } from 'next/navigation';

import { getColumns} from '@/app/dashboard/visits/column';
import { Vehicle } from '@/types/vehicle';
import { User } from '@/types/user';
import { Pack } from '@/types/pack';
import { Service } from '@/types/service';
import {Visit, VISIT_STATUS_OPTIONS, VisitItem, VisitItemType} from '@/types/visit';

import { DataTable } from '@/components/data-table/data-table';
import { DataTableAdvancedToolbar } from '@/components/data-table/data-table-advanced-toolbar';
import { DataTableFilterList } from '@/components/data-table/data-table-filter-list';
import { SearchInput } from '@/components/data-table/data-search';
import { Toaster } from 'sonner';

import { useDataTable } from '@/hooks/useDataTable';
import { CarService } from '@/services/CarService';
import { VisitForm } from '@/components/forms/edit-create-visit-form';
import { createApi } from '@/api/genericApi';

// interface FilterItem {
//     id: string;
//     value: string | string[];
//     variant: string;
//     operator: string;
//     filterId: string;
// }

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
    const [isDataLoading, setIsDataLoading] = useState(true);

    // Local selected row state for edit form
    const [selectedRow, setSelectedRow] = useState<Visit | null>(null);

    // APIs
    const packApi = createApi<Pack>("packs");
    const userApi = createApi<User>("users");
    const vehicleApi = createApi<Vehicle>("vehicles");
    const serviceApi = createApi<Service>("services");

    const statusFilterOptions = VISIT_STATUS_OPTIONS.map(status => ({
        value: status,
        label: status.replace('_', ' ') // "IN_PROGRESS" -> "IN PROGRESS"
    }));

    // FIXED: Add "Custom Pack" to filter options
    const packFilterOptions = [
        // Regular packs from database
        ...packs.map(pack => ({
            label: pack.packName,
            value: pack.packName
        })),
        // Custom pack option for filtering
        { label: "CUSTOM PACK", value: "CUSTOM PACK" }
    ];

    // Parse brand filter from URL
    // useEffect(() => {
    //     const filtersParam = searchParams.get('filters');
    //     if (filtersParam) {
    //         try {
    //             const filters: FilterItem[] = JSON.parse(decodeURIComponent(filtersParam));
    //             const brandFilter = filters.find((f) => f.id === 'brand');
    //             const brand = brandFilter?.value
    //                 ? Array.isArray(brandFilter.value)
    //                     ? brandFilter.value[0] || ''
    //                     : brandFilter.value
    //                 : '';
    //             setSelectedBrand(brand);
    //         } catch (error) {
    //             console.error('Error parsing filters:', error);
    //         }
    //     }
    // }, [searchParams]);

    // Load all data (clients, employees, vehicles, packs, services)
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

                console.log("Loaded packs:", fetchedPacks);
                console.log("Loaded services:", fetchedServices);

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

    // Edit callback
    const handleEdit = useCallback((visit: Visit) => {
        console.log('[DEBUG] Editing visit', visit);
        setSelectedRow(visit);
        setSelectedBrand(visit.vehicle?.brand || '');
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
                brandOptions,
                statusFilterOptions,
                packFilterOptions
            });
        },
    });

    const handleVisitSubmit = useCallback(
        async (visitData: any) => {
            try {
                console.log("📋 RAW DATA FROM FORM:", visitData);
                console.log("🔍 Services data:", visitData.visitServices);

                const isEdit = !!selectedRow?.id;
                const endpoint = isEdit
                    ? `http://localhost:8080/api/update-visit/${selectedRow.id}`
                    : `http://localhost:8080/api/create-visit`;

                const method = isEdit ? 'PUT' : 'POST';

                const visitDate = new Date(visitData.visitDate);

                // ========== CRITICAL FIX: PROPERLY SET SERVICE ID ==========
                let visitItems: VisitItem[] = [];

                // Get services data from form
                const servicesFromForm = visitData.visitServices || [];
                console.log("🔍 Services from form:", servicesFromForm);

                // CASE 1: Custom Pack
                if (visitData.pack?.packName === "CUSTOM PACK") {
                    console.log("🛠️ Processing CUSTOM PACK");

                    if (servicesFromForm.length === 0) {
                        throw new Error("Custom pack must have at least one service");
                    }

                    visitItems = servicesFromForm.map((service: any) => {
                        console.log("Service object:", service);
                        return {
                            // ✅ CRITICAL: Must include serviceId or serviceItemId
                            serviceItem: service,
                            itemType: VisitItemType.SERVICE,
                            itemName: service.serviceName || service.name,
                            price: service.price || 0,
                            quantity: 1
                        };
                    });
                }
                // CASE 2: Regular Pack
                else if (visitData.pack && visitData.pack.packName !== "CUSTOM PACK") {
                    console.log("📦 Processing REGULAR PACK");

                    const selectedPackObj = packs.find(p => p.packName === visitData.pack?.packName);

                    if (!selectedPackObj) {
                        throw new Error(`Pack "${visitData.pack?.packName}" not found`);
                    }

                    visitItems = [{
                        // ✅ CRITICAL: Must include packId
                        pack: selectedPackObj,
                        itemType: VisitItemType.PACK,
                        itemName: selectedPackObj.packName,
                        price: selectedPackObj.amount || 0,
                        quantity: 1
                    }];
                }
                // CASE 3: Individual Services
                else {
                    console.log("🔧 Processing INDIVIDUAL SERVICES");

                    if (servicesFromForm.length === 0) {
                        throw new Error("Please add at least one service");
                    }

                    visitItems = servicesFromForm.map((service: any) => ({
                        // ✅ CRITICAL: Must include serviceId
                        serviceItem: service, // Include both
                        itemType: VisitItemType.SERVICE,
                        itemName: service.serviceName || service.name,
                        price: service.price || 0,
                        quantity: 1
                    }));
                }

                // ========== VALIDATE: Check IDs are present ==========
                console.log("✅ Generated visitItems (BEFORE validation):", visitItems);

                // Remove items without proper IDs
                visitItems = visitItems.filter(item => {
                    const hasServiceId = item.serviceItem?.id;
                    const hasPackId = item.pack?.id;
                    const isValid = (hasServiceId && !hasPackId) || (!hasServiceId && hasPackId);

                    if (!isValid) {
                        console.warn("❌ Removing invalid item (missing ID):", item);
                    }
                    return isValid;
                });

                if (visitItems.length === 0) {
                    throw new Error("No valid items found. Each item must have either serviceId or packId");
                }

                console.log("✅ Final visitItems (AFTER validation):", visitItems);

                // Calculate total
                const totalAmount = visitItems.reduce((sum, item) =>
                    sum + (item.price * item.quantity), 0
                );

                // ========== BUILD PAYLOAD ==========
                const payload = {
                    client: visitData.client,
                    vehicle: visitData.vehicle,
                    employee: visitData.employee,
                    visitDate: visitDate.toISOString(),
                    status: visitData.status || "SCHEDULED",
                    amount: visitData.amount || totalAmount,
                    currency: visitData.currency || "EUR",
                    pack: visitData.pack?.packName === "CUSTOM PACK" ? null : visitData.pack,
                    // ✅ Key: Send as visitItems (from your debug output)
                    visitItems: visitItems
                };

                console.log("📤 Final payload:", JSON.stringify(payload, null, 2));

                // ========== DEBUG: Check what field names backend expects ==========
                // const testPayloads = [
                //     { ...payload, visitItems: visitItems },
                //     { ...payload, visitServices: visitItems },
                //     {
                //         ...payload,
                //         visitItems: visitItems.map(item => ({
                //             ...item,
                //             // Try different ID field names
                //             serviceId: item.serviceItem?.id,
                //             serviceItem: item.serviceItem
                //         }))
                //     }
                // ];

                // for (const [index, testPayload] of testPayloads.entries()) {
                //     console.log(`🧪 Test ${index + 1}:`,
                //         Object.keys(testPayload).filter(k => Array.isArray(testPayload[k]))
                //     );
                // }

                const response = await fetch(endpoint, {
                    method,
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload),
                    credentials: "include",
                });

                const responseText = await response.text();
                console.log("📥 Backend response:", responseText);

                if (!response.ok) {
                    let errorData;
                    try {
                        errorData = JSON.parse(responseText);
                    } catch {
                        errorData = { message: responseText };
                    }
                    console.error("❌ Backend error:", errorData);
                    throw new Error(errorData.message || "Request failed");
                }

                console.log("✅ Success!");
                return true;
            } catch (error) {
                console.error('❌ Error:', error);
                throw error;
            }
        },
        [selectedRow, packs]
    );

    // const brandSelectOptions = useMemo(
    //     () => brandOptions.map((opt) => ({ ...opt, disabled: isModelsLoading })),
    //     [brandOptions, isModelsLoading]
    // );
    //
    // if (!isMounted) return null;

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
                    menuLabel="Add Visit"
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
                // Pass all the pre-fetched data as props
                clients={clients}
                employees={employees}
                vehicles={vehicles}
                packs={packs}
                services={services}
                isLoading={isDataLoading}
                statusOptions={VISIT_STATUS_OPTIONS} // or statusFilterOptions.map(opt => opt.value)
            />
        </div>
    );
}