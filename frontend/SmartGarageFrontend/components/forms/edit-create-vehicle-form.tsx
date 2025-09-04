"use client";

import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useRouter } from "next/navigation";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
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
import { CarService } from "@/services/CarService";

interface User {
    id: string;
    name: string;
    email: string;
    avatar?: string;
}

interface VehicleFormValues {
    id?: string;
    client: User;
    vehiclePlate: string;
    vin: string;
    brand: string;
    model: string;
    year: number;
}

interface VehicleFormProps {
    initialData?: {
        id?: string;
        client?: User | null;
        vehiclePlate: string;
        vin: string;
        brand: string;
        model: string;
        year: number;
    } | null;
    children?: React.ReactNode;
    open?: boolean;
    onOpenChange?: (open: boolean) => void;
    onSuccess?: () => void;
    isSubmitting?: boolean;
}

export function VehicleForm({
                                initialData,
                                children,
                                open,
                                onOpenChange,
                                onSuccess,
                                isSubmitting = false,
                            }: VehicleFormProps) {
    const router = useRouter();
    const currentYear = new Date().getFullYear();

    const form = useForm<VehicleFormValues>({
        defaultValues: {
            client: { id: "", name: "", email: "" },
            vehiclePlate: "",
            vin: "",
            brand: "",
            model: "",
            year: currentYear,
        },
    });

    const [clients, setClients] = useState<User[]>([]);
    const [userDropdownOpen, setUserDropdownOpen] = useState(false);
    const [brandDropdownOpen, setBrandDropdownOpen] = useState(false);
    const [modelDropdownOpen, setModelDropdownOpen] = useState(false);
    const [yearDropdownOpen, setYearDropdownOpen] = useState(false);
    const [brands, setBrands] = useState<string[]>([]);
    const [models, setModels] = useState<string[]>([]);
    const [isLoadingInitialData, setIsLoadingInitialData] = useState(true);

    const years = Array.from({ length: 30 }, (_, i) => currentYear - i);

    // Enable mouse wheel scrolling for dropdowns
    useEffect(() => {
        const handleWheel = (e: WheelEvent) => {
            const target = e.target as HTMLElement;
            if (target.closest('[cmdk-list]')) {
                e.stopPropagation();
            }
        };

        window.addEventListener('wheel', handleWheel, { passive: false });
        return () => window.removeEventListener('wheel', handleWheel);
    }, []);

    // Load brands when component mounts
    useEffect(() => {
        async function loadBrands() {
            try {
                const fetchedBrands = await CarService.fetchBrands();
                setBrands(fetchedBrands);
                setIsLoadingInitialData(false);
            } catch (error) {
                console.error("Failed to load brands", error);
                setIsLoadingInitialData(false);
            }
        }

        loadBrands();
    }, []);

    // Load models when brand changes
    useEffect(() => {
        async function loadModels() {
            const selectedBrand = form.watch("brand");
            if (selectedBrand) {
                try {
                    const fetchedModels = await CarService.fetchModels(selectedBrand);
                    setModels(fetchedModels);
                } catch (error) {
                    console.error("Failed to load models", error);
                    setModels([]);
                }
            } else {
                setModels([]);
            }
        }

        loadModels();
    }, [form.watch("brand")]);

    useEffect(() => {
        if (open) {
            // Fetch users
            fetch("http://localhost:8080/api/users", {
                credentials: "include",
            })
                .then((res) => res.json())
                .then((data) => setClients(data))
                .catch((err) => console.error("Failed to fetch users", err));

            // Set initial data if provided
            if (initialData) {
                form.reset({
                    ...initialData,
                    client: initialData.client || { id: "", name: "", email: "" },
                    year: initialData.year || currentYear
                });
                // If initial data has a brand, load its models
                if (initialData.brand) {
                    CarService.fetchModels(initialData.brand)
                        .then(models => setModels(models))
                        .catch(err => console.error("Failed to load initial models", err));
                }
            } else {
                form.reset({
                    client: { id: "", name: "", email: "" },
                    vehiclePlate: "",
                    vin: "",
                    brand: "",
                    model: "",
                    year: currentYear
                });
            }
        }
    }, [open, initialData, form, currentYear]);

    const handleSubmit = async (data: VehicleFormValues) => {
        try {
            if (!data.client?.id) {
                form.setError("client", { message: "User is required", type: "manual" });
                return;
            }
            if (!data.vehiclePlate) {
                form.setError("vehiclePlate", { message: "License plate is required" });
                return;
            }
            if (!data.vin) {
                form.setError("vin", { message: "VIN is required" });
                return;
            }
            if (!data.brand) {
                form.setError("brand", { message: "Brand is required" });
                return;
            }
            if (!data.model) {
                form.setError("model", { message: "Model is required" });
                return;
            }
            if (!data.year || data.year < 1900 || data.year > currentYear) {
                form.setError("year", { message: "Valid year is required" });
                return;
            }

            const isUpdate = !!initialData?.id;
            const endpoint = isUpdate
                ? `http://localhost:8080/api/update-vehicle/${initialData?.id}`
                : "http://localhost:8080/api/create-vehicle";
            const method = isUpdate ? "PUT" : "POST";

            const payload = {
                vehiclePlate: data.vehiclePlate,
                vin: data.vin,
                brand: data.brand,
                model: data.model,
                year: data.year,
                user: {
                    id: data.client.id,
                    name: data.client.name,
                    email: data.client.email,
                    avatar: data.client.avatar
                }
            };

            const response = await fetch(endpoint, {
                method,
                headers: {
                    "Content-Type": "application/json",
                },
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
                    vehiclePlate: "",
                    vin: "",
                    brand: "",
                    model: "",
                    year: currentYear,
                });
            }
            onOpenChange?.(false);
            onSuccess?.();
            router.refresh();
        } catch (error) {
            console.error("Operation error:", error);
            form.setError("root", {
                message: error instanceof Error ? error.message : "An error occurred",
            });
        }
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
                                    {initialData ? "Edit Vehicle" : "Add Vehicle"}
                                </Dialog.Title>
                                <Dialog.Description className="text-sm text-muted-foreground">
                                    {initialData
                                        ? "Update vehicle details."
                                        : "Fill in the details to add a vehicle."}
                                </Dialog.Description>
                            </div>
                            <Dialog.Close className="opacity-70 hover:opacity-100">
                                <X className="h-5 w-5" />
                            </Dialog.Close>
                        </div>

                        <Form {...form}>
                            <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
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

                                {/* Vehicle Plate */}
                                <FormField
                                    control={form.control}
                                    name="vehiclePlate"
                                    rules={{ required: "License plate is required" }}
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>License Plate</FormLabel>
                                            <FormControl>
                                                <Input {...field} />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                {/* VIN */}
                                <FormField
                                    control={form.control}
                                    name="vin"
                                    rules={{ required: "VIN is required" }}
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>VIN</FormLabel>
                                            <FormControl>
                                                <Input {...field} />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                {/* Brand Select */}
                                <FormField
                                    control={form.control}
                                    name="brand"
                                    render={({ field }) => (
                                        <FormItem className="flex flex-col">
                                            <FormLabel>Brand</FormLabel>
                                            <Popover open={brandDropdownOpen} onOpenChange={setBrandDropdownOpen}>
                                                <PopoverTrigger asChild>
                                                    <FormControl>
                                                        <Button
                                                            variant="outline"
                                                            role="combobox"
                                                            className={cn(
                                                                "w-full h-10 justify-between",
                                                                !field.value && "text-muted-foreground"
                                                            )}
                                                            disabled={isLoadingInitialData}
                                                        >
                                                            {isLoadingInitialData ? "Loading data..." : field.value || "Select brand"}
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
                                                        <CommandInput placeholder="Search brands..." />
                                                        <CommandEmpty>No brands found.</CommandEmpty>
                                                        <CommandGroup className="max-h-[300px] overflow-y-auto">
                                                            {brands.map((brand) => (
                                                                <CommandItem
                                                                    key={brand}
                                                                    value={brand}
                                                                    onSelect={() => {
                                                                        form.setValue("brand", brand);
                                                                        form.setValue("model", "");
                                                                        setBrandDropdownOpen(false);
                                                                    }}
                                                                >
                                                                    {brand}
                                                                    <Check
                                                                        className={cn(
                                                                            "ml-auto h-4 w-4",
                                                                            field.value === brand
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

                                {/* Model Select */}
                                <FormField
                                    control={form.control}
                                    name="model"
                                    render={({ field }) => {
                                        const brandSelected = !!form.watch("brand");
                                        return (
                                            <FormItem className="flex flex-col">
                                                <FormLabel>Model</FormLabel>
                                                <Popover
                                                    open={modelDropdownOpen}
                                                    onOpenChange={setModelDropdownOpen}
                                                    disabled={!brandSelected || isLoadingInitialData}
                                                >
                                                    <PopoverTrigger asChild>
                                                        <FormControl>
                                                            <Button
                                                                variant="outline"
                                                                role="combobox"
                                                                className={cn(
                                                                    "w-full h-10 justify-between",
                                                                    !field.value && "text-muted-foreground",
                                                                    (!brandSelected || isLoadingInitialData) && "opacity-50 cursor-not-allowed"
                                                                )}
                                                                disabled={!brandSelected || isLoadingInitialData}
                                                            >
                                                                {isLoadingInitialData ? "Loading data..." : field.value || "Select model"}
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
                                                            <CommandInput placeholder="Search models..." />
                                                            <CommandEmpty>No models found.</CommandEmpty>
                                                            <CommandGroup className="max-h-[300px] overflow-y-auto">
                                                                {models.map((model) => (
                                                                    <CommandItem
                                                                        key={model}
                                                                        value={model}
                                                                        onSelect={() => {
                                                                            form.setValue("model", model);
                                                                            setModelDropdownOpen(false);
                                                                        }}
                                                                    >
                                                                        {model}
                                                                        <Check
                                                                            className={cn(
                                                                                "ml-auto h-4 w-4",
                                                                                field.value === model
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

                                {/* Year Select */}
                                <FormField
                                    control={form.control}
                                    name="year"
                                    render={({ field }) => {
                                        const modelSelected = !!form.watch("model");
                                        return (
                                            <FormItem className="flex flex-col">
                                                <FormLabel>Year</FormLabel>
                                                <Popover
                                                    open={yearDropdownOpen}
                                                    onOpenChange={setYearDropdownOpen}
                                                    disabled={!modelSelected}
                                                >
                                                    <PopoverTrigger asChild>
                                                        <FormControl>
                                                            <Button
                                                                variant="outline"
                                                                role="combobox"
                                                                className={cn(
                                                                    "w-full h-10 justify-between",
                                                                    !field.value && "text-muted-foreground",
                                                                    !modelSelected && "opacity-50 cursor-not-allowed"
                                                                )}
                                                                disabled={!modelSelected}
                                                            >
                                                                {field.value || "Select year"}
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
                                                            <CommandInput placeholder="Search years..." />
                                                            <CommandEmpty>No years found.</CommandEmpty>
                                                            <CommandGroup className="max-h-[300px] overflow-y-auto">
                                                                {years.map((year) => (
                                                                    <CommandItem
                                                                        key={year}
                                                                        value={year.toString()}
                                                                        onSelect={() => {
                                                                            form.setValue("year", year);
                                                                            setYearDropdownOpen(false);
                                                                        }}
                                                                    >
                                                                        {year}
                                                                        <Check
                                                                            className={cn(
                                                                                "ml-auto h-4 w-4",
                                                                                field.value === year
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
                                                {!modelSelected && (
                                                    <p className="text-xs text-muted-foreground mt-1">
                                                        Please select a model first
                                                    </p>
                                                )}
                                                <FormMessage />
                                            </FormItem>
                                        );
                                    }}
                                />

                                <div className="flex flex-col gap-2 pt-4">
                                    <Dialog.Close asChild>
                                        <Button variant="outline" className="w-full">
                                            Cancel
                                        </Button>
                                    </Dialog.Close>
                                    <Button
                                        type="submit"
                                        className="w-full"
                                        disabled={form.formState.isSubmitting || isSubmitting || isLoadingInitialData}
                                    >
                                        {form.formState.isSubmitting || isSubmitting
                                            ? "Saving..."
                                            : initialData
                                                ? "Update Vehicle"
                                                : "Create Vehicle"}
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