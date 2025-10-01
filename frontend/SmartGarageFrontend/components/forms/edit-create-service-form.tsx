"use client";

import React, { useEffect } from "react";
import { useForm } from "react-hook-form";
import { useRouter } from "next/navigation";
import { X } from "lucide-react";
import * as Dialog from "@radix-ui/react-dialog";

import CurrencyInput from 'react-currency-input-field';

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
import { cn } from "@/lib/utils";
import { Service } from "@/types/service";
import {Textarea} from "@/components/ui/textarea";

interface ServiceFormProps {
    initialData?: {
        serviceId: string;
        serviceName: string;
        serviceDescription: string;
        price: number;
    } | null;
    children?: React.ReactNode;
    open?: boolean;
    onOpenChange?: (open: boolean) => void;
    onSuccess?: () => void;
    isSubmitting?: boolean;
}

export function ServiceForm({
                                initialData,
                                children,
                                open,
                                onOpenChange,
                                onSuccess,
                                isSubmitting = false,
                            }: ServiceFormProps) {
    const router = useRouter();

    const form = useForm<Service>({
        defaultValues: {
            serviceId: "",
            serviceName: "",
            serviceDescription: "",
            price: 0,
        },
    });

    useEffect(() => {
        if (open && initialData) {
            form.reset(initialData);
        } else if (open && !initialData) {
            form.reset({
                serviceId: "",
                serviceName: "",
                serviceDescription: "",
                price: 0,
            });
        }
    }, [open, initialData, form]);

    const handleSubmit = async (data: Service) => {
        try {
            const isUpdate = !!initialData?.serviceId;
            const endpoint = isUpdate
                ? `http://localhost:8080/api/update-service/${initialData.serviceId}`
                : "http://localhost:8080/api/create-service";
            const method = isUpdate ? "PUT" : "POST";

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
                serviceId: "",
                serviceName: "",
                serviceDescription: "",
                price: 0,
            });
            onOpenChange?.(false);
            onSuccess?.();
            router.refresh();
        } catch (error) {
            console.error("Service operation error:", error);
            form.setError("root", {
                message:
                    error instanceof Error ? error.message : "An error occurred",
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
                                    {initialData ? "Edit Service" : "Add Service"}
                                </Dialog.Title>
                                <Dialog.Description className="text-sm text-muted-foreground">
                                    {initialData
                                        ? "Update service details."
                                        : "Fill in the details to add a new service."}
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

                                {/* Service Name */}
                                <FormField
                                    control={form.control}
                                    name="serviceName"
                                    rules={{ required: "Service name is required" }}
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Service Name</FormLabel>
                                            <FormControl>
                                                <Input {...field} placeholder="Service name" />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                {/* Service Description */}
                                <FormField
                                    control={form.control}
                                    name="serviceDescription"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Description</FormLabel>
                                            <FormControl>
                                                <Textarea placeholder="Type your message here." id="message" />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                {/* Price */}
                                <FormField
                                    control={form.control}
                                    name="totalPrice"
                                    rules={{
                                        required: "Total price is required",
                                        min: { value: 0, message: "Price must be positive" },
                                        validate: (value) => {
                                            const cents = Math.round((value - Math.floor(value)) * 100);
                                            return cents === 99 || "Price must end with .99";
                                        }
                                    }}
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Total Price</FormLabel>
                                            <FormControl>
                                                <CurrencyInput
                                                    className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                                                    placeholder="0,99 €"
                                                    decimalsLimit={2}
                                                    decimalScale={2}
                                                    fixedDecimalLength={2}
                                                    decimalSeparator=","
                                                    groupSeparator="."
                                                    onValueChange={(value, name, values) => {
                                                        if (value) {
                                                            let numericValue = parseFloat(value.replace(',', '.'));
                                                            // Adjust to step of 5 and .99 ending
                                                            numericValue = Math.round(numericValue) - 0.01;
                                                            field.onChange(Math.max(0, numericValue));
                                                        } else {
                                                            field.onChange(0);
                                                        }
                                                    }}
                                                    value={field.value}
                                                    suffix=" €"
                                                />
                                            </FormControl>
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
                                        disabled={
                                            form.formState.isSubmitting || isSubmitting
                                        }
                                    >
                                        {form.formState.isSubmitting || isSubmitting
                                            ? "Saving..."
                                            : initialData
                                                ? "Update Service"
                                                : "Create Service"}
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
