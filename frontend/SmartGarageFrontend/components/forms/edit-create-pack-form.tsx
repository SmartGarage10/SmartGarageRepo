"use client";

import React, {useEffect, useState} from "react";
import { useForm } from "react-hook-form";
import { useRouter } from "next/navigation";
import { X } from "lucide-react";
import * as Dialog from "@radix-ui/react-dialog";

import { Button } from "@/components/ui/button";
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
import { cn } from "@/lib/utils";

import { Pack } from "@/types/pack";
import { Service } from "@/types/service";

import MultipleSelector, { Option } from "@/components/ui/multiple-selector";
import {createApi} from "@/api/genericApi";

import {Textarea} from "@/components/ui/textarea";

interface PackFormProps {
    initialData?: Pack | null;
    children?: React.ReactNode;
    open?: boolean;
    onOpenChange?: (open: boolean) => void;
    onSuccess?: () => void;
    isSubmitting?: boolean;
}

export function PackForm({
                             initialData,
                             children,
                             open,
                             onOpenChange,
                             onSuccess,
                             isSubmitting = false,
                         }: PackFormProps) {
    const router = useRouter();

    const serviceApi = createApi<Service>("services");
    const [services, setServices] = useState<Service[]>([]);
    const [serviceOptions, setServiceOptions] = useState<Option[]>([]);

    const form = useForm<Pack>({
        defaultValues: {
            id: "",
            packName: "",
            description: "",
            amount: 0,
            services: [],
        },
    });

    // Load services when component mounts
    useEffect(() => {
        async function loadServices() {
            try {
                const fetchedServices = await serviceApi.getAll();
                setServices(fetchedServices);

                // Convert services to options for MultipleSelector
                const options = fetchedServices.map((service: Service) => ({
                    label: service.serviceName || service.serviceName || "Unnamed Service",
                    value: service.id, // Use actual service ID from database
                }));

                // DEBUG: Check the options we're creating
                console.log("=== MULTISELECT OPTIONS ===");
                options.forEach((option, index) => {
                    console.log(`Option[${index}]:`, {
                        label: option.label,
                        value: option.value,
                        valueType: typeof option.value
                    });
                });
                console.log("=== END OPTIONS DEBUG ===");

                setServiceOptions(options);
            } catch (error) {
                console.error("Failed to load services", error);
            }
        }

        loadServices();
    }, []);

    // Calculate total price whenever services change
    useEffect(() => {
        const selectedServices = form.watch("services") || [];
        const calculatedPrice = selectedServices.reduce((total, service) => {
            return total + (service.price || 0);
        }, 0);

        // Apply 10% discount to the calculated price
        const priceAfterDiscount = calculatedPrice * 0.9;

        // Apply .99 ending and step of 5 logic to the discounted price
        const finalPrice = priceAfterDiscount > 0 ? Math.round(priceAfterDiscount / 5) * 5 - 0.01 : 0;

        // Update form value
        form.setValue("amount", finalPrice);
    }, [form.watch("services")]);

    // Reset form when dialog opens/closes or initialData changes
    useEffect(() => {
        if (open && initialData) {
            form.reset(initialData);
        } else if (open && !initialData) {
            form.reset({
                id: "",
                packName: "",
                description: "",
                amount: 0,
                services: [],
            });
        }
    }, [open, initialData, form]);

    const handleSubmit = async (data: Pack) => {
        try {
            const isUpdate = !!initialData?.id;
            const endpoint = isUpdate
                ? `http://localhost:8080/api/update-pack/${initialData.id}`
                : "http://localhost:8080/api/create-pack";
            const method = isUpdate ? "PUT" : "POST";

            // FIX: Add debug logging to see what we're sending
            console.log("Submitting pack data:", data);
            console.log("Services being sent:", data.services);

            const response = await fetch(endpoint, {
                method,
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data),
                credentials: "include",
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || "Request failed");
            }

            form.reset({
                id: "",
                packName: "",
                description: "",
                amount: 0,
                services: [],
            });
            onOpenChange?.(false);
            onSuccess?.();
            router.refresh();
        } catch (error) {
            console.error("Pack operation error:", error);
            form.setError("root", {
                message: error instanceof Error ? error.message : "An error occurred",
            });
        }
    };

    // FIX: Handle service selection change - use actual service objects
    const handleServicesChange = (selectedOptions: Option[]) => {
        // DEBUG: Log what we're working with
        console.log("Selected MultipleSelector options:", selectedOptions);
        console.log("Available services from API:", services);

        // Convert selected options back to service objects
        const selectedServices = selectedOptions.map(option => {
            // FIX: Find the actual service object from our loaded services
            // This ensures we use the real service with proper ID from database
            const service = services.find(s => s.id === option.value);

            if (!service) {
                console.error("Service not found for option:", option);
                // FIX: Throw error instead of creating fake service object
                throw new Error(`Service '${option.label}' not found in database`);
            }

            console.log("Found service:", service);
            return service; // Return the actual service with real database ID
        });

        console.log("Final selected services to submit:", selectedServices);

        // Update form value with actual service objects
        form.setValue("services", selectedServices);
    };

    // FIX: Get current selected services as options for display
    const getSelectedServiceOptions = (): Option[] => {
        const currentServices = form.watch("services") || [];

        // FIX: Use actual service data to create options
        return currentServices.map(service => ({
            label: service.serviceName || service.serviceName || "Unnamed Service",
            value: service.id, // Use the actual service ID from database
        }));
    };

    // Get current selected services count and totals for display
    const selectedServices = form.watch("services") || [];
    const rawTotal = selectedServices.reduce((total, service) => total + (service.price || 0), 0);
    const discountedTotal = rawTotal * 0.9;

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
                                    {initialData ? "Edit Pack" : "Add Pack"}
                                </Dialog.Title>
                                <Dialog.Description className="text-sm text-muted-foreground">
                                    {initialData
                                        ? "Update pack details."
                                        : "Fill in the details to add a new pack."}
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

                                {/* Pack Name */}
                                <FormField
                                    control={form.control}
                                    name="packName"
                                    rules={{ required: "Pack name is required" }}
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Pack Name</FormLabel>
                                            <FormControl>
                                                <Input {...field} placeholder="Pack name" />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                {/* Description */}
                                <FormField
                                    control={form.control}
                                    name="description"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Description</FormLabel>
                                            <FormControl>
                                                <Textarea
                                                    {...field}
                                                    placeholder="Pack description"
                                                />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                {/* Services */}
                                <FormItem>
                                    <FormLabel>Services</FormLabel>
                                    <FormControl>
                                        <MultipleSelector
                                            options={serviceOptions}
                                            value={getSelectedServiceOptions()}
                                            onChange={handleServicesChange}
                                            placeholder="Select services..."
                                            emptyIndicator={
                                                <p className="text-center text-lg leading-10 text-gray-600 dark:text-gray-400">
                                                    no results found.
                                                </p>
                                            }
                                            className="w-full mt-2"
                                        />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>

                                {/* Total Price - Auto-calculated */}
                                <FormField
                                    control={form.control}
                                    name="amount"
                                    rules={{
                                        required: "Total price is required",
                                        min: { value: 0, message: "Price must be positive" },
                                    }}
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Total Price</FormLabel>
                                            <FormControl>
                                                <div className="relative">
                                                    <Input
                                                        type="number"
                                                        {...field}
                                                        value={field.value || 0}
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
                                                {selectedServices.length > 0
                                                    ? `Calculated from ${selectedServices.length} service(s) - €${discountedTotal.toFixed(2)}`
                                                    : "Select services to calculate price"
                                                }
                                            </FormDescription>
                                            <FormMessage />
                                        </FormItem>
                                    )}
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
                                        disabled={form.formState.isSubmitting || isSubmitting}
                                    >
                                        {form.formState.isSubmitting || isSubmitting
                                            ? "Saving..."
                                            : initialData
                                                ? "Update Pack"
                                                : "Create Pack"}
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