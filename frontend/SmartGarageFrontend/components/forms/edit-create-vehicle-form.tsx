'use client';

import React, { useEffect } from 'react';
import { useForm, useWatch } from 'react-hook-form';
import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Check, ChevronsUpDown, X } from 'lucide-react';
import * as Dialog from '@radix-ui/react-dialog';
import { cn } from '@/lib/utils';
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem } from '@/components/ui/command';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';

import { Vehicle } from '@/types/vehicle';
import { User } from '@/types/user';

export interface VehicleFormValues {
    id?: string;
    client: User;
    vehiclePlate: string;
    vin: string;
    brand: string;
    model: string;
    year: number;
}

interface VehicleFormProps {
    initialData?: Vehicle | null;
    open?: boolean;
    onOpenChange?: (open: boolean) => void;
    onSuccess?: () => void;
    onSubmit: (data: VehicleFormValues) => Promise<void>;
    isSubmitting?: boolean;
    clients: User[];
    brandOptions: { label: string; value: string }[];
    modelOptions: { label: string; value: string }[];
    selectedBrand: string;
    onBrandChange: (brand: string) => void;
    isBrandsLoading?: boolean;
    isModelsLoading?: boolean;
}

export function VehicleForm({
                                initialData,
                                open,
                                onOpenChange,
                                onSuccess,
                                onSubmit,
                                isSubmitting = false,
                                clients,
                                brandOptions,
                                modelOptions,
                                selectedBrand,
                                onBrandChange,
                                isBrandsLoading = false,
                                isModelsLoading = false,
                            }: VehicleFormProps) {
    const router = useRouter();
    const currentYear = new Date().getFullYear();

    const form = useForm<VehicleFormValues>({
        defaultValues: {
            client: { id: '', name: '', email: '' },
            vehiclePlate: '',
            vin: '',
            brand: '',
            model: '',
            year: currentYear,
        },
    });

    const [userDropdownOpen, setUserDropdownOpen] = React.useState(false);
    const [brandDropdownOpen, setBrandDropdownOpen] = React.useState(false);
    const [modelDropdownOpen, setModelDropdownOpen] = React.useState(false);
    const [yearDropdownOpen, setYearDropdownOpen] = React.useState(false);

    const years = Array.from({ length: 30 }, (_, i) => currentYear - i);

    // Reset form when opening or initialData changes
    useEffect(() => {
        if (open) {
            if (initialData) {
                const matchingClient = clients.find(c => c.id === initialData.client?.id) || initialData.client;
                form.reset({
                    id: initialData.id,
                    client: matchingClient,
                    vehiclePlate: initialData.vehiclePlate,
                    vin: initialData.vin,
                    brand: initialData.brand,
                    model: initialData.model,
                    year: initialData.year,
                });
            } else {
                form.reset({
                    client: { id: '', name: '', email: '' },
                    vehiclePlate: '',
                    vin: '',
                    brand: '',
                    model: '',
                    year: currentYear,
                });
            }
        } else {
            form.reset({
                client: { id: '', name: '', email: '' },
                vehiclePlate: '',
                vin: '',
                brand: '',
                model: '',
                year: currentYear,
            });
        }
    }, [open, initialData, form, clients, currentYear]);

    const handleSubmit = async (data: VehicleFormValues) => {
        // Basic validation
        if (!data.client?.id) {
            form.setError('client', { message: 'Client is required', type: 'manual' });
            return;
        }
        if (!data.vehiclePlate) {
            form.setError('vehiclePlate', { message: 'Vehicle plate is required', type: 'manual' });
            return;
        }
        if (!data.vin) {
            form.setError('vin', { message: 'VIN is required', type: 'manual' });
            return;
        }
        if (!data.brand) {
            form.setError('brand', { message: 'Brand is required', type: 'manual' });
            return;
        }
        if (!data.model) {
            form.setError('model', { message: 'Model is required', type: 'manual' });
            return;
        }
        if (!data.year || data.year < 1900 || data.year > currentYear) {
            form.setError('year', { message: 'Valid year is required', type: 'manual' });
            return;
        }

        try {
            await onSubmit(data);
            onOpenChange?.(false);
            onSuccess?.();
            router.refresh();
        } catch (error) {
            console.error(error);
            form.setError('root', { message: error instanceof Error ? error.message : 'An error occurred' });
        }
    };

    const selectedYear = useWatch({ control: form.control, name: 'year' });

    const formatVehicleLabel = (vehicle: Vehicle) =>
        `${vehicle.brand} ${vehicle.model} (${vehicle.year}) - ${vehicle.vehiclePlate}`;

    if (!open) return null;

    return (
        <Dialog.Root open={open} onOpenChange={onOpenChange}>
            <Dialog.Portal>
                <Dialog.Overlay className="fixed inset-0 z-50 bg-black/80 backdrop-blur-sm" />
                <Dialog.Content
                    className={cn(
                        'fixed right-0 top-0 z-50 h-full w-full max-w-sm border-l bg-background shadow-lg',
                        'data-[state=open]:animate-in data-[state=closed]:animate-out',
                        'data-[state=closed]:slide-out-to-right data-[state=open]:slide-in-from-right'
                    )}
                >
                    <div className="h-full overflow-y-auto p-6">
                        <div className="flex justify-between items-center mb-6">
                            <div>
                                <Dialog.Title className="text-xl font-bold">
                                    {initialData ? 'Edit Vehicle' : 'Add Vehicle'}
                                </Dialog.Title>
                                <Dialog.Description className="text-sm text-muted-foreground">
                                    {initialData ? 'Update vehicle details.' : 'Fill in the details to add a vehicle.'}
                                </Dialog.Description>
                            </div>
                            <Dialog.Close className="opacity-70 hover:opacity-100">
                                <X className="h-5 w-5" />
                            </Dialog.Close>
                        </div>

                        <Form {...form}>
                            <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
                                {/* Client */}
                                <FormField
                                    control={form.control}
                                    name="client"
                                    render={({ field }) => (
                                        <FormItem className="flex flex-col">
                                            <FormLabel>Client *</FormLabel>
                                            <Popover open={userDropdownOpen} onOpenChange={setUserDropdownOpen}>
                                                <PopoverTrigger asChild>
                                                    <FormControl>
                                                        <Button variant="outline" role="combobox" className={cn('w-full h-10 justify-between', !field.value?.id && 'text-muted-foreground')}>
                                                            {field.value?.name || 'Select client'}
                                                            <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                                                        </Button>
                                                    </FormControl>
                                                </PopoverTrigger>
                                                <PopoverContent className="w-full p-0" style={{ width: 'var(--radix-popover-trigger-width)' }} align="start">
                                                    <Command>
                                                        <CommandInput placeholder="Search clients..." />
                                                        <CommandEmpty>No clients found.</CommandEmpty>
                                                        <CommandGroup className="max-h-[300px] overflow-y-auto">
                                                            {clients.map((client) => (
                                                                <CommandItem
                                                                    key={client.id}
                                                                    value={client.name}
                                                                    onSelect={() => {
                                                                        form.setValue('client', client);
                                                                        setUserDropdownOpen(false);
                                                                    }}
                                                                >
                                                                    <div className="flex items-center gap-3">
                                                                        <div>
                                                                            <div>{client.name}</div>
                                                                            <div className="text-xs text-muted-foreground">{client.email}</div>
                                                                        </div>
                                                                    </div>
                                                                    <Check className={cn('ml-auto h-4 w-4', field.value?.id === client.id ? 'opacity-100' : 'opacity-0')} />
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
                                <FormField control={form.control} name="vehiclePlate" render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Vehicle Plate *</FormLabel>
                                        <FormControl><Input {...field} /></FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )} />

                                {/* VIN */}
                                <FormField control={form.control} name="vin" render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>VIN *</FormLabel>
                                        <FormControl><Input {...field} /></FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )} />

                                {/* Brand */}
                                <FormField control={form.control} name="brand" render={({ field }) => (
                                    <FormItem className="flex flex-col">
                                        <FormLabel>Brand *</FormLabel>
                                        <Popover open={brandDropdownOpen} onOpenChange={setBrandDropdownOpen}>
                                            <PopoverTrigger asChild>
                                                <FormControl>
                                                    <Button variant="outline" role="combobox" className={cn('w-full h-10 justify-between', !field.value && 'text-muted-foreground')} disabled={isBrandsLoading}>
                                                        {isBrandsLoading ? 'Loading...' : field.value || 'Select brand'}
                                                        <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                                                    </Button>
                                                </FormControl>
                                            </PopoverTrigger>
                                            <PopoverContent className="w-full p-0" style={{ width: 'var(--radix-popover-trigger-width)' }} align="start">
                                                <Command>
                                                    <CommandInput placeholder="Search brands..." />
                                                    <CommandEmpty>No brands found.</CommandEmpty>
                                                    <CommandGroup className="max-h-[300px] overflow-y-auto">
                                                        {brandOptions.map((b) => (
                                                            <CommandItem key={b.value} value={b.value} onSelect={() => { field.onChange(b.value); onBrandChange(b.value); setModelDropdownOpen(false); }}>
                                                                {b.label}
                                                                <Check className={cn('ml-auto h-4 w-4', field.value === b.value ? 'opacity-100' : 'opacity-0')} />
                                                            </CommandItem>
                                                        ))}
                                                    </CommandGroup>
                                                </Command>
                                            </PopoverContent>
                                        </Popover>
                                        <FormMessage />
                                    </FormItem>
                                )} />

                                {/* Model */}
                                <FormField control={form.control} name="model" render={({ field }) => (
                                    <FormItem className="flex flex-col">
                                        <FormLabel>Model *</FormLabel>
                                        <Popover open={modelDropdownOpen} onOpenChange={setModelDropdownOpen}>
                                            <PopoverTrigger asChild>
                                                <FormControl>
                                                    <Button variant="outline" role="combobox" className={cn('w-full h-10 justify-between', !field.value && 'text-muted-foreground')} disabled={!selectedBrand || isModelsLoading}>
                                                        {isModelsLoading ? 'Loading...' : field.value || 'Select model'}
                                                        <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                                                    </Button>
                                                </FormControl>
                                            </PopoverTrigger>
                                            <PopoverContent className="w-full p-0" style={{ width: 'var(--radix-popover-trigger-width)' }} align="start">
                                                <Command>
                                                    <CommandInput placeholder="Search models..." />
                                                    <CommandEmpty>No models found.</CommandEmpty>
                                                    <CommandGroup className="max-h-[300px] overflow-y-auto">
                                                        {modelOptions.map((m) => (
                                                            <CommandItem key={m.value} value={m.value} onSelect={() => { field.onChange(m.value); setModelDropdownOpen(false); }}>
                                                                {m.label}
                                                                <Check className={cn('ml-auto h-4 w-4', field.value === m.value ? 'opacity-100' : 'opacity-0')} />
                                                            </CommandItem>
                                                        ))}
                                                    </CommandGroup>
                                                </Command>
                                            </PopoverContent>
                                        </Popover>
                                        <FormMessage />
                                    </FormItem>
                                )} />

                                {/* Year */}
                                <FormField control={form.control} name="year" render={({ field }) => (
                                    <FormItem className="flex flex-col">
                                        <FormLabel>Year *</FormLabel>
                                        <Popover open={yearDropdownOpen} onOpenChange={setYearDropdownOpen}>
                                            <PopoverTrigger asChild>
                                                <FormControl>
                                                    <Button variant="outline" role="combobox" className={cn('w-full h-10 justify-between', !field.value && 'text-muted-foreground')}>
                                                        {field.value || 'Select year'}
                                                        <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                                                    </Button>
                                                </FormControl>
                                            </PopoverTrigger>
                                            <PopoverContent className="w-full p-0" style={{ width: 'var(--radix-popover-trigger-width)' }} align="start">
                                                <Command>
                                                    <CommandInput placeholder="Search year..." />
                                                    <CommandEmpty>No years found.</CommandEmpty>
                                                    <CommandGroup className="max-h-[300px] overflow-y-auto">
                                                        {years.map((y) => (
                                                            <CommandItem key={y} value={y.toString()} onSelect={() => { field.onChange(y); setYearDropdownOpen(false); }}>
                                                                {y}
                                                                <Check className={cn('ml-auto h-4 w-4', field.value === y ? 'opacity-100' : 'opacity-0')} />
                                                            </CommandItem>
                                                        ))}
                                                    </CommandGroup>
                                                </Command>
                                            </PopoverContent>
                                        </Popover>
                                        <FormMessage />
                                    </FormItem>
                                )} />

                                {form.formState.errors.root && (
                                    <div className="text-sm font-medium text-destructive">{form.formState.errors.root.message}</div>
                                )}

                                <div className="flex flex-col gap-2 pt-4">
                                    <Dialog.Close asChild>
                                        <Button variant="outline" className="w-full">Cancel</Button>
                                    </Dialog.Close>
                                    <Button type="submit" className="w-full" disabled={form.formState.isSubmitting || isSubmitting}>
                                        {form.formState.isSubmitting || isSubmitting ? 'Saving...' : initialData ? 'Update Vehicle' : 'Create Vehicle'}
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
