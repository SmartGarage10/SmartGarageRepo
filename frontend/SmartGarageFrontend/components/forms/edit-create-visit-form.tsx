"use client";

import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import MultipleSelector, { Option } from '@/components/ui/multiple-selector';
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";

import { X, ChevronsUpDown, Check } from "lucide-react";
import * as Dialog from "@radix-ui/react-dialog";
import { cn } from "@/lib/utils";
import {
    Command,
    CommandEmpty,
    CommandGroup,
    CommandInput,
    CommandItem,
} from "@/components/ui/command";
import {
    Popover,
    PopoverContent,
    PopoverTrigger,
} from "@/components/ui/popover";

import { User } from "@/types/user";
import { Vehicle } from "@/types/vehicle";

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

const OPTIONS: Option[] = [
    { label: 'nextjs', value: 'nextjs' },
    { label: 'React', value: 'react' },
    { label: 'Remix', value: 'remix' },
    { label: 'Vite', value: 'vite' },
    { label: 'Nuxt', value: 'nuxt' },
    { label: 'Vue', value: 'vue' },
    { label: 'Svelte', value: 'svelte' },
    { label: 'Angular', value: 'angular' },
    { label: 'Ember', value: 'ember', disable: true },
    { label: 'Gatsby', value: 'gatsby', disable: true },
    { label: 'Astro', value: 'astro' },
];

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
            vehicle: { id: "", vehiclePlate: "", vin: "", client: { id: "", name: "", email: "" }, brand: "", model: "", year: 0 },
            employee: { id: "", name: "", email: "" },
            visitDate: undefined,
            amount: 0,
            currency: "",
            pack: { pack: ""}
        },
    });

    const [clients, setClients] = useState<User[]>([]);
    const [employees, setEmployees] = useState<User[]>([]);
    const [vehicles, setVehicles] = useState<Vehicle[]>([]);

    const [userDropdownOpen, setUserDropdownOpen] = useState(false);
    const [employeeDropdownOpen, setEmployeeDropdownOpen] = useState(false);
    const [vehicleDropdownOpen, setVehicleDropdownOpen] = useState(false);
    const [packDropdownOpen, setPackDropdownOpen] = useState(false);
    const [statusDropdownOpen, setStatusDropdownOpen] = useState(false);

    // Update packs !!!
    const [packs] = useState<string[]>([
        "CUSTOM", "BASIC", "STANDARD", "PREMIUM"
    ]);

    const [statuses] = useState<string[]>([
        "SCHEDULED",
        "IN_PROGRESS",
        "COMPLETED",
        "CANCELLED",
    ]);

    const [services, setServices] = useState<string[]>([]);

    useEffect(() => {
        const handleWheel = (e: WheelEvent) => {
            const target = e.target as HTMLElement;
            if (target.closest("[cmdk-list]")) e.stopPropagation();
        };
        window.addEventListener("wheel", handleWheel, { passive: false });
        return () => window.removeEventListener("wheel", handleWheel);
    }, []);

    useEffect(() => {
        if (open) {
            // Fetch users and split into clients + employees
            fetch("http://localhost:8080/api/users", {
                credentials: "include",
            })
                .then((res) => res.json())
                .then((data) => {
                    const list = Array.isArray(data) ? data : data.users || [];
                    console.log("Fetched users:", list);

                    setClients(list.filter((u: User) => u.role === "ADMIN" || u.role === "EMPLOYEE"));
                    setEmployees(list.filter((u: User) => u.role === "ADMIN" || u.role === "EMPLOYEE"));
                })
                .catch((err) => console.error("Failed to fetch users", err));

            // Fetch vehicles
            fetch("http://localhost:8080/api/vehicles", {
                credentials: "include",
            })
                .then((res) => res.json())
                .then((data) => {
                    const vehicleList = Array.isArray(data) ? data : data.vehicles || [];
                    console.log("Fetched vehicles:", vehicleList);
                    setVehicles(vehicleList);
                })
                .catch((err) => console.error("Failed to fetch vehicles", err));

            // Handle initial data
            if (initialData) {
                form.reset({
                    ...initialData,
                    client: initialData.client || { id: "", name: "", email: "" },
                    employee: initialData.employee || { id: "", name: "", email: "" },
                });
            } else {
                form.reset({
                    client: { id: "", name: "", email: "" },
                    vehicle: { id: "", vehiclePlate: "", vin: "", client: { id: "", name: "", email: "" }, brand: "", model: "", year: 0 },
                    employee: { id: "", name: "", email: "" },
                    visitDate: undefined,
                    amount: 0,
                    currency: "",
                    pack: { pack: ""}
                });
            }
        }
    }, [open, initialData, form]);

    const handleSubmit = async (data: VisitFormValues) => {
        try {
            const isUpdate = !!initialData?.id;
            const endpoint = isUpdate
                ? `http://localhost:8080/api/update-visit/${initialData?.id}`
                : "http://localhost:8080/api/create-visit";
            const method = isUpdate ? "PUT" : "POST";

            const payload = {
                user: {
                    id: data.client.id,
                    name: data.client.name,
                    email: data.client.email,
                },
                vehicle: {
                    id: data.vehicle.id,
                    vehiclePlate: data.vehicle.vehiclePlate,
                    vin: data.vehicle.vin,
                    brand: data.vehicle.brand,
                    model: data.vehicle.model,
                    year: data.vehicle.year,
                },
                employee: {
                    id: data.employee.id,
                    name: data.employee.name,
                    email: data.employee.email,
                },
                visitDate: data.visitDate.toISOString(),
                amount: data.amount,
                currency: data.currency,
                pack: data.pack,
                status: data.status || "SCHEDULED",
            };

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
                form.reset({
                    client: { id: "", name: "", email: "" },
                    vehicle: { id: "", vehiclePlate: "", vin: "", client: { id: "", name: "", email: "" }, brand: "", model: "", year: 0 },
                    employee: { id: "", name: "", email: "" },
                    visitDate: undefined,
                    amount: 0,
                    currency: "",
                    pack: { pack: ""}
                });
            }

            onOpenChange?.(false);
            onSuccess?.();
            router.refresh();
        } catch (error) {
            console.error("Operation error:", error);
            form.setError("root", {
                message:
                    error instanceof Error ? error.message : "An error occurred",
            });
        }
    };

    const formatVehicleLabel = (vehicle: Vehicle) => {
        return `${vehicle.vehiclePlate} - ${vehicle.brand} ${vehicle.model} (${vehicle.year})`;
    };

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
                            <form
                                onSubmit={form.handleSubmit(handleSubmit)}
                                className="space-y-4"
                            >
                                {form.formState.errors.root && (
                                    <div className="text-destructive text-sm p-2 rounded bg-destructive/10">
                                        {form.formState.errors.root.message}
                                    </div>
                                )}

                                {/* User Select */}
                                <FormField
                                    control={form.control}
                                    name="client"
                                    render={({ field }) => (
                                        <FormItem className="flex flex-col">
                                            <FormLabel>User</FormLabel>
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
                                                            {field.value?.name || "Select user"}
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
                                                        <CommandInput placeholder="Search users..." />
                                                        <CommandEmpty>No users found.</CommandEmpty>
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

                                {/* Vehicle Select */}
                                <FormField
                                    control={form.control}
                                    name="vehicle"
                                    render={({ field }) => {
                                        const clientSelected = !!form.watch("client.id");
                                        return (
                                            <FormItem className="flex flex-col">
                                                <FormLabel>Vehicle</FormLabel>
                                                <Popover
                                                    open={vehicleDropdownOpen}
                                                    onOpenChange={(open) => {
                                                        if (!clientSelected && open) return; // Prevent opening if no client
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
                                                                        : "Select vehicle"
                                                                }
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
                                                                {/* "Use client" option */}
                                                                <CommandItem
                                                                    value="use-client"
                                                                    onSelect={() => {
                                                                        const client = form.getValues("client");
                                                                        if (client?.id) {
                                                                            // Create a new empty vehicle object with client info
                                                                            const newVehicle: Vehicle = {
                                                                                id: "",
                                                                                vehiclePlate: "",
                                                                                vin: "",
                                                                                client: client,
                                                                                brand: "",
                                                                                model: "",
                                                                                year: 0
                                                                            };
                                                                            form.setValue("vehicle", newVehicle);
                                                                        }
                                                                        setVehicleDropdownOpen(false);
                                                                    }}
                                                                >
                                                                    <div className="flex items-center gap-2">
                                                                        <span className="text-muted-foreground">+</span>
                                                                        Use client's vehicle (enter details manually)
                                                                    </div>
                                                                </CommandItem>

                                                                {/* Existing vehicles */}
                                                                {vehicles.map((vehicle) => (
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

                                {/* Employee select */}
                                <FormField
                                    control={form.control}
                                    name="employee"
                                    render={({ field }) => (
                                        <FormItem className="flex flex-col">
                                            <FormLabel>Employee</FormLabel>
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

                                {/* Status */}
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
                                                            {field.value || "Select Status"}
                                                            <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                                                        </Button>
                                                    </FormControl>
                                                </PopoverTrigger>
                                                <PopoverContent className="w-full p-0" style={{ width: "var(--radix-popover-trigger-width)" }} align="start">
                                                    <Command>
                                                        <CommandEmpty>No status found.</CommandEmpty>
                                                        <CommandGroup className="max-h-[300px] overflow-y-auto">
                                                            {statuses.map((s) => (
                                                                <CommandItem
                                                                    key={s}
                                                                    value={s}
                                                                    onSelect={() => {
                                                                        form.setValue("status", s);
                                                                        setStatusDropdownOpen(false);
                                                                    }}
                                                                >
                                                                    {s}
                                                                    <Check className={cn(
                                                                        "ml-auto h-4 w-4",
                                                                        field.value === s ? "opacity-100" : "opacity-0"
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

                                {/* Update the pack field to include services handling */}
                                <FormField
                                    control={form.control}
                                    name="pack.pack"
                                    render={({ field }) => {
                                        const isCustomPack = field.value === "CUSTOM";

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
                                                        style={{ width: "var(--radix-popover-trigger-width)" }}
                                                        align="start"
                                                    >
                                                        <Command>
                                                            <CommandEmpty>No packs found.</CommandEmpty>
                                                            <CommandGroup className="max-h-[300px] overflow-y-auto">
                                                                {packs.map((p) => (
                                                                    <CommandItem
                                                                        key={p}
                                                                        value={p}
                                                                        onSelect={() => {
                                                                            form.setValue("pack.pack", p);
                                                                            // Clear services if not custom
                                                                            if (p !== "CUSTOM") {
                                                                                setServices([]);
                                                                            }
                                                                            setPackDropdownOpen(false);
                                                                        }}
                                                                    >
                                                                        {p}
                                                                        <Check
                                                                            className={cn(
                                                                                "ml-auto h-4 w-4",
                                                                                field.value === p ? "opacity-100" : "opacity-0"
                                                                            )}
                                                                        />
                                                                    </CommandItem>
                                                                ))}
                                                            </CommandGroup>
                                                        </Command>
                                                    </PopoverContent>
                                                </Popover>

                                                <FormMessage />

                                                {/* Services field for custom pack */}
                                                {isCustomPack && (
                                                    <div className="mt-4 w-full">
                                                        <FormLabel>Services</FormLabel>
                                                        <MultipleSelector
                                                            defaultOptions={OPTIONS}
                                                            value={services}
                                                            onChange={setServices}
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
                                            </FormItem>
                                        );
                                    }}
                                />


                                {/* Visit Date */}
                                <FormField
                                    control={form.control}
                                    name="visitDate"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Visit Date</FormLabel>
                                            <FormControl>
                                                <Input
                                                    type="datetime-local"
                                                    value={
                                                        field.value
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

                                {/* Amount */}
                                <FormField
                                    control={form.control}
                                    name="amount"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Amount (€)</FormLabel>
                                            <FormControl>
                                                <div className="relative">
                                                    <span className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground">€</span>
                                                    <Input
                                                        type="number"
                                                        {...field}
                                                        className="pl-8"
                                                        placeholder="Enter amount"
                                                        disabled   // ✅ user can’t change manually
                                                        readOnly
                                                    />
                                                </div>
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                {/* Buttons */}
                                <div className="flex flex-col gap-2 pt-4">
                                    <Dialog.Close asChild>
                                        <Button variant="outline" className="w-full">
                                            Cancel
                                        </Button>
                                    </Dialog.Close>
                                    <Button
                                        type="submit"
                                        className="w-full"
                                        disabled={
                                            form.formState.isSubmitting ||
                                            isSubmitting
                                        }
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