"use client";

import React, { useEffect, useState } from "react";
import { useForm, useWatch } from "react-hook-form";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import MultipleSelector from "@/components/ui/multiple-selector";
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
    FormDescription
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
    CommandItem
} from "@/components/ui/command";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";

import { Visit, Status } from "@/types/visit";
import { User } from "@/types/user";
import { Vehicle } from "@/types/vehicle";
import { Pack } from "@/types/pack";
import { Service } from "@/types/service";

interface VisitFormProps {
    initialData?: Visit | null;
    children?: React.ReactNode;
    open?: boolean;
    onOpenChange?: (open: boolean) => void;
    onSuccess?: () => void;
    onSubmit?: (data: Visit) => Promise<boolean>;
    isSubmitting?: boolean;
    clients?: User[];
    employees?: User[];
    vehicles?: Vehicle[];
    packs?: Pack[];
    services?: Service[];
    isLoading?: boolean;
    statusOptions?: string[];
}

export function VisitForm({
                              initialData,
                              children,
                              open,
                              onOpenChange,
                              onSuccess,
                              onSubmit,
                              isSubmitting = false,
                              clients = [],
                              employees = [],
                              vehicles = [],
                              packs = [],
                              services = [],
                              isLoading = false,
                              statusOptions = Object.values(Status)
                          }: VisitFormProps) {
    const router = useRouter();

    const form = useForm<Visit>({
        defaultValues: {
            id: "",
            client: { id: "", name: "", email: "" },
            vehicle: {
                id: "",
                vehiclePlate: "",
                vin: "",
                client: { id: "", name: "", email: "" },
                brand: "",
                model: "",
                year: ""
            },
            employee: { id: "", name: "", email: "" },
            visitDate: "",
            status: statusOptions[0] || "SCHEDULED",
            amount: 0,
            pack: { packName: "" }
        }
    });

    const [selectedServices, setSelectedServices] = useState<Service[]>([]);
    const [userDropdownOpen, setUserDropdownOpen] = useState(false);
    const [employeeDropdownOpen, setEmployeeDropdownOpen] = useState(false);
    const [vehicleDropdownOpen, setVehicleDropdownOpen] = useState(false);
    const [packDropdownOpen, setPackDropdownOpen] = useState(false);
    const [statusDropdownOpen, setStatusDropdownOpen] = useState(false);

    useEffect(() => {
        if (open && initialData) {
            if (clients.length === 0 || vehicles.length === 0 || employees.length === 0) return;

            const vehicleClient = initialData.vehicle?.client;
            const matchingClient = clients.find(c => c.id === vehicleClient?.id);
            const finalClient = matchingClient || vehicleClient;

            const matchingVehicle = vehicles.find(v => v.id === initialData.vehicle?.id);
            const matchingEmployee = employees.find(e => e.id === initialData.employee?.id);

            const visitPack = initialData.visitItems?.find(item => item.pack)?.pack;
            const matchingPack = packs.find(p => p.id === visitPack?.id);

            const formData = {
                ...initialData,
                client: finalClient,
                vehicle: matchingVehicle || initialData.vehicle,
                employee: matchingEmployee || initialData.employee,
                pack: matchingPack || visitPack || { packName: "" }
            };

            const timer = setTimeout(() => {
                form.reset(formData);

                if (visitPack?.packName === "CUSTOM PACK" && initialData.visitServices) {
                    setSelectedServices(initialData.visitServices);
                } else {
                    setSelectedServices([]);
                }
            }, 50);

            return () => clearTimeout(timer);
        }

        if (!open) {
            form.reset({
                id: "",
                client: { id: "", name: "", email: "" },
                vehicle: {
                    id: "",
                    vehiclePlate: "",
                    vin: "",
                    client: { id: "", name: "", email: "" },
                    brand: "",
                    model: "",
                    year: ""
                },
                employee: { id: "", name: "", email: "" },
                visitDate: "",
                status: statusOptions[0] || "SCHEDULED",
                amount: 0,
                pack: { packName: "" }
            });
            setSelectedServices([]);
        }
    }, [initialData, open, clients, vehicles, employees, packs, statusOptions, form]);

    const [amount, setAmount] = useState<number>(0);

    const selectedPack = useWatch({
        control: form.control,
        name: "pack.packName"
    });

    useEffect(() => {
        let newAmount = 0;

        if (selectedPack && selectedPack !== "CUSTOM PACK") {
            const packObj = packs.find(p => p.packName === selectedPack);
            newAmount = packObj?.amount ?? 0;
        } else if (selectedPack === "CUSTOM PACK") {
            newAmount = selectedServices.reduce((sum, s) => sum + (s.price ?? 0), 0);
        }

        setAmount(newAmount);
        form.setValue("amount", newAmount, { shouldValidate: true, shouldDirty: true });
    }, [selectedPack, selectedServices, packs, form]);
    const handleSubmit = async (data: Visit) => {
        try {
            if (!data.visitDate) {
                form.setError("visitDate", { type: "manual", message: "Visit date is required" });
                return;
            }
            if (!data.client?.id) {
                form.setError("client", { type: "manual", message: "Client is required" });
                return;
            }
            if (!data.vehicle?.id) {
                form.setError("vehicle", { type: "manual", message: "Vehicle is required" });
                return;
            }
            if (!data.employee?.id) {
                form.setError("employee", { type: "manual", message: "Employee is required" });
                return;
            }

            const formDataWithServices = {
                ...data,
                visitServices: selectedPack === "CUSTOM PACK" ? selectedServices : []
            };

            if (onSubmit) {
                const success = await onSubmit(formDataWithServices);
                if (success) {
                    if (!initialData) {
                        form.reset();
                        setSelectedServices([]);
                    }
                    onOpenChange?.(false);
                    onSuccess?.();
                    router.refresh();
                }
            }
        } catch (error) {
            form.setError("root", {
                type: "manual",
                message: error instanceof Error ? error.message : "An error occurred"
            });
        }
    };

    const formatVehicleLabel = (vehicle: Vehicle) =>
        `${vehicle.vehiclePlate} - ${vehicle.brand} ${vehicle.model} (${vehicle.year})`;

    const selectedPackObj = packs.find(p => p.packName === selectedPack);

    if (isLoading) {
        return (
            <Dialog.Root open={open} onOpenChange={onOpenChange}>
                {children && <Dialog.Trigger asChild>{children}</Dialog.Trigger>}
                <Dialog.Portal>
                    <Dialog.Overlay className="fixed inset-0 z-50 bg-black/80 backdrop-blur-sm" />
                    <Dialog.Content className="fixed right-0 top-0 z-50 h-full w-full max-w-sm border-l bg-background shadow-lg">
                        <div className="h-full overflow-y-auto p-6 flex justify-center items-center">
                            <p>Loading form data...</p>
                        </div>
                    </Dialog.Content>
                </Dialog.Portal>
            </Dialog.Root>
        );
    }

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
                                    {initialData ? "Update visit details." : "Fill in the details to add a new visit."}
                                </Dialog.Description>
                            </div>
                            <Dialog.Close className="opacity-70 hover:opacity-100">
                                <X className="h-5 w-5" />
                            </Dialog.Close>
                        </div>

                        <Form {...form}>
                            <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">

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
                                                <PopoverContent className="w-full p-0" align="start">
                                                    <Command>
                                                        <CommandInput placeholder="Search clients..." />
                                                        <CommandEmpty>No clients found.</CommandEmpty>
                                                        <CommandGroup className="max-h-[300px] overflow-y-auto">
                                                            {clients.map(client => (
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

                                <FormField
                                    control={form.control}
                                    name="vehicle"
                                    rules={{ required: "Vehicle is required" }}
                                    render={({ field }) => {
                                        const clientId = form.watch("client.id");
                                        const filteredVehicles = vehicles.filter(v => v.client?.id === clientId);

                                        return (
                                            <FormItem className="flex flex-col">
                                                <FormLabel>Vehicle *</FormLabel>
                                                <Popover
                                                    open={vehicleDropdownOpen}
                                                    onOpenChange={open => {
                                                        if (!clientId && open) return;
                                                        setVehicleDropdownOpen(open);
                                                    }}
                                                >
                                                    <PopoverTrigger asChild>
                                                        <FormControl>
                                                            <Button
                                                                variant="outline"
                                                                role="combobox"
                                                                disabled={!clientId}
                                                                className={cn(
                                                                    "w-full min-h-10 justify-between",
                                                                    !field.value?.id && "text-muted-foreground",
                                                                    !clientId && "opacity-50 cursor-not-allowed"
                                                                )}
                                                            >
                                                                <span className="truncate flex-1 text-left">
                                                                    {!clientId
                                                                        ? "Select a client first"
                                                                        : field.value?.id
                                                                            ? formatVehicleLabel(field.value)
                                                                            : "Select vehicle"}
                                                                </span>
                                                                <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                                                            </Button>
                                                        </FormControl>
                                                    </PopoverTrigger>
                                                    <PopoverContent className="w-full p-0" align="start">
                                                        <Command>
                                                            <CommandInput placeholder="Search vehicles..." />
                                                            <CommandEmpty>No vehicles found.</CommandEmpty>
                                                            <CommandGroup className="max-h-[300px] overflow-y-auto">
                                                                {filteredVehicles.map(vehicle => (
                                                                    <CommandItem
                                                                        key={vehicle.id}
                                                                        value={vehicle.vehiclePlate}
                                                                        onSelect={() => {
                                                                            form.setValue("vehicle", vehicle);
                                                                            setVehicleDropdownOpen(false);
                                                                        }}
                                                                        className="flex items-center justify-between"
                                                                    >
                                                                        <span className="flex-1 min-w-0">
                                                                            <div className="font-medium truncate">
                                                                                {formatVehicleLabel(vehicle)}
                                                                            </div>
                                                                            {vehicle.vin && (
                                                                                <div className="text-xs text-muted-foreground truncate">
                                                                                    VIN: {vehicle.vin}
                                                                                </div>
                                                                            )}
                                                                        </span>
                                                                        <Check
                                                                            className={cn(
                                                                                "ml-2 h-4 w-4 shrink-0",
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
                                                <PopoverContent className="w-full p-0" align="start">
                                                    <Command>
                                                        <CommandInput placeholder="Search employees..." />
                                                        <CommandEmpty>No employees found.</CommandEmpty>
                                                        <CommandGroup className="max-h-[300px] overflow-y-auto">
                                                            {employees.map(employee => (
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
                                                    onChange={e =>
                                                        field.onChange(
                                                            e.target.value ? new Date(e.target.value) : undefined
                                                        )
                                                    }
                                                />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

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
                                                <PopoverContent className="w-full p-0" align="start">
                                                    <Command>
                                                        <CommandEmpty>No status found.</CommandEmpty>
                                                        <CommandGroup className="max-h-[300px] overflow-y-auto">
                                                            {statusOptions.map(status => (
                                                                <CommandItem
                                                                    key={status}
                                                                    value={status}
                                                                    onSelect={() => {
                                                                        form.setValue("status", status);
                                                                        setStatusDropdownOpen(false);
                                                                    }}
                                                                >
                                                                    {status}
                                                                    <Check
                                                                        className={cn(
                                                                            "ml-auto h-4 w-4",
                                                                            field.value === status
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

                                <FormField
                                    control={form.control}
                                    name="pack.packName"
                                    render={({ field }) => {
                                        const isCustomPack = field.value === "CUSTOM PACK";
                                        const availablePacks = [
                                            ...packs.map(p => p.packName),
                                            ...(packs.some(p => p.packName === "CUSTOM PACK")
                                                ? []
                                                : ["CUSTOM PACK"])
                                        ];
                                        return (
                                            <FormItem className="flex flex-col w-full">
                                                <FormLabel>Pack</FormLabel>
                                                <Popover open={packDropdownOpen} onOpenChange={setPackDropdownOpen}>
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
                                                        align="start"
                                                    >
                                                        <Command>
                                                            <CommandInput placeholder="Search packs..." />
                                                            <CommandEmpty>No packs found.</CommandEmpty>
                                                            <CommandGroup className="max-h-[300px] overflow-y-auto">
                                                                {availablePacks.map(p => {
                                                                    const pack = packs.find(x => x.packName === p);
                                                                    const packPrice = pack?.amount ?? 0;
                                                                    return (
                                                                        <CommandItem
                                                                            key={p}
                                                                            value={p}
                                                                            onSelect={() => {
                                                                                form.setValue("pack.packName", p);
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
                                                                €{(selectedPackObj.amount ?? 0).toFixed(2)}
                                                            </p>
                                                        </div>
                                                    </div>
                                                )}

                                                {isCustomPack && (
                                                    <div className="mt-4 w-full">
                                                        <FormLabel>Services</FormLabel>
                                                        <MultipleSelector
                                                            options={services.map(s => ({
                                                                label: `${s.serviceName} (€${s.price})`,
                                                                value: s.id || ""
                                                            }))}
                                                            value={selectedServices.map(s => ({
                                                                label: `${s.serviceName} (€${s.price})`,
                                                                value: s.id || ""
                                                            }))}
                                                            onChange={newValue => {
                                                                const selectedServiceObjects = newValue
                                                                    .map(item => services.find(s => s.id === item.value)!)
                                                                    .filter(Boolean);
                                                                setSelectedServices(selectedServiceObjects);
                                                            }}
                                                            placeholder="Select services..."
                                                            className="w-full mt-2"
                                                        />
                                                    </div>
                                                )}
                                                <FormMessage />
                                            </FormItem>
                                        );
                                    }}
                                />
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

                                {form.formState.errors.root && (
                                    <div className="text-sm font-medium text-destructive">
                                        {form.formState.errors.root.message}
                                    </div>
                                )}

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
