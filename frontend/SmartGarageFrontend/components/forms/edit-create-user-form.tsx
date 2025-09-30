"use client";

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
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue
} from "@/components/ui/select";

import { X } from "lucide-react";
import { User, UserRole } from "@/types/user";
import { cn } from "@/lib/utils";

import { useRouter } from "next/navigation";

import * as Dialog from "@radix-ui/react-dialog";
import { useForm } from "react-hook-form";
import { useEffect } from "react";

interface UserFormProps {
    initialData?: User | null;
    children?: React.ReactNode;
    open?: boolean;
    onOpenChange?: (open: boolean) => void;
    onSuccess?: () => void;
    onSubmit?: (data: User) => Promise<void>;
    isSubmitting?: boolean;
}

export function UserForm({
                             initialData,
                             children,
                             open,
                             onOpenChange,
                             onSuccess,
                             onSubmit,
                             isSubmitting = false,
                         }: UserFormProps) {
    const router = useRouter();
    const form = useForm<User>({
        defaultValues: {
            id: "",
            name: "",
            username: "",
            email: "",
            role: UserRole.CLIENT,
            address: "",
            phone: "",
        },
    });

    useEffect(() => {
        if (initialData) {
            const normalizedData = {
                ...initialData,
                role: typeof initialData.role === 'string'
                    ? initialData.role
                    : initialData.role.roleName
            };
            form.reset(normalizedData);
        } else if (open) {
            form.reset({
                id: "",
                name: "",
                username: "",
                email: "",
                role: "",
                address: "",
                phone: "",
            });
        }
    }, [initialData, open, form]);

    const handleSubmit = async (data: User) => {
        try {
            // Validate before submission
            if (!data.name || data.name.trim().length === 0) {
                form.setError("name", { message: "Full name is required" });
                return;
            }
            if (data.name.length > 100) {
                form.setError("name", { message: "Name cannot exceed 100 characters" });
                return;
            }

            if (!data.username || data.username.length < 3) {
                form.setError("username", { message: "Username must be at least 3 characters" });
                return;
            }
            if (data.username.length > 30) {
                form.setError("username", { message: "Username cannot exceed 30 characters" });
                return;
            }
            if (!/^[a-zA-Z0-9_]+$/.test(data.username)) {
                form.setError("username", {
                    message: "Username can only contain letters, numbers, and underscores"
                });
                return;
            }

            if (!data.email) {
                form.setError("email", { message: "Email is required" });
                return;
            }
            if (!/^\S+@\S+\.\S+$/.test(data.email)) {
                form.setError("email", { message: "Invalid email address" });
                return;
            }

            if (!data.role) {
                form.setError("role", { message: "Role is required" });
                return;
            }

            if (data.address && data.address.length > 500) {
                form.setError("address", { message: "Address cannot exceed 500 characters" });
                return;
            }

            if (data.phone && !/^[\d\s+\-()]{10,20}$/.test(data.phone)) {
                form.setError("phone", { message: "Please enter a valid phone number" });
                return;
            }

            // Proceed with submission if validation passes
            if (onSubmit) {
                await onSubmit(data);
            } else {
                const isUpdate = !!initialData?.id;
                const endpoint = isUpdate
                    ? `http://localhost:8080/api/user/${initialData.id}`
                    : 'http://localhost:8080/api/register';
                const method = isUpdate ? 'PUT' : 'POST';

                const payload = {
                    ...data,
                    ...(!isUpdate && {
                        password: "defaultPassword",
                        confirmPassword: "defaultPassword"
                    })
                };

                const response = await fetch(endpoint, {
                    method,
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(payload),
                    credentials: 'include'
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message ||
                        (isUpdate ? 'Update failed' : 'Registration failed'));
                }
            }

            if (!initialData) {
                form.reset();
            }

            onOpenChange?.(false);
            onSuccess?.();
            router.refresh();

        } catch (error) {
            console.error("Operation error:", error);
            form.setError("root", {
                message: error instanceof Error ? error.message : 'An error occurred'
            });
        }
    };

    return (
        <Dialog.Root open={open} onOpenChange={onOpenChange}>
            {children && <Dialog.Trigger asChild>{children}</Dialog.Trigger>}
            <Dialog.Portal>
                <Dialog.Overlay
                    className="fixed inset-0 z-50 bg-black/80 backdrop-blur-sm data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0"/>
                <Dialog.Content
                    className={cn(
                        "fixed right-0 top-0 z-50 h-full w-full max-w-sm border-l bg-background shadow-lg duration-200",
                        "data-[state=open]:animate-in data-[state=closed]:animate-out",
                        "data-[state=closed]:slide-out-to-right data-[state=open]:slide-in-from-right"
                    )}
                >
                    <div className="h-full overflow-y-auto p-6">
                        <div className="flex justify-between items-center mb-6">
                            <div>
                                <Dialog.Title className="text-xl font-bold">
                                    {initialData ? "Edit User" : "Register New User"}
                                </Dialog.Title>
                                <Dialog.Description className="text-sm text-muted-foreground">
                                    {initialData ? "Update the user details below." : "Fill in the details for the new user."}
                                </Dialog.Description>
                            </div>
                            <Dialog.Close
                                className="rounded-sm opacity-70 ring-offset-background transition-opacity hover:opacity-100 focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:pointer-events-none">
                                <X className="h-5 w-5"/>
                                <span className="sr-only">Close</span>
                            </Dialog.Close>
                        </div>

                        <Form {...form}>
                            <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
                                {form.formState.errors.root && (
                                    <div className="text-destructive text-sm p-2 rounded bg-destructive/10">
                                        {form.formState.errors.root.message}
                                    </div>
                                )}

                                <div className="grid grid-cols-1 gap-4">
                                    <FormField
                                        control={form.control}
                                        name="name"
                                        render={({ field, fieldState }) => (
                                            <FormItem>
                                                <FormLabel>Full Name</FormLabel>
                                                <FormControl>
                                                    <Input
                                                        placeholder="John Doe"
                                                        {...field}
                                                        className={fieldState.error ? "border-destructive" : ""}
                                                    />
                                                </FormControl>
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />

                                    <FormField
                                        control={form.control}
                                        name="username"
                                        render={({ field, fieldState }) => (
                                            <FormItem>
                                                <FormLabel>Username</FormLabel>
                                                <FormControl>
                                                    <Input
                                                        placeholder="johndoe123"
                                                        {...field}
                                                        className={fieldState.error ? "border-destructive" : ""}
                                                    />
                                                </FormControl>
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />

                                    <FormField
                                        control={form.control}
                                        name="email"
                                        render={({ field, fieldState }) => (
                                            <FormItem>
                                                <FormLabel>Email</FormLabel>
                                                <FormControl>
                                                    <Input
                                                        placeholder="john@example.com"
                                                        type="email"
                                                        {...field}
                                                        className={fieldState.error ? "border-destructive" : ""}
                                                    />
                                                </FormControl>
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />

                                    <FormField
                                        control={form.control}
                                        name="role"
                                        render={({ field, fieldState }) => (
                                            <FormItem>
                                                <FormLabel>Role</FormLabel>
                                                <Select
                                                    onValueChange={field.onChange}
                                                    value={field.value}
                                                    defaultValue={field.value}
                                                >
                                                    <FormControl>
                                                        <SelectTrigger
                                                            className={cn(
                                                                "w-full",
                                                                fieldState.error && "border-destructive"
                                                            )}
                                                        >
                                                            <SelectValue placeholder="Select a role"/>
                                                        </SelectTrigger>
                                                    </FormControl>
                                                    <SelectContent>
                                                        <SelectItem value={UserRole.ADMIN}>ADMIN</SelectItem>
                                                        <SelectItem value={UserRole.EMPLOYEE}>EMPLOYEE</SelectItem>
                                                        <SelectItem value={UserRole.CLIENT}>CLIENT</SelectItem>
                                                    </SelectContent>
                                                </Select>
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />

                                    <FormField
                                        control={form.control}
                                        name="address"
                                        render={({ field, fieldState }) => (
                                            <FormItem>
                                                <FormLabel>Address</FormLabel>
                                                <FormControl>
                                                    <Input
                                                        placeholder="123 Main St"
                                                        {...field}
                                                        className={fieldState.error ? "border-destructive" : ""}
                                                    />
                                                </FormControl>
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />

                                    <FormField
                                        control={form.control}
                                        name="phone"
                                        render={({ field, fieldState }) => (
                                            <FormItem>
                                                <FormLabel>Phone</FormLabel>
                                                <FormControl>
                                                    <Input
                                                        placeholder="+1234567890"
                                                        {...field}
                                                        className={fieldState.error ? "border-destructive" : ""}
                                                    />
                                                </FormControl>
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />
                                </div>

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
                                        {form.formState.isSubmitting || isSubmitting ? (
                                            <span>Processing...</span>
                                        ) : initialData ? (
                                            "Update User"
                                        ) : (
                                            "Register User"
                                        )}
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