'use client'
import { motion, Variants } from 'framer-motion'
import React from 'react'

interface TextEffectProps {
    children: string
    per?: 'word' | 'line' | 'char'
    as?: keyof JSX.IntrinsicElements
    className?: string
    delay?: number
    preset?: 'fade-in-blur' | 'fade-in' | 'slide-up'
    speedSegment?: number
}

export function TextEffect({
                               children,
                               per = 'word',
                               as: Component = 'p',
                               className,
                               delay = 0,
                               preset = 'fade-in',
                               speedSegment = 0.3
                           }: TextEffectProps) {
    const words = children.split(' ')

    const variants: Variants = {
        hidden: {
            opacity: 0,
            filter: preset === 'fade-in-blur' ? 'blur(10px)' : 'none',
            y: preset === 'slide-up' ? 20 : 0
        },
        visible: {
            opacity: 1,
            filter: 'blur(0px)',
            y: 0,
            transition: {
                duration: 0.5,
                ease: "easeOut"
            }
        }
    }

    if (per === 'word') {
        return (
            <Component className={className}>
                {words.map((word, index) => (
                    <motion.span
                        key={index}
                        variants={variants}
                        initial="hidden"
                        animate="visible"
                        transition={{
                            delay: delay + index * speedSegment,
                        }}
                        className="inline-block mr-1"
                    >
                        {word}
                    </motion.span>
                ))}
            </Component>
        )
    }

    return (
        <motion.div
            initial="hidden"
            animate="visible"
            variants={variants}
            transition={{ delay }}
            className={className}
        >
            {children}
        </motion.div>
    )
}