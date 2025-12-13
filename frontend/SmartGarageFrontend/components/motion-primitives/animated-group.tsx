'use client'
import { motion, Variants } from 'framer-motion'
import React from 'react'

interface AnimatedGroupProps {
    children: React.ReactNode
    variants?: {
        container?: Variants
        item?: Variants
    }
    className?: string
}

export function AnimatedGroup({
                                  children,
                                  variants,
                                  className
                              }: AnimatedGroupProps) {
    const containerVariants: Variants = variants?.container || {
        hidden: { opacity: 0 },
        visible: {
            opacity: 1,
            transition: {
                staggerChildren: 0.1
            }
        }
    }

    const itemVariants: Variants = variants?.item || {
        hidden: { opacity: 0, y: 20 },
        visible: {
            opacity: 1,
            y: 0,
            transition: {
                duration: 0.5
            }
        }
    }

    return (
        <motion.div
            variants={containerVariants}
            initial="hidden"
            animate="visible"
            className={className}
        >
            {React.Children.map(children, (child, index) => (
                <motion.div key={index} variants={itemVariants}>
                    {child}
                </motion.div>
            ))}
        </motion.div>
    )
}