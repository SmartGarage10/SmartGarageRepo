"use client";

import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import MultipleSelector from "@/components/ui/multiple-selector";
import {
    Form,
    FormControl,
    FormDescription,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Check, ChevronsUpDown, X } from "lucide-react";
import * as Dialog from "@radix-ui/react-dialog";
import { cn } from "@/lib/utils";
import {
    Command,
    CommandEmpty,
    CommandGroup,
    CommandInput,
    CommandItem,
} from "@/components/ui/command";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";

import { User, UserRole } from "@/types/user";
import { Vehicle } from "@/types/vehicle";
import { Pack } from "@/types/pack";
import { Service } from "@/types/service";
import { createApi } from "@/api/genericApi";

interface VisitFormValues {
    id?: string;
    client: User;
    vehicle: Vehicle;
    employee: User;
    status: string;
    visitDate: Date;
    amount: number;
    currency: string;
    pack: { pack: string };
}

interface VisitFormProps {
    initialData?: VisitFormValues | null;
    children?: React.ReactNode;
    open?: boolean;
    onOpenChange?: (open: boolean) => void;
    onSuccess?: () => void;
    isSubmitting?: boolean;
}

export function VisitForm({
                              initialData,
                              children,
                              open,
                              onOpenChange,
                              onSuccess,
                              isSubmitting = false,
                          }: VisitFormProps) {
    const router = useRouter();

    const form = useForm<VisitFormValues>({
        defaultValues: {
            client: { id: "", name: "", email: "" },
            vehicle: {
                id: "",
                vehiclePlate: "",
                vin: "",
                client: { id: "", name: "", email: "" },
                brand: "",
                model: "",
                year: 0,
            },
            employee: { id: "", name: "", email: "" },
            visitDate: undefined,
            amount: 0,
            currency: "EUR",
            pack: { pack: "" },
            status: "SCHEDULED",
        },
    });

    const [clients, setClients] = useState<User[]>([]);
    const [employees, setEmployees] = useState<User[]>([]);
    const [vehicles, setVehicles] = useState<Vehicle[]>([]);
    const [packs, setPacks] = useState<Pack[]>([]);
    const [services, setServices] = useState<Service[]>([]);
    const [selectedServices, setSelectedServices] = useState<Service[]>([]);

    const [userDropdownOpen, setUserDropdownOpen] = useState(false);
    const [employeeDropdownOpen, setEmployeeDropdownOpen] = useState(false);
    const [vehicleDropdownOpen, setVehicleDropdownOpen] = useState(false);
    const [packDropdownOpen, setPackDropdownOpen] = useState(false);
    const [statusDropdownOpen, setStatusDropdownOpen] = useState(false);

    const [statuses] = useState<string[]>([
        "SCHEDULED",
        "IN_PROGRESS",
        "COMPLETED",
        "CANCELLED",
    ]);

    const packApi = createApi<Pack>("packs");
    const userApi = createApi<User>("users");
    const vehicleApi = createApi<Vehicle>("vehicles");
    const serviceApi = createApi<Service>("services");

    // Load Data
    useEffect(() => {
        async function loadData() {
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
                setClients(
                    fetchedUsers.filter((u: User) => u.role?.roleName === UserRole.CLIENT)
                );
                setEmployees(
                    fetchedUsers.filter(
                        (u: User) =>
                            u.role?.roleName === UserRole.ADMIN ||
                            u.role?.roleName === UserRole.EMPLOYEE
                    )
                );
                setVehicles(fetchedVehicles);
                setServices(fetchedServices);
            } catch (error) {
                console.error("Failed to load initial data", error);
            }
        }
        loadData();
    }, []);

    // Reset form when initialData changes or dialog opens
    useEffect(() => {
        if (initialData) {
            form.reset(initialData);
            if (initialData.pack.pack === "CUSTOM PACK") {
                // You might want to load selected services from initialData here
                setSelectedServices([]);
            }
        } else if (!open) {
            // Reset form when dialog closes without initialData
            form.reset({
                client: { id: "", name: "", email: "" },
                vehicle: {
                    id: "",
                    vehiclePlate: "",
                    vin: "",
                    client: { id: "", name: "", email: "" },
                    brand: "",
                    model: "",
                    year: 0,
                },
                employee: { id: "", name: "", email: "" },
                visitDate: undefined,
                amount: 0,
                currency: "EUR",
                pack: { pack: "" },
                status: "SCHEDULED",
            });
            setSelectedServices([]);
        }
    }, [initialData, open, form]);

    // --- Amount logic ---
    const [amount, setAmount] = useState<number>(0);
    const selectedPack = form.watch("pack.pack");

    useEffect(() => {
        console.log("Selected pack:", selectedPack);
        console.log("Available packs:", packs);

        if (selectedPack && selectedPack !== "CUSTOM PACK") {
            const packObj = packs.find((p) => p.packName === selectedPack);
            console.log("Found pack object:", packObj);
            const packAmount = packObj?.price ?? packObj?.amount ?? 0;
            console.log("Setting amount to:", packAmount);
            setAmount(packAmount);
            form.setValue("amount", packAmount);
        } else if (selectedPack === "CUSTOM PACK") {
            const total = selectedServices.reduce(
                (sum, s) => sum + (s.price ?? 0),
                0
            );
            console.log("Custom pack total:", total);
            setAmount(total);
            form.setValue("amount", total);
        } else {
            setAmount(0);
            form.setValue("amount", 0);
        }
    }, [selectedPack, selectedServices, packs, form]);

    const handleSubmit = async (data: VisitFormValues) => {
        try {
            // Validate required fields
            if (!data.visitDate) {
                form.setError("visitDate", {
                    type: "manual",
                    message: "Visit date is required"
                });
                return;
            }

            if (!data.client?.id) {
                form.setError("client", {
                    type: "manual",
                    message: "Client is required"
                });
                return;
            }

            if (!data.vehicle?.id) {
                form.setError("vehicle", {
                    type: "manual",
                    message: "Vehicle is required"
                });
                return;
            }

            if (!data.employee?.id) {
                form.setError("employee", {
                    type: "manual",
                    message: "Employee is required"
                });
                return;
            }

            const isUpdate = !!initialData?.id;
            const endpoint = isUpdate
                ? `http://localhost:8080/api/update-visit/${initialData?.id}`
                : "http://localhost:8080/api/create-visit";
            const method = isUpdate ? "PUT" : "POST";

            // Safe date handling
            const visitDate = data.visitDate instanceof Date ? data.visitDate : new Date(data.visitDate);

            // Find the selected pack object
            const selectedPackObj = packs.find(p => p.packName === data.pack.pack);

            // Fix the payload structure - send full objects as expected by the DTO
            const payload = {
                client: data.client, // Send full client object
                vehicle: data.vehicle, // Send full vehicle object
                employee: data.employee, // Send full employee object
                visitDate: visitDate.toISOString(),
                status: data.status || "SCHEDULED",
                amount: data.amount,
                currency: data.currency || "EUR",
                pack: selectedPackObj || { packName: data.pack.pack }, // Send pack object or create minimal one
                services: selectedPack === "CUSTOM PACK" ? selectedServices : null
            };

            console.log("Submitting payload:", payload);

            const response = await fetch(endpoint, {
                method,
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
                credentials: "include",
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || "Request failed");
            }

            if (!initialData) {
                form.reset();
                setSelectedServices([]);
            }

            onOpenChange?.(false);
            onSuccess?.();
            router.refresh();
        } catch (error) {
            console.error("Operation error:", error);
            // Using setError for root errors
            form.setError("root", {
                type: "manual",
                message: error instanceof Error ? error.message : "An error occurred",
            });
        }
    };

    const formatVehicleLabel = (vehicle: Vehicle) => {
        return `${vehicle.vehiclePlate} - ${vehicle.brand} ${vehicle.model} (${vehicle.year})`;
    };

    // Get selected pack object for displaying price below
    const selectedPackObj = packs.find((p) => p.packName === selectedPack);

    return (
        <Dialog.Root open={open} onOpenChange={onOpenChange}>
            {children && <Dialog.Trigger asChild>{children}</Dialog.Trigger>}
            <Dialog.Portal>
                <Dialog.Overlay className="fixed inset-0 z-50 bg-black/80 backdrop-blur-sm" />
                <Dialog.Content
                    className={cn(
                        "fixed right-0 top-0 z-50 h-full w-full max-w-sm border-l bg-background shadow-lg",
                        "data-[state=open]:animate-in data-[state=closed]:animate-out",
                        "data-[state=closed]:slide-out-to-right data-[state=open]:slide-in-from-right"
                    )}
                >
                    <div className="h-full overflow-y-auto p-6">
                        <div className="flex justify-between items-center mb-6">
                            <div>
                                <Dialog.Title className="text-xl font-bold">
                                    {initialData ? "Edit Vehicle Visit" : "Add Vehicle Visit"}
                                </Dialog.Title>
                                <Dialog.Description className="text-sm text-muted-foreground">
                                    {initialData
                                        ? "Update visit details."
                                        : "Fill in the details to add a new visit."}
                                </Dialog.Description>
                            </div>
                            <Dialog.Close className="opacity-70 hover:opacity-100">
                                <X className="h-5 w-5" />
                            </Dialog.Close>
                        </div>

                        <Form {...form}>
                            <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
                                {/* Client Field */}
                                <FormField
                                    control={form.control}
                                    name="client"
                                    rules={{ required: "Client is required" }}
                                    render={({ field }) => (
                                        <FormItem className="flex flex-col">
                                            <FormLabel>Client *</FormLabel>
                                            <Popover open={userDropdownOpen} onOpenChange={setUserDropdownOpen}>
                                                <PopoverTrigger asChild>
                                                    <FormControl>
                                                        <Button
                                                            variant="outline"
                                                            role="combobox"
                                                            className={cn(
                                                                "w-full h-10 justify-between",
                                                                !field.value?.id && "text-muted-foreground"
                                                            )}
                                                        >
                                                            {field.value?.name || "Select client"}
                                                            <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                                                        </Button>
                                                    </FormControl>
                                                </PopoverTrigger>
                                                <PopoverContent
                                                    className="w-full p-0"
                                                    style={{ width: "var(--radix-popover-trigger-width)" }}
                                                    align="start"
                                                >
                                                    <Command>
                                                        <CommandInput placeholder="Search clients..." />
                                                        <CommandEmpty>No clients found.</CommandEmpty>
                                                        <CommandGroup className="max-h-[300px] overflow-y-auto">
                                                            {clients.map((client) => (
                                                                <CommandItem
                                                                    key={client.id}
                                                                    value={client.name}
                                                                    onSelect={() => {
                                                                        form.setValue("client", client);
                                                                        setUserDropdownOpen(false);
                                                                    }}
                                                                >
                                                                    <div className="flex items-center gap-3">
                                                                        <div>
                                                                            <div>{client.name}</div>
                                                                            <div className="text-xs text-muted-foreground">
                                                                                {client.email}
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <Check
                                                                        className={cn(
                                                                            "ml-auto h-4 w-4",
                                                                            field.value?.id === client.id
                                                                                ? "opacity-100"
                                                                                : "opacity-0"
                                                                        )}
                                                                    />
                                                                </CommandItem>
                                                            ))}
                                                        </CommandGroup>
                                                    </Command>
                                                </PopoverContent>
                                            </Popover>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                {/* Vehicle Field */}
                                <FormField
                                    control={form.control}
                                    name="vehicle"
                                    rules={{ required: "Vehicle is required" }}
                                    render={({ field }) => {
                                        const clientId = form.watch("client.id");
                                        const clientSelected = !!clientId;
                                        const filteredVehicles = vehicles.filter(
                                            (vehicle) => vehicle.client?.id === clientId
                                        );

                                        return (
                                            <FormItem className="flex flex-col">
                                                <FormLabel>Vehicle *</FormLabel>
                                                <Popover
                                                    open={vehicleDropdownOpen}
                                                    onOpenChange={(open) => {
                                                        if (!clientSelected && open) return;
                                                        setVehicleDropdownOpen(open);
                                                    }}
                                                >
                                                    <PopoverTrigger asChild>
                                                        <FormControl>
                                                            <Button
                                                                variant="outline"
                                                                role="combobox"
                                                                className={cn(
                                                                    "w-full h-10 justify-between",
                                                                    !field.value?.id && "text-muted-foreground",
                                                                    !clientSelected && "opacity-50 cursor-not-allowed"
                                                                )}
                                                                disabled={!clientSelected}
                                                            >
                                                                {!clientSelected
                                                                    ? "Select a client first"
                                                                    : field.value?.id
                                                                        ? formatVehicleLabel(field.value)
                                                                        : "Select vehicle"}
                                                                <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                                                            </Button>
                                                        </FormControl>
                                                    </PopoverTrigger>
                                                    <PopoverContent
                                                        className="w-full p-0"
                                                        style={{ width: "var(--radix-popover-trigger-width)" }}
                                                        align="start"
                                                    >
                                                        <Command>
                                                            <CommandInput placeholder="Search vehicles..." />
                                                            <CommandEmpty>No vehicles found.</CommandEmpty>
                                                            <CommandGroup className="max-h-[300px] overflow-y-auto">
                                                                {filteredVehicles.map((vehicle) => (
                                                                    <CommandItem
                                                                        key={vehicle.id}
                                                                        value={vehicle.vehiclePlate}
                                                                        onSelect={() => {
                                                                            form.setValue("vehicle", vehicle);
                                                                            setVehicleDropdownOpen(false);
                                                                        }}
                                                                    >
                                                                        {formatVehicleLabel(vehicle)}
                                                                        <Check
                                                                            className={cn(
                                                                                "ml-auto h-4 w-4",
                                                                                field.value?.id === vehicle.id
                                                                                    ? "opacity-100"
                                                                                    : "opacity-0"
                                                                            )}
                                                                        />
                                                                    </CommandItem>
                                                                ))}
                                                            </CommandGroup>
                                                        </Command>
                                                    </PopoverContent>
                                                </Popover>
                                                <FormMessage />
                                            </FormItem>
                                        );
                                    }}
                                />

                                {/* Employee Field */}
                                <FormField
                                    control={form.control}
                                    name="employee"
                                    rules={{ required: "Employee is required" }}
                                    render={({ field }) => (
                                        <FormItem className="flex flex-col">
                                            <FormLabel>Employee *</FormLabel>
                                            <Popover open={employeeDropdownOpen} onOpenChange={setEmployeeDropdownOpen}>
                                                <PopoverTrigger asChild>
                                                    <FormControl>
                                                        <Button
                                                            variant="outline"
                                                            role="combobox"
                                                            className={cn(
                                                                "w-full h-10 justify-between",
                                                                !field.value?.id && "text-muted-foreground"
                                                            )}
                                                        >
                                                            {field.value?.name || "Select employee"}
                                                            <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                                                        </Button>
                                                    </FormControl>
                                                </PopoverTrigger>
                                                <PopoverContent
                                                    className="w-full p-0"
                                                    style={{ width: "var(--radix-popover-trigger-width)" }}
                                                    align="start"
                                                >
                                                    <Command>
                                                        <CommandInput placeholder="Search employees..." />
                                                        <CommandEmpty>No employees found.</CommandEmpty>
                                                        <CommandGroup className="max-h-[300px] overflow-y-auto">
                                                            {employees.map((employee) => (
                                                                <CommandItem
                                                                    key={employee.id}
                                                                    value={employee.name}
                                                                    onSelect={() => {
                                                                        form.setValue("employee", employee);
                                                                        setEmployeeDropdownOpen(false);
                                                                    }}
                                                                >
                                                                    <div className="flex items-center gap-3">
                                                                        <div>
                                                                            <div>{employee.name}</div>
                                                                            <div className="text-xs text-muted-foreground">
                                                                                {employee.email}
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <Check
                                                                        className={cn(
                                                                            "ml-auto h-4 w-4",
                                                                            field.value?.id === employee.id
                                                                                ? "opacity-100"
                                                                                : "opacity-0"
                                                                        )}
                                                                    />
                                                                </CommandItem>
                                                            ))}
                                                        </CommandGroup>
                                                    </Command>
                                                </PopoverContent>
                                            </Popover>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                {/* Visit Date Field */}
                                <FormField
                                    control={form.control}
                                    name="visitDate"
                                    rules={{ required: "Visit date is required" }}
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Visit Date *</FormLabel>
                                            <FormControl>
                                                <Input
                                                    type="datetime-local"
                                                    value={
                                                        field.value && field.value instanceof Date
                                                            ? field.value.toISOString().slice(0, 16)
                                                            : field.value
                                                                ? new Date(field.value).toISOString().slice(0, 16)
                                                                : ""
                                                    }
                                                    onChange={(e) =>
                                                        field.onChange(e.target.value ? new Date(e.target.value) : undefined)
                                                    }
                                                />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                {/* Status Field */}
                                <FormField
                                    control={form.control}
                                    name="status"
                                    render={({ field }) => (
                                        <FormItem className="flex flex-col">
                                            <FormLabel>Status</FormLabel>
                                            <Popover open={statusDropdownOpen} onOpenChange={setStatusDropdownOpen}>
                                                <PopoverTrigger asChild>
                                                    <FormControl>
                                                        <Button
                                                            variant="outline"
                                                            role="combobox"
                                                            className={cn(
                                                                "w-full h-10 justify-between",
                                                                !field.value && "text-muted-foreground"
                                                            )}
                                                        >
                                                            {field.value || "Select status"}
                                                            <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                                                        </Button>
                                                    </FormControl>
                                                </PopoverTrigger>
                                                <PopoverContent className="w-full p-0" style={{ width: "var(--radix-popover-trigger-width)" }} align="start">
                                                    <Command>
                                                        <CommandEmpty>No status found.</CommandEmpty>
                                                        <CommandGroup className="max-h-[300px] overflow-y-auto">
                                                            {statuses.map((status) => (
                                                                <CommandItem
                                                                    key={status}
                                                                    value={status}
                                                                    onSelect={() => {
                                                                        form.setValue("status", status);
                                                                        setStatusDropdownOpen(false);
                                                                    }}
                                                                >
                                                                    {status}
                                                                    <Check className={cn(
                                                                        "ml-auto h-4 w-4",
                                                                        field.value === status ? "opacity-100" : "opacity-0"
                                                                    )}/>
                                                                </CommandItem>
                                                            ))}
                                                        </CommandGroup>
                                                    </Command>
                                                </PopoverContent>
                                            </Popover>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                {/* Pack Select with Custom Option */}
                                <FormField
                                    control={form.control}
                                    name="pack.pack"
                                    render={({ field }) => {
                                        const isCustomPack = field.value === "CUSTOM PACK";
                                        const availablePacks = [
                                            ...packs.map((p) => p.packName),
                                            ...(packs.some((p) => p.packName === "CUSTOM PACK")
                                                ? []
                                                : ["CUSTOM PACK"]),
                                        ];
                                        return (
                                            <FormItem className="flex flex-col w-full">
                                                <FormLabel>Pack</FormLabel>
                                                <Popover
                                                    open={packDropdownOpen}
                                                    onOpenChange={setPackDropdownOpen}
                                                >
                                                    <PopoverTrigger asChild>
                                                        <FormControl>
                                                            <Button
                                                                variant="outline"
                                                                role="combobox"
                                                                className={cn(
                                                                    "w-full h-10 justify-between",
                                                                    !field.value && "text-muted-foreground"
                                                                )}
                                                            >
                                                                {field.value || "Select Pack"}
                                                                <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                                                            </Button>
                                                        </FormControl>
                                                    </PopoverTrigger>
                                                    <PopoverContent
                                                        className="w-full p-0"
                                                        style={{
                                                            width: "var(--radix-popover-trigger-width)",
                                                        }}
                                                        align="start"
                                                    >
                                                        <Command>
                                                            <CommandInput placeholder="Search packs..." />
                                                            <CommandEmpty>No packs found.</CommandEmpty>
                                                            <CommandGroup className="max-h-[300px] overflow-y-auto">
                                                                {availablePacks.map((p) => {
                                                                    const pack = packs.find(pack => pack.packName === p);
                                                                    const packPrice = pack?.price ?? pack?.amount ?? 0;
                                                                    return (
                                                                        <CommandItem
                                                                            key={p}
                                                                            value={p}
                                                                            onSelect={() => {
                                                                                form.setValue("pack.pack", p);
                                                                                if (p !== "CUSTOM PACK") {
                                                                                    setSelectedServices([]);
                                                                                }
                                                                                setPackDropdownOpen(false);
                                                                            }}
                                                                        >
                                                                            {p} {p !== "CUSTOM PACK" && `- €${packPrice}`}
                                                                            <Check
                                                                                className={cn(
                                                                                    "ml-auto h-4 w-4",
                                                                                    field.value === p
                                                                                        ? "opacity-100"
                                                                                        : "opacity-0"
                                                                                )}
                                                                            />
                                                                        </CommandItem>
                                                                    );
                                                                })}
                                                            </CommandGroup>
                                                        </Command>
                                                    </PopoverContent>
                                                </Popover>

                                                {/* Show pack description and price below the dropdown */}
                                                {selectedPack && selectedPack !== "CUSTOM PACK" && selectedPackObj && (
                                                    <div className="mt-2 p-3 bg-muted rounded-md">
                                                        <div className="flex justify-between items-start">
                                                            <div>
                                                                <p className="font-medium text-sm">{selectedPackObj.packName}</p>
                                                                {selectedPackObj.description && (
                                                                    <p className="text-xs text-muted-foreground mt-1">
                                                                        {selectedPackObj.description}
                                                                    </p>
                                                                )}
                                                            </div>
                                                            <p className="font-bold text-sm">
                                                                €{(selectedPackObj.price ?? selectedPackObj.amount ?? 0).toFixed(2)}
                                                            </p>
                                                        </div>
                                                    </div>
                                                )}

                                                {isCustomPack && (
                                                    <div className="mt-4 w-full">
                                                        <FormLabel>Services</FormLabel>
                                                        <MultipleSelector
                                                            options={services.map((s) => ({
                                                                label: `${s.serviceName} (€${s.price})`,
                                                                value: s.id || "",
                                                            }))}
                                                            value={selectedServices.map(s => ({
                                                                label: `${s.serviceName} (€${s.price})`,
                                                                value: s.id || "",
                                                            }))}
                                                            onChange={(newValue) => {
                                                                const selectedServiceObjects = newValue.map(item =>
                                                                    services.find(s => s.id === item.value)!
                                                                ).filter(Boolean);
                                                                setSelectedServices(selectedServiceObjects);
                                                            }}
                                                            placeholder="Select services..."
                                                            emptyIndicator={
                                                                <p className="text-center text-lg leading-10 text-gray-600 dark:text-gray-400">
                                                                    no results found.
                                                                </p>
                                                            }
                                                            className="w-full mt-2"
                                                        />
                                                    </div>
                                                )}
                                                <FormMessage />
                                            </FormItem>
                                        );
                                    }}
                                />

                                {/* Amount */}
                                <FormField
                                    control={form.control}
                                    name="amount"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Total Price</FormLabel>
                                            <FormControl>
                                                <div className="relative">
                                                    <Input
                                                        type="number"
                                                        {...field}
                                                        value={amount}
                                                        readOnly
                                                        className="bg-muted pr-8 cursor-not-allowed font-mono"
                                                        placeholder="0.00"
                                                    />
                                                    <span className="absolute right-3 top-1/2 transform -translate-y-1/2 text-muted-foreground">
                                                        €
                                                    </span>
                                                </div>
                                            </FormControl>
                                            <FormDescription>
                                                {selectedPack === "CUSTOM PACK"
                                                    ? selectedServices.length > 0
                                                        ? `Calculated from ${selectedServices.length} service(s) - €${amount.toFixed(2)}`
                                                        : "Select services to calculate price"
                                                    : selectedPack
                                                        ? `Pack price - €${amount.toFixed(2)}`
                                                        : "Select a pack to see price"}
                                            </FormDescription>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                {/* Root Error Display */}
                                {form.formState.errors.root && (
                                    <div className="text-sm font-medium text-destructive">
                                        {form.formState.errors.root.message}
                                    </div>
                                )}

                                {/* Buttons */}
                                <div className="flex flex-col gap-2 pt-4">
                                    <Dialog.Close asChild>
                                        <Button type="button" variant="outline" className="w-full">
                                            Cancel
                                        </Button>
                                    </Dialog.Close>
                                    <Button
                                        type="submit"
                                        className="w-full"
                                        disabled={form.formState.isSubmitting || isSubmitting}
                                    >
                                        {form.formState.isSubmitting || isSubmitting
                                            ? "Saving..."
                                            : initialData
                                                ? "Update Visit"
                                                : "Create Visit"}
                                    </Button>
                                </div>
                            </form>
                        </Form>
                    </div>
                </Dialog.Content>
            </Dialog.Portal>
        </Dialog.Root>
    );
}