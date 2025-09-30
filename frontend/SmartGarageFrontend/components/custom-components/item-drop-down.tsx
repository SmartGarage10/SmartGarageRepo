import {
    DropdownMenu,
    DropdownMenuTrigger,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
} from "@/components/ui/dropdown-menu";
import { MoreHorizontal, Edit, Copy, Trash2 } from "lucide-react";
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
    AlertDialogTrigger
} from "@/components/ui/alert-dialog";
import { useState } from "react";

type DropdownProps = {
    itemType?: string;
    onEdit?: () => void;
    onDuplicate?: () => void;
    onDelete?: () => void;
    showEdit?: boolean;
    showDuplicate?: boolean;
    showDelete?: boolean;
};

export const Dropdown = ({
                             itemType = "Item",
                             onEdit,
                             onDuplicate,
                             onDelete,
                             showEdit = true,
                             showDuplicate = true,
                             showDelete = true,
                         }: DropdownProps) => {
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

    const handleDeleteConfirm = () => {
        if (onDelete) {
            onDelete();
        }
        setDeleteDialogOpen(false);
    };

    return (
        <div className="opacity-0 group-hover:opacity-100 transition-opacity duration-200">
            <DropdownMenu>
                <DropdownMenuTrigger asChild>
                    <button
                        className="p-2 rounded-full hover:bg-gray-100/80 transition-colors focus:outline-none focus:ring-2 focus:ring-gray-300"
                        aria-label={`${itemType} options`}
                    >
                        <MoreHorizontal className="h-4 w-4" />
                    </button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end" className="w-32">
                    {showEdit && (
                        <DropdownMenuItem onClick={onEdit} className="flex items-center gap-2">
                            <Edit className="h-4 w-4" /> Edit
                        </DropdownMenuItem>
                    )}

                    {showDuplicate && (
                        <DropdownMenuItem onClick={onDuplicate} className="flex items-center gap-2">
                            <Copy className="h-4 w-4" /> Duplicate
                        </DropdownMenuItem>
                    )}

                    {(showEdit || showDuplicate) && showDelete && <DropdownMenuSeparator />}

                    {showDelete && (
                        <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
                            <AlertDialogTrigger asChild>
                                <DropdownMenuItem
                                    onSelect={(e) => e.preventDefault()}
                                    className="text-red-600 cursor-pointer flex items-center gap-2"
                                >
                                    <Trash2 className="h-4 w-4" />
                                    Delete
                                </DropdownMenuItem>
                            </AlertDialogTrigger>
                            <AlertDialogContent>
                                <AlertDialogHeader>
                                    <AlertDialogTitle>Confirm Delete</AlertDialogTitle>
                                    <AlertDialogDescription>
                                        This action cannot be undone. This will permanently delete the {itemType.toLowerCase()} and remove all associated data.
                                    </AlertDialogDescription>
                                </AlertDialogHeader>
                                <AlertDialogFooter>
                                    <AlertDialogCancel>Cancel</AlertDialogCancel>
                                    <AlertDialogAction
                                        onClick={handleDeleteConfirm}
                                        className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
                                    >
                                        Delete
                                    </AlertDialogAction>
                                </AlertDialogFooter>
                            </AlertDialogContent>
                        </AlertDialog>
                    )}
                </DropdownMenuContent>
            </DropdownMenu>
        </div>
    );
};