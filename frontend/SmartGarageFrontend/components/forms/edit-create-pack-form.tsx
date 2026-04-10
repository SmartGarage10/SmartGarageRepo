"use client";

import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import * as Dialog from "@radix-ui/react-dialog";
import { X } from "lucide-react";

import { Button } from "@/components/ui/button";
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
    FormDescription,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import MultipleSelector, { Option } from "@/components/ui/multiple-selector";
import { Pack } from "@/types/pack";
import { cn } from "@/lib/utils";

interface PackFormProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;

    initialData: Pack | null;

    serviceOptions: Option[];

    selectedServiceOptions: Option[];
    onServicesChange: (options: Option[]) => void;

    amount: number;

    onSubmit: (data: Pack) => void;
    isSubmitting?: boolean;
}

export function PackForm({
                             open,
                             onOpenChange,
                             initialData,
                             serviceOptions,
                             onServicesChange,
                             amount,
                             onSubmit,
                             isSubmitting = false,
                         }: PackFormProps) {
    const form = useForm<Pack>({
        defaultValues: {
            id: "",
            packName: "",
            description: "",
            amount: 0,
            services: [],
        },
    });

    const [localSelectedOptions, setLocalSelectedOptions] = useState<Option[]>([]);

    useEffect(() => {
        if (!open) return;

        if (initialData && initialData.id) {
            // EDIT MODE
            form.reset(initialData);

            const mapped = initialData.services.map(s => {
                const opt = serviceOptions.find(o => o.value === s.id);
                return opt || { label: s.serviceName, value: s.id };
            });

            setLocalSelectedOptions(mapped);
            form.setValue("services", initialData.services as any);
        } else {
            // CREATE MODE
            form.reset({
                id: "",
                packName: "",
                description: "",
                amount: 0,
                services: [],
            });

            setLocalSelectedOptions([]);
            form.setValue("services", []);
        }
    }, [open]); // 👈 ONLY open

    // Keep the form's amount field in sync with the calculated amount prop
    useEffect(() => {
        // Always write the latest calculated amount into the form so submit includes it
        form.setValue("amount", amount, { shouldValidate: true, shouldDirty: true });
    }, [amount, form]);


    // FIXED: Centralized handler
    const handleServiceSelection = (opts: Option[]) => {
        setLocalSelectedOptions(opts);
        onServicesChange(opts);

        form.setValue(
            "services",
            opts.map((o) => ({
                id: o.value,
                serviceName: o.label,
                serviceDescription: (o as any).serviceDescription ?? "",
                price: (o as any).price ?? 0,
            }))
        );
    };

    return (
        <Dialog.Root open={open} onOpenChange={onOpenChange}>
            <Dialog.Portal>
                <Dialog.Overlay className="fixed inset-0 bg-black/80 backdrop-blur-sm z-50" />
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
                                    {initialData?.id ? "Edit Pack" : "Add Pack"}
                                </Dialog.Title>
                                <Dialog.Description className="text-sm text-muted-foreground">
                                    {initialData?.id
                                        ? "Update pack details."
                                        : "Fill in the details to add a new pack."}
                                </Dialog.Description>
                            </div>
                            <Dialog.Close className="opacity-70 hover:opacity-100">
                                <X className="h-5 w-5" />
                            </Dialog.Close>
                        </div>

                        <Form {...form}>
                            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
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
                                    rules={{ required: "Description is required" }}
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Description</FormLabel>
                                            <FormControl>
                                                <Textarea {...field} placeholder="Pack description" />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                {/* Services */}
                                <FormField
                                    control={form.control}
                                    name="services"
                                    rules={{ required: "Please select at least one service" }}
                                    render={() => (
                                        <FormItem>
                                            <FormLabel>Services</FormLabel>
                                            <FormControl>
                                                <MultipleSelector
                                                    options={serviceOptions}
                                                    value={localSelectedOptions}
                                                    onChange={handleServiceSelection}
                                                    placeholder="Select services..."
                                                    className="w-full mt-2"
                                                />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                {/* Total Price */}
                                <FormItem>
                                    <FormLabel>Total Price</FormLabel>
                                    <FormControl>
                                        <div className="relative">
                                            <Input
                                                type="number"
                                                value={amount}
                                                readOnly
                                                className="bg-muted pr-8 cursor-not-allowed font-mono"
                                            />
                                            <span className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground">
                                                €
                                            </span>
                                        </div>
                                    </FormControl>
                                    <FormDescription>
                                        Auto-calculated based on selected services
                                    </FormDescription>
                                </FormItem>

                                <div className="flex flex-col gap-2 pt-4">
                                    <Dialog.Close asChild>
                                        <Button variant="outline" className="w-full">
                                            Cancel
                                        </Button>
                                    </Dialog.Close>

                                    <Button type="submit" className="w-full" disabled={isSubmitting}>
                                        {isSubmitting
                                            ? "Saving..."
                                            : initialData?.id
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
