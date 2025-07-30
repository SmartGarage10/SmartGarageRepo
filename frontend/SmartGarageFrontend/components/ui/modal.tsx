'use client';

import { useIsMobile } from '@/hooks/use-mobile';
import React from 'react';
import {
    Drawer,
    DrawerClose,
    DrawerContent,
    DrawerDescription,
    DrawerFooter,
    DrawerHeader,
    DrawerTitle,
    DrawerTrigger,
} from '@/components/ui/drawer';
import {
    Dialog,
    DialogClose,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from '@/components/ui/dialog';

interface ModalRootProps {
    children: React.ReactNode;
    open?: boolean;
    onOpenChange?: (open: boolean) => void;
}

interface ModalBaseProps {
    children: React.ReactNode;
    className?: string;
    asChild?: true;
}

const Modal = ({ children, ...props }: ModalRootProps) => {
    const isMobile = useIsMobile();

    const Modal = isMobile ? Drawer : Dialog;

    return <Modal {...props}>{children}</Modal>;
};

const ModalTrigger = ({ className, children, ...props }: ModalBaseProps) => {
    const isMobile = useIsMobile();

    const ModalTrigger = isMobile ? DrawerTrigger : DialogTrigger;

    return (
        <ModalTrigger className={className} {...props}>
            {children}
        </ModalTrigger>
    );
};

const ModalContent = ({ className, children, ...props }: ModalBaseProps) => {
    const isMobile = useIsMobile();

    const ModalContent = isMobile ? DrawerContent : DialogContent;

    return (
        <ModalContent className={className} {...props}>
            {children}
        </ModalContent>
    );
};

const ModalHeader = ({ className, children, ...props }: ModalBaseProps) => {
    const isMobile = useIsMobile();

    const ModalHeader = isMobile ? DrawerHeader : DialogHeader;

    return (
        <ModalHeader className={className} {...props}>
            {children}
        </ModalHeader>
    );
};

const ModalTitle = ({ className, children, ...props }: ModalBaseProps) => {
    const isMobile = useIsMobile();

    const ModalTitle = isMobile ? DrawerTitle : DialogTitle;

    return (
        <ModalTitle className={className} {...props}>
            {children}
        </ModalTitle>
    );
};

const ModalDescription = ({
                              className,
                              children,
                              ...props
                          }: ModalBaseProps) => {
    const isMobile = useIsMobile();

    const ModalDescription = isMobile ? DrawerDescription : DialogDescription;

    return (
        <ModalDescription className={className} {...props}>
            {children}
        </ModalDescription>
    );
};

const ModalFooter = ({ className, children, ...props }: ModalBaseProps) => {
    const isMobile = useIsMobile();

    const ModalFooter = isMobile ? DrawerFooter : DialogFooter;

    return (
        <ModalFooter className={className} {...props}>
            {children}
        </ModalFooter>
    );
};

const ModalClose = ({ className, children, ...props }: ModalBaseProps) => {
    const isMobile = useIsMobile();

    const ModalClose = isMobile ? DrawerClose : DialogClose;

    return (
        <ModalClose className={className} {...props}>
            {children}
        </ModalClose>
    );
};

export {
    Modal,
    ModalTrigger,
    ModalClose,
    ModalContent,
    ModalHeader,
    ModalTitle,
    ModalDescription,
    ModalFooter,
};